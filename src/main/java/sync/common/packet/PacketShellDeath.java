package sync.common.packet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sync.client.entity.EntityShellDestruction;
import sync.common.tileentity.TileEntityDualVertical;

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
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(xCoord);
        buffer.writeInt(yCoord);
        buffer.writeInt(zCoord);
        buffer.writeInt(face);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        xCoord = buffer.readInt();
        yCoord = buffer.readInt();
        zCoord = buffer.readInt();
        face = buffer.readInt();
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
        if(mc.theWorld.blockExists(xCoord, yCoord, zCoord))
        {
            TileEntity te = mc.theWorld.getTileEntity(xCoord, yCoord, zCoord);
            if(te instanceof TileEntityDualVertical)
            {
                EntityShellDestruction sd = new EntityShellDestruction(mc.theWorld, (face - 2) * 90F, (face - 2) * 90F, 0.0F, 0.0F, 0.0F, ((TileEntityDualVertical)te).locationSkin);
                sd.setLocationAndAngles(xCoord + 0.5D, yCoord, zCoord + 0.5D, 0.0F, 0.0F);
                mc.theWorld.spawnEntityInWorld(sd);
            }
        }
    }
}
