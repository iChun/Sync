package me.ichun.mods.sync.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPlayerEnterStorage extends AbstractPacket
{
    public int x;
    public int y;
    public int z;
    public BlockPos pos;

    public PacketPlayerEnterStorage(){}

    public PacketPlayerEnterStorage(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public PacketPlayerEnterStorage(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        handleClient();
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = mc.world.getTileEntity(pos);

        if(te instanceof TileEntityShellStorage)
        {
            TileEntityShellStorage ss = (TileEntityShellStorage)te;

            mc.player.setLocationAndAngles(ss.getPos().getX() + 0.5D, ss.getPos().getY(), ss.getPos().getZ() + 0.5D, (ss.face - 2) * 90F, 0F);

            Sync.eventHandlerClient.lockedStorage = ss;
            Sync.eventHandlerClient.lockTime = 5;

            Sync.eventHandlerClient.radialShow = true;
            Sync.eventHandlerClient.radialTime = 3;

            Sync.eventHandlerClient.radialPlayerYaw = mc.getRenderViewEntity().rotationYaw;
            Sync.eventHandlerClient.radialPlayerPitch = mc.getRenderViewEntity().rotationPitch;

            Sync.eventHandlerClient.radialDeltaX = Sync.eventHandlerClient.radialDeltaY = 0;
        }
    }
}
