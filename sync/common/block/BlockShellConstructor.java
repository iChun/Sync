package sync.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
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
	public int getRenderType()
	{
		return -1;
	}
	
	@Override
    public int getRenderBlockPass()
    {
        return 0;
    }
	
	@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float hitVecX, float hitVecY, float hitVecZ)
    {
		TileEntity te = world.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityShellConstructor)
		{
			TileEntityShellConstructor sc = (TileEntityShellConstructor)te;
			if(sc.top)
			{
				TileEntity te1 = world.getBlockTileEntity(i, j - 1, k);
				if(te1 instanceof TileEntityShellConstructor)
				{
					sc = (TileEntityShellConstructor)te1;
				}
			}
			if(sc.playerName.equalsIgnoreCase(""))
			{
				sc.playerName = player.username;
				world.markBlockForUpdate(sc.xCoord, sc.yCoord, sc.zCoord);
				world.markBlockForUpdate(sc.xCoord, sc.yCoord + 1, sc.zCoord);
			}
		}
		return false;
    }
	
	@Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int par5)
    {
		TileEntity te = world.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityShellConstructor)
		{
			TileEntityShellConstructor sc = (TileEntityShellConstructor)te;
			if(!sc.top && !world.isBlockOpaqueCube(i, j - 1, k))
			{
				world.setBlockToAir(i, j, k);
			}
		}
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
