package sync.common.core;

import net.minecraft.creativetab.CreativeTabs;
import sync.client.core.TickHandlerClient;
import sync.common.Sync;
import sync.common.block.BlockShellConstructor;
import sync.common.item.ItemSyncBlockPlacer;
import sync.common.tileentity.TileEntityShellConstructor;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy 
{

	public TickHandlerClient tickHandlerClient;
	
	public void initMod()
	{
		Sync.blockShellConstructor = (new BlockShellConstructor(Sync.idBlockShellConstructor)).setLightValue(0.5F).setHardness(0.5F).setUnlocalizedName("Sync_ShellConstructor");
		
		Sync.itemBlockPlacer = (new ItemSyncBlockPlacer(Sync.idItemBlockPlacer)).setUnlocalizedName("Sync_BlockPlacer").setCreativeTab(CreativeTabs.tabTransport);
		
		GameRegistry.registerBlock(Sync.blockShellConstructor, "Sync_ShellConstructor");
		GameRegistry.registerItem(Sync.itemBlockPlacer, "Sync_BlockPlacer");
		
		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		
		LanguageRegistry.instance().addName(Sync.blockShellConstructor, "Shell Constructor");
		
		LanguageRegistry.instance().addName(Sync.itemBlockPlacer, "This isn't read anyways :(");
	}

	public void initTickHandlers() 
	{
	}
	
}
