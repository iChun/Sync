package sync.common.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import sync.common.shell.ShellHandler;

public class PacketUpdatePlayerOnZoomFinish extends AbstractPacket
{
    public double posX;
    public double posY;
    public double posZ;
    public float rotationYaw;
    public float rotationPitch;

    public PacketUpdatePlayerOnZoomFinish(){}

    public PacketUpdatePlayerOnZoomFinish(double posX, double posY, double posZ, float rotationYaw, float rotationPitch)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeDouble(posX);
        buffer.writeDouble(posY);
        buffer.writeDouble(posZ);
        buffer.writeFloat(rotationYaw);
        buffer.writeFloat(rotationPitch);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        posX = buffer.readDouble();
        posY = buffer.readDouble();
        posZ = buffer.readDouble();
        rotationYaw = buffer.readFloat();
        rotationPitch = buffer.readFloat();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        player.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);

        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().syncPlayerInventory((EntityPlayerMP)player);

        ShellHandler.updatePlayerOfShells(player, null, true);

    }
}
