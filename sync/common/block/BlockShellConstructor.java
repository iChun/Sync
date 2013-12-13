package sync.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sync.common.Sync;
import sync.common.tileentity.TileEntityShellConstructor;

public class BlockShellConstructor extends BlockContainer 
{

	public BlockShellConstructor(int par1)
	{
		super(par1, Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return new TileEntityShellConstructor();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override 
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
    public void breakBlock(World world, int i, int j, int k, int par5, int par6)
    {
		TileEntity te = world.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityShellConstructor)
		{
			TileEntityShellConstructor sc = (TileEntityShellConstructor)te;
			TileEntity te1 = world.getBlockTileEntity(i, j + (sc.top ? -1 : 1), k);
			if(te1 instanceof TileEntityShellConstructor)
			{
				TileEntityShellConstructor sc1 = (TileEntityShellConstructor)te1;
				if(sc1.pair == sc)
				{
					world.playAuxSFX(2001, i, j + (sc.top ? -1 : 1), k, Sync.blockShellConstructor.blockID);
					world.setBlockToAir(i, j + (sc.top ? -1 : 1), k);
				}
			}
		}
        super.breakBlock(world, i, j, k, par5, par6);
    }
}
