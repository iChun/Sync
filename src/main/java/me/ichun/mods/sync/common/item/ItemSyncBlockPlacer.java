package me.ichun.mods.sync.common.item;

import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.block.EnumType;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper; import net.minecraft.world.World;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;

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
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        subItems.add(new ItemStack(itemIn, 1, 0));
        subItems.add(new ItemStack(itemIn, 1, 1));
        subItems.add(new ItemStack(itemIn, 1, 2));
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
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        EnumType TYPE = EnumType.getByID(stack.getItemDamage());
		System.out.println(stack.getItemDamage());

        if (block == Blocks.SNOW_LAYER && (state.getValue(BlockSnow.LAYERS) & 7) < 1)
        {
//            par7 = 1;
        }
        else if (block != Blocks.VINE && block != Blocks.TALLGRASS && block != Blocks.DEADBUSH && !block.isReplaceable(world, pos))
        {
//            if (par7 == 0) TODO I think par7 is side, figure out what value is what side
//            {
//                --pos.getY();
//            }
//
//            if (par7 == 1)
//            {
//                ++pos.getY();
//            }
//
//            if (par7 == 2)
//            {
//                --pos.getZ();
//            }
//
//            if (par7 == 3)
//            {
//                ++pos.getZ();
//            }
//
//            if (par7 == 4)
//            {
//                --pos.getX();
//            }
//
//            if (par7 == 5)
//            {
//                ++pos.getX();
//            }
        }

        if (!player.canPlayerEdit(pos, facing, stack))
        {
            return EnumActionResult.FAIL;
        }
        else if (stack.stackSize == 0)
        {
            return EnumActionResult.FAIL;
        }
        else
        {
        	Block block1 = Sync.blockDualVertical;
        	if(stack.getItemDamage() == 2)
        	{
                int face = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

                //0 = +Z
                //1 = -X
                //2 = -Z
                //3 = +X
                
                int ii = face == 1 ? pos.getX() - 1 : face == 3 ? pos.getX() + 1 : pos.getX();
                int kk = face == 0 ? pos.getZ() + 1 : face == 2 ? pos.getZ() - 1 : pos.getZ();
                BlockPos newBlockPos = new BlockPos(ii, pos.getY(), kk);
                
                boolean flag = !(world.getTileEntity(pos.down()) instanceof TileEntityTreadmill) && world.canBlockBePlaced(block1, pos, false, facing, null, stack) && !(world.getTileEntity(newBlockPos.down()) instanceof TileEntityTreadmill) && world.canBlockBePlaced(block1, newBlockPos, false, facing, null, stack);
                if(flag)
                {
                	if(world.setBlockState(pos, block1.getDefaultState().withProperty(BlockDualVertical.TYPE, TYPE), 3) && world.setBlockState(new BlockPos(ii, pos.getY(), kk), block1.getDefaultState().withProperty(BlockDualVertical.TYPE, TYPE), 3))
                	{
	                	TileEntity te = world.getTileEntity(pos);
	                	TileEntity te1 = world.getTileEntity(newBlockPos);

	                	if(te instanceof TileEntityTreadmill && te1 instanceof TileEntityTreadmill)
	                	{
	                		TileEntityTreadmill sc = (TileEntityTreadmill)te;
	                		TileEntityTreadmill sc1 = (TileEntityTreadmill)te1;
	
	                        sc.setup(sc1, false, face);
	                        sc1.setup(sc, true, face);
	                	}
	                    world.playSound(null, new BlockPos(((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F)), block1.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (block1.getSoundType().getVolume() + 1.0F) / 2.0F, block1.getSoundType().getPitch() * 0.8F);
	                    --stack.stackSize;
                	}
                }
        	}
        	else
        	{
	        	boolean flag = world.getBlockState(pos.down()).isOpaqueCube() && world.canBlockBePlaced(block1, pos, false, facing, null, stack) && world.canBlockBePlaced(block1, pos.up(), false, facing, null, stack);
	        	if(!flag)
	        	{
	        		pos.down();
	        		flag = world.getBlockState(pos.down()).isOpaqueCube() && world.canBlockBePlaced(block1, pos, false, facing, null, stack) && world.canBlockBePlaced(block1, pos.up(), false, facing, null, stack);
	        	}
	            if (flag)
	            {
	                if (world.setBlockState(pos, block1.getDefaultState().withProperty(BlockDualVertical.TYPE, TYPE), 3) && world.setBlockState(pos.up(), block1.getDefaultState().withProperty(BlockDualVertical.TYPE, TYPE), 3))
	                {
	                	TileEntity te = world.getTileEntity(pos);
	                	TileEntity te1 = world.getTileEntity(pos.up());
	                	if(te instanceof TileEntityDualVertical && te1 instanceof TileEntityDualVertical)
	                	{
	                		TileEntityDualVertical sc = (TileEntityDualVertical)te;
	                		TileEntityDualVertical sc1 = (TileEntityDualVertical)te1;
	
	                        int face = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	
	                        sc.setup(sc1, false, face);
	                        sc1.setup(sc, true, face);
	                	}
	                    world.playSound(null, new BlockPos(((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F)), block1.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (block1.getSoundType().getVolume() + 1.0F) / 2.0F, block1.getSoundType().getPitch() * 0.8F);
	                    --stack.stackSize;
	                }
	            }
        	}
            return EnumActionResult.SUCCESS;
        }
    }
}
