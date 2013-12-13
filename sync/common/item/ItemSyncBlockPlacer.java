package sync.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import sync.common.Sync;
import sync.common.tileentity.TileEntityShellConstructor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSyncBlockPlacer extends Item
{
	public ItemSyncBlockPlacer(int par1) 
	{
		super(par1);
		maxStackSize = 1;
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister reg)
	{
		this.itemIcon = reg.registerIcon("sync:shellConstructorPlacer");
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int par1, CreativeTabs tab, List itemList)
    {
        itemList.add(new ItemStack(par1, 1, 0));
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
        	Block block = Sync.blockShellConstructor;
        	boolean flag = world.canPlaceEntityOnSide(block.blockID, i, j, k, false, side, (Entity)null, is) && world.canPlaceEntityOnSide(block.blockID, i, j + 1, k, false, side, (Entity)null, is);
        	if(!flag)
        	{
        		j--;
        		flag = world.canPlaceEntityOnSide(block.blockID, i, j, k, false, side, (Entity)null, is) && world.canPlaceEntityOnSide(block.blockID, i, j + 1, k, false, side, (Entity)null, is);
        	}
            if (flag)
            {
                if (world.setBlock(i, j, k, block.blockID) && world.setBlock(i, j + 1, k, block.blockID))
                {
                	TileEntity te = world.getBlockTileEntity(i, j, k);
                	TileEntity te1 = world.getBlockTileEntity(i, j + 1, k);
                	if(te instanceof TileEntityShellConstructor && te1 instanceof TileEntityShellConstructor)
                	{
                		TileEntityShellConstructor sc = (TileEntityShellConstructor)te;
                		TileEntityShellConstructor sc1 = (TileEntityShellConstructor)te1;

                        int face = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

                        sc.setup(sc1, false, face);
                        sc1.setup(sc, true, face);
                	}
                    world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                    --is.stackSize;
                }
            }

            return true;
        }
    }
}
