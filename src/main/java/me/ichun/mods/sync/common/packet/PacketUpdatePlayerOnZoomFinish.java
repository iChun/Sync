package me.ichun.mods.sync.common.packet;

import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import me.ichun.mods.sync.common.shell.ShellHandler;

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
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeDouble(posX);
        buffer.writeDouble(posY);
        buffer.writeDouble(posZ);
        buffer.writeFloat(rotationYaw);
        buffer.writeFloat(rotationPitch);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        posX = buffer.readDouble();
        posY = buffer.readDouble();
        posZ = buffer.readDouble();
        rotationYaw = buffer.readFloat();
        rotationPitch = buffer.readFloat();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        player.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);

        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().syncPlayerInventory((EntityPlayerMP)player);

        ShellHandler.updatePlayerOfShells(player, null, true);

        return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.SERVER;
    }
}
