package me.ichun.mods.sync.common.packet;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.sync.api.SyncStartEvent;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class PacketSyncRequest extends AbstractPacket
{
    private static final List<String> CAP_NBT_BLACKLIST = ImmutableList.of("baubles:container");

    public BlockPos pos;
    public int dimID;
    public BlockPos shellPos;
    public int shellDimID;

    public PacketSyncRequest(){}

    public PacketSyncRequest(BlockPos pos, int dimID, BlockPos targetShellPos, int targetShellDimID) {
        this.pos = pos;
        this.dimID = dimID;
        this.shellPos = targetShellPos;
        this.shellDimID = targetShellDimID;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeLong(pos.toLong());
        buffer.writeInt(dimID);
        buffer.writeLong(shellPos.toLong());
        buffer.writeInt(shellDimID);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        pos = BlockPos.fromLong(buffer.readLong());

        dimID = buffer.readInt();

        shellPos = BlockPos.fromLong(buffer.readLong());

        shellDimID = buffer.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        //Receive sync request from client;
        boolean valid = false;

        WorldServer worldOri = DimensionManager.getWorld(dimID);
        WorldServer world = DimensionManager.getWorld(shellDimID);

        if(worldOri != null && world != null)
        {
            TileEntity oriTe = worldOri.getTileEntity(pos);
            TileEntity te = world.getTileEntity(shellPos);

            if(oriTe instanceof TileEntityDualVertical && te instanceof TileEntityDualVertical)
            {
                TileEntityDualVertical originShell = (TileEntityDualVertical)oriTe;
                TileEntityDualVertical targetShell = (TileEntityDualVertical)te;

                if(originShell.getPlayerName().equalsIgnoreCase(player.getName()) && targetShell.getPlayerName().equalsIgnoreCase(player.getName()))
                {
                    if(targetShell instanceof TileEntityShellConstructor)
                    {
                        TileEntityShellConstructor sc = (TileEntityShellConstructor)targetShell;
                        if(sc.constructionProgress < Sync.config.shellConstructionPowerRequirement)
                        {
                            ShellHandler.updatePlayerOfShells(player, null, true);
                            return;
                        }
                    }
                    if(targetShell instanceof TileEntityShellStorage)
                    {
                        TileEntityShellStorage ss = (TileEntityShellStorage)targetShell;
                        if(!ss.syncing)
                        {
                            ShellHandler.updatePlayerOfShells(player, null, true);
                            return;
                        }
                    }

                    if(originShell instanceof TileEntityShellStorage)
                    {
                        TileEntityShellStorage ss = (TileEntityShellStorage)originShell;
                        ss.setPlayerName(player.getName());
                        ss.occupied = true;
                        ss.occupationTime = TileEntityDualVertical.animationTime;
                        ss.syncing = true;

                        player.extinguish(); //Remove fire so when you sync back into this shell, you aren't on fire

                        NBTTagCompound tag = new NBTTagCompound();
                        player.writeToNBT(tag);
                        if (tag.hasKey("ForgeCaps", Constants.NBT.TAG_COMPOUND)) //deduplicate capability based items
                        {
                            NBTTagCompound forgeCaps = tag.getCompoundTag("ForgeCaps");
                            for (String blacklistedKey : CAP_NBT_BLACKLIST) //We can only do this for mods who are known for this behavior...
                            {
                                if (forgeCaps.hasKey(blacklistedKey))
                                {
                                    forgeCaps.removeTag(blacklistedKey);
                                }
                            }
                        }

                        tag.setInteger("sync_playerGameMode", ((EntityPlayerMP)player).interactionManager.getGameType().getID());

                        ss.setPlayerNBT(tag);

                        IBlockState state = worldOri.getBlockState(ss.getPos());
                        IBlockState state1 = worldOri.getBlockState(ss.getPos().offset(EnumFacing.UP));

                        worldOri.notifyBlockUpdate(ss.getPos(), state, state, 3);
                        worldOri.notifyBlockUpdate(ss.getPos().offset(EnumFacing.UP), state1, state1, 3);
                    }

                    Sync.channel.sendTo(new PacketZoomCamera(pos, dimID, originShell.face, false, false), player);

                    targetShell.resyncPlayer = 120;
                    originShell.canSavePlayer = -1;
                    targetShell.resyncOrigin = originShell; //Doing it this way probably isn't the best way
                    ShellHandler.syncInProgress.put(player.getName(), targetShell);

                    MinecraftForge.EVENT_BUS.post(new SyncStartEvent(player, originShell.getPlayerNBT(), targetShell.getPlayerNBT(), targetShell.getPos()));

                    Sync.channel.sendToAll(new PacketPlayerDeath(player.getName(), false));

                    valid = true;
                }
            }
        }
        if(!valid)
        {
            ShellHandler.updatePlayerOfShells(player, null, true);
        }
    }

    @Override
    public Side receivingSide()
    {
        return Side.SERVER;
    }
}
