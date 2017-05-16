package me.ichun.mods.sync.common.packet;

import net.minecraftforge.fml.relauncher.Side;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import me.ichun.mods.sync.common.Sync;

public class PacketClearShellList extends AbstractPacket
{
    public PacketClearShellList(){}

    @Override
    public void writeTo(ByteBuf buffer)
    {
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        Sync.eventHandlerClient.shells.clear();
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
