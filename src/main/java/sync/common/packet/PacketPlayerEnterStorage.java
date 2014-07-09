package sync.common.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sync.common.Sync;
import sync.common.tileentity.TileEntityShellStorage;

public class PacketPlayerEnterStorage extends AbstractPacket
{
    public int x;
    public int y;
    public int z;

    public PacketPlayerEnterStorage(){}

    public PacketPlayerEnterStorage(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient();
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        Minecraft mc = Minecraft.getMinecraft();
        TileEntity te = mc.theWorld.getTileEntity(x, y, z);

        if(te instanceof TileEntityShellStorage)
        {
            TileEntityShellStorage ss = (TileEntityShellStorage)te;

            mc.thePlayer.setLocationAndAngles(ss.xCoord + 0.5D, ss.yCoord, ss.zCoord + 0.5D, (ss.face - 2) * 90F, 0F);

            Sync.proxy.tickHandlerClient.lockedStorage = ss;
            Sync.proxy.tickHandlerClient.lockTime = 5;

            Sync.proxy.tickHandlerClient.radialShow = true;
            Sync.proxy.tickHandlerClient.radialTime = 3;

            Sync.proxy.tickHandlerClient.radialPlayerYaw = mc.renderViewEntity.rotationYaw;
            Sync.proxy.tickHandlerClient.radialPlayerPitch = mc.renderViewEntity.rotationPitch;

            Sync.proxy.tickHandlerClient.radialDeltaX = Sync.proxy.tickHandlerClient.radialDeltaY = 0;
        }
    }
}
