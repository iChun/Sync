package sync.common.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import sync.common.Sync;
import sync.common.core.SessionState;
import sync.common.item.ChunkLoadHandler;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import sync.common.tileentity.TileEntityTreadmill;
import cpw.mods.fml.common.FMLCommonHandler;
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
			case 2:
			{
				return new TileEntityTreadmill();
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
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("sync:dvBlockPlaceholder");
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

					if(!world.isRemote)
					{	
						NBTTagCompound tag = new NBTTagCompound();
						
				        EntityPlayerMP dummy = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension), player.getCommandSenderName(), new ItemInWorldManager(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension)));
				        dummy.playerNetServerHandler = ((EntityPlayerMP)player).playerNetServerHandler;
				        
				        dummy.setLocationAndAngles(sc.xCoord + 0.5D, sc.yCoord, sc.zCoord + 0.5D, (sc.face - 2) * 90F, 0F);
				        
				        boolean keepInv = world.getGameRules().getGameRuleBooleanValue("keepInventory");
				        
				        world.getGameRules().setOrCreateGameRule("keepInventory", "false");
				        
				        dummy.clonePlayer(player, false);
				        dummy.dimension = player.dimension;
				        dummy.entityId = player.entityId;

				        world.getGameRules().setOrCreateGameRule("keepInventory", keepInv ? "true" : "false");
						
				        dummy.writeToNBT(tag);
				        
				        tag.setInteger("sync_playerGameMode", ((EntityPlayerMP)player).theItemInWorldManager.getGameType().getID());
				        
						sc.playerNBT = tag;
						
						if(!player.capabilities.isCreativeMode)
						{
							String name = DamageSource.outOfWorld.damageType;
							DamageSource.outOfWorld.damageType = "shellConstruct";
							player.attackEntityFrom(DamageSource.outOfWorld, (float)Sync.damageGivenOnShellConstruction);
							
							DamageSource.outOfWorld.damageType = name;
						}
					}
					
					world.markBlockForUpdate(sc.xCoord, sc.yCoord, sc.zCoord);
					world.markBlockForUpdate(sc.xCoord, sc.yCoord + 1, sc.zCoord);
					return true;
				}
			}
			else if(dv instanceof TileEntityShellStorage)
			{
				TileEntityShellStorage ss = (TileEntityShellStorage)dv;
				
				ItemStack is = player.getCurrentEquippedItem();
				
				if(is != null)
				{
					if(is.getItem() instanceof ItemNameTag)
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
							EntityPlayerMP player1 = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dv.playerName);
							if(player1 != null)
							{
								ShellHandler.updatePlayerOfShells(player1, null, true);
							}
						}
						return true;
					}
					else if(is.getItem() instanceof ItemBed)
					{
						ss.isHomeUnit = !ss.isHomeUnit;
						
						world.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
						world.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
						
						if(!world.isRemote)
						{
							EntityPlayerMP player1 = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dv.playerName);
							if(player1 != null)
							{
								ShellHandler.updatePlayerOfShells(player1, null, true);
							}
						}
						return true;						
					}
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
		else if(te instanceof TileEntityTreadmill)
		{
			TileEntityTreadmill tm = (TileEntityTreadmill)te;
			
			if(tm.back)
			{
				tm = tm.pair;
			}
			
			if(tm != null && tm.latchedEnt == null)
			{
		        double d0 = 7.0D;
		        List list = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)i - d0, (double)j - d0, (double)k - d0, (double)i + d0, (double)j + d0, (double)k + d0));
	
		        if (list != null)
		        {
		            Iterator iterator = list.iterator();
	
		            while (iterator.hasNext())
		            {
		                EntityLiving entityliving = (EntityLiving)iterator.next();
	
		                if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player && !entityliving.isChild() && (entityliving instanceof EntityPig || entityliving instanceof EntityWolf && !((EntityWolf)entityliving).isSitting()))
		                {
		                	if(!world.isRemote)
		                	{
		                		tm.latchedEnt = entityliving;
								tm.latchedHealth = entityliving.getHealth();
								entityliving.setLocationAndAngles(tm.getMidCoord(0), tm.yCoord + 0.175D, tm.getMidCoord(1), (tm.face - 2) * 90F, 0.0F);
								world.markBlockForUpdate(tm.xCoord, tm.yCoord, tm.zCoord);
		                		entityliving.clearLeashed(true, !player.capabilities.isCreativeMode);
		                	}
		                    return true;
		                }
		            }
		        }
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
			        double dist = getDistance(i, j, k);
					
			        if(dist < (ss.top ? 1.1D : 0.6D))
			        {
			        	this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
			        }
				}
			}
		}
		else if(te instanceof TileEntityTreadmill)
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.4F, 1.0F);
		}
    }
	
	@SideOnly(Side.CLIENT)
	public double getDistance(int i, int j, int k)
	{
		EntityPlayer ent = Minecraft.getMinecraft().thePlayer;
		
        double d3 = ent.posX - (i + 0.5D);
        double d4 = ent.boundingBox.minY - j;
        double d5 = ent.posZ - (k + 0.5D);
        
        return (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
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
					if(!ss.occupied && !world.isRemote && !ss.syncing && ss.resyncPlayer <= -10)
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
				TileEntityShellConstructor ss = (TileEntityShellConstructor)dv;
				if(ss.doorOpen)
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
		else if(te instanceof TileEntityTreadmill)
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.175F, 1.0F);
			super.addCollisionBoxesToList(world, i, j, k, aabb, list, ent);
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
		else if(te instanceof TileEntityTreadmill)
		{
			if(!world.isBlockOpaqueCube(i, j - 1, k))
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
					world.playAuxSFX(2001, i, j + (dv.top ? -1 : 1), k, Sync.blockDualVertical.blockID);
					world.setBlockToAir(i, j + (dv.top ? -1 : 1), k);
				}
				TileEntityDualVertical bottom = dv1.top ? dv : dv1;
				
				if(!world.isRemote)
				{
					if(bottom.resyncPlayer > 30 && bottom.resyncPlayer < 60)
					{
						EntityPlayerMP player1 = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dv.playerName);
						if(player1 != null)
						{
							if(dv.playerNBT.hasKey("Inventory"))
							{
								player1.readFromNBT(dv.playerNBT);
							}
							String name = DamageSource.outOfWorld.damageType;
							DamageSource.outOfWorld.damageType = "syncFail";
							player1.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
							
							DamageSource.outOfWorld.damageType = name;
						}
					}
					else if(bottom instanceof TileEntityShellStorage && bottom.resyncPlayer == -10 && ((TileEntityShellStorage)dv).syncing && dv.playerNBT.hasKey("Inventory"))
					{
                        FakePlayer fake = new FakePlayer(world, dv.playerName);
                        fake.readFromNBT(dv.playerNBT);                        
                        fake.setLocationAndAngles(i + 0.5D, j, k + 0.5D, (dv.face - 2) * 90F, 0F);

                        if (!ForgeHooks.onLivingDeath(fake, DamageSource.outOfWorld))
                        {
	                        fake.captureDrops = true;
	                        fake.capturedDrops.clear();
	
	                        if (fake.username.equals("Notch"))
	                        {
	                        	fake.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
	                        }
	
	                        if (!fake.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
	                        {
	                        	fake.inventory.dropAllItems();
	                        }
	
	                        fake.captureDrops = false;
	
                            PlayerDropsEvent event = new PlayerDropsEvent(fake, DamageSource.outOfWorld, fake.capturedDrops, false);
                            if (!MinecraftForge.EVENT_BUS.post(event))
                            {
                                for (EntityItem item : fake.capturedDrops)
                                {
                                	fake.joinEntityItemWithWorld(item);
                                }
                            }
                        }
                        
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						DataOutputStream stream1 = new DataOutputStream(bytes);
						try
						{
							stream1.writeInt(bottom.xCoord);
							stream1.writeInt(bottom.yCoord);
							stream1.writeInt(bottom.zCoord);
							
							stream1.writeInt(bottom.face);
							
							PacketDispatcher.sendPacketToAllAround(bottom.xCoord, bottom.yCoord, bottom.zCoord, 64D, dv.worldObj.provider.dimensionId, new Packet131MapData((short)Sync.getNetId(), (short)8, bytes.toByteArray()));
						}
						catch(IOException e)
						{
						}
					}
					else if(bottom instanceof TileEntityShellConstructor)
					{
						TileEntityShellConstructor sc = (TileEntityShellConstructor)bottom;
						if(!sc.playerName.equalsIgnoreCase("") && sc.constructionProgress >= SessionState.shellConstructionPowerRequirement)
						{
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							DataOutputStream stream1 = new DataOutputStream(bytes);
							try
							{
								stream1.writeInt(bottom.xCoord);
								stream1.writeInt(bottom.yCoord);
								stream1.writeInt(bottom.zCoord);
								
								stream1.writeInt(bottom.face);
								
								PacketDispatcher.sendPacketToAllAround(bottom.xCoord, bottom.yCoord, bottom.zCoord, 64D, dv.worldObj.provider.dimensionId, new Packet131MapData((short)Sync.getNetId(), (short)8, bytes.toByteArray()));
							}
							catch(IOException e)
							{
							}
						}
					}

					if(dv.top)
					{
						float f = 0.5F;
			            double d = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			            EntityItem entityitem = new EntityItem(world, (double)i + d, (double)j + d1, (double)k + d2, new ItemStack(Sync.itemBlockPlacer, 1, world.getBlockMetadata(i, j, k)));
			            entityitem.delayBeforeCanPickup = 10;
			            world.spawnEntityInWorld(entityitem);
					}
					
					ChunkLoadHandler.removeShellAsChunkloader(dv.top ? dv1 : dv);
				}
			}
		}
		else if(te instanceof TileEntityTreadmill)
		{
			TileEntityTreadmill tm = (TileEntityTreadmill)te;
			TileEntity te1 = world.getBlockTileEntity(tm.back ? (tm.face == 1 ? i + 1 : tm.face == 3 ? i - 1 : i) : (tm.face == 1 ? i - 1 : tm.face == 3 ? i + 1 : i), j, tm.back ? (tm.face == 0 ? k - 1 : tm.face == 2 ? k + 1 : k) : (tm.face == 0 ? k + 1 : tm.face == 2 ? k - 1 : k));
			
			if(te1 instanceof TileEntityTreadmill)
			{
				TileEntityTreadmill tm1 = (TileEntityTreadmill)te1;
				if(tm1.pair == tm)
				{
					world.playAuxSFX(2001, tm1.xCoord, tm1.yCoord, tm1.zCoord, Sync.blockDualVertical.blockID);
					world.setBlockToAir(tm1.xCoord, tm1.yCoord, tm1.zCoord);
				}
				
				if(!tm1.back && !world.isRemote)
				{
					float f = 0.5F;
		            double d = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		            EntityItem entityitem = new EntityItem(world, (double)i + d, (double)j + d1, (double)k + d2, new ItemStack(Sync.itemBlockPlacer, 1, world.getBlockMetadata(i, j, k)));
		            entityitem.delayBeforeCanPickup = 10;
		            world.spawnEntityInWorld(entityitem);
				}
			}
		}
        super.breakBlock(world, i, j, k, par5, par6);
    }
	
	@Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return new ItemStack(Sync.itemBlockPlacer, 1, world.getBlockMetadata(x, y, z));
    }
}
