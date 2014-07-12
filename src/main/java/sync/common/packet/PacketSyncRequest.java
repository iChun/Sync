package sync.common.packet;

import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import ichun.common.core.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import sync.api.SyncStartEvent;
import sync.common.Sync;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;

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
    public void writeTo(ByteBuf buffer, Side side)
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
    public void readFrom(ByteBuf buffer, Side side)
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
    public void execute(Side side, EntityPlayer player)
    {
        //Receive sync request from client;
        boolean valid = false;

        WorldServer worldOri = DimensionManager.getWorld(dimID);
        WorldServer world = DimensionManager.getWorld(shellDimID);

        if(worldOri != null && world != null)
        {
            TileEntity oriTe = worldOri.getTileEntity(xCoord, yCoord, zCoord);
            TileEntity te = world.getTileEntity(shellPosX, shellPosY, shellPosZ);

            if(oriTe instanceof TileEntityDualVertical && te instanceof TileEntityDualVertical)
            {
                TileEntityDualVertical originShell = (TileEntityDualVertical)oriTe;
                TileEntityDualVertical targetShell = (TileEntityDualVertical)te;

                if(originShell.getPlayerName().equalsIgnoreCase(player.getCommandSenderName()) && targetShell.getPlayerName().equalsIgnoreCase(player.getCommandSenderName()))
                {
                    if(targetShell instanceof TileEntityShellConstructor)
                    {
                        TileEntityShellConstructor sc = (TileEntityShellConstructor)targetShell;
                        if(sc.constructionProgress < Sync.config.getSessionInt("shellConstructionPowerRequirement"))
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
                        ss.setPlayerName(player.getCommandSenderName());
                        ss.occupied = true;
                        ss.occupationTime = TileEntityDualVertical.animationTime;
                        ss.syncing = true;

                        player.extinguish(); //Remove fire so when you sync back into this shell, you aren't on fire

                        NBTTagCompound tag = new NBTTagCompound();
                        player.writeToNBT(tag);

                        tag.setInteger("sync_playerGameMode", ((EntityPlayerMP)player).theItemInWorldManager.getGameType().getID());

                        ss.setPlayerNBT(tag);

                        worldOri.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
                        worldOri.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
                    }

                    PacketHandler.sendToPlayer(Sync.channels, new PacketZoomCamera(xCoord, yCoord, zCoord, dimID, originShell.face, false, false), player);

                    targetShell.resyncPlayer = 120;
                    originShell.canSavePlayer = -1;
                    targetShell.resyncOrigin = originShell; //Doing it this way probably isn't the best way
                    ShellHandler.syncInProgress.put(player.getCommandSenderName(), targetShell);

                    MinecraftForge.EVENT_BUS.post(new SyncStartEvent(player, originShell.getPlayerNBT(), targetShell.getPlayerNBT(), targetShell.xCoord, targetShell.yCoord, targetShell.zCoord));

                    PacketHandler.sendToAll(Sync.channels, new PacketPlayerDeath(player.getCommandSenderName(), false));

                    valid = true;
                }
            }
        }
        if(!valid)
        {
            ShellHandler.updatePlayerOfShells(player, null, true);
        }
    }
}
