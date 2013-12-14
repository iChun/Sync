package sync.common.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import sync.common.Sync;
import sync.common.shell.ShellState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import cpw.mods.fml.common.network.ITinyPacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MapPacketHandler
	implements ITinyPacketHandler
{

	@Override
	public void handle(NetHandler handler, Packet131MapData mapData) 
	{
		int id = mapData.uniqueID;
		if(handler instanceof NetServerHandler)
		{
			handleServerPacket((NetServerHandler)handler, mapData.uniqueID, mapData.itemData, (EntityPlayerMP)handler.getPlayer());
		}
		else
		{
			handleClientPacket((NetClientHandler)handler, mapData.uniqueID, mapData.itemData);
		}
	}

	private void handleServerPacket(NetServerHandler handler, short id, byte[] data, EntityPlayerMP player) 
	{
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data));
		try
		{
			switch(id)
			{
				case 0:
				{
					stream.readByte();
					break;
				}
			}
		}
		catch(IOException e)
		{
		}
	}
	
	//TODO Side Split
	
	@SideOnly(Side.CLIENT)
	private void handleClientPacket(NetClientHandler handler, short id, byte[] data) 
	{
		Minecraft mc = Minecraft.getMinecraft();
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data));
		try
		{
			switch(id)
			{
				case 0:
				{
					SessionState.shellConstructionPowerRequirement = stream.readInt();
					break;
				}
				case 1:
				{
					//Create shell state
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();
					int dim = stream.readInt();
					
					ShellState state = new ShellState(x, y, z, dim);

					state.buildProgress = stream.readFloat();
					state.powerReceived = stream.readFloat();
					
					boolean add = true;
					for(int i = Sync.proxy.tickHandlerClient.shells.size() - 1; i >= 0; i--)
					{
						ShellState state1 = Sync.proxy.tickHandlerClient.shells.get(i);
						if(state1.matches(state))
						{
							Sync.proxy.tickHandlerClient.shells.remove(i);
						}
						if(!Sync.proxy.tickHandlerClient.shells.contains(state))
						{
							Sync.proxy.tickHandlerClient.shells.add(i, state);
						}
						add = false;
					}
					
					if(add)
					{
						Sync.proxy.tickHandlerClient.shells.add(state);
					}

					System.out.println("add!!");
					
					break;
				}
				case 2:
				{
					//Remove shell state
					
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();
					int dim = stream.readInt();
					
					ShellState state = new ShellState(x, y, z, dim);

					for(int i = Sync.proxy.tickHandlerClient.shells.size() - 1; i >= 0; i--)
					{
						ShellState state1 = Sync.proxy.tickHandlerClient.shells.get(i);
						if(state1.matches(state))
						{
							Sync.proxy.tickHandlerClient.shells.remove(i);
						}
					}
					
					System.out.println("removal!!");
					
					break;
				}
			}
		}
		catch(IOException e)
		{
		}
	}
	
}
