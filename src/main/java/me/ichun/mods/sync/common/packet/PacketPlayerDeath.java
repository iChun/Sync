package me.ichun.mods.sync.common.packet;

import me.ichun.mods.sync.client.entity.EntityShellDestruction;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import me.ichun.mods.sync.client.entity.EntityShellDestruction;
import me.ichun.mods.sync.common.Sync;

public class PacketPlayerDeath extends AbstractPacket
{
    public String playerName;
    public boolean doDeathAnim;

    public PacketPlayerDeath(){}

    public PacketPlayerDeath(String playerName, boolean doDeathAnimation)
    {
        this.playerName = playerName;
        this.doDeathAnim = doDeathAnimation;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, playerName);
        buffer.writeBoolean(doDeathAnim);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        playerName = ByteBufUtils.readUTF8String(buffer);
        doDeathAnim = buffer.readBoolean();
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
        //Player death animation
        Sync.eventHandlerClient.refusePlayerRender.put(playerName, 120);
        if(doDeathAnim)
        {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);

            if(player != null)
            {
                player.deathTime = 0;
                player.setHealth(1);

                EntityShellDestruction sd = new EntityShellDestruction(player.worldObj, player.rotationYaw, player.renderYawOffset, player.rotationPitch, player.limbSwing, player.limbSwingAmount, ((AbstractClientPlayer)player).getLocationSkin());
                sd.setLocationAndAngles(player.posX, player.posY, player.posZ, 0.0F, 0.0F);
                player.worldObj.spawnEntityInWorld(sd);

            }
        }
    }
}
