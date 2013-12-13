package sync.common.core;

import sync.common.Sync;
import sync.common.block.BlockShellConstructor;

public class CommonProxy 
{

	public void initMod()
	{
		Sync.blockShellConstructor = (new BlockShellConstructor(Sync.idBlockShellConstructor)).setLightValue(0.5F).setHardness(0.5F).setUnlocalizedName("Sync_ShellConstructor");
	}
}
