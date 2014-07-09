package sync.common.core;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import org.apache.logging.log4j.Level;
import sync.common.Sync;
import sync.common.packet.PacketPlayerDeath;
import sync.common.packet.PacketSession;
import sync.common.packet.PacketZoomCamera;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityTreadmill;

import java.util.ArrayList;
import java.util.Map.Entry;

public class EventHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouseEvent(MouseEvent event) {
		if (Sync.proxy.tickHandlerClient.radialShow) {
			if (!Sync.proxy.tickHandlerClient.shells.isEmpty()) {
				Sync.proxy.tickHandlerClient.radialDeltaX += event.dx / 100D;
				Sync.proxy.tickHandlerClient.radialDeltaY += event.dy / 100D;

				double mag = Math.sqrt(Sync.proxy.tickHandlerClient.radialDeltaX * Sync.proxy.tickHandlerClient.radialDeltaX + Sync.proxy.tickHandlerClient.radialDeltaY * Sync.proxy.tickHandlerClient.radialDeltaY);
				if(mag > 1.0D) {
					Sync.proxy.tickHandlerClient.radialDeltaX /= mag;
					Sync.proxy.tickHandlerClient.radialDeltaY /= mag;
				}
			}
			if (event.button == 0 || event.button == 1) {
				event.setCanceled(true);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if (Sync.proxy.tickHandlerClient.refusePlayerRender.containsKey(event.entityPlayer.getCommandSenderName()) && !Sync.proxy.tickHandlerClient.forceRender && Sync.proxy.tickHandlerClient.refusePlayerRender.get(event.entityPlayer.getCommandSenderName()) < 118) {
			event.entityPlayer.lastTickPosX = event.entityPlayer.prevPosX = event.entityPlayer.posX;
			event.entityPlayer.lastTickPosY = event.entityPlayer.prevPosY = event.entityPlayer != Minecraft.getMinecraft().thePlayer && Sync.proxy.tickHandlerClient.refusePlayerRender.get(event.entityPlayer.getCommandSenderName()) > 60 ? 500D : event.entityPlayer.posY;
			event.entityPlayer.lastTickPosZ = event.entityPlayer.prevPosZ = event.entityPlayer.posZ;
			event.entityPlayer.renderYawOffset = event.entityPlayer.rotationYaw;
			event.entityPlayer.deathTime = 0;
			if (!event.entityPlayer.isEntityAlive()) {
				event.entityPlayer.setHealth(1);
			}
			event.setCanceled(true);
		}
	}

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        Sync.proxy.tickHandlerClient.radialShow = false;
        Sync.proxy.tickHandlerClient.zoom = false;
        Sync.proxy.tickHandlerClient.lockTime = 0;
        Sync.proxy.tickHandlerClient.zoomTimer = -10;
        Sync.proxy.tickHandlerClient.zoomTimeout = 0;
        Sync.proxy.tickHandlerClient.shells.clear();
        Sync.proxy.tickHandlerClient.refusePlayerRender.clear();
        Sync.proxy.tickHandlerClient.lockedStorage = null;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        PacketHandler.sendToPlayer(Sync.channels, new PacketSession(), event.player);
        ShellHandler.updatePlayerOfShells((EntityPlayer) event.player, null, true);

        //Check if the player was mid death sync
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) event.player;
        if (entityPlayerMP.getEntityData().hasKey("isDeathSyncing") && entityPlayerMP.getEntityData().getBoolean("isDeathSyncing")) {
            TileEntityDualVertical tpPosition = EventHandler.getClosestRespawnShell(entityPlayerMP);

            if (tpPosition != null) {
                PacketHandler.sendToPlayer(Sync.channels, new PacketZoomCamera((int)Math.floor(entityPlayerMP.posX), (int)Math.floor(entityPlayerMP.posY), (int)Math.floor(entityPlayerMP.posZ), entityPlayerMP.dimension, -1, false, true), event.player);

                tpPosition.resyncPlayer = 120;

                PacketHandler.sendToAll(Sync.channels, new PacketPlayerDeath(entityPlayerMP.getCommandSenderName(), true));

                entityPlayerMP.setHealth(20);

                if (!ShellHandler.syncInProgress.containsKey(entityPlayerMP.getCommandSenderName())) {
                    ShellHandler.syncInProgress.put(entityPlayerMP.getCommandSenderName(), tpPosition);
                }
            }
            else {
                entityPlayerMP.setDead();
                entityPlayerMP.setHealth(0);
                entityPlayerMP.getEntityData().setBoolean("isDeathSyncing", false);
                ShellHandler.syncInProgress.remove(entityPlayerMP.getCommandSenderName());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        //If player was syncing then reset the sync
        if (ShellHandler.syncInProgress.containsKey(event.player.getCommandSenderName())) {
            TileEntityDualVertical tileEntityDualVertical = ShellHandler.syncInProgress.get(event.player.getCommandSenderName());
            Sync.logger.log(Level.INFO, String.format("%s logged out mid-sync whilst sync process was at %s", event.player.getCommandSenderName(), tileEntityDualVertical.resyncPlayer));
            //If they're still syncing away (ie camera zoom out), just reset it all
            if (tileEntityDualVertical.resyncPlayer > 60) {
                tileEntityDualVertical.resyncPlayer = -10;
                //If they're syncing from an existing shell, reset that shell. They should only ever sync from a shell storage but lets check to be safe
                if (tileEntityDualVertical.resyncOrigin != null) {
                    tileEntityDualVertical.reset();
                    tileEntityDualVertical.getWorldObj().markBlockForUpdate(tileEntityDualVertical.xCoord, tileEntityDualVertical.yCoord, tileEntityDualVertical.zCoord);
                    tileEntityDualVertical.getWorldObj().markBlockForUpdate(tileEntityDualVertical.xCoord, tileEntityDualVertical.yCoord + 1, tileEntityDualVertical.zCoord);
                }
            }
            //Remove player from syncing list
            ShellHandler.syncInProgress.remove(event.player.getCommandSenderName());
        }
    }

    @SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
		if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && Sync.proxy.tickHandlerClient.radialShow) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		//If we allow death syncing
		if (SessionState.deathMode > 0) {
			//And the player is actually a player on a server
			if (event.entityLiving instanceof EntityPlayerMP && !(event.entityLiving instanceof FakePlayer) && !event.entityLiving.worldObj.isRemote) {
				EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
				TileEntityDualVertical tpPosition = getClosestRespawnShell(player);

				//If we have a valid location to sync into, tell the player to zoom out
				if (tpPosition != null) {
					PacketHandler.sendToPlayer(Sync.channels, new PacketZoomCamera((int) Math.floor(event.entityLiving.posX), (int) Math.floor(event.entityLiving.posY), (int) Math.floor(event.entityLiving.posZ), event.entityLiving.dimension, -1, false, true), player);

					tpPosition.resyncPlayer = 120;

					//Create the death animation packet
                    PacketHandler.sendToAll(Sync.channels, new PacketPlayerDeath(event.entityLiving.getCommandSenderName(), true));

					player.setHealth(20);

					if (!ShellHandler.syncInProgress.containsKey(player.getCommandSenderName())) {
						player.getEntityData().setBoolean("isDeathSyncing", true); //TODO remove this tag
						ShellHandler.syncInProgress.put(player.getCommandSenderName(), tpPosition);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityAttacked(LivingAttackEvent event) {
		//Prevent damage during sync
		if (event.entityLiving instanceof EntityPlayer && event.source != DamageSource.outOfWorld) {
			if (ShellHandler.syncInProgress.containsKey(((EntityPlayer) event.entityLiving).getCommandSenderName())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityHurt(LivingHurtEvent event) {
		//Prevent damage during sync
		if (event.entityLiving instanceof EntityPlayer && event.source != DamageSource.outOfWorld) {
			if (ShellHandler.syncInProgress.containsKey(((EntityPlayer) event.entityLiving).getCommandSenderName())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		//Don't allow players to pickup items during sync
		if (ShellHandler.syncInProgress.containsKey(event.entityPlayer.getCommandSenderName())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		if (TileEntityTreadmill.isEntityValidForTreadmill(event.target)) {
			TileEntity tileEntity = event.target.worldObj.getTileEntity((int)Math.floor(event.target.posX), (int)Math.floor(event.target.posY), (int)Math.floor(event.target.posZ));
			if (tileEntity instanceof TileEntityTreadmill) {
				TileEntityTreadmill tm = (TileEntityTreadmill) tileEntity;

				if (tm.back) {
					tm = tm.pair;
				}
				if (tm != null && tm.latchedEnt == event.target) {
					double velo = 1.3D;
					switch (tm.face) {
						case 0: {
							tm.latchedEnt.motionZ = velo;
							break;
						}
						case 1: {
							tm.latchedEnt.motionX = -velo;
							break;
						}
						case 2: {
							tm.latchedEnt.motionZ = -velo;
							break;
						}
						case 3: {
							tm.latchedEnt.motionX = velo;
							break;
						}
					}
					tm.latchedEnt = null;
					tm.timeRunning = 0;
					tm.getWorldObj().markBlockForUpdate(tm.xCoord, tm.yCoord, tm.zCoord);

					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemToss(ItemTossEvent e) {
		//Don't allow item drops whilst syncing to prevent dupe issues
		if (ShellHandler.syncInProgress.containsKey(e.player.getCommandSenderName())) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerOpenContainer(PlayerOpenContainerEvent e) {
		//Don't show any containers during sync
		if (ShellHandler.syncInProgress.containsKey(e.entityPlayer.getCommandSenderName())) {
			e.setResult(Event.Result.DENY);
		}
	}

	//Will return the closest shell that the player can be synced too
	public static TileEntityDualVertical getClosestRespawnShell(EntityPlayer player) {
		ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
		boolean reiterateShells = false;

		//Shells are chunk loaded so look through the tickets for the players shells
		for (Entry<String, TileEntityDualVertical> e : ShellHandler.playerShells.entries()) {
			if (e.getKey().equalsIgnoreCase(player.getCommandSenderName())) {
				TileEntityDualVertical dv1 = e.getValue();
				if (dv1.getWorldObj().getTileEntity(dv1.xCoord, dv1.yCoord, dv1.zCoord) == dv1) {
					dvs.add(dv1);
				}
				else {
					reiterateShells = true;
				}
			}
		}

		if (reiterateShells) {
			ShellHandler.updatePlayerOfShells(player, null, true);
		}

		TileEntityDualVertical tpPosition, nearestHome, nearestDv, nearestCrossDim, nearestCrossDimHome;
		tpPosition = nearestHome = nearestDv = nearestCrossDim = nearestCrossDimHome = null;
		double homeDist, dist, crossDimDist, crossDimHomeDist;
		homeDist = dist = crossDimDist = crossDimHomeDist = -1D;

		for (TileEntityDualVertical dv : dvs) {
			if (dv instanceof TileEntityShellConstructor) {
				TileEntityShellConstructor sc = (TileEntityShellConstructor) dv;
				//If enabled in config and shell is complete, allow sync into constructor
				if (SessionState.deathMode == 1 || sc.constructionProgress < SessionState.shellConstructionPowerRequirement) {
					continue;
				}
			}

			double dvDist = player.getDistance(dv.xCoord + 0.5D, dv.yCoord, dv.zCoord + 0.5D);
			if (dv.getWorldObj().provider.dimensionId == player.dimension) {
				if (dv.isHomeUnit) {
					if (homeDist == -1D || dvDist < homeDist) {
						nearestHome = dv;
						homeDist = dvDist;
					}
				}
				if (dist == -1D || dvDist < dist) {
					nearestDv = dv;
					dist = dvDist;
				}
			}
			else if ((SessionState.allowCrossDimensional == 1 && (player.dimension != 1 || SessionState.allowCrossDimensional == 2)) && Sync.crossDimensionalSyncingOnDeath == 1) {
				if (dv.isHomeUnit) {
					if (crossDimHomeDist == -1D || dvDist < crossDimHomeDist) {
						nearestCrossDimHome = dv;
						crossDimHomeDist = dvDist;
					}
				}
				if (crossDimDist == -1D || dvDist < crossDimDist) {
					nearestCrossDim = dv;
					crossDimDist = dvDist;
				}
			}
		}

		if (Sync.prioritizeHomeShellOnDeath == 1) {
			if (nearestHome != null) {
				tpPosition = nearestHome;
			}
			else if (nearestCrossDimHome != null) {
				tpPosition = nearestCrossDimHome;
			}
		}
		if (tpPosition == null) {
			if (nearestDv != null) {
				tpPosition = nearestDv;
			}
			else if (nearestCrossDim != null) {
				tpPosition = nearestCrossDim;
			}
		}
		return tpPosition;
	}
}
