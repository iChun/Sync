package sync.common.packet;

import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import sync.common.Sync;
import sync.common.core.SessionState;

public class PacketSession extends AbstractPacket
{
    public PacketSession(){}

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(SessionState.shellConstructionPowerRequirement);
        buffer.writeInt(SessionState.allowCrossDimensional);
        buffer.writeInt(SessionState.deathMode);
        buffer.writeBoolean(SessionState.hardMode);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        SessionState.shellConstructionPowerRequirement = buffer.readInt();
        SessionState.allowCrossDimensional = buffer.readInt();
        SessionState.deathMode = buffer.readInt();
        SessionState.hardMode = buffer.readBoolean();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Sync.mapHardmodeRecipe();
    }
}
