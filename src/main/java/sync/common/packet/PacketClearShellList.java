package sync.common.packet;

import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import sync.common.Sync;

public class PacketClearShellList extends AbstractPacket
{
    public PacketClearShellList(){}

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Sync.proxy.tickHandlerClient.shells.clear();
    }
}
