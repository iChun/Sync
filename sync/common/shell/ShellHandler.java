package sync.common.shell;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import sync.common.Sync;
import sync.common.core.SessionState;
import sync.common.item.ChunkLoadHandler;
import sync.common.tileentity.TileEntityDualVertical;

public class ShellHandler 
{

	public static void updatePlayerOfShells(EntityPlayer player, TileEntityDualVertical dv, boolean all)
	{
		ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
		if(all)
		{
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(bytes);
			try
			{
				stream.writeByte(0);
				
				PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)5, bytes.toByteArray()), (Player)player);
			}
			catch(IOException e)
			{
			}

			
			for(Entry<TileEntityDualVertical, Ticket> e : ChunkLoadHandler.shellTickets.entrySet())
			{
				if(e.getKey().playerName.equalsIgnoreCase(player.username))
				{
					dvs.add(e.getKey());
				}
			}
		}
		else if(dv != null)
		{
			dvs.add(dv);
		}
		
		for(TileEntityDualVertical dv1 : dvs)
		{
			if(dv1.top)
				continue;
			PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)1, dv1.createShellStateData()), (Player)player);
		}
	}
	
	public static void updatePlayerOfShellRemoval(EntityPlayer player, TileEntityDualVertical dv)
	{
		if(dv.top)
			return;
		PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)2, dv.createShellStateData()), (Player)player);
	}
}
