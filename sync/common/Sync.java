package sync.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import sync.common.core.CommonProxy;
import sync.common.core.MapPacketHandler;
import sync.common.core.SessionState;
import sync.common.item.ChunkLoadHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkModHandler;

@Mod(modid = "Sync", name = "Sync",
			version = Sync.version
		 		)
@NetworkMod(clientSideRequired = true,
			serverSideRequired = false,
			tinyPacketHandler = MapPacketHandler.class,
			connectionHandler = ConnectionHandler.class,
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
	public static int idItemBlockPlacer;
	
	public static int shellConstructionPowerRequirement;
	
	public static Block blockShellConstructor;
	
	public static Item itemBlockPlacer;
	
	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		logger = Logger.getLogger("Sync");
		logger.setParent(FMLLog.getLogger());
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		idBlockShellConstructor = addCommentAndReturnBlockId(config, "ids", "idBlockShellConstructor", "Block ID for the Shell Constructor", 1345);
		idItemBlockPlacer = addCommentAndReturnItemId(config, "ids", "idItemBlockPlacer", "Item ID for the Sync's Block Placer", 13330);
		
		shellConstructionPowerRequirement = addCommentAndReturnInt(config, "gameplay", "shellConstructionPowerRequirement", "Power requirement for Shell Construction", 48000); // Dogs power 4, Pigs power... 2?
		
		config.save();
		
		MinecraftForge.EVENT_BUS.register(new sync.common.core.EventHandler());
		
		//Following new modding convention, blocks and items are initialized in pre-init
		proxy.initMod();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.initTickHandlers();
		
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadHandler());
	}
	
	@EventHandler
	public void serverStarted(FMLServerStartedEvent event)
	{
		SessionState.shellConstructionPowerRequirement = shellConstructionPowerRequirement;
	}
	
	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event)
	{
		ChunkLoadHandler.shellTickets.clear();
	}
	
	public static int addCommentAndReturnBlockId(Configuration config, String cat, String s, String comment, int i)
	{
		Property prop = config.getBlock(cat, s, i, comment);
		return prop.getInt();
	}
	
	public static int addCommentAndReturnItemId(Configuration config, String cat, String s, String comment, int i)
	{
		Property prop = config.getItem(cat, s, i, comment);
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
	
	public static int getNetId()
	{
		return ((NetworkModHandler)FMLNetworkHandler.instance().findNetworkModHandler(Sync.instance)).getNetworkId();
	}
	
    public static NBTTagCompound readNBTTagCompound(DataInput par0DataInput) throws IOException
    {
        short short1 = par0DataInput.readShort();

        if (short1 < 0)
        {
            return null;
        }
        else
        {
            byte[] abyte = new byte[short1];
            par0DataInput.readFully(abyte);
            return CompressedStreamTools.decompress(abyte);
        }
    }

    public static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException
    {
        if (par0NBTTagCompound == null)
        {
            par1DataOutput.writeShort(-1);
        }
        else
        {
            byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
            par1DataOutput.writeShort((short)abyte.length);
            par1DataOutput.write(abyte);
        }
    }
}
