package sync.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ITinyPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import sync.api.SyncStartEvent;
import sync.client.entity.EntityShellDestruction;
import sync.common.Sync;
import sync.common.shell.ShellHandler;
import sync.common.shell.ShellState;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;

import java.io.*;

public class MapPacketHandler
{
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
					break;
				}
				case 3:
				{
					//Player locks into SyncStorage
					
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();

					TileEntity te = mc.theWorld.getTileEntity(x, y, z);
					
					if(te instanceof TileEntityShellStorage)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)te;
						
						mc.thePlayer.setLocationAndAngles(ss.xCoord + 0.5D, ss.yCoord, ss.zCoord + 0.5D, (ss.face - 2) * 90F, 0F);
						
						Sync.proxy.tickHandlerClient.lockedStorage = ss;
						Sync.proxy.tickHandlerClient.lockTime = 5;
						
						Sync.proxy.tickHandlerClient.radialShow = true;
						Sync.proxy.tickHandlerClient.radialTime = 3;
						
						Sync.proxy.tickHandlerClient.radialPlayerYaw = mc.renderViewEntity.rotationYaw;
						Sync.proxy.tickHandlerClient.radialPlayerPitch = mc.renderViewEntity.rotationPitch;
						
						Sync.proxy.tickHandlerClient.radialDeltaX = Sync.proxy.tickHandlerClient.radialDeltaY = 0;
					}
					
					break;
				}
				case 4:
				{
					break;
				}
				case 5:
				{
					Sync.proxy.tickHandlerClient.shells.clear();
					break;
				}
				case 6:
				{
					//Update player NBT
					NBTTagCompound tag = Sync.readNBTTagCompound(stream);
					mc.thePlayer.readFromNBT(tag);
					if(mc.thePlayer.isEntityAlive())
					{
						mc.thePlayer.deathTime = 0;
					}
					
					mc.playerController.setGameType(WorldSettings.GameType.getByID(tag.getInteger("sync_playerGameMode")));
					break;
				}
				case 7:
				{
					break;
				}
				case 8:
				{
					//destruction of a completed shell.
					
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();
					
					int face = stream.readInt();
					
					
					if(mc.theWorld.blockExists(x, y, z))
					{
						TileEntity te = mc.theWorld.getTileEntity(x, y, z);
						if(te instanceof TileEntityDualVertical)
						{
							EntityShellDestruction sd = new EntityShellDestruction(mc.theWorld, (face - 2) * 90F, (face - 2) * 90F, 0.0F, 0.0F, 0.0F, ((TileEntityDualVertical)te).locationSkin);
							sd.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
							mc.theWorld.spawnEntityInWorld(sd);
						}
					}
					break;
				}
				case 9:
				{
					//Sync config from server
					SessionState.shellConstructionPowerRequirement = stream.readInt();
					SessionState.allowCrossDimensional = stream.readInt();
					SessionState.deathMode = stream.readInt();
					SessionState.hardMode = stream.readBoolean();

					Sync.mapHardmodeRecipe();

					break;
				}
				case 10:
				{
					//Create shell state
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();
					int dim = stream.readInt();

					ShellState state = new ShellState(x, y, z, dim);

					state.buildProgress = stream.readFloat();
					state.powerReceived = stream.readFloat();

					state.name = stream.readUTF();

					state.dimName = stream.readUTF();

					state.isConstructor = stream.readBoolean();

					state.isHome = stream.readBoolean();

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

					state.playerState = TileEntityShellStorage.createPlayer(mc.theWorld, mc.thePlayer.getCommandSenderName());

					if(!state.isConstructor)
					{
						NBTTagCompound tag = Sync.readNBTTagCompound(stream);
						if(tag.hasKey("Inventory"))
						{
							TileEntityDualVertical.addShowableEquipToPlayer(state.playerState, tag);
						}
					}

					break;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	//Packet ID 2
	//Sent from server to client TODO: Merge this and the createShellData packet
	public static Packet131MapData createRemoveShellDataPacket(TileEntityDualVertical dv) {
		return new Packet131MapData((short)Sync.getNetId(), (short)2, dv.createShellStateData());
	}

	//Packet ID 3
	//Sent from server to client
	public static Packet131MapData createPlayerEnterStoragePacket(int posX, int posY, int posZ) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try {
			stream.writeInt(posX);
			stream.writeInt(posY);
			stream.writeInt(posZ);
		} catch(IOException e) {
			e.printStackTrace();
		}

		return new Packet131MapData((short)Sync.getNetId(), (short)3, bytes.toByteArray());
	}

	//Packet ID 5
	//Sent from server to client
	public static Packet131MapData createClearShellListPacket(byte notUsed) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);

		try {
			stream.writeByte(notUsed);
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		return new Packet131MapData((short)Sync.getNetId(), (short)5, bytes.toByteArray());
	}

	//Packet ID 6
	//Sent from server to client
	public static Packet131MapData createNBTPacket(NBTTagCompound tagCompound) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);

		try {
			Sync.writeNBTTagCompound(tagCompound, stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new Packet131MapData((short)Sync.getNetId(), (short)6, bytes.toByteArray());
	}

	//Packet ID 8
	//Sent from server to client
	public static Packet131MapData createShellDeathPacket(int xCoord, int yCoord, int zCoord, int face) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);

		try {
			stream.writeInt(xCoord);
			stream.writeInt(yCoord);
			stream.writeInt(zCoord);
			stream.writeInt(face);
		} catch(IOException e) {
			e.printStackTrace();
		}

		return new Packet131MapData((short)Sync.getNetId(), (short)8, bytes.toByteArray());
	}

	//Packet ID 9
	//Sent from server to client
	public static Packet131MapData createConfigDataPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);

		try {
			stream.writeInt(SessionState.shellConstructionPowerRequirement);
			stream.writeInt(SessionState.allowCrossDimensional);
			stream.writeInt(SessionState.deathMode);
			stream.writeBoolean(SessionState.hardMode);
		} catch(IOException e) {
			e.printStackTrace();
		}

		return new Packet131MapData((short)Sync.getNetId(), (short)9, bytes.toByteArray());
	}

	//Packet ID 10
	//Sent from server to client
	public static Packet131MapData createShellStatePacket(TileEntityDualVertical dv) {
		return new Packet131MapData((short)Sync.getNetId(), (short)10, dv.createShellStateData());
	}
}
