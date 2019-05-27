package me.ichun.mods.sync.common.item;

import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.block.EnumType;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
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

public class ItemTreadmill extends Item {

    public ItemTreadmill() {
        maxStackSize = 1;
        setMaxDamage(0);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack stack = player.getHeldItem(hand);

        if (block == Blocks.SNOW_LAYER && (state.getValue(BlockSnow.LAYERS) & 7) < 1)
        {
            facing = EnumFacing.UP;
        }
        else if (block != Blocks.VINE && block != Blocks.TALLGRASS && block != Blocks.DEADBUSH && !block.isReplaceable(world, pos))
        {
            pos = pos.offset(facing);
        }

        if (!player.canPlayerEdit(pos, facing, stack))
        {
            return EnumActionResult.FAIL;
        }
        else if (stack.isEmpty())
        {
            return EnumActionResult.FAIL;
        }

        Block block1 = Sync.blockDualVertical;
        EnumFacing face = EnumFacing.fromAngle(player.rotationYaw);
        BlockPos newBlockPos = pos.offset(face);

        boolean flag = !(world.getTileEntity(pos.down()) instanceof TileEntityTreadmill) && world.mayPlace(block1, pos, false, facing, null) && !(world.getTileEntity(newBlockPos.down()) instanceof TileEntityTreadmill) && world.mayPlace(block1, newBlockPos, false, facing, null);
        if(flag)
        {
            if(world.setBlockState(pos, block1.getDefaultState().withProperty(BlockDualVertical.TYPE, EnumType.TREADMILL), 3) && world.setBlockState(newBlockPos, block1.getDefaultState().withProperty(BlockDualVertical.TYPE, EnumType.TREADMILL), 3))
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
                SoundType soundType = block1.getSoundType(state, world, pos, player);
                world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                stack.shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
