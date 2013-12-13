package sync.common.core;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import sync.common.Sync;
import sync.common.block.BlockShellConstructor;
import sync.common.tileentity.TileEntityShellConstructor;

public class CommonProxy 
{

	public TickHandlerClient tickHandlerClient;
	
	public void initMod()
	{
		Sync.blockShellConstructor = (new BlockShellConstructor(Sync.idBlockShellConstructor)).setLightValue(0.5F).setHardness(0.5F).setUnlocalizedName("Sync_ShellConstructor");
		
		GameRegistry.registerBlock(Sync.blockShellConstructor, "Sync_ShellConstructor");
		
		GameRegistry.registerTileEntity(TileEntityShellConstructor.class, "Sync_TEShellConstructor");
		
		LanguageRegistry.instance().addName(Sync.blockShellConstructor, "Shell Constructor");
	}

	public void initTickHandlers() 
	{
	}
	
}
