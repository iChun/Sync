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
import sync.common.block.BlockDualVertical;
import sync.common.core.SessionState;
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
            if (config.getConfig("sync.showowner")) currenttip.add(StatCollector.translateToLocal("sync.waila.owner") + ": " + (tileEntityShellConstructor.playerName.equals("") ? "None" : tileEntityShellConstructor.playerName));
            if (config.getConfig("sync.showprogress")) currenttip.add(StatCollector.translateToLocal("sync.waila.progress") + ": " + String.valueOf((int) Math.ceil(tileEntityShellConstructor.getBuildProgress() / SessionState.shellConstructionPowerRequirement * 100)) + "%");
        }
        else if (accessor.getTileEntity() instanceof TileEntityShellStorage) {
            TileEntityShellStorage tileEntityShellStorage = (TileEntityShellStorage) accessor.getTileEntity();
            if (config.getConfig("sync.showowner")) currenttip.add(StatCollector.translateToLocal("sync.waila.owner") + ": " + (tileEntityShellStorage.playerName.equals("") ? "None" : tileEntityShellStorage.playerName));
			if (config.getConfig("sync.showactive")) currenttip.add(StatCollector.translateToLocal("sync.waila.active") + ": " + (tileEntityShellStorage.isPowered() ? StatCollector.translateToLocal("gui.yes") : StatCollector.translateToLocal("gui.no")));
        }
        else if (accessor.getTileEntity() instanceof TileEntityTreadmill) {
            TileEntityTreadmill tileEntityTreadmill = (TileEntityTreadmill) accessor.getTileEntity();
            if (config.getConfig("sync.showentity")) currenttip.add(StatCollector.translateToLocal("sync.waila.entity") + ": " + (tileEntityTreadmill.latchedEnt != null ? tileEntityTreadmill.latchedEnt.getEntityName() : "None"));
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

        registrar.addConfig("Sync", "sync.showowner", "Show Shell Owner");
        registrar.addConfig("Sync", "sync.showprogress", "Show Build Progress");
        registrar.addConfig("Sync", "sync.showentity", "Show Entity Name");
		registrar.addConfig("Sync", "sync.showactive", "Show Active");
		registrar.addConfig("Sync", "sync.showpower.output", "Show Power Output");
	}
}
