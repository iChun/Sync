package me.ichun.mods.sync.common.shell;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import ichun.common.core.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Level;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.core.ChunkLoadHandler;
import me.ichun.mods.sync.common.packet.PacketClearShellList;
import me.ichun.mods.sync.common.packet.PacketShellState;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;

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
			dualVertical.getWorld().markBlockForUpdate(dualVertical.xCoord, dualVertical.yCoord, dualVertical.zCoord);
			dualVertical.getWorld().markBlockForUpdate(dualVertical.xCoord, dualVertical.yCoord + 1, dualVertical.zCoord);
		}
		else Sync.LOGGER.log(Level.WARN, String.format("Attempted to remove a shell but something was null for %s at %s", playerName, dualVertical));
	}

	public static boolean isShellAlreadyRegistered(TileEntityDualVertical dualVertical) {
		return playerShells.containsValue(dualVertical);
	}

	public static void updatePlayerOfShells(EntityPlayer player, TileEntityDualVertical dv, boolean all) {
		ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
		ArrayList<TileEntityDualVertical> remove = new ArrayList<TileEntityDualVertical>();
		
		if (all) {
			//Tell player client to clear current list
            Sync.channel.sendTo(new PacketClearShellList(), player);

			for (Map.Entry<String, TileEntityDualVertical> e : playerShells.entries()) {
				if (e.getKey().equalsIgnoreCase(player.getName())) {
					TileEntityDualVertical dualVertical = e.getValue();
					if (dualVertical.getWorld().getTileEntity(dualVertical.xCoord, dualVertical.yCoord, dualVertical.zCoord) == dualVertical) {
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
            Sync.channel.sendTo(new PacketShellState(dv1, false), player);
		}
		
		for (TileEntityDualVertical dv1 : remove) {
			removeShell(dv1.getPlayerName(), dv1);
		}
	}
	
	public static void updatePlayerOfShellRemoval(EntityPlayer player, TileEntityDualVertical dv) {
		if (dv.top) return;
        Sync.channel.sendTo(new PacketShellState(dv, true), player);
	}
}
