package me.ichun.mods.sync.common.item;

import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.block.EnumType;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemShellBase extends Item {
    public final EnumType type;

    public ItemShellBase(EnumType type) {
        maxStackSize = 1;
        setMaxDamage(0);
        this.type = type;
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block == Blocks.SNOW_LAYER && (state.getValue(BlockSnow.LAYERS) & 7) < 1)
        {
            facing = EnumFacing.UP;
        }
        else if (block != Blocks.VINE && block != Blocks.TALLGRASS && block != Blocks.DEADBUSH && !block.isReplaceable(world, pos))
            pos = pos.offset(facing);

        if (!player.canPlayerEdit(pos, facing, stack))
        {
            return EnumActionResult.FAIL;
        }
        else if (stack.isEmpty())
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            Block block1 = Sync.blockDualVertical;
            boolean flag = world.getBlockState(pos.down()).isOpaqueCube() && world.mayPlace(block1, pos, false, facing, null) && world.mayPlace(block1, pos.up(), false, facing, null);
            if(!flag) {
                pos = pos.up();
                flag = world.getBlockState(pos.down()).isOpaqueCube() && world.mayPlace(block1, pos, false, facing, null) && world.mayPlace(block1, pos.up(), false, facing, null);
            }
            if (flag)
            {
                if (world.setBlockState(pos, block1.getDefaultState().withProperty(BlockDualVertical.TYPE, type), 3) && world.setBlockState(pos.up(), block1.getDefaultState().withProperty(BlockDualVertical.TYPE, type), 3))
                {
                    TileEntity te = world.getTileEntity(pos);
                    TileEntity te1 = world.getTileEntity(pos.up());
                    if(te instanceof TileEntityDualVertical && te1 instanceof TileEntityDualVertical)
                    {
                        TileEntityDualVertical sc = (TileEntityDualVertical)te;
                        TileEntityDualVertical sc1 = (TileEntityDualVertical)te1;

                        EnumFacing face = EnumFacing.fromAngle(player.rotationYaw);

                        sc.setup(sc1, false, face);
                        sc1.setup(sc, true, face);
                    }
                    world.playSound(null, new BlockPos(((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F)), block1.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (block1.getSoundType().getVolume() + 1.0F) / 2.0F, block1.getSoundType().getPitch() * 0.8F);
                    stack.shrink(1);
                }
            }
            return EnumActionResult.SUCCESS;
        }
    }
}
