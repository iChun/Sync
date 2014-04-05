package sync.common.tileentity;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldServer;
import sync.common.Sync;
import sync.common.block.BlockDualVertical;
import sync.common.core.MapPacketHandler;
import sync.common.core.SessionState;
import sync.common.shell.ShellHandler;
import sync.common.shell.TeleporterShell;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class TileEntityDualVertical extends TileEntity implements IEnergyHandler
{

	public TileEntityDualVertical pair;
	public boolean top;
	public int face;
	public boolean vacating;
	public boolean isHomeUnit;

	public String playerName;
	public String name;

	public int resyncPlayer;
	public int canSavePlayer;
	public TileEntityDualVertical resyncOrigin;

	public NBTTagCompound playerNBT;

	public ResourceLocation locationSkin;

	public boolean resync;

	public final static int animationTime = 40;

	protected int powReceived;
	protected int rfIntake;

	public TileEntityDualVertical()
	{
		pair = null;
		top = false;
		vacating = false;
		isHomeUnit = false;
		face = 0;
		playerName = "";
		name = "";

		resyncPlayer = 0;
		canSavePlayer = 0;
		resyncOrigin = null;

		playerNBT = new NBTTagCompound();

		resync = false;

		powReceived = 0;
		rfIntake = 0;
	}

	@Override
	public void updateEntity()
	{
		if(resync)
		{
			TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + (top ? -1 : 1), zCoord);
			if(te != null && te.getClass() == this.getClass())
			{
				TileEntityDualVertical sc = (TileEntityDualVertical)te;
				sc.pair = this;
				pair = sc;
			}

			if(worldObj.isRemote)
			{
				locationSkin = AbstractClientPlayer.getLocationSkin(playerName);
				AbstractClientPlayer.getDownloadImageSkin(this.locationSkin, playerName);
			}
		}
		if(top && pair != null)
		{
			playerName = pair.playerName;
			name = pair.name;
			vacating = pair.vacating;
		}
		if(!top && !worldObj.isRemote)
		{
			//If this is true, we're syncing a player to this location
			if(resyncPlayer > -10)
			{
				resyncPlayer--;
				//Start of syncing player to this place
				if(resyncPlayer == 60)
				{
					if(this.getClass() == TileEntityShellStorage.class)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)this;

						ss.occupied = true;
					}

					EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName);
					if(player != null)
					{
						if(!player.isEntityAlive())
						{
							player.setHealth(20);
						}
						player.setFire(0); //Remove fire

						int dim = player.dimension;
						//If player is in different dimension, bring them here
						if(player.dimension != worldObj.provider.dimensionId)
						{
							FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension(player, worldObj.provider.dimensionId, new TeleporterShell((WorldServer)worldObj, worldObj.provider.dimensionId, xCoord, yCoord, zCoord, (face - 2) * 90F, 0F));

							player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName);

							if (dim == 1)
							{
								if (player.isEntityAlive())
								{
									worldObj.spawnEntityInWorld(player);
									player.setLocationAndAngles(xCoord + 0.5D, yCoord, zCoord + 0.5D, (face - 2) * 90F, 0F);
									worldObj.updateEntityWithOptionalForce(player, false);
									player.fallDistance = 0F;
								}
							}
						}
						else
						{
							player.setLocationAndAngles(xCoord + 0.5D, yCoord, zCoord + 0.5D, (face - 2) * 90F, 0F);
							player.fallDistance = 0F;
						}

						Packet131MapData zoomPacket = MapPacketHandler.createZoomCameraPacket(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, face, true, false);
						PacketDispatcher.sendPacketToPlayer(zoomPacket, (Player)player);
					}
				}
				//Beginning of kicking the player out
				if(resyncPlayer == 40)
				{
					vacating = true;

					if(this.getClass() == TileEntityShellStorage.class)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)this;

						ss.occupied = true;

						ss.occupationTime = TileEntityShellStorage.animationTime;
					}
					else if(this.getClass() == TileEntityShellConstructor.class)
					{
						TileEntityShellConstructor sc = (TileEntityShellConstructor)this;

						sc.doorOpen = true;
					}
					worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
					worldObj.markBlockForUpdate(this.xCoord, this.yCoord + 1, this.zCoord);
				}
				//This is where we begin to sync the data
				if(resyncPlayer == 30)
				{
					EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName);

					if(player != null && player.isEntityAlive())
					{
						//Clear active potion effects before syncing
						player.clearActivePotions();

						//Basically we need to create the NBT required for a new player as the current data in this shell is invalid/missing
						if (!playerNBT.hasKey("Inventory")) {
							NBTTagCompound tag = new NBTTagCompound();
							boolean keepInv = worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");

							EntityPlayerMP dummy = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension), player.getCommandSenderName(), new ItemInWorldManager(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension)));
							dummy.playerNetServerHandler = player.playerNetServerHandler;
							dummy.setLocationAndAngles(xCoord + 0.5D, yCoord, zCoord + 0.5D, (face - 2) * 90F, 0F);
							dummy.fallDistance = 0F;
							worldObj.getGameRules().setOrCreateGameRule("keepInventory", "false");

							dummy.clonePlayer(player, false);
							dummy.dimension = player.dimension;
							dummy.entityId = player.entityId;

							worldObj.getGameRules().setOrCreateGameRule("keepInventory", keepInv ? "true" : "false");

							dummy.writeToNBT(tag);
							tag.setInteger("sync_playerGameMode", player.theItemInWorldManager.getGameType().getID());
							playerNBT = tag;
						}
						//Sync Forge persistent data as it's supposed to carry over on death
						NBTTagCompound persistentData = player.getEntityData();
						if (persistentData != null) {
							NBTTagCompound forgeData = playerNBT.getCompoundTag("ForgeData");
							forgeData.setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG));
							forgeData.setBoolean("isDeathSyncing", false);
							playerNBT.setCompoundTag("ForgeData", forgeData);
						}
						//Also sync ender chest.
						playerNBT.setTag("EnderItems", player.getInventoryEnderChest().saveInventoryToNBT());

						//Update the players NBT stuff
						Packet131MapData nbtPacket = MapPacketHandler.createNBTPacket(playerNBT);
						player.readFromNBT(playerNBT);

						ShellHandler.syncInProgress.remove(playerName);
						player.theItemInWorldManager.initializeGameType(EnumGameType.getByID(playerNBT.getInteger("sync_playerGameMode")));
						PacketDispatcher.sendPacketToPlayer(nbtPacket, (Player)player);
					}
				}
				if(resyncPlayer == 0)
				{
					resyncOrigin = null;
					if(this.getClass() == TileEntityShellStorage.class)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)this;

						ss.occupied = true;
					}
					if(this.getClass() == TileEntityShellConstructor.class)
					{
						TileEntityShellConstructor sc = (TileEntityShellConstructor)this;

						ShellHandler.removeShell(sc.playerName, sc);

						sc.constructionProgress = 0.0F;
						sc.playerName = "";
						sc.playerNBT = new NBTTagCompound();

						worldObj.markBlockForUpdate(sc.xCoord, sc.yCoord, sc.zCoord);
						worldObj.markBlockForUpdate(sc.xCoord, sc.yCoord + 1, sc.zCoord);
					}
				}
				if(resyncPlayer == -10)
				{
					if(this.getClass() == TileEntityShellStorage.class)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)this;

						ss.occupied = true;
					}
				}
			}
			if(canSavePlayer > 0)
			{
				canSavePlayer--;
			}
			if(canSavePlayer < 0)
			{
				canSavePlayer = 60;
			}
		}
		resync = false;
	}

	public void setup(TileEntityDualVertical scPair, boolean isTop, int placeYaw)
	{
		pair = scPair;
		top = isTop;
		face = placeYaw;
	}

	public float powerAmount()
	{
		float power = 0.0F;
		for(int i = xCoord - 1; i <= xCoord + 1; i++)
		{
			for(int k = zCoord - 1; k <= zCoord + 1; k++)
			{
				if(!(i == xCoord && k == zCoord))
				{
					TileEntity te = worldObj.getBlockTileEntity(i, yCoord, k);
					if(te instanceof TileEntityTreadmill && !((TileEntityTreadmill)te).back)
					{
						power += ((TileEntityTreadmill)te).powerOutput();
					}
				}
			}
		}
		return power + (worldObj.isRemote ? rfIntake : powReceived);
	}

	public float getBuildProgress()
	{
		return this.playerName.equals("") ? 0 : SessionState.shellConstructionPowerRequirement; //Using this for comparator output calculation
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		BlockDualVertical.renderPass = pass;
		return pass == 0 || pass == 1;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		readFromNBT(pkt.data);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setBoolean("top", top);
		tag.setInteger("face", face);
		tag.setBoolean("vacating", vacating);
		tag.setBoolean("isHomeUnit", isHomeUnit);
		tag.setString("playerName", canSavePlayer > 0 ? "" : playerName);
		tag.setString("name", name);
		tag.setCompoundTag("playerNBT", canSavePlayer > 0 ? new NBTTagCompound() : playerNBT);
		tag.setInteger("rfIntake", rfIntake);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		top = tag.getBoolean("top");
		face = tag.getInteger("face");
		vacating = tag.getBoolean("vacating");
		isHomeUnit = tag.getBoolean("isHomeUnit");
		playerName = tag.getString("playerName");
		name = tag.getString("name");
		playerNBT = tag.getCompoundTag("playerNBT");
		rfIntake = tag.getInteger("rfIntake");

		resync = true;
	}

	public byte[] createShellStateData() 
	{
		if(top && pair != null)
		{
			return pair.createShellStateData();
		}

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try
		{
			stream.writeInt(xCoord);
			stream.writeInt(yCoord);
			stream.writeInt(zCoord);
			stream.writeInt(worldObj.provider.dimensionId);

			stream.writeFloat(getBuildProgress());
			stream.writeFloat(powerAmount());

			stream.writeUTF(name);
			stream.writeUTF(worldObj.provider.getDimensionName());

			stream.writeBoolean(this.getClass() == TileEntityShellConstructor.class);

			stream.writeBoolean(isHomeUnit);

			NBTTagCompound invTag = new NBTTagCompound();

			invTag.setTag("Inventory", generateShowableEquipTags(playerNBT));

			Sync.writeNBTTagCompound(invTag, stream);
		}
		catch(IOException e)
		{
		}
		return bytes.toByteArray();
	}

	public static NBTTagList generateShowableEquipTags(NBTTagCompound tag)
	{
		NBTTagList list = new NBTTagList();

		NBTTagList nbttaglist = tag.getTagList("Inventory");

		int currentItem = tag.getInteger("SelectedItemSlot");

		ItemStack[] items = new ItemStack[5];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

			if (itemstack != null)
			{
				if (j == currentItem)
				{
					items[0] = itemstack;
				}

				if (j >= 100 && j < 104)
				{
					items[j - 100 + 1] = itemstack;
				}
			}
		}

		int i;
		NBTTagCompound nbttagcompound;

		for (i = 0; i < items.length; ++i)
		{
			if (items[i] != null)
			{
				nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				items[i].writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}

		return list;
	}

	public static void addShowableEquipToPlayer(EntityPlayer player, NBTTagCompound tag)
	{
		NBTTagList nbttaglist = tag.getTagList("Inventory");

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

			if (itemstack != null)
			{
				player.setCurrentItemOrArmor(j, itemstack);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
	}

	@Override
	public Block getBlockType()
	{
		return Sync.blockDualVertical;
	}
}
