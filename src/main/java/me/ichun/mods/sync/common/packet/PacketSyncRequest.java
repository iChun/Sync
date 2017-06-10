package me.ichun.mods.sync.common.packet;

import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.sync.api.SyncStartEvent;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import me.ichun.mods.sync.api.SyncStartEvent;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;

public class PacketSyncRequest extends AbstractPacket
{
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public int dimID;
    public int shellPosX;
    public int shellPosY;
    public int shellPosZ;
    public int shellDimID;

    public PacketSyncRequest(){}

    public PacketSyncRequest(int xCoord, int yCoord, int zCoord, int dimID, int shellPosX, int shellPosY, int shellPosZ, int shellDimID)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.dimID = dimID;
        this.shellPosX = shellPosX;
        this.shellPosY = shellPosY;
        this.shellPosZ = shellPosZ;
        this.shellDimID = shellDimID;
    }


    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(xCoord);
        buffer.writeInt(yCoord);
        buffer.writeInt(zCoord);
        buffer.writeInt(dimID);
        buffer.writeInt(shellPosX);
        buffer.writeInt(shellPosY);
        buffer.writeInt(shellPosZ);
        buffer.writeInt(shellDimID);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        xCoord = buffer.readInt();
        yCoord = buffer.readInt();
        zCoord = buffer.readInt();

        dimID = buffer.readInt();

        shellPosX = buffer.readInt();
        shellPosY = buffer.readInt();
        shellPosZ = buffer.readInt();

        shellDimID = buffer.readInt();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        //Receive sync request from client;
        boolean valid = false;

        WorldServer worldOri = DimensionManager.getWorld(dimID);
        WorldServer world = DimensionManager.getWorld(shellDimID);

        if(worldOri != null && world != null)
        {
            BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
            BlockPos shellPos = new BlockPos(shellPosX, shellPosY, shellPosZ);
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
                            return null;
                        }
                    }
                    if(targetShell instanceof TileEntityShellStorage)
                    {
                        TileEntityShellStorage ss = (TileEntityShellStorage)targetShell;
                        if(!ss.syncing)
                        {
                            ShellHandler.updatePlayerOfShells(player, null, true);
                            return null;
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

                        tag.setInteger("sync_playerGameMode", ((EntityPlayerMP)player).interactionManager.getGameType().getID());

                        ss.setPlayerNBT(tag);

                        IBlockState state = worldOri.getBlockState(ss.getPos());
                        IBlockState state1 = worldOri.getBlockState(ss.getPos().add(0, 1, 0));

                        worldOri.notifyBlockUpdate(ss.getPos(), state, state, 3);
                        worldOri.notifyBlockUpdate(ss.getPos().add(0, 1, 0), state1, state1, 3);
                    }

                    Sync.channel.sendTo(new PacketZoomCamera(xCoord, yCoord, zCoord, dimID, originShell.face, false, false), player);

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
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.SERVER;
    }
}
