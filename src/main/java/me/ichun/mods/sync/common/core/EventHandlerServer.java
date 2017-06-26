package me.ichun.mods.sync.common.core;

import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.packet.PacketPlayerDeath;
import me.ichun.mods.sync.common.packet.PacketZoomCamera;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
                Sync.channel.sendTo(new PacketZoomCamera((int)Math.floor(entityPlayerMP.posX), (int)Math.floor(entityPlayerMP.posY), (int)Math.floor(entityPlayerMP.posZ), entityPlayerMP.dimension, EnumFacing.UP, false, true), event.player);

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
                    World world = tileEntityDualVertical.getWorld();
                    tileEntityDualVertical.reset();
                    IBlockState thisState = world.getBlockState(tileEntityDualVertical.getPos());
                    world.notifyBlockUpdate(tileEntityDualVertical.getPos(), thisState, thisState, 3);
                    BlockPos up = tileEntityDualVertical.getPos().up();
                    IBlockState above = world.getBlockState(up);
                    world.notifyBlockUpdate(up, above, above, 3);
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
            if (event.getEntityLiving() instanceof EntityPlayerMP && !(event.getEntityLiving() instanceof FakePlayer) && !event.getEntityLiving().world.isRemote) {
                EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
                TileEntityDualVertical tpPosition = getClosestRespawnShell(player);

                //If we have a valid location to sync into, tell the player to zoom out
                if (tpPosition != null) {
                    Sync.channel.sendTo(new PacketZoomCamera((int) Math.floor(event.getEntityLiving().posX), (int) Math.floor(event.getEntityLiving().posY), (int) Math.floor(event.getEntityLiving().posZ), event.getEntityLiving().dimension, EnumFacing.UP, false, true), player);

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
            if (ShellHandler.syncInProgress.containsKey(event.getEntityLiving().getName())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityHurt(LivingHurtEvent event) {
        //Prevent damage during sync
        if (event.getEntityLiving() instanceof EntityPlayerMP && !(event.getEntityLiving() instanceof FakePlayer) && !event.getEntityLiving().world.isRemote) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
            if (player.getEntityData().hasKey("isDeathSyncing") && player.getEntityData().getBoolean("isDeathSyncing"))
            {
                event.setCanceled(true);
                return;
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer && event.getSource() != DamageSource.outOfWorld) {
            if (ShellHandler.syncInProgress.containsKey(event.getEntityLiving().getName())) {
                event.setCanceled(true);
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
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (TileEntityTreadmill.isEntityValidForTreadmill(event.getTarget())) {
            TileEntity tileEntity = event.getTarget().world.getTileEntity(new BlockPos((int)Math.floor(event.getTarget().posX), (int)Math.floor(event.getTarget().posY), (int)Math.floor(event.getTarget().posZ)));
            if (tileEntity instanceof TileEntityTreadmill) {
                TileEntityTreadmill tm = (TileEntityTreadmill) tileEntity;

                if (tm.back) {
                    tm = tm.pair;
                }
                if (tm != null && tm.latchedEnt == event.getTarget()) {
                    double velo = 1.3D;
                    switch (tm.face) {
                        case SOUTH: {
                            tm.latchedEnt.motionZ = velo;
                            break;
                        }
                        case WEST: {
                            tm.latchedEnt.motionX = -velo;
                            break;
                        }
                        case NORTH: {
                            tm.latchedEnt.motionZ = -velo;
                            break;
                        }
                        case EAST: {
                            tm.latchedEnt.motionX = velo;
                            break;
                        }
                    }
                    tm.latchedEnt = null;
                    tm.timeRunning = 0;
                    IBlockState state = tm.getWorld().getBlockState(tm.getPos());
                    tm.getWorld().notifyBlockUpdate(tm.getPos(), state, state, 3);

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
        ArrayList<TileEntityDualVertical> dvs = new ArrayList<>();
        boolean reiterateShells = false;

        //Shells are chunk loaded so look through the tickets for the players shells
        for (Map.Entry<String, TileEntityDualVertical> e : ShellHandler.playerShells.entries()) {
            if (e.getKey().equalsIgnoreCase(player.getName())) {
                TileEntityDualVertical dv1 = e.getValue();
                if (dv1.getWorld().getTileEntity(dv1.getPos()) == dv1) {
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

            double dvDist = player.getDistance(dv.getPos().getX() + 0.5D, dv.getPos().getY(), dv.getPos().getZ() + 0.5D);
            if (dv.getWorld().provider.getDimension() == player.dimension) {
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
            else if ((Sync.config.allowCrossDimensional == 1 && (player.dimension != 1 || Sync.config.allowCrossDimensional == 2)) && Sync.config.crossDimensionalSyncingOnDeath == 1) {
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
