package sync.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import sync.common.Sync;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;

public class ConnectionHandler implements IConnectionHandler, IPlayerTracker {

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) { //client: remote server
		this.onClientConnection();
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) { //client: local server
		this.onClientConnection();
	}
	
	public void onClientConnection() {
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
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) { } //Client

	//Server
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createConfigDataPacket(), player);
		ShellHandler.updatePlayerOfShells((EntityPlayer) player, null, true);

		//Check if the player was mid death sync
		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
		if (entityPlayerMP.getEntityData().hasKey("isDeathSyncing") && entityPlayerMP.getEntityData().getBoolean("isDeathSyncing")) {
			TileEntityDualVertical tpPosition = EventHandler.getClosestRespawnShell(entityPlayerMP);

			if (tpPosition != null) {
				Packet131MapData zoomPacket = MapPacketHandler.createZoomCameraPacket(
						(int) Math.floor(entityPlayerMP.posX), (int) Math.floor(entityPlayerMP.posY), (int) Math.floor(entityPlayerMP.posZ), entityPlayerMP.dimension, -1, false, true);
				PacketDispatcher.sendPacketToPlayer(zoomPacket, player);

				tpPosition.resyncPlayer = 120;

				MapPacketHandler.createPlayerDeathPacket(entityPlayerMP.getCommandSenderName(), true);
				PacketDispatcher.sendPacketToAllPlayers(MapPacketHandler.createPlayerDeathPacket(entityPlayerMP.getCommandSenderName(), true));

				entityPlayerMP.setHealth(20);

				if (!ShellHandler.syncInProgress.containsKey(entityPlayerMP.getCommandSenderName())) {
					ShellHandler.syncInProgress.put(entityPlayerMP.getCommandSenderName(), tpPosition);
				}
			}
			else {
				entityPlayerMP.setDead();
				entityPlayerMP.setHealth(0);
				entityPlayerMP.getEntityData().setBoolean("isDeathSyncing", false);
				ShellHandler.syncInProgress.remove(entityPlayerMP.getCommandSenderName());
			}
		}
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.onClientConnection();
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) { }

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		//If player was syncing then reset the sync
		if (ShellHandler.syncInProgress.containsKey(player.getCommandSenderName())) {
			TileEntityDualVertical tileEntityDualVertical = ShellHandler.syncInProgress.get(player.getCommandSenderName());
			Sync.logger.fine(String.format("%s logged out mid-sync whilst sync process was at %s", player.getCommandSenderName(), tileEntityDualVertical.resyncPlayer));
			//If they're still syncing away (ie camera zoom out), just reset it all
			if (tileEntityDualVertical.resyncPlayer > 60) {
				tileEntityDualVertical.resyncPlayer = -10;
				//If they're syncing from an existing shell, reset that shell. They should only ever sync from a shell storage but lets check to be safe
				if (tileEntityDualVertical.resyncOrigin != null) {
					tileEntityDualVertical.reset();
					tileEntityDualVertical.getWorldObj().markBlockForUpdate(tileEntityDualVertical.xCoord, tileEntityDualVertical.yCoord, tileEntityDualVertical.zCoord);
					tileEntityDualVertical.getWorldObj().markBlockForUpdate(tileEntityDualVertical.xCoord, tileEntityDualVertical.yCoord + 1, tileEntityDualVertical.zCoord);
				}
			}
			//Remove player from syncing list
			ShellHandler.syncInProgress.remove(player.getCommandSenderName());
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) { }

	@Override
	public void onPlayerRespawn(EntityPlayer player) { }
}
