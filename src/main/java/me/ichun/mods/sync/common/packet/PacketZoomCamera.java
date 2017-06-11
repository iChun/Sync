package me.ichun.mods.sync.common.packet;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import me.ichun.mods.sync.common.Sync;

public class PacketZoomCamera extends AbstractPacket
{
    public int posX;
    public int posY;
    public int posZ;
    public int dimID;
    public int zoomFace;
    public boolean zoom;
    public boolean zoomDeath;

    public PacketZoomCamera(){}

    public PacketZoomCamera(int posX, int posY, int posZ, int dimID, int zoomFace, boolean zoom, boolean zoomDeath)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.dimID = dimID;
        this.zoomFace = zoomFace;
        this.zoom = zoom;
        this.zoomDeath = zoomDeath;
    }


    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt((int) Math.floor(posX));
        buffer.writeInt((int) Math.floor(posY));
        buffer.writeInt((int) Math.floor(posZ));
        buffer.writeInt(dimID);
        buffer.writeInt(zoomFace);
        buffer.writeBoolean(zoom);
        buffer.writeBoolean(zoomDeath);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        //zoom state
        Sync.eventHandlerClient.zoomPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());

        Sync.eventHandlerClient.zoomDimension = buffer.readInt();

        Sync.eventHandlerClient.zoomFace = buffer.readInt();
        Sync.eventHandlerClient.zoom = buffer.readBoolean();

        Sync.eventHandlerClient.zoomTimer = 60;

        Sync.eventHandlerClient.zoomDeath = buffer.readBoolean();

    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
