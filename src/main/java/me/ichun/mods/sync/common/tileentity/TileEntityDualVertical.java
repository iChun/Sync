package me.ichun.mods.sync.common.tileentity;

import cofh.api.energy.IEnergyHandler;
import com.mojang.authlib.GameProfile;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.packet.PacketNBT;
import me.ichun.mods.sync.common.packet.PacketZoomCamera;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.shell.TeleporterShell;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ITickable;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.packet.PacketNBT;
import me.ichun.mods.sync.common.packet.PacketZoomCamera;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.shell.TeleporterShell;

@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHCore")
public abstract class TileEntityDualVertical extends TileEntity implements IEnergyHandler, ITickable
{

    public TileEntityDualVertical pair;
    public boolean top;
    public int face; //TODO use forgedirection or vanilla in 1.7?
    public boolean vacating;
    public boolean isHomeUnit;

    protected String playerName;
    protected String name;
    public TileEntityDualVertical resyncOrigin;
    protected NBTTagCompound playerNBT;
    public ResourceLocation locationSkin;

    public boolean resync;
    public int resyncPlayer;
    public int canSavePlayer;

    public final static int animationTime = 40;

    protected int powReceived;
    protected int rfIntake;

    public TileEntityDualVertical() {
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
    public void update() {
        if (this.resync) {
            TileEntity tileEntity = worldObj.getTileEntity(this.pos.add(0, (this.top ? -1 : 1), 0));
            if (tileEntity != null && tileEntity.getClass() == this.getClass()) {
                TileEntityDualVertical dualVertical = (TileEntityDualVertical)tileEntity;
                dualVertical.pair = this;
                this.pair = dualVertical;
            }

            //Reload Player Skin
            if (this.worldObj.isRemote) {
                this.locationSkin = AbstractClientPlayer.getLocationSkin(this.getPlayerName());
                AbstractClientPlayer.getDownloadImageSkin(this.locationSkin, this.getPlayerName());
            }
        }
        if (this.top && this.pair != null) {
            this.setPlayerName(this.pair.getPlayerName());
            this.setName(this.pair.getName());
            this.vacating = this.pair.vacating;
        }
        if (!this.top && !this.worldObj.isRemote) {
            //If this is true, we're syncing a player to this location
            if (this.resyncPlayer > -10) {
                this.resyncPlayer--;
                //Start of syncing player to this place
                if (this.resyncPlayer == 60) {
                    if (this.getClass() == TileEntityShellStorage.class) {
                        TileEntityShellStorage shellStorage = (TileEntityShellStorage)this;
                        shellStorage.occupied = true;
                    }

                    EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(this.getPlayerName());
                    if (player != null) {
                        if (!player.isEntityAlive()) {
                            player.setHealth(20);
                            player.isDead = false;
                        }
                        player.extinguish(); //Remove fire

                        int dim = player.dimension;
                        //If player is in different dimension, bring them here
                        if (player.dimension != worldObj.provider.getDimension()) {
                            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().transferPlayerToDimension(player, this.worldObj.provider.getDimension(), new TeleporterShell((WorldServer) this.worldObj, this.worldObj.provider.getDimension(), this.xCoord, this.yCoord, this.zCoord, (this.face - 2) * 90F, 0F));

                            //Refetch player TODO is this needed?
                            player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(this.getPlayerName());

							if (dim == 1) {
								if (player.isEntityAlive()) {
									this.worldObj.spawnEntityInWorld(player);
									player.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, (this.face - 2) * 90F, 0F);
									this.worldObj.updateEntityWithOptionalForce(player, false);
									player.fallDistance = 0F;
								}
							}
                        }
                        else {
                            player.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, (this.face - 2) * 90F, 0F);
                            player.fallDistance = 0F;
                        }


                        Sync.channel.sendTo(new PacketZoomCamera(getPos().getX(), getPos().getY(), getPos().getZ(), this.worldObj.provider.getDimension(), this.face, true, false), player);
                    }
                }
                //Beginning of kicking the player out
                if (this.resyncPlayer == 40) {
                    this.vacating = true;

                    if (this.getClass() == TileEntityShellStorage.class) {
                        TileEntityShellStorage shellStorage = (TileEntityShellStorage) this;
                        shellStorage.occupied = true;
                        shellStorage.occupationTime = animationTime;
                    }
                    else if (this.getClass() == TileEntityShellConstructor.class) {
                        TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) this;
                        shellConstructor.doorOpen = true;
                    }
                    IBlockState state = worldObj.getBlockState(getPos());
                    IBlockState state1 = worldObj.getBlockState(getPos().add(0, 1, 0));
                    worldObj.notifyBlockUpdate(getPos(), state, state, 3);
                    worldObj.notifyBlockUpdate(getPos().add(0, 1, 0), state1, state1, 3);
                }
                //This is where we begin to sync the data aka point of no return
                if (this.resyncPlayer == 30) {
                    EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);

                    if (player != null && player.isEntityAlive()) {
                        //Clear active potion effects before syncing
                        player.clearActivePotions();

                        if (!getPlayerNBT().hasKey("Inventory")) {
                            //Copy data needed from player
                            NBTTagCompound tag = new NBTTagCompound();
                            boolean keepInv = this.worldObj.getGameRules().getBoolean("keepInventory");
                            this.worldObj.getGameRules().setOrCreateGameRule("keepInventory", "false");

                            //Setup location for dummy
                            EntityPlayerMP dummy = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension), EntityHelper.getGameProfile(player.getName()), new PlayerInteractionManager(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension)));
                            dummy.connection = player.connection;
                            dummy.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, (this.face - 2) * 90F, 0F);
                            dummy.fallDistance = 0F;

                            //Clone data
                            dummy.clonePlayer(player, false);
                            dummy.dimension = player.dimension;
                            dummy.setEntityId(player.getEntityId());

                            this.worldObj.getGameRules().setOrCreateGameRule("keepInventory", keepInv ? "true" : "false");

                            //Set data
                            dummy.writeToNBT(tag);
                            tag.setInteger("sync_playerGameMode", player.interactionManager.getGameType().getID());
                            this.setPlayerNBT(tag);
                        }

                        //Sync Forge persistent data as it's supposed to carry over on death
                        NBTTagCompound persistentData = player.getEntityData();
                        if (persistentData != null) {
                            NBTTagCompound forgeData = playerNBT.getCompoundTag("ForgeData");
                            forgeData.setTag(EntityPlayer.PERSISTED_NBT_TAG, player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG));

                            persistentData.setBoolean("isDeathSyncing", false);
                            forgeData.setBoolean("isDeathSyncing", false);
                            playerNBT.setTag("ForgeData", forgeData);
                        }

                        NBTTagCompound persistent = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
                        int healthReduction = persistent.getInteger("Sync_HealthReduction");

                        //Also sync ender chest.
                        playerNBT.setTag("EnderItems", player.getInventoryEnderChest().saveInventoryToNBT());

                        //Update the players NBT stuff
                        player.readFromNBT(this.getPlayerNBT());

                        if(healthReduction > 0)
                        {
                            double curMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                            double morphMaxHealth = curMaxHealth - healthReduction;
                            if(morphMaxHealth < 1D)
                            {
                                morphMaxHealth = 1D;
                            }

                            if(morphMaxHealth != curMaxHealth)
                            {
                                player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(morphMaxHealth);
                            }
                        }

                        player.interactionManager.initializeGameType(GameType.getByID(this.getPlayerNBT().getInteger("sync_playerGameMode")));
                        Sync.channel.sendTo(new PacketNBT(this.getPlayerNBT()), player);
                    }
                }
                if(this.resyncPlayer == 25)
                {
                    ShellHandler.syncInProgress.remove(this.getPlayerName());
                }
                if (this.resyncPlayer == 0) {
                    ShellHandler.removeShell(this.getPlayerName(), this);

                    if(this.getClass() == TileEntityShellStorage.class)
                    {
                        TileEntityShellStorage ss = (TileEntityShellStorage)this;
                        ss.occupied = true;
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
            if (this.canSavePlayer > 0) {
                this.canSavePlayer--;
            }
            if (this.canSavePlayer < 0) {
                this.canSavePlayer = 60;
            }
        }
        this.resync = false;
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
        for(int i = -1; i <= 1; i++)
        {
            for(int k = -1; k <= 1; k++)
            {
                if(!(i == 0 && k == 0))
                {
                    TileEntity te = worldObj.getTileEntity(pos.add(i, 0, k));
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
        return this.playerName.equals("") ? 0 : Sync.config.shellConstructionPowerRequirement; //Using this for comparator output calculation
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        BlockDualVertical.renderPass = pass;
        return pass == 0 || pass == 1;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setBoolean("top", top);
        tag.setInteger("face", face);
        tag.setBoolean("vacating", vacating);
        tag.setBoolean("isHomeUnit", isHomeUnit);
        tag.setString("playerName", canSavePlayer > 0 ? "" : playerName);
        tag.setString("name", name);
        tag.setTag("playerNBT", canSavePlayer > 0 ? new NBTTagCompound() : playerNBT);
        tag.setInteger("rfIntake", rfIntake);
        return tag;
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

    public void writeShellStateData(ByteBuf buffer)
    {
        if(top && pair != null)
        {
            pair.writeShellStateData(buffer);
            return;
        }

        buffer.writeInt(getPos().getX());
        buffer.writeInt(getPos().getY());
        buffer.writeInt(getPos().getZ());
        buffer.writeInt(worldObj.provider.getDimension());

        buffer.writeFloat(getBuildProgress());
        buffer.writeFloat(powerAmount());

        ByteBufUtils.writeUTF8String(buffer, name);
        ByteBufUtils.writeUTF8String(buffer, worldObj.provider.getDimensionType().getName());

        buffer.writeBoolean(this.getClass() == TileEntityShellConstructor.class);

        buffer.writeBoolean(isHomeUnit);

        NBTTagCompound invTag = new NBTTagCompound();

        invTag.setTag("Inventory", generateShowableEquipTags(playerNBT));

        ByteBufUtils.writeTag(buffer, invTag);
    }

    public static NBTTagList generateShowableEquipTags(NBTTagCompound tag)
    {
        NBTTagList list = new NBTTagList();

        NBTTagList nbttaglist = tag.getTagList("Inventory", 10);

        int currentItem = tag.getInteger("SelectedItemSlot");

        ItemStack[] items = new ItemStack[5];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
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
        NBTTagList nbttaglist = tag.getTagList("Inventory", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
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
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 2, getPos().getZ() + 1);
    }

    @Override
    public Block getBlockType()
    {
        return Sync.blockDualVertical;
    }

    public void reset() {
        this.setPlayerName("");
        this.setPlayerNBT(new NBTTagCompound());
        this.resyncOrigin = null;
    }

    //Setters and getters
    public void setPlayerName(String playerName) {
        if (playerName == null) playerName = "";
        this.playerName = playerName;
    }

    public void setPlayerNBT(NBTTagCompound tagCompound) {
        if (tagCompound == null) tagCompound = new NBTTagCompound();
        this.playerNBT = tagCompound;
    }

    public void setName(String name) {
        if (name == null) name = "";
        this.name = name;
    }

    public String getPlayerName() {
        if (this.playerName == null) this.setPlayerName("");
        return this.playerName;
    }

    public NBTTagCompound getPlayerNBT() {
        if (this.playerNBT == null) this.setPlayerNBT(new NBTTagCompound());
        return this.playerNBT;
    }

    public String getName() {
        if (this.name == null) name = "";
        return this.name;
    }
}
