/*
 * Copyright (c) 2014.
 *
 * @author Kihira
 */

package sync.client;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import sync.common.Sync;
import sync.common.block.BlockDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import sync.common.tileentity.TileEntityTreadmill;

import java.text.DecimalFormat;
import java.util.List;

public class HUDHandlerSync implements IWailaDataProvider {

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
            if (config.getConfig("sync.showowner")) currenttip.add(StatCollector.translateToLocal("sync.waila.owner") + ": " + (tileEntityShellConstructor.getPlayerName().equals("") ? "None" : tileEntityShellConstructor.getPlayerName()));
            if (config.getConfig("sync.showprogress")) currenttip.add(StatCollector.translateToLocal("sync.waila.progress") + ": " + String.valueOf((int) Math.ceil(tileEntityShellConstructor.getBuildProgress() / Sync.config.getSessionInt("shellConstructionPowerRequirement") * 100)) + "%");
        }
        else if (accessor.getTileEntity() instanceof TileEntityShellStorage) {
            TileEntityShellStorage tileEntityShellStorage = (TileEntityShellStorage) accessor.getTileEntity();
            if (config.getConfig("sync.showowner")) currenttip.add(StatCollector.translateToLocal("sync.waila.owner") + ": " + (tileEntityShellStorage.getPlayerName().equals("") ? "None" : tileEntityShellStorage.getPlayerName()));
			if (config.getConfig("sync.showactive")) currenttip.add(StatCollector.translateToLocal("sync.waila.active") + ": " + (tileEntityShellStorage.isPowered() ? StatCollector.translateToLocal("gui.yes") : StatCollector.translateToLocal("gui.no")));
        }
        else if (accessor.getTileEntity() instanceof TileEntityTreadmill) {
            TileEntityTreadmill tileEntityTreadmill = (TileEntityTreadmill) accessor.getTileEntity();
            if (config.getConfig("sync.showentity")) currenttip.add(StatCollector.translateToLocal("sync.waila.entity") + ": " + (tileEntityTreadmill.latchedEnt != null ? tileEntityTreadmill.latchedEnt.getCommandSenderName() : "None"));
			if (config.getConfig("sync.showpower.output")) {
				DecimalFormat decimalFormat = new DecimalFormat("##.##");
				currenttip.add(StatCollector.translateToLocal("sync.waila.powerout") + ": " + decimalFormat.format(tileEntityTreadmill.powerOutput()) + "PW");
			}
		}
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new HUDHandlerSync(), BlockDualVertical.class);

        registrar.addConfig("Sync", "sync.showowner", StatCollector.translateToLocal("sync.waila.showowner"));
        registrar.addConfig("Sync", "sync.showprogress", StatCollector.translateToLocal("sync.waila.showprogress"));
        registrar.addConfig("Sync", "sync.showentity", StatCollector.translateToLocal("sync.waila.showentity"));
        registrar.addConfig("Sync", "sync.showactive", StatCollector.translateToLocal("sync.waila.showactive"));
        registrar.addConfig("Sync", "sync.showpower.output", StatCollector.translateToLocal("sync.waila.showpower.output"));
	}
}
