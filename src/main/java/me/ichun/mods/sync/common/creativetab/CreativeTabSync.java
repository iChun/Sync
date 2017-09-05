package me.ichun.mods.sync.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import me.ichun.mods.sync.common.Sync;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		return Sync.itemSyncCore;
	}
}
