package sync.common.packet;

import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import sync.common.Sync;

public class PacketSession extends AbstractPacket
{
    public PacketSession(){}

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(Sync.config.getSessionInt("shellConstructionPowerRequirement"));
        buffer.writeInt(Sync.config.getSessionInt("allowCrossDimensional"));
        buffer.writeInt(Sync.config.getSessionInt("overrideDeathIfThereAreAvailableShells"));
        buffer.writeInt(Sync.config.getSessionInt("hardMode"));
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        Sync.config.updateSession("shellConstructionPowerRequirement", buffer.readInt());
        Sync.config.updateSession("allowCrossDimensional", buffer.readInt());
        Sync.config.updateSession("overrideDeathIfThereAreAvailableShells", buffer.readInt());
        Sync.config.updateSession("hardMode", buffer.readInt());
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Sync.mapHardmodeRecipe();
    }
}
