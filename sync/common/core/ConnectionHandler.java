package sync.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import sync.common.Sync;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;

public class ConnectionHandler implements IConnectionHandler, IPlayerTracker
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

		//Check if the player was mid death sync
		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
		if (entityPlayerMP.getEntityData().hasKey("isDeathSyncing") && entityPlayerMP.getEntityData().getBoolean("isDeathSyncing")) {
			TileEntityDualVertical tpPosition = EventHandler.getClosestRespawnShell(entityPlayerMP);

			if (tpPosition != null) {
				Packet131MapData zoomPacket = MapPacketHandler.createZoomCameraPacket(
						(int) Math.floor(entityPlayerMP.posX), (int) Math.floor(entityPlayerMP.posY), (int) Math.floor(entityPlayerMP.posZ), entityPlayerMP.dimension, -1, false, true);
				PacketDispatcher.sendPacketToPlayer(zoomPacket, player);

				tpPosition.resyncPlayer = 120;
				EntityPlayer dvInstance = null;

				if (tpPosition instanceof TileEntityShellStorage) {
					dvInstance = ((TileEntityShellStorage)tpPosition).playerInstance;
				}
				else if (tpPosition instanceof TileEntityShellConstructor) {
					dvInstance = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), tpPosition.worldObj, entityPlayerMP.getCommandSenderName(), new ItemInWorldManager(tpPosition.worldObj));
					((EntityPlayerMP)dvInstance).playerNetServerHandler = ((EntityPlayerMP)player).playerNetServerHandler;
				}

				if (dvInstance != null) {
					NBTTagCompound tag = new NBTTagCompound();

					if(tpPosition.playerNBT != null && tpPosition.playerNBT.hasKey("Inventory")) {
						dvInstance.readFromNBT(tpPosition.playerNBT);
					}

					dvInstance.setLocationAndAngles(tpPosition.xCoord + 0.5D, tpPosition.yCoord, tpPosition.zCoord + 0.5D, (tpPosition.face - 2) * 90F, 0F);

					boolean keepInv = entityPlayerMP.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");

					tpPosition.worldObj.getGameRules().setOrCreateGameRule("keepInventory", "false");

					dvInstance.clonePlayer(entityPlayerMP, false);
					dvInstance.entityId = entityPlayerMP.entityId;

					tpPosition.worldObj.getGameRules().setOrCreateGameRule("keepInventory", keepInv ? "true" : "false");

					dvInstance.writeToNBT(tag);
					tag.setInteger("sync_playerGameMode", tpPosition.playerNBT.getInteger("sync_playerGameMode"));
					tpPosition.playerNBT = tag;
				}

				MapPacketHandler.createPlayerDeathPacket(entityPlayerMP.username, true);
				PacketDispatcher.sendPacketToAllPlayers(MapPacketHandler.createPlayerDeathPacket(entityPlayerMP.username, true));

				entityPlayerMP.setHealth(1);

				if (!ShellHandler.syncInProgress.containsKey(entityPlayerMP.username)) {
					ShellHandler.syncInProgress.put(entityPlayerMP.username, tpPosition);
				}
			}
			else{
				entityPlayerMP.setDead();
				entityPlayerMP.setHealth(0);
			}
		}
	}

	@Override
	public void connectionClosed(INetworkManager manager) //both 
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			onClientConnection();
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) { }

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		//If player was syncing then reset the sync
		if (ShellHandler.syncInProgress.containsKey(player.username)) {
			TileEntityDualVertical tileEntityDualVertical = ShellHandler.syncInProgress.get(player.username);
			if (tileEntityDualVertical.resyncPlayer > 60) {
				tileEntityDualVertical.resyncPlayer = -10;
				//If they're syncing from an existing shell, reset that shell. They should only ever sync from a shell storage but lets check to be safe
				//TODO This won't work if server has been restarted as the syncInProgress list would have been reset. Handle with NBT Tag like isDeathSyncing?
				if (tileEntityDualVertical.resyncOrigin != null && tileEntityDualVertical.resyncOrigin instanceof TileEntityShellStorage) {
					TileEntityShellStorage ss = (TileEntityShellStorage)tileEntityDualVertical.resyncOrigin;

					ss.playerName = "";
					ss.occupied = false;
					ss.occupationTime = 0;
					ss.syncing = false;
					ss.playerNBT = new NBTTagCompound();
					ss.worldObj.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
					ss.worldObj.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
				}
			}
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) { }

	@Override
	public void onPlayerRespawn(EntityPlayer player) { }
}
