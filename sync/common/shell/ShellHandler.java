package sync.common.shell;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import sync.common.core.ChunkLoadHandler;
import sync.common.core.MapPacketHandler;
import sync.common.tileentity.TileEntityDualVertical;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class ShellHandler 
{

	public static void updatePlayerOfShells(EntityPlayer player, TileEntityDualVertical dv, boolean all)
	{
		ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
		
		ArrayList<TileEntityDualVertical> remove = new ArrayList<TileEntityDualVertical>();
		
		if(all)
		{
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(bytes);
			try
			{
				stream.writeByte(0);
				
				PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createClearShellListPacket((byte) 0), (Player)player);
			}
			catch(IOException e)
			{
			}

			
			for(Entry<TileEntityDualVertical, Ticket> e : ChunkLoadHandler.shellTickets.entrySet())
			{
				if(e.getKey().playerName.equalsIgnoreCase(player.username))
				{
					TileEntityDualVertical dv1 = e.getKey();
					if(dv1.worldObj.getBlockTileEntity(dv1.xCoord, dv1.yCoord, dv1.zCoord) == dv1)
					{
						dvs.add(dv1);
					}
					else
					{
						remove.add(dv1);
					}
				}
			}
		}
		else if(dv != null)
		{
			//This is never used due to issues synching to the point I gave up.
			dvs.add(dv);
		}
		
		for(TileEntityDualVertical dv1 : dvs)
		{
			if(dv1.top)
				continue;
			PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createShellStatePacket(dv1), (Player)player);
		}
		
		for(TileEntityDualVertical dv1 : remove)
		{
			ChunkLoadHandler.removeShellAsChunkloader(dv1);
		}
	}
	
	public static void updatePlayerOfShellRemoval(EntityPlayer player, TileEntityDualVertical dv)
	{
		if (dv.top) return;
		PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createRemoveShellDataPacket(dv), (Player)player);
	}
	
	public static HashMap<String, TileEntityDualVertical> syncInProgress = new HashMap<String, TileEntityDualVertical>();
}
