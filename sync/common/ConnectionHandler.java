package sync.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import sync.common.core.SessionState;
import sync.common.shell.ShellHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

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
		Sync.proxy.tickHandlerClient.renderCrosshair = true;
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
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try
		{
			stream.writeInt(SessionState.shellConstructionPowerRequirement);
			stream.writeBoolean(SessionState.allowCrossDimensional);
			
			PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)0, bytes.toByteArray()), player);
		}
		catch(IOException e)
		{
		}
		
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
