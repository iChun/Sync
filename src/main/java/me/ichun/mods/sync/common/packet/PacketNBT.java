package me.ichun.mods.sync.common.packet;

import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSettings;

public class PacketNBT extends AbstractPacket
{
    public NBTTagCompound tag;

    public PacketNBT(){}

    public PacketNBT(NBTTagCompound tag)
    {
        this.tag = tag;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        ByteBufUtils.writeTag(buffer, tag);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        tag = ByteBufUtils.readTag(buffer);
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
        mc.player.readFromNBT(tag);
        if(mc.player.isEntityAlive())
        {
            mc.player.deathTime = 0;
        }

        mc.playerController.setGameType(GameType.getByID(tag.getInteger("sync_playerGameMode")));
    }
}
