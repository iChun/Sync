package sync.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
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
    public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int i, int j, int k, int side, float hitVecX, float hitVecY, float hitVecZ)
    {
        int i1 = world.getBlockId(i, j, k);

        if (i1 == Block.snow.blockID && (world.getBlockMetadata(i, j, k) & 7) < 1)
        {
            side = 1;
        }
        else if (i1 != Block.vine.blockID && i1 != Block.tallGrass.blockID && i1 != Block.deadBush.blockID)
        {
            if (side == 0)
            {
                --j;
            }

            if (side == 1)
            {
                ++j;
            }

            if (side == 2)
            {
                --k;
            }

            if (side == 3)
            {
                ++k;
            }

            if (side == 4)
            {
                --i;
            }

            if (side == 5)
            {
                ++i;
            }
        }

        if (!player.canPlayerEdit(i, j, k, side, is))
        {
            return false;
        }
        else if (is.stackSize == 0)
        {
            return false;
        }
        else
        {
        	Block block = Sync.blockDualVertical;
        	if(is.getItemDamage() == 2)
        	{
                int face = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

                //0 = +Z
                //1 = -X
                //2 = -Z
                //3 = +X
                
                int ii = face == 1 ? i - 1 : face == 3 ? i + 1 : i;
                int kk = face == 0 ? k + 1 : face == 2 ? k - 1 : k;
                
                boolean flag = !(world.getTileEntity(i, j - 1, k) instanceof TileEntityTreadmill) && world.canPlaceEntityOnSide(block.blockID, i, j, k, false, side, null, is) && !(world.getBlockTileEntity(ii, j - 1, kk) instanceof TileEntityTreadmill) && world.canPlaceEntityOnSide(block.blockID, ii, j, kk, false, side, null, is);
                if(flag)
                {
                	if(world.setBlock(i, j, k, block.blockID, is.getItemDamage(), 3) && world.setBlock(ii, j, kk, block.blockID, is.getItemDamage(), 3))
                	{
	                	TileEntity te = world.getTileEntity(i, j, k);
	                	TileEntity te1 = world.getTileEntity(ii, j, kk);

	                	if(te instanceof TileEntityTreadmill && te1 instanceof TileEntityTreadmill)
	                	{
	                		TileEntityTreadmill sc = (TileEntityTreadmill)te;
	                		TileEntityTreadmill sc1 = (TileEntityTreadmill)te1;
	
	                        sc.setup(sc1, false, face);
	                        sc1.setup(sc, true, face);
	                	}
	                    world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
	                    --is.stackSize;
                	}
                }
        	}
        	else
        	{
	        	boolean flag = world.isBlockOpaqueCube(i, j - 1, k) && world.canPlaceEntityOnSide(block.blockID, i, j, k, false, side, null, is) && world.canPlaceEntityOnSide(block.blockID, i, j + 1, k, false, side, null, is);
	        	if(!flag)
	        	{
	        		j--;
	        		flag = world.isBlockOpaqueCube(i, j - 1, k) && world.canPlaceEntityOnSide(block.blockID, i, j, k, false, side, null, is) && world.canPlaceEntityOnSide(block.blockID, i, j + 1, k, false, side, null, is);
	        	}
	            if (flag)
	            {
	                if (world.setBlock(i, j, k, block.blockID, is.getItemDamage(), 3) && world.setBlock(i, j + 1, k, block.blockID, is.getItemDamage(), 3))
	                {
	                	TileEntity te = world.getTileEntity(i, j, k);
	                	TileEntity te1 = world.getTileEntity(i, j + 1, k);
	                	if(te instanceof TileEntityDualVertical && te1 instanceof TileEntityDualVertical)
	                	{
	                		TileEntityDualVertical sc = (TileEntityDualVertical)te;
	                		TileEntityDualVertical sc1 = (TileEntityDualVertical)te1;
	
	                        int face = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	
	                        sc.setup(sc1, false, face);
	                        sc1.setup(sc, true, face);
	                	}
	                    world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
	                    --is.stackSize;
	                }
	            }
        	}
            return true;
        }
    }
}
