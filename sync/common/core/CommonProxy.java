package sync.common.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import sync.client.core.TickHandlerClient;
import sync.common.Sync;
import sync.common.block.BlockDualVertical;
import sync.common.item.ItemSyncBlockPlacer;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import sync.common.tileentity.TileEntityTreadmill;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy 
{

	public TickHandlerClient tickHandlerClient;
	
	public void initMod()
	{
		Sync.blockDualVertical = (new BlockDualVertical(Sync.idBlockShellConstructor)).setLightValue(0.5F).setHardness(0.5F).setUnlocalizedName("Sync_ShellConstructor");
		
		Sync.itemBlockPlacer = (new ItemSyncBlockPlacer(Sync.idItemBlockPlacer)).setFull3D().setUnlocalizedName("Sync_BlockPlacer").setCreativeTab(CreativeTabs.tabTransport);
		
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
//				new Object[] { new ItemStack(Block.dirt) });
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
//				new Object[] { new ItemStack(Item.stick) });
		
		GameRegistry.registerBlock(Sync.blockDualVertical, "Sync_ShellConstructor");
		GameRegistry.registerItem(Sync.itemBlockPlacer, "Sync_BlockPlacer");
		
		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		GameRegistry.registerTileEntity(TileEntityShellStorage.class, "Sync_TEShellStorage");
		GameRegistry.registerTileEntity(TileEntityTreadmill.class, "Sync_TETreadmill");
		
		LanguageRegistry.instance().addName(Sync.blockDualVertical, "Shell Constructor");
		
		LanguageRegistry.instance().addName(new ItemStack(Sync.itemBlockPlacer, 1, 0), "Shell Constructor");
		LanguageRegistry.instance().addName(new ItemStack(Sync.itemBlockPlacer, 1, 1), "Shell Storage");
		LanguageRegistry.instance().addName(new ItemStack(Sync.itemBlockPlacer, 1, 2), "Treadmill");
		
		LanguageRegistry.instance().addStringLocalization("death.attack.syncFail", "%1$s synced into a dead shell");
		LanguageRegistry.instance().addStringLocalization("death.attack.shellConstruct", "%1$s died trying to create a new shell");
	}

	public void initTickHandlers() 
	{
	}
	
}
