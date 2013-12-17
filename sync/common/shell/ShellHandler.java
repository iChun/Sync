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
import sync.common.core.ChunkLoadHandler;
import sync.common.core.SessionState;
import sync.common.tileentity.TileEntityDualVertical;

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
				
				PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)5, bytes.toByteArray()), (Player)player);
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
			dvs.add(dv);
		}
		
		for(TileEntityDualVertical dv1 : dvs)
		{
			if(dv1.top)
				continue;
			PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)1, dv1.createShellStateData()), (Player)player);
		}
		
		for(TileEntityDualVertical dv1 : remove)
		{
			ChunkLoadHandler.removeShellAsChunkloader(dv1);
		}
	}
	
	public static void updatePlayerOfShellRemoval(EntityPlayer player, TileEntityDualVertical dv)
	{
		if(dv.top)
			return;
		PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)2, dv.createShellStateData()), (Player)player);
	}
	
	public static ArrayList<String> deathRespawns = new ArrayList<String>();
}
