package sync.common.shell;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import sync.common.Sync;
import sync.common.core.ChunkLoadHandler;
import sync.common.core.MapPacketHandler;
import sync.common.tileentity.TileEntityDualVertical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShellHandler {

	public static SetMultimap<String, TileEntityDualVertical> playerShells = HashMultimap.create();
	public static HashMap<String, TileEntityDualVertical> syncInProgress = new HashMap<String, TileEntityDualVertical>();

	public static void addShell(String playerName, TileEntityDualVertical dualVertical, boolean shouldChunkLoad) {
		if (!playerShells.containsEntry(playerName, dualVertical)) {
			playerShells.put(playerName, dualVertical);
			if (shouldChunkLoad && !ChunkLoadHandler.isAlreadyChunkLoaded(dualVertical)) ChunkLoadHandler.addShellAsChunkloader(dualVertical);
		}
	}

	public static void removeShell(String playerName, TileEntityDualVertical dualVertical) {
		if (playerName != null && dualVertical != null) {
			playerShells.remove(playerName, dualVertical);
			ChunkLoadHandler.removeShellAsChunkloader(dualVertical);
			dualVertical.reset();
			dualVertical.getWorldObj().markBlockForUpdate(dualVertical.xCoord, dualVertical.yCoord, dualVertical.zCoord);
			dualVertical.getWorldObj().markBlockForUpdate(dualVertical.xCoord, dualVertical.yCoord + 1, dualVertical.zCoord);
		}
		else Sync.logger.warning(String.format("Attempted to remove a shell but something was null for %s at %s", playerName, dualVertical));
	}

	public static boolean isShellAlreadyRegistered(TileEntityDualVertical dualVertical) {
		return playerShells.containsValue(dualVertical);
	}

	public static void updatePlayerOfShells(EntityPlayer player, TileEntityDualVertical dv, boolean all) {
		ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
		ArrayList<TileEntityDualVertical> remove = new ArrayList<TileEntityDualVertical>();
		
		if (all) {
			//Tell player client to clear current list
			PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createClearShellListPacket((byte) 0), (Player)player);

			for (Map.Entry<String, TileEntityDualVertical> e : playerShells.entries()) {
				if (e.getKey().equalsIgnoreCase(player.getCommandSenderName())) {
					TileEntityDualVertical dualVertical = e.getValue();
					if (dualVertical.getWorldObj().getBlockTileEntity(dualVertical.xCoord, dualVertical.yCoord, dualVertical.zCoord) == dualVertical) {
						dvs.add(dualVertical);
					}
					else {
						remove.add(dualVertical);
					}
				}
			}
		}
		else if (dv != null) {
			//This is never used due to issues synching to the point I gave up.
			dvs.add(dv);
		}
		
		for (TileEntityDualVertical dv1 : dvs) {
			if (dv1.top) continue;
			PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createShellStatePacket(dv1), (Player)player);
		}
		
		for (TileEntityDualVertical dv1 : remove) {
			removeShell(dv1.getPlayerName(), dv1);
		}
	}
	
	public static void updatePlayerOfShellRemoval(EntityPlayer player, TileEntityDualVertical dv) {
		if (dv.top) return;
		PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createRemoveShellDataPacket(dv), (Player)player);
	}
}
