package sync.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import sync.common.Sync;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityTreadmill;

import java.util.List;

public class ItemSyncBlockPlacer extends Item
{
	public ItemSyncBlockPlacer()
	{
		super();
		maxStackSize = 1;
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister reg)
	{
		this.itemIcon = reg.registerIcon("sync:shellConstructorPlacer");
	}
	
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List itemList)
    {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
    }
    
    @Override
    public String getUnlocalizedName(ItemStack is)
    {
    	if(is.getItemDamage() == 1)
    	{
    		return "item.Sync_ShellStorage";
    	}
    	else if(is.getItemDamage() == 2)
    	{
    		return "item.Sync_Treadmill";
    	}
        return "item.Sync_ShellConstructor";
    }
    
	@Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float hitVecX, float hitVecY, float hitVecZ)
    {
        Block block = par3World.getBlock(par4, par5, par6);

        if (block == Blocks.snow_layer && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(par3World, par4, par5, par6))
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }

        if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else
        {
        	Block block1 = Sync.blockDualVertical;
        	if(par1ItemStack.getItemDamage() == 2)
        	{
                int face = MathHelper.floor_double((double)(par2EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

                //0 = +Z
                //1 = -X
                //2 = -Z
                //3 = +X
                
                int ii = face == 1 ? par4 - 1 : face == 3 ? par4 + 1 : par4;
                int kk = face == 0 ? par6 + 1 : face == 2 ? par6 - 1 : par6;
                
                boolean flag = !(par3World.getTileEntity(par4, par5 - 1, par6) instanceof TileEntityTreadmill) && par3World.canPlaceEntityOnSide(block1, par4, par5, par6, false, par7, null, par1ItemStack) && !(par3World.getTileEntity(ii, par5 - 1, kk) instanceof TileEntityTreadmill) && par3World.canPlaceEntityOnSide(block1, ii, par5, kk, false, par7, null, par1ItemStack);
                if(flag)
                {
                	if(par3World.setBlock(par4, par5, par6, block1, par1ItemStack.getItemDamage(), 3) && par3World.setBlock(ii, par5, kk, block1, par1ItemStack.getItemDamage(), 3))
                	{
	                	TileEntity te = par3World.getTileEntity(par4, par5, par6);
	                	TileEntity te1 = par3World.getTileEntity(ii, par5, kk);

	                	if(te instanceof TileEntityTreadmill && te1 instanceof TileEntityTreadmill)
	                	{
	                		TileEntityTreadmill sc = (TileEntityTreadmill)te;
	                		TileEntityTreadmill sc1 = (TileEntityTreadmill)te1;
	
	                        sc.setup(sc1, false, face);
	                        sc1.setup(sc, true, face);
	                	}
	                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), block1.stepSound.func_150496_b(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);
	                    --par1ItemStack.stackSize;
                	}
                }
        	}
        	else
        	{
	        	boolean flag = par3World.getBlock(par4, par5 - 1, par6).isOpaqueCube() && par3World.canPlaceEntityOnSide(block1, par4, par5, par6, false, par7, null, par1ItemStack) && par3World.canPlaceEntityOnSide(block1, par4, par5 + 1, par6, false, par7, null, par1ItemStack);
	        	if(!flag)
	        	{
	        		par5--;
	        		flag = par3World.getBlock(par4, par5 - 1, par6).isOpaqueCube() && par3World.canPlaceEntityOnSide(block1, par4, par5, par6, false, par7, null, par1ItemStack) && par3World.canPlaceEntityOnSide(block1, par4, par5 + 1, par6, false, par7, null, par1ItemStack);
	        	}
	            if (flag)
	            {
	                if (par3World.setBlock(par4, par5, par6, block1, par1ItemStack.getItemDamage(), 3) && par3World.setBlock(par4, par5 + 1, par6, block1, par1ItemStack.getItemDamage(), 3))
	                {
	                	TileEntity te = par3World.getTileEntity(par4, par5, par6);
	                	TileEntity te1 = par3World.getTileEntity(par4, par5 + 1, par6);
	                	if(te instanceof TileEntityDualVertical && te1 instanceof TileEntityDualVertical)
	                	{
	                		TileEntityDualVertical sc = (TileEntityDualVertical)te;
	                		TileEntityDualVertical sc1 = (TileEntityDualVertical)te1;
	
	                        int face = MathHelper.floor_double((double)(par2EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	
	                        sc.setup(sc1, false, face);
	                        sc1.setup(sc, true, face);
	                	}
	                    par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), block1.stepSound.func_150496_b(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);
	                    --par1ItemStack.stackSize;
	                }
	            }
        	}
            return true;
        }
    }
}
