package me.ichun.mods.sync.common.item;

import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.block.EnumType;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemTreadmill extends Item {

    public ItemTreadmill() {
        maxStackSize = 1;
        setMaxDamage(0);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

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
        else if (stack.stackSize == 0)
        {
            return EnumActionResult.FAIL;
        }

        Block block1 = Sync.blockDualVertical;
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
            if(world.setBlockState(pos, block1.getDefaultState().withProperty(BlockDualVertical.TYPE, EnumType.TREADMILL), 3) && world.setBlockState(new BlockPos(ii, pos.getY(), kk), block1.getDefaultState().withProperty(BlockDualVertical.TYPE, EnumType.TREADMILL), 3))
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
        return EnumActionResult.SUCCESS;
    }
}
