package sync.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import sync.common.Sync;
import sync.common.shell.ShellHandler;

public class ConnectionHandler 
	implements IConnectionHandler 
{

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) //client: remove server 
	{
		onClientConnection();
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) //client: local server
	{
		onClientConnection();
	}
	
	public void onClientConnection()
	{
		Sync.proxy.tickHandlerClient.radialShow = false;
		Sync.proxy.tickHandlerClient.zoom = false;
		Sync.proxy.tickHandlerClient.lockTime = 0;
		Sync.proxy.tickHandlerClient.zoomTimer = -10;
		Sync.proxy.tickHandlerClient.zoomTimeout = 0;
		Sync.proxy.tickHandlerClient.shells.clear();
		Sync.proxy.tickHandlerClient.refusePlayerRender.clear();
		Sync.proxy.tickHandlerClient.lockedStorage = null;
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) 
	{
		return null;
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)  //client
	{
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) //server
	{
		PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createConfigDataPacket(), player);
		ShellHandler.updatePlayerOfShells((EntityPlayer)player, null, true);
	}

	@Override
	public void connectionClosed(INetworkManager manager) //both 
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			onClientConnection();
		}
	}

}
