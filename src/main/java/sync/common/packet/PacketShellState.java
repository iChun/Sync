package sync.common.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sync.common.Sync;
import sync.common.shell.ShellState;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellStorage;

public class PacketShellState extends AbstractPacket
{
    public boolean remove;
    public TileEntityDualVertical dv;

    public int x;
    public int y;
    public int z;
    public int dim;

    public float buildProgress;
    public float powerReceived;

    public String name;
    public String dimName;
    public boolean isConstructor;
    public boolean isHome;

    public NBTTagCompound tag;

    public PacketShellState(){}

    public PacketShellState(TileEntityDualVertical dv1, boolean remove)
    {
        this.remove = remove;
        this.dv = dv1;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeBoolean(remove);
        dv.writeShellStateData(buffer);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        remove = buffer.readBoolean();

        //Create shell state
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        dim = buffer.readInt();

        buildProgress = buffer.readFloat();
        powerReceived = buffer.readFloat();

        name = ByteBufUtils.readUTF8String(buffer);

        dimName = ByteBufUtils.readUTF8String(buffer);

        isConstructor = buffer.readBoolean();

        isHome = buffer.readBoolean();

        if(!isConstructor)
        {
            tag = ByteBufUtils.readTag(buffer);
        }
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient();
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        //Create shell state
        Minecraft mc = Minecraft.getMinecraft();

        ShellState state = new ShellState(x, y, z, dim);

        if(remove)
        {
            //Remove shell state
            for(int i = Sync.proxy.tickHandlerClient.shells.size() - 1; i >= 0; i--)
            {
                ShellState state1 = Sync.proxy.tickHandlerClient.shells.get(i);
                if(state1.matches(state))
                {
                    Sync.proxy.tickHandlerClient.shells.remove(i);
                }
            }
        }
        else
        {
            state.buildProgress = buildProgress;
            state.powerReceived = powerReceived;

            state.name = name;

            state.dimName = dimName;

            state.isConstructor = isConstructor;

            state.isHome = isHome;

            boolean add = true;
            for(int i = Sync.proxy.tickHandlerClient.shells.size() - 1; i >= 0; i--)
            {
                ShellState state1 = Sync.proxy.tickHandlerClient.shells.get(i);
                if(state1.matches(state))
                {
                    Sync.proxy.tickHandlerClient.shells.remove(i);
                }
                if(!Sync.proxy.tickHandlerClient.shells.contains(state))
                {
                    Sync.proxy.tickHandlerClient.shells.add(i, state);
                }
                add = false;
            }

            if(add)
            {
                Sync.proxy.tickHandlerClient.shells.add(state);
            }

            state.playerState = TileEntityShellStorage.createPlayer(mc.theWorld, mc.thePlayer.getCommandSenderName());

            if(!state.isConstructor)
            {
                NBTTagCompound tag = this.tag;
                if(tag.hasKey("Inventory"))
                {
                    TileEntityDualVertical.addShowableEquipToPlayer(state.playerState, tag);
                }
            }
        }
    }
}
