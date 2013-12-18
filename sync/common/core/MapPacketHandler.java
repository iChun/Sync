package sync.common.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.GuiIngameForge;
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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ITinyPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
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
					//Receive sync request from client;
					
					boolean valid = false;
					
					int oriX = stream.readInt();
					int oriY = stream.readInt();
					int oriZ = stream.readInt();
					
					int oriDim = stream.readInt();
					
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();
					
					int dim = stream.readInt();
					
					WorldServer worldOri = DimensionManager.getWorld(oriDim);
					WorldServer world = DimensionManager.getWorld(dim);
					
					if(worldOri != null && world != null)
					{
						TileEntity oriTe = worldOri.getBlockTileEntity(oriX, oriY, oriZ);
						TileEntity te = world.getBlockTileEntity(x, y, z);
						
						if(oriTe instanceof TileEntityDualVertical && te instanceof TileEntityDualVertical)
						{
							TileEntityDualVertical dv = (TileEntityDualVertical)oriTe;
							TileEntityDualVertical dv1 = (TileEntityDualVertical)te;
							
							if(dv.playerName.equalsIgnoreCase(player.username) && dv1.playerName.equalsIgnoreCase(player.username))
							{
								if(dv1 instanceof TileEntityShellConstructor)
								{
									TileEntityShellConstructor sc = (TileEntityShellConstructor)dv1;
									if(sc.constructionProgress < SessionState.shellConstructionPowerRequirement)
									{
										ShellHandler.updatePlayerOfShells(player, null, true);
										break;
									}
								}
								if(dv1 instanceof TileEntityShellStorage)
								{
									TileEntityShellStorage ss = (TileEntityShellStorage)dv1;
									if(!ss.syncing)
									{
										ShellHandler.updatePlayerOfShells(player, null, true);
										break;
									}
								}
								
								if(dv instanceof TileEntityShellStorage)
								{
									TileEntityShellStorage ss = (TileEntityShellStorage)dv;
									
									ss.playerName = player.username;
									
									ss.occupied = true;
									
									ss.occupationTime = TileEntityDualVertical.animationTime;
									
									ss.syncing = true;
									
									NBTTagCompound tag = new NBTTagCompound();
									
									player.writeToNBT(tag);
									
									tag.setInteger("sync_playerGameMode", player.theItemInWorldManager.getGameType().getID());
									
									ss.playerNBT = tag;
									
									worldOri.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
									worldOri.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
								}

								ByteArrayOutputStream bytes = new ByteArrayOutputStream();
								DataOutputStream stream1 = new DataOutputStream(bytes);
								try
								{
									stream1.writeInt(oriX);
									stream1.writeInt(oriY);
									stream1.writeInt(oriZ);
									
									stream1.writeInt(oriDim);
									
									stream1.writeInt(dv.face);
									
									stream1.writeBoolean(false);
									
									stream1.writeBoolean(false);//isDeathZoom?
									
									PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)4, bytes.toByteArray()), (Player)player);
								}
								catch(IOException e)
								{
								}
								
								dv1.resyncPlayer = 120;
								dv.canSavePlayer = -1;
								
								MinecraftForge.EVENT_BUS.post(new SyncStartEvent(player, dv.playerNBT, dv1.playerNBT, dv1.xCoord, dv1.yCoord, dv1.zCoord));
								
								bytes = new ByteArrayOutputStream();
								stream1 = new DataOutputStream(bytes);
								try
								{
									stream1.writeUTF(player.username);
									stream1.writeBoolean(false);
									PacketDispatcher.sendPacketToAllPlayers(new Packet131MapData((short)Sync.getNetId(), (short)7, bytes.toByteArray()));
								}
								catch(IOException e)
								{
								}
								
								valid = true;
							}
						}
					}
					if(!valid)
					{
						ShellHandler.updatePlayerOfShells(player, null, true);
					}
					break;
				}
				case 1:
				{
					player.setLocationAndAngles(stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readFloat(), stream.readFloat());
					
					FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().syncPlayerInventory(player);
					
					ShellHandler.updatePlayerOfShells(player, null, true);
					
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
					SessionState.allowCrossDimensional = stream.readInt();
					SessionState.deathMode = stream.readInt();
					SessionState.hardMode = stream.readBoolean();
					
					Sync.mapHardmodeRecipe();
					
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
					
					state.playerState = TileEntityShellStorage.createPlayer(mc.theWorld, mc.thePlayer.username);
					
					if(!state.isConstructor)
					{
						NBTTagCompound tag = Sync.readNBTTagCompound(stream);
						if(tag.hasKey("Inventory"))
						{
							state.playerState.readFromNBT(tag);
						}
					}
					
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
					break;
				}
				case 3:
				{
					//Player locks into SyncStorage
					
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();

					TileEntity te = mc.theWorld.getBlockTileEntity(x, y, z);
					
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
						
						Sync.proxy.tickHandlerClient.renderCrosshair = GuiIngameForge.renderCrosshairs;
						GuiIngameForge.renderCrosshairs = false;
					}
					
					break;
				}
				case 4:
				{
					//zoom state
					
					Sync.proxy.tickHandlerClient.zoomX = stream.readInt();
					Sync.proxy.tickHandlerClient.zoomY = stream.readInt();
					Sync.proxy.tickHandlerClient.zoomZ = stream.readInt();
					
					Sync.proxy.tickHandlerClient.zoomDimension = stream.readInt();
					
					Sync.proxy.tickHandlerClient.zoomFace = stream.readInt();
					Sync.proxy.tickHandlerClient.zoom = stream.readBoolean();
					
					Sync.proxy.tickHandlerClient.zoomTimer = 60;
					
					Sync.proxy.tickHandlerClient.zoomDeath = stream.readBoolean();
					break;
				}
				case 5:
				{
					Sync.proxy.tickHandlerClient.shells.clear();
					break;
				}
				case 6:
				{
					NBTTagCompound tag = Sync.readNBTTagCompound(stream);
					mc.thePlayer.readFromNBT(tag);
					if(mc.thePlayer.isEntityAlive())
					{
						mc.thePlayer.deathTime = 0;
					}
					
					mc.playerController.setGameType(EnumGameType.getByID(tag.getInteger("sync_playerGameMode")));
					break;
				}
				case 7:
				{
					String name = stream.readUTF();
					Sync.proxy.tickHandlerClient.refusePlayerRender.put(name, 120);
					if(stream.readBoolean())
					{
						EntityPlayer player = mc.theWorld.getPlayerEntityByName(name);
						
						if(player != null)
						{
							player.deathTime = 0;
							player.setHealth(1);
							
							EntityShellDestruction sd = new EntityShellDestruction(player.worldObj, player.rotationYaw, player.renderYawOffset, player.rotationPitch, player.limbSwing, player.limbSwingAmount, AbstractClientPlayer.locationStevePng);
							sd.setLocationAndAngles(player.posX, player.posY - player.yOffset, player.posZ, 0.0F, 0.0F);
							player.worldObj.spawnEntityInWorld(sd);

						}
					}
					break;
				}
				case 8:
				{
					//destruction of a completed shell.
					
					int x = stream.readInt();
					int y = stream.readInt();
					int z = stream.readInt();
					
					int face = stream.readInt();
					
					EntityShellDestruction sd = new EntityShellDestruction(mc.theWorld, (face - 2) * 90F, (face - 2) * 90F, 0.0F, 0.0F, 0.0F, AbstractClientPlayer.locationStevePng);
					sd.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
					mc.theWorld.spawnEntityInWorld(sd);
				}
			}
		}
		catch(IOException e)
		{
		}
	}
	
}
