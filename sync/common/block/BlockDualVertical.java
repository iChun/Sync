package sync.common.block;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import sync.common.Sync;
import sync.common.core.MapPacketHandler;
import sync.common.core.SessionState;
import sync.common.network.FakeNetServerHandler;
import sync.common.network.FakeNetworkManager;
import sync.common.shell.ShellHandler;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;
import sync.common.tileentity.TileEntityTreadmill;

import java.util.List;
import java.util.Random;

public class BlockDualVertical extends BlockContainer {

	public static int renderPass;

	public BlockDualVertical(int id) {
		super(id, Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world)  {
		return new TileEntityShellConstructor();
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metadata) {
			case 0: {
				return new TileEntityShellConstructor();
			}
			case 1: {
				return new TileEntityShellStorage();
			}
			case 2: {
				return new TileEntityTreadmill();
			}
			default: {
				return this.createNewTileEntity(world);
			}
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override 
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("sync:dvBlockPlaceholder");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitVecX, float hitVecY, float hitVecZ) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDualVertical && !(player instanceof FakePlayer)) {
			TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
			if (dualVertical.top) {
				TileEntity tileEntityPair = world.getBlockTileEntity(x, y - 1, z);
				if (tileEntityPair instanceof TileEntityDualVertical) {
					dualVertical = (TileEntityDualVertical) tileEntityPair;
				}
			}

			//Shell Constructor
			if (dualVertical instanceof TileEntityShellConstructor) {
				TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) dualVertical;

				//If nothing is there
				if (shellConstructor.getPlayerName().equalsIgnoreCase("")) {
					if (Sync.hasMorphMod && morph.api.Api.hasMorph(player.username, false)) {
						player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("sync.isMorphed"));
						return true;
					}
					shellConstructor.setPlayerName(player.getCommandSenderName());

					if (!world.isRemote && !player.capabilities.isCreativeMode) {
						String name = DamageSource.outOfWorld.damageType;
						DamageSource.outOfWorld.damageType = "shellConstruct";
						player.attackEntityFrom(DamageSource.outOfWorld, (float)Sync.damageGivenOnShellConstruction);
						DamageSource.outOfWorld.damageType = name;
					}

					world.markBlockForUpdate(shellConstructor.xCoord, shellConstructor.yCoord, shellConstructor.zCoord);
					world.markBlockForUpdate(shellConstructor.xCoord, shellConstructor.yCoord + 1, shellConstructor.zCoord);
					return true;
				}
				else if (shellConstructor.getPlayerName().equalsIgnoreCase(player.getCommandSenderName()) && player.capabilities.isCreativeMode) {
					if (!world.isRemote) {
						shellConstructor.constructionProgress = SessionState.shellConstructionPowerRequirement;
						ShellHandler.updatePlayerOfShells(player, null, true);
						world.markBlockForUpdate(shellConstructor.xCoord, shellConstructor.yCoord, shellConstructor.zCoord);
						world.markBlockForUpdate(shellConstructor.xCoord, shellConstructor.yCoord + 1, shellConstructor.zCoord);
					}
					return true;
				}

			}
			//Shell Storage
			else if (dualVertical instanceof TileEntityShellStorage) {
				TileEntityShellStorage shellStorage = (TileEntityShellStorage) dualVertical;
				ItemStack itemStack = player.getCurrentEquippedItem();

				if (itemStack != null) {
					//Set storage name
					if (itemStack.getItem() instanceof ItemNameTag) {
						//Don't do anything if name is the same
						if ((!itemStack.hasDisplayName() && dualVertical.getName().equalsIgnoreCase("")) || (itemStack.hasDisplayName() && dualVertical.getName().equalsIgnoreCase(itemStack.getDisplayName()))) {
							return false;
						}
						dualVertical.setName(itemStack.hasDisplayName() ? itemStack.getDisplayName() : "");

						if (!player.capabilities.isCreativeMode && itemStack.stackSize-- <= 0) {
							player.setCurrentItemOrArmor(0, null);
						}
						world.markBlockForUpdate(shellStorage.xCoord, shellStorage.yCoord, shellStorage.zCoord);
						world.markBlockForUpdate(shellStorage.xCoord, shellStorage.yCoord + 1, shellStorage.zCoord);

						if (!world.isRemote) {
							EntityPlayerMP entityPlayerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dualVertical.getPlayerName());
							if (entityPlayerMP != null) {
								ShellHandler.updatePlayerOfShells(entityPlayerMP, null, true);
							}
						}
						return true;
					}
					//Set Home Shell
					else if (itemStack.getItem() instanceof ItemBed) {
						//Changes state
						shellStorage.isHomeUnit = !shellStorage.isHomeUnit;

						world.markBlockForUpdate(shellStorage.xCoord, shellStorage.yCoord, shellStorage.zCoord);
						world.markBlockForUpdate(shellStorage.xCoord, shellStorage.yCoord + 1, shellStorage.zCoord);

						if (!world.isRemote) {
							EntityPlayerMP entityPlayerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dualVertical.getPlayerName());
							if (entityPlayerMP != null) {
								ShellHandler.updatePlayerOfShells(entityPlayerMP, null, true);
							}
						}
						return true;						
					}
				}
			}
		}
		else if (tileEntity instanceof TileEntityTreadmill) {
			TileEntityTreadmill treadmill = (TileEntityTreadmill)tileEntity;

			if (treadmill.back) {
				treadmill = treadmill.pair;
			}

			if (treadmill != null && treadmill.latchedEnt == null) {
				double radius = 7D; //Radius
				List list = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)x - radius, (double)y - radius, (double)z - radius, (double)x + radius, (double)y + radius, (double)z + radius));

				if (list != null && !list.isEmpty()) {
                    for (Object obj : list) {
                        EntityLiving entityliving = (EntityLiving) obj;
                        if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player && TileEntityTreadmill.isEntityValidForTreadmill(entityliving)) {
                            if (!world.isRemote) {
                                treadmill.latchedEnt = entityliving;
                                treadmill.latchedHealth = entityliving.getHealth();
                                entityliving.setLocationAndAngles(treadmill.getMidCoord(0), treadmill.yCoord + 0.175D, treadmill.getMidCoord(1), (treadmill.face - 2) * 90F, 0.0F);
                                world.markBlockForUpdate(treadmill.xCoord, treadmill.yCoord, treadmill.zCoord);
                                entityliving.clearLeashed(true, !player.capabilities.isCreativeMode);
                            }
                            return true;
                        }
                    }
				}

				ItemStack itemStack = player.getCurrentEquippedItem();
				//Allow easier creative testing. Only works for pig and wolves cause easier
				if (itemStack != null && itemStack.getItem() instanceof ItemMonsterPlacer && (itemStack.getItemDamage() == 90 || itemStack.getItemDamage() == 95)) {
					if (!world.isRemote) {
						Entity entity = ItemMonsterPlacer.spawnCreature(world, itemStack.getItemDamage(), treadmill.getMidCoord(0), treadmill.yCoord + 0.175D, treadmill.getMidCoord(1));
						if (TileEntityTreadmill.isEntityValidForTreadmill(entity)) {
							treadmill.latchedEnt = (EntityLiving)entity;
							treadmill.latchedHealth = ((EntityLiving)entity).getHealth();
							entity.setLocationAndAngles(treadmill.getMidCoord(0), treadmill.yCoord + 0.175D, treadmill.getMidCoord(1), (treadmill.face - 2) * 90F, 0.0F);
							world.markBlockForUpdate(treadmill.xCoord, treadmill.yCoord, treadmill.zCoord);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityShellStorage) {
			TileEntityShellStorage shellStorage = (TileEntityShellStorage) tileEntity;

			if ((shellStorage.top && shellStorage.pair != null && ((TileEntityShellStorage)shellStorage.pair).occupied || shellStorage.occupied) && shellStorage.worldObj.isRemote && isLocalPlayer(shellStorage.getPlayerName())) {
				double dist = getDistance(x, y, z);

				if (dist < (shellStorage.top ? 1.1D : 0.6D)) {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
				}
			}
		}
		else if (tileEntity instanceof TileEntityTreadmill) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.4F, 1.0F);
		}
	}

	@SideOnly(Side.CLIENT)
	public double getDistance(int x, int y, int z) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		double d3 = player.posX - (x + 0.5D);
		double d4 = player.boundingBox.minY - y;
		double d5 = player.posZ - (z + 0.5D);

		return (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		//Ignore non players and fake players
		if (!(entity instanceof EntityPlayer) || (entity instanceof FakePlayer)) {
			return;
		}

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDualVertical) {
			TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
			if (dualVertical.top) {
				TileEntity tileEntityPair = world.getBlockTileEntity(x, y - 1, z);
				if (tileEntityPair instanceof TileEntityDualVertical) {
					this.onEntityCollidedWithBlock(world, x, y - 1, z, entity);
				}
			}
			else {
				if (dualVertical instanceof TileEntityShellStorage) {
					TileEntityShellStorage shellStorage = (TileEntityShellStorage) dualVertical;
					if (!shellStorage.occupied && !world.isRemote && !shellStorage.syncing && shellStorage.resyncPlayer <= -10) {
						double d3 = entity.posX - (x + 0.5D);
						double d4 = entity.boundingBox.minY - y;
						double d5 = entity.posZ - (z + 0.5D);
						double dist = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);

						if (dist < 0.3D && shellStorage.isPowered()) {
							EntityPlayer player = (EntityPlayer)entity;

							if (Sync.hasMorphMod && morph.api.Api.hasMorph(player.getCommandSenderName(), false)) {
								player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("sync.isMorphed"));
							}
							else {
								PacketDispatcher.sendPacketToPlayer(MapPacketHandler.createPlayerEnterStoragePacket(x, y, z), (Player) player);
								player.setLocationAndAngles(x + 0.5D, y, z + 0.5D, (shellStorage.face - 2) * 90F, 0F);
							}

							//Mark this as in use
							shellStorage.setPlayerName(player.getCommandSenderName());
							shellStorage.occupied = true;
							world.markBlockForUpdate(shellStorage.xCoord, shellStorage.yCoord, shellStorage.zCoord);
							world.markBlockForUpdate(shellStorage.xCoord, shellStorage.yCoord + 1, shellStorage.zCoord);
						}
					}
				}
			}
		}
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDualVertical) {
			TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
			boolean top = false;
			if (dualVertical.top) {
				TileEntity tileEntityPair = world.getBlockTileEntity(x, y - 1, z);
				if (tileEntityPair instanceof TileEntityDualVertical) {
					dualVertical = (TileEntityDualVertical) tileEntityPair;
				}
				top = true;
			}

			if (dualVertical instanceof TileEntityShellConstructor) {
				TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) dualVertical;
				if (shellConstructor.doorOpen) {
					this.setDualVerticalCollisionBoxes(dualVertical, 0.05F, top, world, x, y, z, aabb, list, entity);
				}
				else {
					super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
				}
			}
			else if (dualVertical instanceof TileEntityShellStorage) {
				TileEntityShellStorage shellStorage = (TileEntityShellStorage)dualVertical;
				if ((!shellStorage.occupied || (world.isRemote && this.isLocalPlayer(shellStorage.getPlayerName()))) && !shellStorage.syncing) {
					this.setDualVerticalCollisionBoxes(dualVertical, 0.05F, top, world, x, y, z, aabb, list, entity);
				}
				else {
					super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
				}
			}
		}
		else if(tileEntity instanceof TileEntityTreadmill) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.175F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	//This makes the sides solid but not the front
	private void setDualVerticalCollisionBoxes(TileEntityDualVertical dualVertical, float thickness, boolean isTop,  World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
		if (dualVertical.face != 0) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, thickness);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if (dualVertical.face != 1) {
			this.setBlockBounds(1.0F - thickness, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if (dualVertical.face != 2) {
			this.setBlockBounds(0.0F, 0.0F, 1.0F - thickness, 1.0F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if (dualVertical.face != 3) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, thickness, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if (isTop) {
			this.setBlockBounds(0.0F, 1.0F - thickness / 2, 0.0F, 1.0F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean isLocalPlayer(String playerName) {
		return playerName != null && Minecraft.getMinecraft().thePlayer != null && playerName.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDualVertical) {
			TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
			if (!dualVertical.top && !world.isBlockOpaqueCube(x, y - 1, z)) {
				world.setBlockToAir(x, y, z);
			}
		}
		if (tileEntity instanceof TileEntityTreadmill) {
			if (world.getBlockTileEntity(x, y - 1, z) instanceof TileEntityTreadmill) {
				world.setBlockToAir(x, y, z);
			}
		}
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote) {
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityDualVertical) {
				TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
				TileEntity tileEntityPair = world.getBlockTileEntity(x, y + (dualVertical.top ? -1 : 1), z);
				if (tileEntityPair instanceof TileEntityDualVertical) {
					TileEntityDualVertical dualVerticalPair = (TileEntityDualVertical) tileEntityPair;
					if (dualVerticalPair.pair == dualVertical) {
						world.playAuxSFX(2001, x, y + (dualVertical.top ? -1 : 1), z, Sync.blockDualVertical.blockID);
						world.setBlockToAir(x, y + (dualVertical.top ? -1 : 1), z);
					}
					TileEntityDualVertical bottom = dualVerticalPair.top ? dualVertical : dualVerticalPair;

					if (!bottom.getPlayerName().equalsIgnoreCase("") && !bottom.getPlayerName().equalsIgnoreCase(player.getCommandSenderName())) {
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromTranslationWithSubstitutions("sync.breakShellUnit", player.getCommandSenderName(), bottom.getPlayerName()));
					}

					if (!player.capabilities.isCreativeMode) {
						float f = 0.5F;
						double d = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
						double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
						double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, (double)dualVertical.xCoord + d, (double)dualVertical.yCoord + d1, (double)dualVertical.zCoord + d2, new ItemStack(Sync.itemBlockPlacer, 1, dualVertical instanceof TileEntityShellConstructor ? 0 : 1));
						entityitem.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
		}
		return super.removeBlockByPlayer(world, player, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockID, int blockMeta) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDualVertical) {
			TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
			TileEntity tileEntityPair = world.getBlockTileEntity(x, y + (dualVertical.top ? -1 : 1), z);
			if (tileEntityPair instanceof TileEntityDualVertical) {
				TileEntityDualVertical dualVerticalPair = (TileEntityDualVertical) tileEntityPair;
				//Confirm they are linked then remove
				if (dualVerticalPair.pair == dualVertical) {
					world.playAuxSFX(2001, x, y + (dualVertical.top ? -1 : 1), z, Sync.blockDualVertical.blockID);
					world.setBlockToAir(x, y + (dualVertical.top ? -1 : 1), z);
				}
				TileEntityDualVertical dualVerticalBottom = dualVerticalPair.top ? dualVertical : dualVerticalPair;

				if (!world.isRemote) {
					//If sync is in progress, cancel and kill the player syncing
					//TODO Re-implement this
/*					if (dualVerticalBottom.resyncPlayer > 0 && dualVerticalBottom.resyncPlayer < 120) {
						ShellHandler.syncInProgress.remove(dualVerticalBottom.getPlayerName());
						//Need to let dualVertical know sync is cancelled
						EntityPlayerMP syncingPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(dualVertical.getPlayerName());
						if (syncingPlayer != null) {
							String name = DamageSource.outOfWorld.damageType;
							DamageSource.outOfWorld.damageType = "syncFail";
							syncingPlayer.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
							DamageSource.outOfWorld.damageType = name;
						}
					}*/
					//TODO Should we treat this as an actual player death in terms of drops?
					if (dualVerticalBottom instanceof TileEntityShellStorage && dualVerticalBottom.resyncPlayer == -10 && ((TileEntityShellStorage) dualVertical).syncing && dualVertical.getPlayerNBT().hasKey("Inventory")) {
						FakePlayer fake = new FakePlayer(world, dualVertical.getPlayerName());
						fake.readFromNBT(dualVertical.getPlayerNBT());
						fake.setLocationAndAngles(x + 0.5D, y, z + 0.5D, (dualVertical.face - 2) * 90F, 0F);
						new FakeNetServerHandler(FMLCommonHandler.instance().getMinecraftServerInstance(), new FakeNetworkManager(), fake);

						fake.captureDrops = true;
						fake.capturedDrops.clear();
						fake.inventory.dropAllItems();
						fake.captureDrops = false;

						PlayerDropsEvent event = new PlayerDropsEvent(fake, DamageSource.outOfWorld, fake.capturedDrops, false);
						if (!MinecraftForge.EVENT_BUS.post(event)) {
							for (EntityItem item : fake.capturedDrops) {
								fake.joinEntityItemWithWorld(item);
							}
						}
						Packet131MapData shellDeathPacket = MapPacketHandler.createShellDeathPacket(dualVerticalBottom.xCoord, dualVerticalBottom.yCoord, dualVerticalBottom.zCoord, dualVerticalBottom.face);
						PacketDispatcher.sendPacketToAllAround(dualVerticalBottom.xCoord, dualVerticalBottom.yCoord, dualVerticalBottom.zCoord, 64D, dualVertical.worldObj.provider.dimensionId, shellDeathPacket);
					}
					else if (dualVerticalBottom instanceof TileEntityShellConstructor) {
						TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) dualVerticalBottom;
						if (!shellConstructor.getPlayerName().equalsIgnoreCase("") && shellConstructor.constructionProgress >= SessionState.shellConstructionPowerRequirement) {
							Packet131MapData shellDeathPacket = MapPacketHandler.createShellDeathPacket(dualVerticalBottom.xCoord, dualVerticalBottom.yCoord, dualVerticalBottom.zCoord, dualVerticalBottom.face);
							PacketDispatcher.sendPacketToAllAround(dualVerticalBottom.xCoord, dualVerticalBottom.yCoord, dualVerticalBottom.zCoord, 64D, dualVertical.worldObj.provider.dimensionId, shellDeathPacket);
						}
					}
					ShellHandler.removeShell(dualVerticalBottom.getPlayerName(), dualVerticalBottom);
				}
			}
		}
		else if (tileEntity instanceof TileEntityTreadmill) {
			TileEntityTreadmill treadmill = (TileEntityTreadmill) tileEntity;
			TileEntity tileEntityPair = world.getBlockTileEntity(treadmill.back ? (treadmill.face == 1 ? x + 1 : treadmill.face == 3 ? x - 1 : x) : (treadmill.face == 1 ? x - 1 : treadmill.face == 3 ? x + 1 : x), y, treadmill.back ? (treadmill.face == 0 ? z - 1 : treadmill.face == 2 ? z + 1 : z) : (treadmill.face == 0 ? z + 1 : treadmill.face == 2 ? z - 1 : z));

			if (tileEntityPair instanceof TileEntityTreadmill) {
				TileEntityTreadmill treadmillPair = (TileEntityTreadmill)tileEntityPair;
				if (treadmillPair.pair == treadmill) {
					world.playAuxSFX(2001, treadmillPair.xCoord, treadmillPair.yCoord, treadmillPair.zCoord, Sync.blockDualVertical.blockID);
					world.setBlockToAir(treadmillPair.xCoord, treadmillPair.yCoord, treadmillPair.zCoord);
				}

				if (!treadmillPair.back && !world.isRemote) {
					float f = 0.5F;
					double d = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(world, (double)treadmill.xCoord + d, (double)treadmill.yCoord + d1, (double)treadmill.zCoord + d2, new ItemStack(Sync.itemBlockPlacer, 1, 2));
					entityitem.delayBeforeCanPickup = 10;
					world.spawnEntityInWorld(entityitem);
				}
			}
		}
		super.breakBlock(world, x, y, z, blockID, blockMeta);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(Sync.itemBlockPlacer, 1, world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
		if (par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityDualVertical) {
			TileEntityDualVertical tileEntityDualVertical = (TileEntityDualVertical) par1World.getBlockTileEntity(par2, par3, par4);
			return (int) Math.floor(tileEntityDualVertical.getBuildProgress() / (SessionState.shellConstructionPowerRequirement / 15));
		}
		else return 0;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDualVertical) {
			TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
			if (side == ForgeDirection.DOWN || side == ForgeDirection.UP) return true;
			if (!(tileEntity instanceof TileEntityShellConstructor)) {
				switch (dualVertical.face) {
					case 0: {
						return side == ForgeDirection.SOUTH;
					}
					case 1: {
						return side == ForgeDirection.WEST;
					}
					case 2: {
						return side == ForgeDirection.NORTH;
					}
					case 3: {
						return side == ForgeDirection.EAST;
					}
					default: {
						return false;
					}
				}
			}
		}
		return false;
	}
}
