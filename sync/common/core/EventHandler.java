package sync.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import sync.common.Sync;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import sync.common.tileentity.TileEntityTreadmill;

import java.util.ArrayList;
import java.util.Map.Entry;

public class EventHandler
{

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onMouseEvent(MouseEvent event)
	{
		if(Sync.proxy.tickHandlerClient.radialShow)
		{
			if(!Sync.proxy.tickHandlerClient.shells.isEmpty())
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
			if(event.button == 0 || event.button == 1)
			{
				event.setCanceled(true);
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
			event.entityPlayer.lastTickPosY = event.entityPlayer.prevPosY = event.entityPlayer != Minecraft.getMinecraft().thePlayer && Sync.proxy.tickHandlerClient.refusePlayerRender.get(event.entityPlayer.username) > 60 ? 500D : event.entityPlayer.posY;
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

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event)
	{
		if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && Sync.proxy.tickHandlerClient.radialShow)
		{
			event.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event) {
		if (SessionState.deathMode > 0) {
			if (event.entityLiving instanceof EntityPlayerMP && !(event.entityLiving instanceof FakePlayer) && !event.entityLiving.worldObj.isRemote) {
				EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
				TileEntityDualVertical tpPosition = getClosestRespawnShell(player);

				if (tpPosition != null) {
					Packet131MapData zoomPacket = MapPacketHandler.createZoomCameraPacket(
							(int) Math.floor(event.entityLiving.posX),
							(int) Math.floor(event.entityLiving.posY),
							(int) Math.floor(event.entityLiving.posZ),
							event.entityLiving.dimension,
							-1, false, true);
					PacketDispatcher.sendPacketToPlayer(zoomPacket, (Player)player);

					tpPosition.resyncPlayer = 120;
					EntityPlayer dvInstance = null;

					if (tpPosition instanceof TileEntityShellStorage) {
						dvInstance = ((TileEntityShellStorage)tpPosition).playerInstance;
					}
					else if (tpPosition instanceof TileEntityShellConstructor) {
						dvInstance = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), tpPosition.worldObj, player.getCommandSenderName(), new ItemInWorldManager(tpPosition.worldObj));
						((EntityPlayerMP)dvInstance).playerNetServerHandler = player.playerNetServerHandler;
					}

					if (dvInstance != null) {
						NBTTagCompound tag = new NBTTagCompound();

						if (tpPosition.playerNBT != null && tpPosition.playerNBT.hasKey("Inventory")) {
							dvInstance.readFromNBT(tpPosition.playerNBT);
						}

						dvInstance.setLocationAndAngles(tpPosition.xCoord + 0.5D, tpPosition.yCoord, tpPosition.zCoord + 0.5D, (tpPosition.face - 2) * 90F, 0F);

						boolean keepInv = player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");

						tpPosition.worldObj.getGameRules().setOrCreateGameRule("keepInventory", "false");

						dvInstance.clonePlayer(player, false);
						dvInstance.entityId = player.entityId;

						tpPosition.worldObj.getGameRules().setOrCreateGameRule("keepInventory", keepInv ? "true" : "false");

						dvInstance.writeToNBT(tag);

						tag.setInteger("sync_playerGameMode", tpPosition.playerNBT.getInteger("sync_playerGameMode"));

						tpPosition.playerNBT = tag;
					}

					PacketDispatcher.sendPacketToAllPlayers(MapPacketHandler.createPlayerDeathPacket(((EntityPlayer)event.entityLiving).username, true));

					player.setHealth(1);

					if (!ShellHandler.syncInProgress.containsKey(player.username)) {
						player.getEntityData().setBoolean("isDeathSyncing", true);
						ShellHandler.syncInProgress.put(player.username, tpPosition);
					}
				}
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onEntityAttacked(LivingAttackEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer && event.source != DamageSource.outOfWorld)
		{
			if(ShellHandler.syncInProgress.containsKey(((EntityPlayer)event.entityLiving).username))
			{
				event.setCanceled(true);
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onEntityHurt(LivingHurtEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer && event.source != DamageSource.outOfWorld)
		{
			if(ShellHandler.syncInProgress.containsKey(((EntityPlayer)event.entityLiving).username))
			{
				event.setCanceled(true);
			}
		}
	}

	@ForgeSubscribe
	public void onItemPickup(EntityItemPickupEvent event)
	{
		if(ShellHandler.syncInProgress.containsKey(event.entityPlayer.username))
		{
			event.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void onEntityInteract(EntityInteractEvent event)
	{
		if(TileEntityTreadmill.isEntityValidForTreadmill(event.target))
		{
			TileEntity te = event.target.worldObj.getBlockTileEntity((int)Math.floor(event.target.posX), (int)Math.floor(event.target.posY), (int)Math.floor(event.target.posZ));
			if(te instanceof TileEntityTreadmill)
			{
				TileEntityTreadmill tm = (TileEntityTreadmill)te;

				if(tm.back)
				{
					tm = tm.pair;
				}
				if(tm != null && tm.latchedEnt == event.target)
				{
					double velo = 1.3D;
					switch(tm.face)
					{
						case 0:
						{
							tm.latchedEnt.motionZ = velo;
							break;
						}
						case 1:
						{
							tm.latchedEnt.motionX = -velo;
							break;
						}
						case 2:
						{
							tm.latchedEnt.motionZ = -velo;
							break;
						}
						case 3:
						{
							tm.latchedEnt.motionX = velo;
							break;
						}
					}
					tm.latchedEnt = null;
					tm.timeRunning = 0;
					tm.worldObj.markBlockForUpdate(tm.xCoord, tm.yCoord, tm.zCoord);

					event.setCanceled(true);
				}
			}
		}
	}

	//Will return the closest shell that the player can be synced too
	public static TileEntityDualVertical getClosestRespawnShell(EntityPlayer player) {
		ArrayList<TileEntityDualVertical> dvs = new ArrayList<TileEntityDualVertical>();
		boolean reiterateShells = false;

		//Shells are chunk loaded so look through the tickets for the players shells
		for (Entry<String, TileEntityDualVertical> e : ShellHandler.playerShells.entries()) {
			if (e.getKey().equalsIgnoreCase(player.username)) {
				TileEntityDualVertical dv1 = e.getValue();
				if (dv1.worldObj.getBlockTileEntity(dv1.xCoord, dv1.yCoord, dv1.zCoord) == dv1) {
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
			if (dv.worldObj.provider.dimensionId == player.dimension) {
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
