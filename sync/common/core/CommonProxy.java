package sync.common.core;

import java.util.Calendar;

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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy 
{

	public TickHandlerClient tickHandlerClient;
	
	public void initMod()
	{
		Sync.creativeTabSync = new CreativeTabSync("sync");
		
		Sync.blockDualVertical = (new BlockDualVertical(Sync.idBlockShellConstructor)).setLightValue(0.5F).setHardness(2.0F).setUnlocalizedName("Sync_ShellConstructor");
		
		Sync.itemBlockPlacer = (new ItemSyncBlockPlacer(Sync.idItemBlockPlacer)).setFull3D().setUnlocalizedName("Sync_BlockPlacer").setCreativeTab(Sync.creativeTabSync);
		Sync.itemPlaceholder = (new ItemPlaceholder(Sync.idItemSyncCore)).setUnlocalizedName("Sync_SyncCore").setCreativeTab(Sync.creativeTabSync);
		
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
//				new Object[] { new ItemStack(Block.dirt) });
//		GameRegistry.addShapelessRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
//				new Object[] { new ItemStack(Item.stick) });
		
		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 0),
				new Object[] { "OCO", "GGG", "ORO", Character.valueOf('O'), Block.obsidian, Character.valueOf('C'), Sync.itemPlaceholder, Character.valueOf('G'), Block.thinGlass, Character.valueOf('R'), Item.redstone});

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 1),
				new Object[] { "OCO", "GIG", "OPO", Character.valueOf('O'), Block.obsidian, Character.valueOf('C'), Sync.itemPlaceholder, Character.valueOf('G'), Block.thinGlass, Character.valueOf('R'), Item.redstone, Character.valueOf('I'), Block.blockIron, Character.valueOf('P'), Block.pressurePlateIron});

		GameRegistry.addRecipe(new ItemStack(Sync.itemBlockPlacer, 1, 2),
				new Object[] { "  D", "CCI", "OOR", Character.valueOf('O'), Block.obsidian, Character.valueOf('C'), new ItemStack(Block.carpet, 1, 15), Character.valueOf('I'), Block.fenceIron, Character.valueOf('D'), Block.daylightSensor, Character.valueOf('R'), Item.redstone});
		
		GameRegistry.registerBlock(Sync.blockDualVertical, "Sync_ShellConstructor");
		GameRegistry.registerItem(Sync.itemBlockPlacer, "Sync_BlockPlacer");
		GameRegistry.registerItem(Sync.itemPlaceholder, "Sync_ItemPlaceholder");
		
		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		GameRegistry.registerTileEntity(TileEntityShellStorage.class, "Sync_TEShellStorage");
		GameRegistry.registerTileEntity(TileEntityTreadmill.class, "Sync_TETreadmill");
		
		LanguageRegistry.instance().addStringLocalization("itemGroup.sync", "Sync");
		
		LanguageRegistry.instance().addName(Sync.blockDualVertical, "Shell Constructor");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		if(calendar.get(2) + 1 == 12 && calendar.get(5) == 25 || calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
		{
			Sync.isChristmasOrNewYear = true;
		}
	}

	public void initTickHandlers() 
	{
	}
	
}
