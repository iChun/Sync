package me.ichun.mods.sync.common;

import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.sync.client.HUDHandlerTheOneProbe;
import me.ichun.mods.sync.client.core.EventHandlerClient;
import me.ichun.mods.sync.common.core.*;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod(modid = Sync.MOD_ID, name = Sync.MOD_NAME,
        version = Sync.VERSION,
        guiFactory = iChunUtil.GUI_CONFIG_FACTORY,
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR + ".0.1," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0);after:waila",
        acceptableRemoteVersions = "[" + iChunUtil.VERSION_MAJOR +".0.0," + iChunUtil.VERSION_MAJOR + ".1.0)",
        acceptedMinecraftVersions = iChunUtil.MC_VERSION_RANGE
)
public class Sync
{
    public static final String MOD_NAME = "Sync";
    public static final String MOD_ID = "sync";
    public static final String VERSION = iChunUtil.VERSION_MAJOR +".0.0";

    @Instance(MOD_ID)
    public static Sync instance;

    @SidedProxy(clientSide = "me.ichun.mods.sync.client.core.ProxyClient", serverSide = "me.ichun.mods.sync.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final HashMap<Class, Integer> TREADMILL_ENTITY_HASH_MAP = new HashMap<>();

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient;

    public static CreativeTabs creativeTabSync;

    public static Config config;

    public static Block blockDualVertical;

    public static PacketChannel channel;
    public static Item itemShellConstructor, itemShellStorage, itemTreadmill;

    public static Item itemSyncCore;

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInitMod();

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, false));

        FMLInterModComms.sendMessage("backtools", "blacklist", new ItemStack(itemShellConstructor, 1));
        FMLInterModComms.sendMessage("backtools", "blacklist", new ItemStack(itemShellStorage, 1));
        FMLInterModComms.sendMessage("backtools", "blacklist", new ItemStack(itemTreadmill, 1));
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadHandler());

        FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", TileEntityDualVertical.class.getName());
        FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", TileEntityTreadmill.class.getName());
        FMLInterModComms.sendMessage("waila", "register", "me.ichun.mods.sync.client.HUDHandlerWaila.callbackRegister");
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "me.ichun.mods.sync.client.HUDHandlerTheOneProbe");

        TREADMILL_ENTITY_HASH_MAP.put(EntityWolf.class, 4);
        TREADMILL_ENTITY_HASH_MAP.put(EntityPig.class, 2);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSync());
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
                        LOGGER.log(Level.WARN, "Invalid IMC treadmill register (incorrect length) received from " + message.getSender());
                    }
                    else
                    {
                        try
                        {
                            String entityClassName = s[0];
                            int entityPower = Integer.valueOf(s[1]);
                            Class entityClass = Class.forName(entityClassName);

                            if (EntityPlayer.class.isAssignableFrom(entityClass)) LOGGER.log(Level.WARN, "Seriously? You're gonna try that?");
                            else {
                                TREADMILL_ENTITY_HASH_MAP.put(entityClass, entityPower);
                                LOGGER.info(String.format("Registered IMC treadmill register from %s for %s with power %s", message.getSender(), entityClassName, entityPower));
                            }
                        } catch (NumberFormatException e)
                        {
                            LOGGER.log(Level.WARN, "Invalid IMC treadmill register (power not integer) received from " + message.getSender());
                        } catch (ClassNotFoundException e)
                        {
                            LOGGER.log(Level.WARN, "Invalid IMC treadmill register (class not found) received from " + message.getSender());
                        }
                    }
                }
            }
        }
    }
}
