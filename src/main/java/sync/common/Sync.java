package sync.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ichun.client.keybind.KeyBind;
import ichun.common.core.config.Config;
import ichun.common.core.config.ConfigHandler;
import ichun.common.core.config.IConfigUser;
import ichun.common.core.updateChecker.ModVersionChecker;
import ichun.common.core.updateChecker.ModVersionInfo;
import ichun.common.iChunUtil;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sync.common.core.ChunkLoadHandler;
import sync.common.core.CommandSync;
import sync.common.core.CommonProxy;
import sync.common.shell.ShellHandler;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

@Mod(modid = "Sync", name = "Sync",
        version = Sync.version,
        dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".3.0,);after:CoFHCore;after:Waila",
        acceptableRemoteVersions = "[" + iChunUtil.versionMC +".0.0," + iChunUtil.versionMC + ".1.0)"
)
public class Sync
        implements IConfigUser
{
    public static final String version = iChunUtil.versionMC +".0.1";

    @Instance("Sync")
    public static Sync instance;

    @SidedProxy(clientSide = "sync.client.core.ClientProxy", serverSide = "sync.common.core.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger = LogManager.getLogger("Sync");

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    public static CreativeTabs creativeTabSync;

    public static Config config;

    public static Block blockDualVertical;

    public static Item itemBlockPlacer;
    public static Item itemPlaceholder;

    public static boolean hasMorphMod;
    public static boolean hasCoFHCore;

    public static final HashMap<Class, Integer> treadmillEntityHashMap = new HashMap<Class, Integer>();

    @Override
    public boolean onConfigChange(Config cfg, Property prop)
    {
        return true;
    }

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "sync", "Sync", logger, instance);

        config.setCurrentCategory("gameplay", "sync.config.cat.gameplay.name", "sync.config.cat.gameplay.comment");
        config.createIntProperty("shellConstructionPowerRequirement", "sync.config.prop.shellConstructionPowerRequirement.name", "sync.config.prop.shellConstructionPowerRequirement.comment", true, true, 48000, 0, Integer.MAX_VALUE);
        config.createIntProperty("shellStoragePowerRequirement", "sync.config.prop.shellStoragePowerRequirement.name", "sync.config.prop.shellStoragePowerRequirement.comment", true, false, 0, 0, Integer.MAX_VALUE);

        config.createIntProperty("allowCrossDimensional", "sync.config.prop.allowCrossDimensional.name", "sync.config.prop.allowCrossDimensional.comment", true, true, 1, 0, 2);
        config.createIntProperty("damageGivenOnShellConstruction", "sync.config.prop.damageGivenOnShellConstruction.name", "sync.config.prop.damageGivenOnShellConstruction.comment", true, false, 2, 0, Integer.MAX_VALUE);
        config.createIntProperty("overrideDeathIfThereAreAvailableShells", "sync.config.prop.overrideDeathIfThereAreAvailableShells.name", "sync.config.prop.overrideDeathIfThereAreAvailableShells.comment", true, true, 1, 0, 2);
        config.createIntBoolProperty("prioritizeHomeShellOnDeath", "sync.config.prop.prioritizeHomeShellOnDeath.name", "sync.config.prop.prioritizeHomeShellOnDeath.comment", true, false, true);
        config.createIntBoolProperty("crossDimensionalSyncingOnDeath", "sync.config.prop.crossDimensionalSyncingOnDeath.name", "sync.config.prop.crossDimensionalSyncingOnDeath.comment", true, false, true);

        config.createIntBoolProperty("allowChunkLoading", "sync.config.prop.allowChunkLoading.name", "sync.config.prop.allowChunkLoading.comment", true, false, true);

        config.createIntProperty("hardcoreMode", "sync.config.prop.hardcoreMode.name", "sync.config.prop.hardcoreMode.comment", true, true, 2, 0, 2);

        config.createIntProperty("ratioRF", "sync.config.prop.ratioRF.name", "sync.config.prop.ratioRF.comment", true, false, 2, 0, Integer.MAX_VALUE);

        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            iChunUtil.proxy.registerKeyBind(new KeyBind(-100, false, false, false, false), null);
            iChunUtil.proxy.registerKeyBind(new KeyBind(-99, false, false, false, false), null);
            config.setCurrentCategory("clientOnly", "sync.config.cat.clientOnly.name", "sync.config.cat.clientOnly.comment");
            config.createIntProperty("showAllShellInfoInGui", "sync.config.prop.showAllShellInfoInGui.name", "sync.config.prop.showAllShellInfoInGui.comment", true, false, 1, 0, 2);
        }

        sync.common.core.EventHandler handler = new sync.common.core.EventHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);

        //Following new modding convention, blocks and items are initialized in pre-init
        proxy.initMod();

        ModVersionChecker.register_iChunMod(new ModVersionInfo("Sync", "1.7", version, false));
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.initTickHandlers();

        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadHandler());

        hasMorphMod = Loader.isModLoaded("Morph");
        hasCoFHCore = Loader.isModLoaded("CoFHCore");

        FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", "sync.common.tileentity.TileEntityDualVertical" );
        FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", "sync.common.tileentity.TileEntityTreadmill" );
        FMLInterModComms.sendMessage("Waila", "register", "sync.client.HUDHandlerSync.callbackRegister");

        treadmillEntityHashMap.put(EntityWolf.class, 4);
        treadmillEntityHashMap.put(EntityPig.class, 2);
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event)
    {
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSync());
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event)
    {
        Sync.config.resetSession();

        Sync.config.updateSession("hardMode", (Sync.config.getSessionInt("hardcoreMode") == 1 || Sync.config.getSessionInt("hardcoreMode") == 2 && DimensionManager.getWorld(0).getWorldInfo().isHardcoreModeEnabled()) ? 1 : 0);

        mapHardmodeRecipe();
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent event)
    {
        ChunkLoadHandler.shellTickets.clear();
        ShellHandler.syncInProgress.clear();
        ShellHandler.playerShells.clear();
    }

    @EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages())
        {
            if (message.isStringMessage())
            {
                if (message.key.equals("treadmill"))
                {
                    String[] s = message.getStringValue().split(":");
                    if (s.length != 2)
                    {
                        logger.log(Level.WARN, "Invalid IMC treadmill register (incorrect length) received from " + message.getSender());
                    }
                    else
                    {
                        try
                        {
                            String entityClassName = s[0];
                            int entityPower = Integer.valueOf(s[1]);
                            Class entityClass = Class.forName(entityClassName);

                            if (EntityPlayer.class.isAssignableFrom(entityClass)) logger.log(Level.WARN, "Seriously? You're gonna try that?");
                            else {
                                treadmillEntityHashMap.put(entityClass, entityPower);
                                logger.info(String.format("Registered IMC treadmill register from %s for %s with power %s", message.getSender(), entityClassName, entityPower));
                            }
                        } catch (NumberFormatException e)
                        {
                            logger.log(Level.WARN, "Invalid IMC treadmill register (power not integer) received from " + message.getSender());
                        } catch (ClassNotFoundException e)
                        {
                            logger.log(Level.WARN, "Invalid IMC treadmill register (class not found) received from " + message.getSender());
                        }
                    }
                }
            }
        }
    }

    public static void mapHardmodeRecipe()
    {
        List recipes = CraftingManager.getInstance().getRecipeList();
        for(int i = recipes.size() - 1; i >= 0 ; i--)
        {
            if(recipes.get(i) instanceof ShapedRecipes)
            {
                ShapedRecipes recipe = (ShapedRecipes)recipes.get(i);
                if(recipe.getRecipeOutput().isItemEqual(new ItemStack(Sync.itemPlaceholder)))
                {
                    recipes.remove(i);
                }
            }
        }

        GameRegistry.addRecipe(new ItemStack(Sync.itemPlaceholder),
                "DLD", "QEQ", "MRM", 'D', Blocks.daylight_detector, 'L', Blocks.lapis_block, 'Q', Items.quartz, 'E', (Sync.config.getSessionInt("hardMode") == 1 ? Blocks.beacon : Items.ender_pearl), 'M', Items.emerald, 'R', Blocks.redstone_block);
    }
}
