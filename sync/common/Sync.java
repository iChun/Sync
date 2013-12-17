package sync.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import sync.common.core.CommonProxy;
import sync.common.core.ConnectionHandler;
import sync.common.core.MapPacketHandler;
import sync.common.core.SessionState;
import sync.common.item.ChunkLoadHandler;
import sync.common.shell.ShellHandler;
import cpw.mods.fml.common.FMLCommonHandler;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "Sync", name = "Sync",
			version = Sync.version
		 		)
@NetworkMod(clientSideRequired = true,
			serverSideRequired = false,
			tinyPacketHandler = MapPacketHandler.class,
			connectionHandler = ConnectionHandler.class,
			versionBounds = "[1.0.0,1.1.0)"
				)
public class Sync 
{
	public static final String version = "1.0.0";
	
	@Instance("Sync")
	public static Sync instance;
	
	@SidedProxy(clientSide = "sync.client.core.ClientProxy", serverSide = "sync.common.core.CommonProxy")
	public static CommonProxy proxy;
	
	private static Logger logger;
	
	private static Configuration config;
	
	public static CreativeTabs creativeTabSync;
	
	public static int idBlockShellConstructor;
	public static int idItemBlockPlacer;
	public static int idItemSyncCore;
	
	public static int shellConstructionPowerRequirement;
	
	public static int allowCrossDimensional;
	public static int damageGivenOnShellConstruction;
	public static int overrideDeathIfThereAreAvailableShells;
	public static int prioritizeHomeShellOnDeath;
	public static int crossDimensionalSyncingOnDeath;
	
	public static int hardcoreMode;
	
	public static int showAllShellInfoInGui;
	
	public static Block blockDualVertical;
	
	public static Item itemBlockPlacer;
	public static Item itemPlaceholder;
	
	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		logger = Logger.getLogger("Sync");
		logger.setParent(FMLLog.getLogger());
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		idBlockShellConstructor = addCommentAndReturnBlockId(config, "ids", "idBlockShellConstructor", "Block ID for the Shell Constructor", 1345);
		idItemBlockPlacer = addCommentAndReturnItemId(config, "ids", "idItemBlockPlacer", "Item ID for the Sync's Block Placer", 13330);
		idItemSyncCore = addCommentAndReturnItemId(config, "ids", "idItemSyncCore", "Item ID for the Sync Core", 13331);
		
		shellConstructionPowerRequirement = addCommentAndReturnInt(config, "gameplay", "shellConstructionPowerRequirement", "Power requirement for Shell Construction", 48000); // Dogs power 4, Pigs power... 2?
		
		allowCrossDimensional = addCommentAndReturnInt(config, "gameplay", "allowCrossDimensional", "Allow cross-dimensional shell syncing?\nWARNING: There are issues with going in and out of The End, where you require a relog AFTER syncing because chunks may not load.\nEnable The End travel at your own risk.\n0 = No\n1 = Yes, but not in The End\n2 = Yes, even in the End", 1);
		damageGivenOnShellConstruction = Math.max(addCommentAndReturnInt(config, "gameplay", "damageGivenOnShellConstruction", "Number of half hearts damage given to the player when a new shell is constructed.", 2), 0);
		overrideDeathIfThereAreAvailableShells = addCommentAndReturnInt(config, "gameplay", "overrideDeathIfThereAreAvailableShells", "Allow overriding the death of a player if the player has other shells?\nThe player will resync to the nearest shell.\n0 = No\n1 = Yes, but only to storage units\n2 = Yes, to storage and construction units", 1);
		prioritizeHomeShellOnDeath = addCommentAndReturnInt(config, "gameplay", "prioritizeHomeShellOnDeath", "Prioritize \"Home\" Shells when a player dies and resyncs?\n0 = No\n1 = Yes", 1);
		crossDimensionalSyncingOnDeath = addCommentAndReturnInt(config, "gameplay", "crossDimensionalSyncingOnDeath", "Allow cross dimensional syncing when a player dies and resyncs?\n0 = No\n1 = Yes", 1);
		
		hardcoreMode = addCommentAndReturnInt(config, "gameplay", "hardcoreMode", "Enable hardcore mode recipes?\n0 = No\n1 = Yes\n2 = Yes, but only on actual Hardcore mode.", 2);
		
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			showAllShellInfoInGui = addCommentAndReturnInt(config, "clientOnly", "showAllShellInfoInGui", "Show info of all shells in the GUI?\n0 = No\n1 = Just the name will do\n2 = Yes, in all full blown glory!", 1);
		}
		
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
		SessionState.allowCrossDimensional = allowCrossDimensional;
		SessionState.deathMode = overrideDeathIfThereAreAvailableShells;
		SessionState.hardMode = hardcoreMode == 1 || hardcoreMode == 2 && DimensionManager.getWorld(0).getWorldInfo().isHardcoreModeEnabled();
		
		mapHardmodeRecipe();
	}
	
	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event)
	{
		ChunkLoadHandler.shellTickets.clear();
		ShellHandler.deathRespawns.clear();
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
				new Object[] { "DLD", "QEQ", "MRM", Character.valueOf('D'), Block.daylightSensor, Character.valueOf('L'), Block.blockLapis, Character.valueOf('Q'), Item.netherQuartz, Character.valueOf('E'), (SessionState.hardMode ? Block.beacon : Item.enderPearl), Character.valueOf('M'), Item.emerald, Character.valueOf('R'), Block.blockRedstone});
	}
}
