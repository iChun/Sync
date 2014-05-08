package sync.common.core;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sync.client.core.TickHandlerClient;
import sync.common.Sync;
import sync.common.block.BlockDualVertical;
import sync.common.creativetab.CreativeTabSync;
import sync.common.item.ItemPlaceholder;
import sync.common.item.ItemSyncBlockPlacer;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import sync.common.tileentity.TileEntityTreadmill;

import java.util.Calendar;

public class CommonProxy 
{

	public TickHandlerClient tickHandlerClient;
	
	public void initMod()
	{
		Sync.creativeTabSync = new CreativeTabSync();
		
		Sync.blockDualVertical = (new BlockDualVertical(Sync.idBlockShellConstructor)).setLightValue(0.5F).setHardness(2.0F).setUnlocalizedName("Sync_ShellConstructor");
		
		Sync.itemBlockPlacer = (new ItemSyncBlockPlacer(Sync.idItemBlockPlacer)).setFull3D().setUnlocalizedName("Sync_BlockPlacer").setCreativeTab(Sync.creativeTabSync);
		Sync.itemPlaceholder = (new ItemPlaceholder(Sync.idItemSyncCore)).setUnlocalizedName("Sync_SyncCore").setCreativeTab(Sync.creativeTabSync);
		
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
//				new Object[] { new ItemStack(Block.dirt) });
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
//				new Object[] { new ItemStack(Item.stick) });
		
		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
                "OCO", "GGG", "ORO", 'O', Block.obsidian, 'C', Sync.itemPlaceholder, 'G', Block.thinGlass, 'R', Item.redstone);

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
                "OCO", "GIG", "OPO", 'O', Block.obsidian, 'C', Sync.itemPlaceholder, 'G', Block.thinGlass, 'R', Item.redstone, 'I', Block.blockIron, 'P', Block.pressurePlateIron);

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 2),
                "  D", "CCI", "OOR", 'O', Block.obsidian, 'C', new ItemStack(Block.carpet, 1, 15), 'I', Block.fenceIron, 'D', Block.daylightSensor, 'R', Item.redstone);
		
		GameRegistry.registerBlock(Sync.blockDualVertical, "Sync_ShellConstructor");
		GameRegistry.registerItem(Sync.itemBlockPlacer, "Sync_BlockPlacer");
		GameRegistry.registerItem(Sync.itemPlaceholder, "Sync_ItemPlaceholder");
		
		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		GameRegistry.registerTileEntity(TileEntityShellStorage.class, "Sync_TEShellStorage");
		GameRegistry.registerTileEntity(TileEntityTreadmill.class, "Sync_TETreadmill");
		
		LanguageRegistry.instance().addStringLocalization("itemGroup.sync", "Sync");
		
		LanguageRegistry.addName(Sync.blockDualVertical, "Shell Constructor");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		if(calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) == 25 || calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DAY_OF_MONTH) == 1)
		{
			Sync.isChristmasOrNewYear = true;
		}
	}

	public void initTickHandlers() 
	{
	}
	
}
