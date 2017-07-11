/*
 * Copyright (c) 2014.
 *
 * @author Kihira
 */

package me.ichun.mods.sync.client;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.List;

public class HUDHandlerWaila implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof TileEntityShellConstructor) {
            TileEntityShellConstructor tileEntityShellConstructor = (TileEntityShellConstructor) accessor.getTileEntity();
            if (config.getConfig("sync.showowner")) currenttip.add(I18n.translateToLocal("sync.waila.owner") + ": " + (tileEntityShellConstructor.getPlayerName().equals("") ? "None" : tileEntityShellConstructor.getPlayerName()));
            if (config.getConfig("sync.showprogress")) currenttip.add(I18n.translateToLocal("sync.waila.progress") + ": " + String.valueOf((int) Math.ceil(tileEntityShellConstructor.getBuildProgress() / Sync.config.shellConstructionPowerRequirement * 100)) + "%");
        }
        else if (accessor.getTileEntity() instanceof TileEntityShellStorage) {
            TileEntityShellStorage tileEntityShellStorage = (TileEntityShellStorage) accessor.getTileEntity();
            if (config.getConfig("sync.showowner")) currenttip.add(I18n.translateToLocal("sync.waila.owner") + ": " + (tileEntityShellStorage.getPlayerName().equals("") ? "None" : tileEntityShellStorage.getPlayerName()));
			if (config.getConfig("sync.showactive")) currenttip.add(I18n.translateToLocal("sync.waila.active") + ": " + (tileEntityShellStorage.isPowered() ? I18n.translateToLocal("gui.yes") : I18n.translateToLocal("gui.no")));
        }
        else if (accessor.getTileEntity() instanceof TileEntityTreadmill) {
            TileEntityTreadmill tileEntityTreadmill = (TileEntityTreadmill) accessor.getTileEntity();
            if (config.getConfig("sync.showentity")) currenttip.add(I18n.translateToLocal("sync.waila.entity") + ": " + (tileEntityTreadmill.latchedEnt != null ? tileEntityTreadmill.latchedEnt.getName() : "None"));
			if (config.getConfig("sync.showpower.output")) {
				DecimalFormat decimalFormat = new DecimalFormat("##.##");
				currenttip.add(I18n.translateToLocal("sync.waila.powerout") + ": " + decimalFormat.format(tileEntityTreadmill.powerOutput()) + "PW");
			}
		}
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        return null;
    }

    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new HUDHandlerWaila(), BlockDualVertical.class);

        registrar.addConfig("Sync", "sync.showowner", I18n.translateToLocal("sync.waila.showowner"));
        registrar.addConfig("Sync", "sync.showprogress", I18n.translateToLocal("sync.waila.showprogress"));
        registrar.addConfig("Sync", "sync.showentity", I18n.translateToLocal("sync.waila.showentity"));
        registrar.addConfig("Sync", "sync.showactive", I18n.translateToLocal("sync.waila.showactive"));
        registrar.addConfig("Sync", "sync.showpower.output", I18n.translateToLocal("sync.waila.showpower.output"));
	}
}
