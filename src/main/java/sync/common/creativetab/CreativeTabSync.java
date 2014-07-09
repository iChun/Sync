package sync.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import sync.common.Sync;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabSync extends CreativeTabs 
{

	public CreativeTabSync()
	{
		super("sync");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getTabIconItemIndex()
	{
		return Sync.itemPlaceholder.itemID;
	}
}
