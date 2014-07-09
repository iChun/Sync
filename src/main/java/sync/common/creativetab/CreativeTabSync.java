package sync.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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
    public Item getTabIconItem()
	{
		return Sync.itemPlaceholder;
	}
}
