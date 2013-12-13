package sync.common;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import sync.common.core.CommonProxy;
import sync.common.core.MapPacketHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "Sync", name = "Sync",
			version = Sync.version
		 		)
@NetworkMod(clientSideRequired = true,
			serverSideRequired = false,
			tinyPacketHandler = MapPacketHandler.class,
			versionBounds = "[0.0.0,0.1.0)"
				)
public class Sync 
{
	public static final String version = "0.0.0";
	
	@Instance("Sync")
	public static Sync instance;
	
	@SidedProxy(clientSide = "sync.client.core.ClientProxy", serverSide = "sync.common.core.CommonProxy")
	public static CommonProxy proxy;
	
	private static Logger logger;
	
	private static Configuration config;
	
	public static int idBlockShellConstructor;
	
	public static Block blockShellConstructor;
	
	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		logger = Logger.getLogger("Sync");
		logger.setParent(FMLLog.getLogger());
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		idBlockShellConstructor = addCommentAndReturnBlockId(config, "ids", "idBlockShellConstructor", "Block ID for the Shell Constructor", 1345);
		
		config.save();
		
		//Following new modding convention, blocks and items are initialized in pre-init
		proxy.initMod();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.initTickHandlers();
	}
	
	@EventHandler
	public void serverStarted(FMLServerStartedEvent event)
	{
	}
	
	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event)
	{
	}
	
	public static int addCommentAndReturnBlockId(Configuration config, String cat, String s, String comment, int i)
	{
		Property prop = config.getBlock(cat, s, i, comment);
		return prop.getInt();
	}
	
	public static int addCommentAndReturnInt(Configuration config, String cat, String s, String comment, int i)
	{
		Property prop = config.get(cat, s, i);
		if(!comment.equalsIgnoreCase(""))
		{
			prop.comment = comment;
		}
		return prop.getInt();
	}
}
