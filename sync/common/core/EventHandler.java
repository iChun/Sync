package sync.common.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import sync.common.Sync;
import sync.common.item.ChunkLoadHandler;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandler 
{

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onMouseEvent(MouseEvent event)
	{
		if(Sync.proxy.tickHandlerClient.radialShow && !Sync.proxy.tickHandlerClient.shells.isEmpty())
		{
			Sync.proxy.tickHandlerClient.radialDeltaX += event.dx / 100D;
			Sync.proxy.tickHandlerClient.radialDeltaY += event.dy / 100D;
			
			double mag = Math.sqrt(Sync.proxy.tickHandlerClient.radialDeltaX * Sync.proxy.tickHandlerClient.radialDeltaX + Sync.proxy.tickHandlerClient.radialDeltaY * Sync.proxy.tickHandlerClient.radialDeltaY);
			if(mag > 1.0D)
			{
				Sync.proxy.tickHandlerClient.radialDeltaX /= mag;
				Sync.proxy.tickHandlerClient.radialDeltaY /= mag;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onRenderPlayer(RenderPlayerEvent.Pre event)
	{
		if(Sync.proxy.tickHandlerClient.refusePlayerRender.containsKey(event.entityPlayer.username) && !Sync.proxy.tickHandlerClient.forceRender && Sync.proxy.tickHandlerClient.refusePlayerRender.get(event.entityPlayer.username) < 118)
		{
			event.entityPlayer.lastTickPosX = event.entityPlayer.prevPosX = event.entityPlayer.posX;
			event.entityPlayer.lastTickPosY = event.entityPlayer.prevPosY = event.entityPlayer.posY;
			event.entityPlayer.lastTickPosZ = event.entityPlayer.prevPosZ = event.entityPlayer.posZ;
			event.entityPlayer.renderYawOffset = event.entityPlayer.rotationYaw;
			event.entityPlayer.deathTime = 0;
			if(!event.entityPlayer.isEntityAlive())
			{
				event.entityPlayer.setHealth(1);
			}
			event.setCanceled(true);
		}
	}
	
	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event)
	{
		if(SessionState.deathMode > 0)
		{
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			{
				if(event.entityLiving instanceof EntityPlayerMP && !(event.entityLiving instanceof FakePlayer))
				{
					
					//TODO check for this
//					if(dv1 instanceof TileEntityShellStorage)
//					{
//						TileEntityShellStorage ss = (TileEntityShellStorage)dv1;
//						if(!ss.syncing)
//						{
//							ShellHandler.updatePlayerOfShells(player, null, true);
//							break;
//						}
//					}
					
					EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
					
					ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
					
					boolean reiterateShells = false;
					
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
								reiterateShells = true;
							}
						}
					}
					
					if(reiterateShells)
					{
						ShellHandler.updatePlayerOfShells(player, null, true);
					}
					
					TileEntityDualVertical tpPosition = null;
					
					TileEntityDualVertical nearestHome = null;
					double homeDist = -1D;
					TileEntityDualVertical nearestDv = null;
					double dist = -1D;
					TileEntityDualVertical nearestCrossDim = null;
					double crossDimDist = -1D;
					TileEntityDualVertical nearestCrossDimHome = null;
					double crossDimHomeDist = -1D;
					
					for(TileEntityDualVertical dv : dvs)
					{
						if(SessionState.deathMode == 1 && dv instanceof TileEntityShellConstructor)
						{
							continue;
						}
						
						double dvDist = player.getDistance(dv.xCoord + 0.5D, dv.yCoord, dv.zCoord + 0.5D);
						if(dv.worldObj.provider.dimensionId == player.dimension)
						{
							if(dv.isHomeUnit)
							{
								if(homeDist == -1D || dvDist < homeDist)
								{
									nearestHome = dv;
									homeDist = dvDist;
								}
							}
							if(dist == -1D || dvDist < dist)
							{
								nearestDv = dv;
								dist = dvDist;
							}
						}
						else if((SessionState.allowCrossDimensional == 1 && player.dimension != 1 || SessionState.allowCrossDimensional == 2) && Sync.crossDimensionalSyncingOnDeath == 1 )
						{
							if(dv.isHomeUnit)
							{
								if(crossDimHomeDist == -1D || dvDist < crossDimHomeDist)
								{
									nearestCrossDimHome = dv;
									crossDimHomeDist = dvDist;
								}
							}
							if(crossDimDist == -1D || dvDist < crossDimDist)
							{
								nearestCrossDim = dv;
								crossDimDist = dvDist;
							}
						}
					}
					
					if(Sync.prioritizeHomeShellOnDeath == 1)
					{
						if(nearestHome != null)
						{
							tpPosition = nearestHome;
						}
						else if(nearestCrossDimHome != null)
						{
							tpPosition = nearestCrossDimHome;
						}
					}
					if(tpPosition == null)
					{
						if(nearestDv != null)
						{
							tpPosition = nearestDv;
						}
						else if(nearestCrossDim != null)
						{
							tpPosition = nearestCrossDim;
						}
					}
					
					if(tpPosition != null)
					{
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						DataOutputStream stream = new DataOutputStream(bytes);
						try
						{
							stream.writeInt((int)Math.floor(event.entityLiving.posX));
							stream.writeInt((int)Math.floor(event.entityLiving.posY));
							stream.writeInt((int)Math.floor(event.entityLiving.posZ));
							
							stream.writeInt(event.entityLiving.dimension);
							
							stream.writeInt(-1);
							
							stream.writeBoolean(false);
							
							stream.writeBoolean(true);
							
							PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)4, bytes.toByteArray()), (Player)player);
						}
						catch(IOException e)
						{
						}
						
						tpPosition.resyncPlayer = 120;
						
						bytes = new ByteArrayOutputStream();
						stream = new DataOutputStream(bytes);
						try
						{
							stream.writeUTF(((EntityPlayer)event.entityLiving).username);
							stream.writeBoolean(true);
							PacketDispatcher.sendPacketToAllPlayers(new Packet131MapData((short)Sync.getNetId(), (short)7, bytes.toByteArray()));
						}
						catch(IOException e)
						{
						}
						
						try
						{
							ObfuscationReflectionHelper.setPrivateValue(Entity.class, player, true, "field_83001_bt", "i", "invulnerable");
						}
						catch(Exception e)
						{
							
						}
						player.setHealth(1);
						
						if(!ShellHandler.deathRespawns.contains(player.username))
						{
							ShellHandler.deathRespawns.add(player.username);
						}
					}
				}
			}
		}
	}

	@ForgeSubscribe
	public void onItemPickup(EntityItemPickupEvent event)
	{
		if(ShellHandler.deathRespawns.contains(event.entityPlayer.username))
		{
			event.setCanceled(true);
		}
	}
	
}
