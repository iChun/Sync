package me.ichun.mods.sync.common.core;

import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.packet.PacketPlayerDeath;
import me.ichun.mods.sync.common.packet.PacketZoomCamera;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Map;

public class EventHandlerServer
{
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        ShellHandler.updatePlayerOfShells(event.player, null, true);

        //Check if the player was mid death sync
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) event.player;
        if (entityPlayerMP.getEntityData().hasKey("isDeathSyncing") && entityPlayerMP.getEntityData().getBoolean("isDeathSyncing")) {
            TileEntityDualVertical tpPosition = EventHandlerServer.getClosestRespawnShell(entityPlayerMP);

            if (tpPosition != null) {
                Sync.channel.sendTo(new PacketZoomCamera((int)Math.floor(entityPlayerMP.posX), (int)Math.floor(entityPlayerMP.posY), (int)Math.floor(entityPlayerMP.posZ), entityPlayerMP.dimension, -1, false, true), event.player);

                tpPosition.resyncPlayer = 120;

                Sync.channel.sendToAll(new PacketPlayerDeath(entityPlayerMP.getName(), true));

                entityPlayerMP.setHealth(20);

                if (!ShellHandler.syncInProgress.containsKey(entityPlayerMP.getName())) {
                    ShellHandler.syncInProgress.put(entityPlayerMP.getName(), tpPosition);
                }
            }
            else {
                entityPlayerMP.setDead();
                entityPlayerMP.setHealth(0);
                entityPlayerMP.getEntityData().setBoolean("isDeathSyncing", false);
                ShellHandler.syncInProgress.remove(entityPlayerMP.getName());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        //If player was syncing then reset the sync
        if (ShellHandler.syncInProgress.containsKey(event.player.getName())) {
            TileEntityDualVertical tileEntityDualVertical = ShellHandler.syncInProgress.get(event.player.getName());
            Sync.LOGGER.log(Level.INFO, String.format("%s logged out mid-sync whilst sync process was at %s", event.player.getName(), tileEntityDualVertical.resyncPlayer));
            //If they're still syncing away (ie camera zoom out), just reset it all
            if (tileEntityDualVertical.resyncPlayer > 60) {
                tileEntityDualVertical.resyncPlayer = -10;
                //If they're syncing from an existing shell, reset that shell. They should only ever sync from a shell storage but lets check to be safe
                if (tileEntityDualVertical.resyncOrigin != null) {
                    tileEntityDualVertical.reset();
                    tileEntityDualVertical.getWorld().markBlockForUpdate(tileEntityDualVertical.xCoord, tileEntityDualVertical.yCoord, tileEntityDualVertical.zCoord);
                    tileEntityDualVertical.getWorld().markBlockForUpdate(tileEntityDualVertical.xCoord, tileEntityDualVertical.yCoord + 1, tileEntityDualVertical.zCoord);
                }
            }
            //Remove player from syncing list
            ShellHandler.syncInProgress.remove(event.player.getName());
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        //If we allow death syncing
        if (Sync.config.overrideDeathIfThereAreAvailableShells > 0) {
            //And the player is actually a player on a server
            if (event.getEntityLiving() instanceof EntityPlayerMP && !(event.getEntityLiving() instanceof FakePlayer) && !event.getEntityLiving().worldObj.isRemote) {
                EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
                TileEntityDualVertical tpPosition = getClosestRespawnShell(player);

                //If we have a valid location to sync into, tell the player to zoom out
                if (tpPosition != null) {
                    Sync.channel.sendTo(new PacketZoomCamera((int) Math.floor(event.getEntityLiving().posX), (int) Math.floor(event.getEntityLiving().posY), (int) Math.floor(event.getEntityLiving().posZ), event.getEntityLiving().dimension, -1, false, true), player);

                    tpPosition.resyncPlayer = 120;

                    //Create the death animation packet
                    Sync.channel.sendToAll(new PacketPlayerDeath(event.getEntityLiving().getName(), true));

                    player.setHealth(20);

                    if (!ShellHandler.syncInProgress.containsKey(player.getName())) {
                        player.getEntityData().setBoolean("isDeathSyncing", true);
                        ShellHandler.syncInProgress.put(player.getName(), tpPosition);
                    }

                    NBTTagCompound persistent = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
                    persistent.setInteger("Sync_HealthReduction", persistent.getInteger("Sync_HealthReduction") + Sync.config.reduceHealthOnDeathSync);
                    player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistent);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityAttacked(LivingAttackEvent event) {
        //Prevent damage during sync
        if (event.getEntityLiving() instanceof EntityPlayer && event.getSource() != DamageSource.outOfWorld) {
            if (ShellHandler.syncInProgress.containsKey(((EntityPlayer) event.getEntityLiving()).getName())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityHurt(LivingHurtEvent event) {
        //Prevent damage during sync
        if (event.getEntityLiving() instanceof EntityPlayerMP && !(event.getEntityLiving() instanceof FakePlayer) && !event.getEntityLiving().worldObj.isRemote) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
            if (player.getEntityData().hasKey("isDeathSyncing") && player.getEntityData().getBoolean("isDeathSyncing"))
            {
                event.setCanceled(true);
                return;
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer && event.getSource() != DamageSource.outOfWorld) {
            if (ShellHandler.syncInProgress.containsKey(((EntityPlayer) event.getEntityLiving()).getName())) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        //Don't allow players to pickup items during sync
        if (ShellHandler.syncInProgress.containsKey(event.getEntityPlayer().getName())) {
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
        if (ShellHandler.syncInProgress.containsKey(e.getPlayer().getName())) {
            e.setCanceled(true);
        }
    }

    //Will return the closest shell that the player can be synced too
    public static TileEntityDualVertical getClosestRespawnShell(EntityPlayer player) {
        ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
        boolean reiterateShells = false;

        //Shells are chunk loaded so look through the tickets for the players shells
        for (Map.Entry<String, TileEntityDualVertical> e : ShellHandler.playerShells.entries()) {
            if (e.getKey().equalsIgnoreCase(player.getName())) {
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
                if (Sync.config.overrideDeathIfThereAreAvailableShells == 1 || sc.constructionProgress < Sync.config.shellConstructionPowerRequirement) {
                    continue;
                }
            }

            double dvDist = player.getDistance(dv.xCoord + 0.5D, dv.yCoord, dv.zCoord + 0.5D);
            if (dv.getWorldObj().provider.getDimension() == player.dimension) {
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
            else if ((Sync.config.allowCrossDimensional == 1 && (player.dimension != 1 || Sync.config.allowCrossDimensional == 2)) && Sync.config.getSessionInt("crossDimensionalSyncingOnDeath") == 1) {
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

        if (Sync.config.prioritizeHomeShellOnDeath == 1) {
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
