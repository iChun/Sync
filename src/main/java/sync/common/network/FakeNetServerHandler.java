package sync.common.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet107CreativeSetSlot;
import net.minecraft.network.packet.Packet108EnchantItem;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet14BlockDig;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet203AutoComplete;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.network.packet.Packet205ClientCommand;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet27PlayerInput;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet7UseEntity;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.FakePlayer;

public class FakeNetServerHandler extends NetServerHandler
{
    public FakeNetServerHandler(MinecraftServer mcServer, INetworkManager netMan, FakePlayer player)
    {
    	super(mcServer, netMan, player);
    }

    @Override
    public void networkTick(){}
    @Override
    public void kickPlayerFromServer(String par1Str){}
    @Override
    public void func_110774_a(Packet27PlayerInput par1Packet27PlayerInput){}
    @Override
    public void handleFlying(Packet10Flying par1Packet10Flying){}
    @Override
    public void setPlayerLocation(double par1, double par3, double par5, float par7, float par8){}
    @Override
    public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig){}
    @Override
    public void handlePlace(Packet15Place par1Packet15Place){}
    @Override
    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj){}
    @Override
    public void unexpectedPacket(Packet par1Packet){}
    @Override
    public void sendPacketToPlayer(Packet par1Packet){}
    @Override
    public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch){}
    @Override
    public void handleChat(Packet3Chat par1Packet3Chat){}
    @Override
    public void handleAnimation(Packet18Animation par1Packet18Animation){}
    @Override
    public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction){}
    @Override
    public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect){}
    @Override
    public int packetSize(){ return 0; }
    @Override
    public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity){}
    @Override
    public void handleClientCommand(Packet205ClientCommand par1Packet205ClientCommand){}
    @Override
    public boolean canProcessPacketsAsync(){ return false; }
    @Override
    public void handleRespawn(Packet9Respawn par1Packet9Respawn) {}
    @Override
    public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow){}
    @Override
    public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick){}
    @Override
    public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem){}
    @Override
    public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot){}
    @Override
    public void handleTransaction(Packet106Transaction par1Packet106Transaction){}
    @Override
    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign){}
    @Override
    public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive){}
    @Override
    public boolean isServerHandler(){ return true; }
    @Override
    public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities){}
    @Override
    public void handleAutoComplete(Packet203AutoComplete par1Packet203AutoComplete){}
    @Override
    public void handleClientInfo(Packet204ClientInfo par1Packet204ClientInfo){}
    @Override
    public void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload){}
    @Override
    public void handleVanilla250Packet(Packet250CustomPayload par1Packet250CustomPayload){}
    @Override
    public void handleMapData(Packet131MapData par1Packet131MapData){}
}
