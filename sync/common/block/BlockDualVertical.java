package sync.common.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sync.common.Sync;
import sync.common.item.ChunkLoadHandler;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
				
				ItemStack is = player.getCurrentEquippedItem();
				
				if(is != null && is.getItem() instanceof ItemNameTag)
				{
					if(!is.hasDisplayName())
					{
						dv.name = "";
					}
					else
					{
						dv.name = is.getDisplayName();
					}
					
					if(!player.capabilities.isCreativeMode)
					{
						is.stackSize--;
						if(is.stackSize <= 0)
						{
							player.inventory.mainInventory[player.inventory.currentItem] = null;
						}
					}
					
					world.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
					world.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
					
					if(!world.isRemote)
					{
						ShellHandler.updatePlayerOfShells(player, dv, false);
					}
					return true;
				}
				
//				else if(!ss.playerName.equalsIgnoreCase("") && ss.occupied)
//				{
//					ss.vacating = true;
//					ss.occupationTime = TileEntityShellStorage.animationTime;
//
//					world.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
//					world.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
//					return true;
//				}
			}
		}
		return false;
    }
	
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k)
    {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		TileEntity te = world.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityDualVertical)
		{
			TileEntityDualVertical dv = (TileEntityDualVertical)te;
			if(dv instanceof TileEntityShellConstructor)
			{
				TileEntityShellConstructor sc = (TileEntityShellConstructor)dv;
			}
			else if(dv instanceof TileEntityShellStorage)
			{
				TileEntityShellStorage ss = (TileEntityShellStorage)dv;
				
				if((ss.top && ss.pair != null && ((TileEntityShellStorage)ss.pair).occupied || ss.occupied) && ss.worldObj.isRemote && isPlayer(ss.playerName))
				{
					EntityPlayer ent = Minecraft.getMinecraft().thePlayer;
					
			        double d3 = ent.posX - (i + 0.5D);
			        double d4 = ent.boundingBox.minY - j;
			        double d5 = ent.posZ - (k + 0.5D);
			        double dist = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
					
			        if(dist < (ss.top ? 1.1D : 0.6D))
			        {
			        	this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
			        }
				}
			}
		}
    }
	
	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity ent) 
	{
		if(!(ent instanceof EntityPlayer))
		{
			return;
		}
		
		TileEntity te = world.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityDualVertical)
		{
			TileEntityDualVertical dv = (TileEntityDualVertical)te;
			if(dv.top)
			{
				TileEntity te1 = world.getBlockTileEntity(i, j - 1, k);
				if(te1 instanceof TileEntityDualVertical)
				{
					this.onEntityCollidedWithBlock(world, i, j - 1, k, ent);
				}
			}
			else
			{
				if(dv instanceof TileEntityShellConstructor)
				{
					TileEntityShellConstructor sc = (TileEntityShellConstructor)dv;
				}
				else if(dv instanceof TileEntityShellStorage)
				{
					TileEntityShellStorage ss = (TileEntityShellStorage)dv;
					if(!ss.occupied && !world.isRemote)
					{
				        double d3 = ent.posX - (i + 0.5D);
				        double d4 = ent.boundingBox.minY - j;
				        double d5 = ent.posZ - (k + 0.5D);
				        double dist = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
				        
				        if(dist < 0.3D && ss.isPowered())
				        {
				        	EntityPlayer player = (EntityPlayer)ent;
				        	
				    		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				    		DataOutputStream stream = new DataOutputStream(bytes);
				    		try
				    		{
				    			stream.writeInt(i);
				    			stream.writeInt(j);
				    			stream.writeInt(k);
				    			
				    			PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)3, bytes.toByteArray()), (Player)player);
				    		}
				    		catch(IOException e)
				    		{
				    		}
				    		
				    		player.setLocationAndAngles(i + 0.5D, j, k + 0.5D, (ss.face - 2) * 90F, 0F);
				    		
				    		ss.playerName = player.username;
				    		
				    		ss.occupied = true;
				    		
							world.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
							world.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
				        }
					}
				}
			}
		}
	}
	
	@Override
    public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB aabb, List list, Entity ent)
    {
		TileEntity te = world.getBlockTileEntity(i, j, k);
		if(te instanceof TileEntityDualVertical)
		{
			TileEntityDualVertical dv = (TileEntityDualVertical)te;
			boolean top = false;
			if(dv.top)
			{
				TileEntity te1 = world.getBlockTileEntity(i, j - 1, k);
				if(te1 instanceof TileEntityDualVertical)
				{
					dv = (TileEntityDualVertical)te1;
				}
				top = true;
			}
			
			if(dv instanceof TileEntityShellConstructor)
			{
				TileEntityShellConstructor sc = (TileEntityShellConstructor)dv;
				super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);
			}
			else if(dv instanceof TileEntityShellStorage)
			{
				TileEntityShellStorage ss = (TileEntityShellStorage)dv;
				if((!ss.occupied || (world.isRemote && isPlayer(ss.playerName))) && !ss.syncing)
				{
					float thickness = 0.05F;
					if(ss.face != 0)
					{
						this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, thickness);
						super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);
					}
					if(ss.face != 1)
					{
						this.setBlockBounds(1.0F - thickness, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
						super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);
					}
					if(ss.face != 2)
					{
						this.setBlockBounds(0.0F, 0.0F, 1.0F - thickness, 1.0F, 1.0F, 1.0F);
						super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);
					}
					if(ss.face != 3)
					{
						this.setBlockBounds(0.0F, 0.0F, 0.0F, thickness, 1.0F, 1.0F);
						super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);
					}
					if(top)
					{
						this.setBlockBounds(0.0F, 1.0F - thickness / 2, 0.0F, 1.0F, 1.0F, 1.0F);
						super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);
					}
				}
				else
				{
					super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);	
				}
			}
		}
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
	
	@SideOnly(Side.CLIENT)
	public boolean isPlayer(String playerName)
	{
		return playerName.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.username);
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
			TileEntityDualVertical dv = (TileEntityDualVertical)te;
			TileEntity te1 = world.getBlockTileEntity(i, j + (dv.top ? -1 : 1), k);
			if(te1 instanceof TileEntityDualVertical)
			{
				TileEntityDualVertical dv1 = (TileEntityDualVertical)te1;
				if(dv1.pair == dv)
				{
					world.playAuxSFX(2001, i, j + (dv.top ? -1 : 1), k, Sync.blockShellConstructor.blockID);
					world.setBlockToAir(i, j + (dv.top ? -1 : 1), k);
				}
				if(!world.isRemote)
				{
					ChunkLoadHandler.removeShellAsChunkloader(dv.top ? dv1 : dv);
				}
			}
		}
        super.breakBlock(world, i, j, k, par5, par6);
    }
}
