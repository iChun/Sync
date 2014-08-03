package sync.common.tileentity;

import cofh.api.energy.IEnergyHandler;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.EntityHelperBase;
import ichun.common.core.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import sync.common.Sync;
import sync.common.block.BlockDualVertical;
import sync.common.packet.PacketNBT;
import sync.common.packet.PacketZoomCamera;
import sync.common.shell.ShellHandler;
import sync.common.shell.TeleporterShell;

@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHCore")
public abstract class TileEntityDualVertical extends TileEntity implements IEnergyHandler {

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
    public void updateEntity() {
        if (this.resync) {
            TileEntity tileEntity = worldObj.getTileEntity(this.xCoord, this.yCoord + (this.top ? -1 : 1), this.zCoord);
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

                    EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(this.getPlayerName());
                    if (player != null) {
                        if (!player.isEntityAlive()) {
                            player.setHealth(20);
                            player.isDead = false;
                        }
                        player.extinguish(); //Remove fire

                        int dim = player.dimension;
                        //If player is in different dimension, bring them here
                        if (player.dimension != worldObj.provider.dimensionId) {
                            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension(player, this.worldObj.provider.dimensionId, new TeleporterShell((WorldServer) this.worldObj, this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, (this.face - 2) * 90F, 0F));

                            //Refetch player TODO is this needed?
                            player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(this.getPlayerName());

							if (dim == 1) {
								if (player.isEntityAlive()) {
									this.worldObj.spawnEntityInWorld(player);
									player.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord, this.zCoord + 0.5D, (this.face - 2) * 90F, 0F);
									this.worldObj.updateEntityWithOptionalForce(player, false);
									player.fallDistance = 0F;
								}
							}
                        }
                        else {
                            player.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord, this.zCoord + 0.5D, (this.face - 2) * 90F, 0F);
                            player.fallDistance = 0F;
                        }

                        PacketHandler.sendToPlayer(Sync.channels, new PacketZoomCamera(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId, this.face, true, false), player);
                    }
                }
                //Beginning of kicking the player out
                if (this.resyncPlayer == 40) {
                    this.vacating = true;

                    if (this.getClass() == TileEntityShellStorage.class) {
                        TileEntityShellStorage shellStorage = (TileEntityShellStorage) this;
                        shellStorage.occupied = true;
                        shellStorage.occupationTime = TileEntityShellStorage.animationTime;
                    }
                    else if (this.getClass() == TileEntityShellConstructor.class) {
                        TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) this;
                        shellConstructor.doorOpen = true;
                    }
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord + 1, this.zCoord);
                }
                //This is where we begin to sync the data aka point of no return
                if (this.resyncPlayer == 30) {
                    EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(playerName);

                    if (player != null && player.isEntityAlive()) {
                        //Clear active potion effects before syncing
                        player.clearActivePotions();

                        if (!getPlayerNBT().hasKey("Inventory")) {
                            //Copy data needed from player
                            NBTTagCompound tag = new NBTTagCompound();
                            boolean keepInv = this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");
                            this.worldObj.getGameRules().setOrCreateGameRule("keepInventory", "false");

                            //Setup location for dummy
                            EntityPlayerMP dummy = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension), EntityHelperBase.getSimpleGameProfileFromName(player.getCommandSenderName()), new ItemInWorldManager(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.dimension)));
                            dummy.playerNetServerHandler = player.playerNetServerHandler;
                            dummy.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord, this.zCoord + 0.5D, (this.face - 2) * 90F, 0F);
                            dummy.fallDistance = 0F;

                            //Clone data
                            dummy.clonePlayer(player, false);
                            dummy.dimension = player.dimension;
                            dummy.setEntityId(player.getEntityId());

                            this.worldObj.getGameRules().setOrCreateGameRule("keepInventory", keepInv ? "true" : "false");

                            //Set data
                            dummy.writeToNBT(tag);
                            tag.setInteger("sync_playerGameMode", player.theItemInWorldManager.getGameType().getID());
                            this.setPlayerNBT(tag);
                        }
                        //Sync Forge persistent data as it's supposed to carry over on death
                        NBTTagCompound persistentData = player.getEntityData();
                        if (persistentData != null) {
                            NBTTagCompound forgeData = playerNBT.getCompoundTag("ForgeData");
                            forgeData.setTag(EntityPlayer.PERSISTED_NBT_TAG, player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG));
                            forgeData.setBoolean("isDeathSyncing", false);
                            playerNBT.setTag("ForgeData", forgeData);
                        }
                        //Also sync ender chest.
                        playerNBT.setTag("EnderItems", player.getInventoryEnderChest().saveInventoryToNBT());

                        //Update the players NBT stuff
                        player.readFromNBT(this.getPlayerNBT());

                        player.theItemInWorldManager.initializeGameType(WorldSettings.GameType.getByID(this.getPlayerNBT().getInteger("sync_playerGameMode")));
                        PacketHandler.sendToPlayer(Sync.channels, new PacketNBT(this.getPlayerNBT()), player);
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
        for(int i = xCoord - 1; i <= xCoord + 1; i++)
        {
            for(int k = zCoord - 1; k <= zCoord + 1; k++)
            {
                if(!(i == xCoord && k == zCoord))
                {
                    TileEntity te = worldObj.getTileEntity(i, yCoord, k);
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
        return this.playerName.equals("") ? 0 : Sync.config.getSessionInt("shellConstructionPowerRequirement"); //Using this for comparator output calculation
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        BlockDualVertical.renderPass = pass;
        return pass == 0 || pass == 1;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
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
        tag.setTag("playerNBT", canSavePlayer > 0 ? new NBTTagCompound() : playerNBT);
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

    public void writeShellStateData(ByteBuf buffer)
    {
        if(top && pair != null)
        {
            pair.writeShellStateData(buffer);
            return;
        }

        buffer.writeInt(xCoord);
        buffer.writeInt(yCoord);
        buffer.writeInt(zCoord);
        buffer.writeInt(worldObj.provider.dimensionId);

        buffer.writeFloat(getBuildProgress());
        buffer.writeFloat(powerAmount());

        ByteBufUtils.writeUTF8String(buffer, name);
        ByteBufUtils.writeUTF8String(buffer, worldObj.provider.getDimensionName());

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
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
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
