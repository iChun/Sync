package me.ichun.mods.sync.client;

import com.google.common.base.Function;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class HUDHandlerTheOneProbe implements Function<ITheOneProbe, Void>, IProbeInfoProvider {
    private static final ProgressStyle STYLE_BUILD_PROGRESS = new ProgressStyle().showText(true).prefix(I18n.translateToLocal("sync.waila.progress") + ": ").suffix("%");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");

    @Nullable
    @Override
    public Void apply(@Nullable ITheOneProbe input) {
        if (input == null) {
            Sync.LOGGER.error("Could not load The One Probe Compat!");
            return null;
        }
        Sync.LOGGER.info("Loading The One Probe compat");
        input.registerProvider(this);
        return null;
    }

    @Override
    public String getID() {
        return Sync.MOD_ID;
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo info, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (iBlockState.getBlock() == Sync.blockDualVertical) {
            TileEntity tileEntity = world.getTileEntity(iProbeHitData.getPos());
            if (tileEntity instanceof TileEntityShellConstructor) {
                TileEntityShellConstructor te = (TileEntityShellConstructor) tileEntity;
                info.text(I18n.translateToLocal("sync.waila.owner") + ": " + (te.getPlayerName().equals("") ? "None" : te.getPlayerName()));
                float progress = te.getBuildProgress() / Sync.config.shellConstructionPowerRequirement;
                if (progress < 1 || probeMode == ProbeMode.EXTENDED || probeMode == ProbeMode.DEBUG)
                    info.progress((int) Math.ceil(progress * 100), 100, STYLE_BUILD_PROGRESS);
            }
            else if (tileEntity instanceof TileEntityShellStorage) {
                TileEntityShellStorage te = (TileEntityShellStorage) tileEntity;
                info.text(I18n.translateToLocal("sync.waila.owner") + ": " + (te.getPlayerName().equals("") ? "None" : te.getPlayerName()));
                info.text(I18n.translateToLocal("sync.waila.active") + ": " + (te.isPowered() ? I18n.translateToLocal("gui.yes") : I18n.translateToLocal("gui.no")));
            }
            else if (tileEntity instanceof TileEntityTreadmill) {
                TileEntityTreadmill te = (TileEntityTreadmill) tileEntity;
                info.text(I18n.translateToLocal("sync.waila.entity") + ": " + (te.latchedEnt != null ? te.latchedEnt.getName() : "None"));
                info.text(I18n.translateToLocal("sync.waila.powerout") + ": " + DECIMAL_FORMAT.format(te.powerOutput()) + "PW");
            }
        }
    }
}
