package sync.common.core;

import java.util.HashMap;
import java.util.List;

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
import cpw.mods.fml.common.FMLCommonHandler;

public class ChunkLoadHandler implements LoadingCallback {

	public static HashMap<TileEntityDualVertical, Ticket> shellTickets = new HashMap<TileEntityDualVertical, Ticket>();
	
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) 
	{
		for(Ticket ticket : tickets)
		{
			TileEntity te = world.getBlockTileEntity(ticket.getModData().getInteger("shellX"), ticket.getModData().getInteger("shellY"), ticket.getModData().getInteger("shellZ"));
			if(te instanceof TileEntityDualVertical)
			{
				TileEntityDualVertical dv = (TileEntityDualVertical)te;
				Ticket ticket1 = shellTickets.get(dv);
				if(ticket1 != null)
				{
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

	public static void removeShellAsChunkloader(TileEntityDualVertical dv)
	{
		Ticket ticket = shellTickets.get(dv);
		if(ticket != null)
		{
			ForgeChunkManager.releaseTicket(ticket);
		}
		shellTickets.remove(dv);
		
		EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dv.playerName);
		
		if(player != null)
		{
			ShellHandler.updatePlayerOfShellRemoval(player, dv);
		}
	}
	
	public static void addShellAsChunkloader(TileEntityDualVertical dv)
	{
		if(dv != null)
		{
			Ticket ticket = shellTickets.get(dv);
			if(ticket == null)
			{
				ticket = ForgeChunkManager.requestTicket(Sync.instance, dv.worldObj, ForgeChunkManager.Type.NORMAL);
				if(ticket != null)
				{
					shellTickets.put(dv, ticket);
				}
			}
			if(ticket != null)
			{
				ticket.getModData().setInteger("shellX", dv.xCoord);
				ticket.getModData().setInteger("shellY", dv.yCoord);
				ticket.getModData().setInteger("shellZ", dv.zCoord);
				ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(dv.xCoord >> 4, dv.zCoord >> 4));
			}
			
			EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dv.playerName);
			
			if(player != null)
			{
				ShellHandler.updatePlayerOfShells(player, null, true);
			}
		}
	}
}
