package me.ichun.mods.sync.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.sync.common.Sync;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class PacketZoomCamera extends AbstractPacket
{
    public BlockPos pos;
    public int dimID;
    public int zoomFace;
    public boolean zoom;
    public boolean zoomDeath;

    public PacketZoomCamera(){}

    public PacketZoomCamera(BlockPos pos, int dimID, EnumFacing zoomFace, boolean zoom, boolean zoomDeath)
    {
        this.pos = pos;
        this.dimID = dimID;
        this.zoomFace = zoomFace.getIndex();
        this.zoom = zoom;
        this.zoomDeath = zoomDeath;
    }


    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeLong(pos.toLong());
        buffer.writeInt(dimID);
        buffer.writeInt(zoomFace);
        buffer.writeBoolean(zoom);
        buffer.writeBoolean(zoomDeath);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        //zoom state
        Sync.eventHandlerClient.zoomPos = BlockPos.fromLong(buffer.readLong());

        Sync.eventHandlerClient.zoomDimension = buffer.readInt();

        Sync.eventHandlerClient.zoomFace = EnumFacing.byIndex(buffer.readInt());
        Sync.eventHandlerClient.zoom = buffer.readBoolean();

        Sync.eventHandlerClient.zoomTimer = 60;

        Sync.eventHandlerClient.zoomDeath = buffer.readBoolean();

    }

    @Override
    public void execute(Side side, EntityPlayer player){}

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
