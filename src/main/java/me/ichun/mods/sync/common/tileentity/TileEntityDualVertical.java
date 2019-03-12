package me.ichun.mods.sync.common.tileentity;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.sync.client.core.SyncSkinManager;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.packet.PacketNBT;
import me.ichun.mods.sync.common.packet.PacketZoomCamera;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.shell.TeleporterShell;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class TileEntityDualVertical<T extends TileEntityDualVertical> extends TileEntity implements ITickable
{
    public T pair;
    public boolean top;
    public EnumFacing face;
    public boolean vacating;
    public boolean isHomeUnit;

    protected String playerName;
    protected UUID playerUUID;
    protected String name;
    public T resyncOrigin;
    protected NBTTagCompound playerNBT;
    public ResourceLocation locationSkin;

    public boolean resync;
    public int resyncPlayer;
    public int canSavePlayer;

    public final static int animationTime = 40;

    protected int powReceived;
    protected int rfIntake;
    public boolean wasDead;

    public TileEntityDualVertical() {
        pair = null;
        top = false;
        vacating = false;
        isHomeUnit = false;
        face = EnumFacing.SOUTH;
        playerName = "";
        playerUUID = null;
        name = "";

        resyncPlayer = 0;
        canSavePlayer = 0;
        resyncOrigin = null;

        playerNBT = new NBTTagCompound();

        resync = false;
        wasDead = false;

        powReceived = 0;
        rfIntake = 0;
    }

    @Override
    public void update() {
        if (this.resync) {
            TileEntity tileEntity = world.getTileEntity(this.pos.add(0, (this.top ? -1 : 1), 0));
            if (tileEntity != null && tileEntity.getClass() == this.getClass()) {
                T dualVertical = (T)tileEntity;
                dualVertical.pair = this;
                this.pair = dualVertical;
            }

            //Reload Player Skin
            if (this.world.isRemote) {
                boolean hasPlayer = StringUtils.isNotBlank(this.playerName);
                if (hasPlayer) {
                    SyncSkinManager.get(this.playerName, this.playerUUID, resourceLocation -> this.locationSkin = resourceLocation);
                }
            }
            if (!world.isRemote && this.playerUUID == null) {
                EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
                if (player != null && !player.getUniqueID().equals(EntityPlayer.getOfflineUUID(playerName))) {
                    this.playerUUID = player.getUniqueID();
                    IBlockState here = world.getBlockState(pos);
                    world.notifyBlockUpdate(pos, here, here, 1 | 2 | 16);
                }
            }
        }
        if (this.top && this.pair != null) {
            this.setPlayerName(this.pair.getPlayerName(), this.pair.playerUUID);
            this.setName(this.pair.getName());
            this.vacating = this.pair.vacating;
        }
        if (!this.top && !this.world.isRemote) {
            //If this is true, we're syncing a player to this location
            if (this.resyncPlayer > -10) {
                this.resyncPlayer--;
                //Start of syncing player to this place
                if (this.resyncPlayer == 60) {
                    if (this.getClass() == TileEntityShellStorage.class) {
                        TileEntityShellStorage shellStorage = (TileEntityShellStorage)this;
                        shellStorage.occupied = true;
                    }

                    EntityPlayerMP player = getPlayerIfAvailable();
                    if (player != null) {
                        if (!player.isEntityAlive()) {
                            player.setHealth(20);
                            player.isDead = false;
                        }
                        player.extinguish(); //Remove fire

                        int dim = player.dimension;
                        //If player is in different dimension, bring them here
                        if (player.dimension != world.provider.getDimension()) {
                            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().transferPlayerToDimension(player, this.world.provider.getDimension(), new TeleporterShell((WorldServer) this.world, this.world.provider.getDimension(), this.getPos(), face.getOpposite().getHorizontalAngle(), 0F));

                            //Refetch player TODO is this needed?
                            player = getPlayerIfAvailable();

                            if (dim == 1) {
                                if (player.isEntityAlive()) {
                                    this.world.spawnEntity(player);
                                    player.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.face.getOpposite().getHorizontalAngle(), 0F);
                                    this.world.updateEntityWithOptionalForce(player, false);
                                    player.fallDistance = 0F;
                                }
                            }
                        }
                        else {
                            player.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, face.getOpposite().getHorizontalAngle(), 0F);
                            player.fallDistance = 0F;
                        }


                        Sync.channel.sendTo(new PacketZoomCamera(pos, this.world.provider.getDimension(), this.face, true, false), player);
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
                    IBlockState state = world.getBlockState(getPos());
                    IBlockState state1 = world.getBlockState(getPos().add(0, 1, 0));
                    world.notifyBlockUpdate(getPos(), state, state, 3);
                    world.notifyBlockUpdate(getPos().add(0, 1, 0), state1, state1, 3);
                }
                //This is where we begin to sync the data aka point of no return
                if (this.resyncPlayer == 30) {
                    EntityPlayerMP player = getPlayerIfAvailable();

                    if (player != null && player.isEntityAlive()) {
                        //Clear active potion effects before syncing
                        player.clearActivePotions();

                        if (Sync.config.transferPersistentItems == 1)
                        {
                            if (wasDead)
                            {
                                //copy new items that are given on death, like the key to a tombstone
                                EntityPlayerMP deadDummy = setupDummy(player);
                                mergeStoredInv(deadDummy.inventory);
                            }

                            //Copy data needed from player
                            NBTTagCompound tag = new NBTTagCompound();
                            EntityPlayerMP dummy = setupDummy(player);

                            //Set data
                            dummy.writeToNBT(tag);
                            if (resyncOrigin != null) //deduplicate items
                            {
                                //Strip items from the old inv that have been transferred to the new inventory
                                deleteItemsFrom(player.inventory.mainInventory, dummy.inventory.mainInventory);
                                deleteItemsFrom(player.inventory.armorInventory, dummy.inventory.armorInventory);
                                deleteItemsFrom(player.inventory.offHandInventory, dummy.inventory.offHandInventory);
                                //Write the changes to the old inventory
                                resyncOrigin.getPlayerNBT().setTag("Inventory", player.inventory.writeToNBT(new NBTTagList()));
                                resyncOrigin.markDirty();
                                if (getPlayerNBT().hasKey("Inventory")) //try inserting persistent items by merging
                                {
                                    //noinspection ConstantConditions
                                    mergeStoredInv(dummy.inventory);
                                }
                            }
                            else
                            {
                                dummy.inventory.clear();
                            }

                            if (!getPlayerNBT().hasKey("Inventory"))
                            {
                                tag.setInteger("sync_playerGameMode", player.interactionManager.getGameType().getID());
                                this.setPlayerNBT(tag); //Write the new data
                            }
                        }
                        else if (!getPlayerNBT().hasKey("Inventory")) //just go this way if configured
                        {
                            //Copy data needed from player
                            NBTTagCompound tag = new NBTTagCompound();

                            //Setup location for dummy
                            EntityPlayerMP dummy = setupDummy(player);
                            dummy.inventory.clear();

                            //Set data
                            dummy.writeToNBT(tag);
                            tag.setInteger("sync_playerGameMode", player.interactionManager.getGameType().getID());
                            this.setPlayerNBT(tag);
                        }

                        wasDead = false;

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

    public void setup(T scPair, boolean isTop, EnumFacing placeYaw)
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
                    TileEntity te = world.getTileEntity(pos.add(i, 0, k));
                    if(te instanceof TileEntityTreadmill && !((TileEntityTreadmill)te).back)
                    {
                        power += ((TileEntityTreadmill)te).powerOutput();
                    }
                }
            }
        }
        return power + (world.isRemote ? rfIntake : powReceived);
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
        tag = super.writeToNBT(tag);
        tag.setBoolean("top", top);
        tag.setInteger("face", face.getHorizontalIndex());
        tag.setBoolean("vacating", vacating);
        tag.setBoolean("isHomeUnit", isHomeUnit);
        tag.setString("playerName", canSavePlayer > 0 ? "" : playerName);
        if (playerUUID != null)
        {
            tag.setUniqueId("playerUUID", playerUUID);
        }
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
        face = EnumFacing.byHorizontalIndex(tag.getInteger("face"));
        vacating = tag.getBoolean("vacating");
        isHomeUnit = tag.getBoolean("isHomeUnit");
        playerName = tag.getString("playerName");
        playerUUID = tag.hasKey("playerUUID" + "Most", Constants.NBT.TAG_LONG) && tag.hasKey("playerUUID" + "Least", Constants.NBT.TAG_LONG) ? tag.getUniqueId("playerUUID") : null;
        System.err.println("Read playerUUID:" + (playerUUID == null ? "null" : playerUUID) + " for name " + playerName);
        name = tag.getString("name");
        playerNBT = tag.getCompoundTag("playerNBT");
        rfIntake = tag.getInteger("rfIntake");

        resync = true;
    }

    private EntityPlayerMP setupDummy(EntityPlayerMP player)
    {
        //Setup location for dummy
        boolean keepInv = this.world.getGameRules().getBoolean("keepInventory");
        this.world.getGameRules().setOrCreateGameRule("keepInventory", "false");

        //Setup location for dummy
        EntityPlayerMP dummy = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(player.dimension), EntityHelper.getGameProfile(player.getName()), new PlayerInteractionManager(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(player.dimension)));
        dummy.connection = player.connection;
        dummy.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, face.getOpposite().getHorizontalAngle(), 0F);
        dummy.fallDistance = 0F;

        //Clone data
        dummy.copyFrom(player, false);
        dummy.dimension = player.dimension;
        dummy.setEntityId(player.getEntityId());

        this.world.getGameRules().setOrCreateGameRule("keepInventory", Boolean.toString(keepInv));
        return dummy;
    }

    private static void deleteItemsFrom(NonNullList<ItemStack> inv, List<ItemStack> toDelete)
    {
        for (ItemStack stack : toDelete)
        {
            if (stack.isEmpty())
            {
                continue;
            }
            int index = inv.indexOf(stack); //This only catches the same itemstack instances. This way it will ignore items if both shells have them
            if (index != -1)
            {
                inv.set(index, ItemStack.EMPTY);
            }
        }
    }

    private void mergeStoredInv(InventoryPlayer toMerge)
    {
        InventoryPlayer nbtSavedInv = new InventoryPlayer(null);
        nbtSavedInv.readFromNBT(getPlayerNBT().getTagList("Inventory", 10));
        mergeInvOrDrop(nbtSavedInv.mainInventory, toMerge.mainInventory);
        mergeInvOrDrop(nbtSavedInv.armorInventory, toMerge.armorInventory);
        mergeInvOrDrop(nbtSavedInv.offHandInventory, toMerge.offHandInventory);
        getPlayerNBT().setTag("Inventory", nbtSavedInv.writeToNBT(new NBTTagList()));
    }

    private void mergeInvOrDrop(NonNullList<ItemStack> mergeInto, NonNullList<ItemStack> from)
    {
        for (ItemStack toInsert : from)
        {
            if (toInsert.isEmpty())
            {
                continue;
            }
            boolean inserted = false;
            for (int i = 0; i < mergeInto.size(); i++)
            {
                ItemStack origStack = mergeInto.get(i);
                if (origStack.isEmpty())
                {
                    mergeInto.set(i, toInsert);
                    inserted = true;
                    break;
                }
            }
            if (!inserted)
            {
                BlockPos dropPos = pos.offset(face).up();
                EntityItem entityItem = new EntityItem(world, dropPos.getX(), dropPos.getY(), dropPos.getZ(), toInsert);
                entityItem.setPickupDelay(60);
                world.spawnEntity(entityItem);
            }
        }
    }

    public void writeShellStateData(ByteBuf buffer)
    {
        if(top && pair != null)
        {
            pair.writeShellStateData(buffer);
            return;
        }

        buffer.writeLong(pos.toLong());
        buffer.writeInt(world.provider.getDimension());

        buffer.writeFloat(getBuildProgress());
        buffer.writeFloat(powerAmount());

        ByteBufUtils.writeUTF8String(buffer, name);
        ByteBufUtils.writeUTF8String(buffer, world.provider.getDimensionType().getName());

        buffer.writeBoolean(this.getClass() == TileEntityShellConstructor.class);

        buffer.writeBoolean(isHomeUnit);

        NBTTagCompound invTag = new NBTTagCompound();

        invTag.setTag("Inventory", generateShowableEquipTags(playerNBT));

        ByteBufUtils.writeTag(buffer, invTag);
    }

    public static NBTTagList generateShowableEquipTags(NBTTagCompound tag)
    { //look at InventoryPlayer#writeFromNBT
        NBTTagList list = new NBTTagList();

        NBTTagList nbttaglist = tag.getTagList("Inventory", 10);
        boolean setMainHand = false;

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if( j < 9 && !setMainHand) // Hotbar
            {
                ItemStack itemstack = new ItemStack(nbttagcompound);
                if (!itemstack.isEmpty())
                {
                    setMainHand = true;
                    list.appendTag(nbttagcompound);
                }
            }
            else if (j > 100 && j < 104) // ARMOR
            {
                if (!setMainHand)
                    list.appendTag(ItemStack.EMPTY.serializeNBT());
                list.appendTag(nbttagcompound);
            }
            else if (j == 150)
            {
                list.appendTag(nbttagcompound);
            }
        }

        return list;
    }

    public static void addShowableEquipToPlayer(EntityPlayer player, NBTTagCompound tag)
    { //look at InventoryPlayer#readFromNBT
        NBTTagList nbttaglist = tag.getTagList("Inventory", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = new ItemStack(nbttagcompound);

            if (!itemstack.isEmpty())
            {
                if (j < 9)
                {
                    player.setHeldItem(EnumHand.MAIN_HAND, itemstack);
                }
                else if (j == 150)
                {
                    player.setHeldItem(EnumHand.OFF_HAND, itemstack);
                }
                else if (j >= 100 && j < 104)
                {
                    player.inventory.armorInventory.set(j - 100, itemstack);
                }
                else
                {
                    Sync.LOGGER.error("Invalid slotID for showable equip " + j);
                }
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

    @Override
    public double getMaxRenderDistanceSquared()
    {
        //As this is seen from the zoom and it's 2 blocks tall, we might increase the render dist by a factor of 1.5, and even further when zooming
        return (Sync.eventHandlerClient != null && Sync.eventHandlerClient.zoomTimer > 0) ? 16384D : 9216D;
    }

    public void reset() {
        this.setPlayerName(null);
        this.setPlayerNBT(new NBTTagCompound());
        this.resyncOrigin = null;
    }


    public boolean matchesPlayer(EntityPlayer player) {
        UUID playerUUID = player.getUniqueID();
        String rightName = player.getName();
        if (this.playerUUID != null && !EntityPlayer.getOfflineUUID(this.playerName).equals(playerUUID) && playerUUID.equals(this.playerUUID)) {
            if (!player.world.isRemote && !rightName.equals(this.playerName)) { //Players can change their name, let's take care of this
                String oldPlayerName = this.playerName;
                Sync.LOGGER.info("Updating player name for UUID " + playerUUID + ": " + oldPlayerName + " -> " + rightName);
                Set<TileEntityDualVertical> dualVerticals = ShellHandler.playerShells.removeAll(oldPlayerName);
                for (TileEntityDualVertical dualVertical : dualVerticals) {
                    if (dualVertical.playerName.equals(oldPlayerName)) {
                        dualVertical.playerUUID = this.playerUUID;
                        dualVertical.playerName = rightName;
                        IBlockState state = world.getBlockState(dualVertical.pos);
                        world.notifyBlockUpdate(dualVertical.pos, state, state, 1 | 2 | 16);
                    }
                }
                ShellHandler.playerShells.putAll(rightName, dualVerticals);
                if (!rightName.equals(this.playerName)) {
                    IBlockState state = world.getBlockState(pos);
                    this.world.notifyBlockUpdate(pos, state, state, 1 | 2 | 16);
                    this.playerName = rightName;
                }
                ShellHandler.updatePlayerOfShells(player, null, true);
            }
            return true;
        }
        return this.playerName.equals(rightName);
    }

    @Nullable
    public EntityPlayerMP getPlayerIfAvailable() {
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        return playerUUID == null ? playerList.getPlayerByUsername(this.getPlayerName()) : playerList.getPlayerByUUID(playerUUID);
    }

    public void setPlayerName(String playerName, UUID uuid) {
        if (playerName == null) playerName = "";
        this.playerName = playerName;
        this.playerUUID = uuid;
    }

    //Setters and getters
    public void setPlayerName(EntityPlayer player) {
        if (player == null) {
            setPlayerName("", null);
            return;
        }

        String playerName = player.getName();
        if (!playerName.equals(this.playerName)) {
            UUID playerUUID = player.getUniqueID();
            setPlayerName(playerName, playerUUID == EntityPlayer.getOfflineUUID(playerName) ? null : playerUUID);
        }
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
        if (this.playerName == null) this.setPlayerName(null);
        return this.playerName;
    }

    @Nullable
    public UUID getPlayerUUID() {
        return playerUUID;
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
