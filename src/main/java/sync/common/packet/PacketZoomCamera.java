package sync.common.packet;

import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import sync.common.Sync;

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
    public void writeTo(ByteBuf buffer, Side side)
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
    public void readFrom(ByteBuf buffer, Side side)
    {
        //zoom state
        Sync.proxy.tickHandlerClient.zoomX = buffer.readInt();
        Sync.proxy.tickHandlerClient.zoomY = buffer.readInt();
        Sync.proxy.tickHandlerClient.zoomZ = buffer.readInt();

        Sync.proxy.tickHandlerClient.zoomDimension = buffer.readInt();

        Sync.proxy.tickHandlerClient.zoomFace = buffer.readInt();
        Sync.proxy.tickHandlerClient.zoom = buffer.readBoolean();

        Sync.proxy.tickHandlerClient.zoomTimer = 60;

        Sync.proxy.tickHandlerClient.zoomDeath = buffer.readBoolean();

    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {

    }
}
