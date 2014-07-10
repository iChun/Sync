package sync.common.core;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import sync.common.Sync;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ChunkLoadHandler implements LoadingCallback {

	public static final HashMap<TileEntityDualVertical, Ticket> shellTickets = new HashMap<TileEntityDualVertical, Ticket>();
	
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) 
	{
		for(Ticket ticket : tickets) {
			if (ticket != null) {
				TileEntity te = world.getTileEntity(ticket.getModData().getInteger("shellX"), ticket.getModData().getInteger("shellY"), ticket.getModData().getInteger("shellZ"));
				if(te instanceof TileEntityDualVertical) {
					TileEntityDualVertical dv = (TileEntityDualVertical) te;

					//Check we haven't already loaded this ticket or there are dupes
					Ticket ticket1 = shellTickets.get(dv);
					if (ticket1 != null) {
						ForgeChunkManager.releaseTicket(ticket1);
					}

					shellTickets.put(dv, ticket);
					ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(dv.xCoord >> 4, dv.zCoord >> 4));
				}
				else
				{
					ForgeChunkManager.releaseTicket(ticket);
				}
			}
		}
	}

	public static void removeShellAsChunkloader(TileEntityDualVertical dv)
	{
		Ticket ticket = shellTickets.get(dv);
		if(ticket != null)
		{
			ForgeChunkManager.releaseTicket(ticket);
		}
		shellTickets.remove(dv);
		
		EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dv.getPlayerName());
		
		if(player != null)
		{
			ShellHandler.updatePlayerOfShellRemoval(player, dv);
		}
	}
	
	public static void addShellAsChunkloader(TileEntityDualVertical dv) {
		if (dv != null) {
			ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(dv.xCoord >> 4, dv.zCoord >> 4);
			if (!isAlreadyChunkLoaded(chunkCoordIntPair, dv.getWorldObj().provider.dimensionId)) {
				Ticket ticket = shellTickets.get(dv);
				if (ticket == null) {
					ticket = ForgeChunkManager.requestTicket(Sync.instance, dv.getWorldObj(), ForgeChunkManager.Type.NORMAL);
				}
				if (ticket != null) {
					ticket.getModData().setInteger("shellX", dv.xCoord);
					ticket.getModData().setInteger("shellY", dv.yCoord);
					ticket.getModData().setInteger("shellZ", dv.zCoord);
					ForgeChunkManager.forceChunk(ticket, chunkCoordIntPair);

					if (Sync.config.getInt("allowChunkLoading") == 0) {
						//Reflecting into Ticket to remove chunk! Sorry! :(
						try {
							LinkedHashSet<ChunkCoordIntPair> requestedChunks = ObfuscationReflectionHelper.getPrivateValue(Ticket.class, ticket, "requestedChunks");
							requestedChunks.clear();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				shellTickets.put(dv, ticket);

				EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dv.getPlayerName());
				if (player != null) {
					ShellHandler.updatePlayerOfShells(player, null, true);
				}
			}
		}
	}

	public static boolean isAlreadyChunkLoaded(TileEntityDualVertical dualVertical) {
		ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(dualVertical.xCoord >> 4, dualVertical.zCoord >> 4);
		return shellTickets.containsKey(dualVertical) || isAlreadyChunkLoaded(chunkCoordIntPair, dualVertical.getWorldObj().provider.dimensionId);
	}

	public static boolean isAlreadyChunkLoaded(ChunkCoordIntPair chunkCoordIntPair, int dimID) {
		for (Map.Entry<TileEntityDualVertical, Ticket> set : shellTickets.entrySet()) {
			if (set != null && set.getValue() != null) {
				ImmutableSet loadedChunks = set.getValue().getChunkList();
				if (loadedChunks != null && set.getValue().world.provider.dimensionId == dimID) {
					for (Object obj : loadedChunks) {
						ChunkCoordIntPair theChunks = (ChunkCoordIntPair) obj;
						//Will only return true if the exact same chunks are loaded but seeing as we are only loading one chunk, that's fine
						if (theChunks.equals(chunkCoordIntPair)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
