package me.ichun.mods.sync.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.sync.client.entity.EntityShellDestruction;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketShellDeath extends AbstractPacket
{
    public BlockPos pos;
    public EnumFacing face;

    public PacketShellDeath(){}

    public PacketShellDeath(BlockPos pos, EnumFacing face)
    {
        this.pos = pos;
        this.face = face;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeLong(pos.toLong());
        buffer.writeInt(face.getIndex());
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        pos = BlockPos.fromLong(buffer.readLong());
        face = EnumFacing.byIndex(buffer.readInt());
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient();
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
        TileEntity te = mc.world.getTileEntity(this.pos);
        if (te == null)
        {
            te = mc.world.getTileEntity(this.pos.up()); //This block is already removed, try up
        }
        if(te instanceof TileEntityDualVertical)
        {
            EntityShellDestruction sd = new EntityShellDestruction(mc.world, face.getOpposite().getHorizontalAngle(), face.getOpposite().getHorizontalAngle(), 0.0F, 0.0F, 0.0F, ((TileEntityDualVertical)te).locationSkin);
            sd.setLocationAndAngles(this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, 0.0F, 0.0F);
            mc.world.spawnEntity(sd);
        }
    }
}
