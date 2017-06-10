package me.ichun.mods.sync.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.sync.client.entity.EntityShellDestruction;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketShellDeath extends AbstractPacket
{
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public int face;

    public PacketShellDeath(){}

    public PacketShellDeath(int xCoord, int yCoord, int zCoord, int face)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.face = face;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(xCoord);
        buffer.writeInt(yCoord);
        buffer.writeInt(zCoord);
        buffer.writeInt(face);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        xCoord = buffer.readInt();
        yCoord = buffer.readInt();
        zCoord = buffer.readInt();
        face = buffer.readInt();
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
        TileEntity te = mc.theWorld.getTileEntity(new BlockPos(xCoord, yCoord, zCoord));
        if(te instanceof TileEntityDualVertical)
        {
            EntityShellDestruction sd = new EntityShellDestruction(mc.theWorld, (face - 2) * 90F, (face - 2) * 90F, 0.0F, 0.0F, 0.0F, ((TileEntityDualVertical)te).locationSkin);
            sd.setLocationAndAngles(xCoord + 0.5D, yCoord, zCoord + 0.5D, 0.0F, 0.0F);
            mc.theWorld.spawnEntityInWorld(sd);
        }
    }
}
