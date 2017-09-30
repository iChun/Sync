package me.ichun.mods.sync.common.core;

import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.sync.common.Sync;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "gameplay", useSession = true)
    @IntMinMax(min = 0)
    public int shellConstructionPowerRequirement = 48000;

    @ConfigProp(category = "gameplay")
    @IntMinMax(min = 0)
    public int shellStoragePowerRequirement = 0;

    @ConfigProp(category = "gameplay", useSession = true)
    @IntMinMax(min = 0, max = 2)
    public int allowCrossDimensional = 1;

    @ConfigProp(category = "gameplay")
    @IntMinMax(min = 0)
    public int reduceHealthOnDeathSync = 0;

    @ConfigProp(category = "gameplay")
    @IntMinMax(min = 0)
    public int damageGivenOnShellConstruction = 2;

    @ConfigProp(category = "gameplay", useSession = true)
    @IntMinMax(min = 0, max = 2)
    public int overrideDeathIfThereAreAvailableShells = 1;

    @ConfigProp(category = "gameplay")
    @IntBool
    public int prioritizeHomeShellOnDeath = 1;

    @ConfigProp(category = "gameplay")
    @IntBool
    public int crossDimensionalSyncingOnDeath = 1;

    @ConfigProp(category = "gameplay")
    @IntBool
    public int allowChunkLoading = 1;

    @ConfigProp(category = "gameplay", useSession = true)
    @IntMinMax(min = 0, max = 2)
    public int hardcoreMode = 2;

    @ConfigProp(category = "gameplay")
    @IntMinMax(min = 0)
    public int ratioRF = 2;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 2)
    public int showAllShellInfoInGui = 1;

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return Sync.MOD_NAME.toLowerCase();
    }

    @Override
    public String getModName()
    {
        return Sync.MOD_NAME;
    }
}
