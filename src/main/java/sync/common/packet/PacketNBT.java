package sync.common.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.network.AbstractPacket;
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
    public void writeTo(ByteBuf buffer, Side side)
    {
        ByteBufUtils.writeTag(buffer, tag);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        tag = ByteBufUtils.readTag(buffer);
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
        mc.thePlayer.readFromNBT(tag);
        if(mc.thePlayer.isEntityAlive())
        {
            mc.thePlayer.deathTime = 0;
        }

        mc.playerController.setGameType(WorldSettings.GameType.getByID(tag.getInteger("sync_playerGameMode")));
    }
}
