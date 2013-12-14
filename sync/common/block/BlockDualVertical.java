package sync.common.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sync.common.Sync;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;

public class BlockDualVertical extends BlockContainer 
{

	public BlockDualVertical(int par1)
	{
		super(par1, Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return new TileEntityShellConstructor();
	}
	
	@Override
    public TileEntity createTileEntity(World world, int metadata)
    {
		switch(metadata)
		{
			case 0:
			{
				return new TileEntityShellConstructor();
			}
			case 1:
			{
				return new TileEntityShellStorage();
			}
		}
        return createNewTileEntity(world);
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
		if(te instanceof TileEntityDualVertical)
		{
			TileEntityDualVertical dv = (TileEntityDualVertical)te;
			if(dv.top)
			{
				TileEntity te1 = world.getBlockTileEntity(i, j - 1, k);
				if(te1 instanceof TileEntityDualVertical)
				{
					dv = (TileEntityDualVertical)te1;
				}
			}
			
			if(dv instanceof TileEntityShellConstructor)
			{
				TileEntityShellConstructor sc = (TileEntityShellConstructor)dv;
				
				if(sc.playerName.equalsIgnoreCase(""))
				{
					sc.playerName = player.username;
					world.markBlockForUpdate(sc.xCoord, sc.yCoord, sc.zCoord);
					world.markBlockForUpdate(sc.xCoord, sc.yCoord + 1, sc.zCoord);
					return true;
				}
			}
			else if(dv instanceof TileEntityShellStorage)
			{
				TileEntityShellStorage ss = (TileEntityShellStorage)dv;
				
				if(ss.playerName.equalsIgnoreCase("") && !ss.occupied)
				{
					ss.playerName = player.username;
					
					ss.occupied = true;
					
					ss.occupationTime = TileEntityShellStorage.animationTime;
					
					world.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
					world.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
					return true;
				}
			}
		}
		return false;
    }
	
	@Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int par5)
    {
		TileEntity te = world.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityDualVertical)
		{
			TileEntityDualVertical sc = (TileEntityDualVertical)te;
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
		if(te instanceof TileEntityDualVertical)
		{
			TileEntityDualVertical sc = (TileEntityDualVertical)te;
			TileEntity te1 = world.getBlockTileEntity(i, j + (sc.top ? -1 : 1), k);
			if(te1 instanceof TileEntityDualVertical)
			{
				TileEntityDualVertical sc1 = (TileEntityDualVertical)te1;
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
