package me.ichun.mods.sync.common.creativetab;

import me.ichun.mods.sync.common.Sync;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CreativeTabSync extends CreativeTabs 
{

	public CreativeTabSync()
	{
		super("sync");
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
    public ItemStack getTabIconItem()
	{
		return new ItemStack(Sync.itemSyncCore);
	}
}
