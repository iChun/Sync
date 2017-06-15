package me.ichun.mods.sync.common.block;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.morph.api.MorphApi;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.packet.PacketPlayerEnterStorage;
import me.ichun.mods.sync.common.packet.PacketShellDeath;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockDualVertical extends BlockContainer {

    public static int renderPass;
    private AxisAlignedBB boundingBox = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, 1D);
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);

    public BlockDualVertical() {
        super(Material.IRON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumType.CONSTRUCTOR));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        switch (meta) {
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
                return this.createNewTileEntity(world, meta);
            }
        }
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDualVertical && !(player instanceof FakePlayer)) {
            TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
            boolean top = dualVertical.top;
            if (top) {
                TileEntity tileEntityPair = world.getTileEntity(pos.down());
                if (tileEntityPair instanceof TileEntityDualVertical) {
                    dualVertical = (TileEntityDualVertical) tileEntityPair;
                }
            }

            //Shell Constructor
            if (dualVertical instanceof TileEntityShellConstructor) {
                TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) dualVertical;

                //If nothing is there
                if (shellConstructor.getPlayerName().equalsIgnoreCase("")) {
                    if (iChunUtil.hasMorphMod() && MorphApi.getApiImpl().hasMorph(player.getName(), Side.SERVER)) {
                        player.sendMessage(new TextComponentTranslation("sync.isMorphed"));
                        return true;
                    }
                    shellConstructor.setPlayerName(player.getName());

                    if (!world.isRemote && !player.capabilities.isCreativeMode) {
                        String name = DamageSource.outOfWorld.damageType;
                        DamageSource.outOfWorld.damageType = "shellConstruct";
                        player.attackEntityFrom(DamageSource.outOfWorld, (float)Sync.config.damageGivenOnShellConstruction);
                        DamageSource.outOfWorld.damageType = name;
                    }

                    notifyThisAndAbove(state, EnumType.CONSTRUCTOR, pos, world, top);
                    return true;
                }
                else if (shellConstructor.getPlayerName().equalsIgnoreCase(player.getName()) && player.capabilities.isCreativeMode) {
                    if (!world.isRemote) {
                        shellConstructor.constructionProgress = Sync.config.shellConstructionPowerRequirement;
                        ShellHandler.updatePlayerOfShells(player, null, true);
                        notifyThisAndAbove(state, EnumType.CONSTRUCTOR, pos, world, top);
                    }
                    return true;
                }

            }
            //Shell Storage
            else if (dualVertical instanceof TileEntityShellStorage) {
                TileEntityShellStorage shellStorage = (TileEntityShellStorage) dualVertical;
                ItemStack itemStack = player.getActiveItemStack();

                if (itemStack != null) {
                    //Set storage name
                    if (itemStack.getItem() instanceof ItemNameTag) {
                        //Don't do anything if name is the same
                        if ((!itemStack.hasDisplayName() && dualVertical.getName().equalsIgnoreCase("")) || (itemStack.hasDisplayName() && dualVertical.getName().equalsIgnoreCase(itemStack.getDisplayName()))) {
                            return false;
                        }
                        dualVertical.setName(itemStack.hasDisplayName() ? itemStack.getDisplayName() : "");

                        if (!player.capabilities.isCreativeMode && itemStack.stackSize-- <= 0) {
                            player.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, null);
                        }
                        notifyThisAndAbove(state, EnumType.STORAGE, pos, world, top);

                        if (!world.isRemote) {
                            EntityPlayerMP entityPlayerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(dualVertical.getPlayerName());
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

                        notifyThisAndAbove(state, EnumType.STORAGE, pos, world, top);

                        if (!world.isRemote) {
                            EntityPlayerMP entityPlayerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(dualVertical.getPlayerName());
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
                List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, (double)pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius));

                if (!list.isEmpty()) {
                    for (EntityLiving obj : list) {
                        EntityLiving entityliving = obj;
                        if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player && TileEntityTreadmill.isEntityValidForTreadmill(entityliving)) {
                            if (!world.isRemote) {
                                treadmill.latchedEnt = entityliving;
                                treadmill.latchedHealth = entityliving.getHealth();
                                entityliving.setLocationAndAngles(treadmill.getMidCoord(0), pos.getY() + 0.175D, treadmill.getMidCoord(1), treadmill.face.getOpposite().getHorizontalAngle(), 0.0F);
                                world.notifyBlockUpdate(pos, state, state.withProperty(TYPE, EnumType.TREADMILL), 3);
                                entityliving.clearLeashed(true, !player.capabilities.isCreativeMode);
                            }
                            return true;
                        }
                    }
                }

                ItemStack itemStack = player.getActiveItemStack();
                //Allow easier creative testing. Only works for pig and wolves cause easier
                if (itemStack != null && itemStack.getItem() instanceof ItemMonsterPlacer && (itemStack.getItemDamage() == 90 || itemStack.getItemDamage() == 95)) {
                    if (!world.isRemote) {
                        Entity entity = ItemMonsterPlacer.spawnCreature(world, ItemMonsterPlacer.getEntityIdFromItem(itemStack), treadmill.getMidCoord(0), pos.getY() + 0.175D, treadmill.getMidCoord(1));
                        if (TileEntityTreadmill.isEntityValidForTreadmill(entity)) {
                            treadmill.latchedEnt = (EntityLiving)entity;
                            treadmill.latchedHealth = ((EntityLiving)entity).getHealth();
                            entity.setLocationAndAngles(treadmill.getMidCoord(0), pos.getY() + 0.175D, treadmill.getMidCoord(1), treadmill.face.getOpposite().getHorizontalAngle(), 0.0F);
                            world.notifyBlockUpdate(pos, state, state.withProperty(TYPE, EnumType.TREADMILL), 3);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
//        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityShellStorage) {
            TileEntityShellStorage shellStorage = (TileEntityShellStorage) tileEntity;

            if ((shellStorage.top && shellStorage.pair != null && shellStorage.pair.occupied || shellStorage.occupied) && shellStorage.getWorld().isRemote && isLocalPlayer(shellStorage.getPlayerName())) {
                double dist = getDistance(pos);

                if (dist < (shellStorage.top ? 1.1D : 0.6D)) {
                    boundingBox =  new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else if (tileEntity instanceof TileEntityTreadmill) {
            boundingBox = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4D, 1.0D);
        }
        return boundingBox;
    }

    @SideOnly(Side.CLIENT)
    public double getDistance(BlockPos pos) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        double d3 = player.posX - (pos.getX() + 0.5D);
        double d4 = player.getEntityBoundingBox().minY - pos.getY();
        double d5 = player.posZ - (pos.getZ() + 0.5D);

        return (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        //Ignore non players and fake players
        if (!(entity instanceof EntityPlayer) || (entity instanceof FakePlayer)) {
            return;
        }

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDualVertical) {
            TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
            if (dualVertical.top) {
                TileEntity tileEntityPair = world.getTileEntity(pos.down());
                if (tileEntityPair instanceof TileEntityDualVertical) {
                    this.onEntityCollidedWithBlock(world, pos.down(), state, entity);
                }
            }
            else {
                if (dualVertical instanceof TileEntityShellStorage) {
                    TileEntityShellStorage shellStorage = (TileEntityShellStorage) dualVertical;
                    if (!shellStorage.occupied && !world.isRemote && !shellStorage.syncing && shellStorage.resyncPlayer <= -10) {
                        double d3 = entity.posX - (pos.getX() + 0.5D);
                        double d4 = entity.getEntityBoundingBox().minY - pos.getY();
                        double d5 = entity.posZ - (pos.getZ() + 0.5D);
                        double dist = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                        if (dist < 0.3D && shellStorage.isPowered()) {
                            EntityPlayer player = (EntityPlayer)entity;

                            if (iChunUtil.hasMorphMod() && MorphApi.getApiImpl().hasMorph(player.getName(), Side.SERVER)) {
                                player.sendMessage(new TextComponentTranslation("sync.isMorphed"));
                            }
                            else {
                                Sync.channel.sendTo(new PacketPlayerEnterStorage(pos), player);
                                player.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, shellStorage.face.getOpposite().getHorizontalAngle(), 0F);
                            }

                            //Mark this as in use
                            shellStorage.setPlayerName(player.getName());
                            shellStorage.occupied = true;
                            notifyThisAndAbove(state, EnumType.STORAGE, pos, world, dualVertical.top);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> list, Entity entity)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDualVertical) {
            TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
            boolean top = false;
            if (dualVertical.top) {
                TileEntity tileEntityPair = world.getTileEntity(pos.add(0, -1, 0));
                if (tileEntityPair instanceof TileEntityDualVertical) {
                    dualVertical = (TileEntityDualVertical) tileEntityPair;
                }
                top = true;
            }

            if (dualVertical instanceof TileEntityShellConstructor) {
                TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) dualVertical;
                if (shellConstructor.doorOpen) {
                    this.setDualVerticalCollisionBoxes(dualVertical, 0.05F, top, world, pos, aabb, list, entity);
                }
                else {
                    super.addCollisionBoxToList(state, world, pos, aabb, list, entity);
                }
            }
            else if (dualVertical instanceof TileEntityShellStorage) {
                TileEntityShellStorage shellStorage = (TileEntityShellStorage)dualVertical;
                if ((!shellStorage.occupied || (world.isRemote && this.isLocalPlayer(shellStorage.getPlayerName()))) && !shellStorage.syncing) {
                    this.setDualVerticalCollisionBoxes(dualVertical, 0.05F, top, world, pos, aabb, list, entity);
                }
                else {
                    super.addCollisionBoxToList(state, world, pos, aabb, list, entity);
                }
            }
        }
        else if(tileEntity instanceof TileEntityTreadmill) {
            boundingBox = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.175F, 1.0F);
            super.addCollisionBoxToList(state, world, pos, aabb, list, entity);
        }
        boundingBox = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    //This makes the sides solid but not the front
    private void setDualVerticalCollisionBoxes(TileEntityDualVertical dualVertical, float thickness, boolean isTop, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> list, Entity entity) {
        if (dualVertical.face != EnumFacing.SOUTH) {
            boundingBox = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, thickness);
            super.addCollisionBoxToList(world.getBlockState(pos), world, pos, aabb, list, entity);
        }
        if (dualVertical.face != EnumFacing.WEST) {
            boundingBox = new AxisAlignedBB(1.0F - thickness, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxToList(world.getBlockState(pos), world, pos, aabb, list, entity);
        }
        if (dualVertical.face != EnumFacing.NORTH) {
            boundingBox = new AxisAlignedBB(0.0F, 0.0F, 1.0F - thickness, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxToList(world.getBlockState(pos), world, pos, aabb, list, entity);
        }
        if (dualVertical.face != EnumFacing.EAST) {
            boundingBox = new AxisAlignedBB(0.0F, 0.0F, 0.0F, thickness, 1.0F, 1.0F);
            super.addCollisionBoxToList(world.getBlockState(pos), world, pos, aabb, list, entity);
        }
        if (isTop) {
            boundingBox = new AxisAlignedBB(0.0F, 1.0F - thickness / 2, 0.0F, 1.0F, 1.0F, 1.0F);
            super.addCollisionBoxToList(world.getBlockState(pos), world, pos, aabb, list, entity);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isLocalPlayer(String playerName) {
        return playerName != null && Minecraft.getMinecraft().player != null && playerName.equalsIgnoreCase(Minecraft.getMinecraft().player.getName());
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDualVertical) {
            TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
            if (!dualVertical.top && !world.getBlockState(pos.add(0, -1, 0)).getBlock().isOpaqueCube(world.getBlockState(pos.add(0, -1, 0)))) {
                world.setBlockToAir(pos);
            }
        }
        if (tileEntity instanceof TileEntityTreadmill) {
            if (world.getTileEntity(pos.add(0, -1, 0)) instanceof TileEntityTreadmill) {
                world.setBlockToAir(pos);
            }
        }
    }



    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityDualVertical) {
                TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
                BlockPos newPos = dualVertical.top ? pos.down() : pos.up();
                TileEntity tileEntityPair = world.getTileEntity(newPos);
                if (tileEntityPair instanceof TileEntityDualVertical) {
                    TileEntityDualVertical dualVerticalPair = (TileEntityDualVertical) tileEntityPair;
                    if (dualVerticalPair.pair == dualVertical) {
                        world.playEvent(2001, newPos, Block.getIdFromBlock(Sync.blockDualVertical));
                        world.setBlockToAir(newPos);
                    }
                    TileEntityDualVertical bottom = dualVerticalPair.top ? dualVertical : dualVerticalPair;

                    if (!bottom.getPlayerName().equalsIgnoreCase("") && !bottom.getPlayerName().equalsIgnoreCase(player.getName())) {
                        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendChatMsg(new TextComponentTranslation("sync.breakShellUnit", player.getName(), bottom.getPlayerName()));
                    }

                    if (!player.capabilities.isCreativeMode) {
                        float f = 0.5F;
                        double d = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                        double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                        double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                        BlockPos dualVerticalPos = dualVertical.getPos();
                        EntityItem entityitem = new EntityItem(world, (double)dualVerticalPos.getX() + d, (double)dualVerticalPos.getY() + d1, (double)dualVerticalPos.getZ() + d2, new ItemStack(dualVertical instanceof TileEntityShellConstructor ? Sync.itemShellConstructor : Sync.itemShellStorage, 1));
                        entityitem.setPickupDelay(10);
                        world.spawnEntity(entityitem);
                    }
                }
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }



    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDualVertical) {
            TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
            BlockPos newPos = dualVertical.top ? pos.down() : pos.up();
            TileEntity tileEntityPair = world.getTileEntity(newPos);
            if (tileEntityPair instanceof TileEntityDualVertical) {
                TileEntityDualVertical dualVerticalPair = (TileEntityDualVertical) tileEntityPair;
                //Confirm they are linked then remove
                if (dualVerticalPair.pair == dualVertical) {
                    world.playEvent(2001, newPos, Block.getIdFromBlock(Sync.blockDualVertical));
                    world.setBlockToAir(newPos);
                }
                TileEntityDualVertical dualVerticalBottom = dualVerticalPair.top ? dualVertical : dualVerticalPair;

                if (!world.isRemote) {
                    //TODO Should we treat this as an actual player death in terms of drops?
                    if (dualVerticalBottom instanceof TileEntityShellStorage && dualVerticalBottom.resyncPlayer == -10 && ((TileEntityShellStorage) dualVertical).syncing && dualVertical.getPlayerNBT().hasKey("Inventory")) {
                        FakePlayer fake = new FakePlayer((WorldServer)world, EntityHelper.getGameProfile(dualVertical.getPlayerName()));
                        fake.readFromNBT(dualVertical.getPlayerNBT());
                        fake.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, dualVertical.face.getOpposite().getHorizontalAngle(), 0F);

                        fake.captureDrops = true;
                        fake.capturedDrops.clear();
                        fake.inventory.dropAllItems();
                        fake.captureDrops = false;

                        PlayerDropsEvent event = new PlayerDropsEvent(fake, DamageSource.outOfWorld, fake.capturedDrops, false);
                        if (!MinecraftForge.EVENT_BUS.post(event)) {
                            for (EntityItem item : fake.capturedDrops) {
                                fake.dropItemAndGetStack(item);
                            }
                        }
                        Sync.channel.sendToAllAround(new PacketShellDeath(pos, dualVerticalBottom.face), new NetworkRegistry.TargetPoint(dualVertical.getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64D));
                    }
                    else if (dualVerticalBottom instanceof TileEntityShellConstructor) {
                        TileEntityShellConstructor shellConstructor = (TileEntityShellConstructor) dualVerticalBottom;
                        if (!shellConstructor.getPlayerName().equalsIgnoreCase("") && shellConstructor.constructionProgress >= Sync.config.shellConstructionPowerRequirement) {
                            Sync.channel.sendToAllAround(new PacketShellDeath(dualVerticalBottom.getPos(), dualVerticalBottom.face), new NetworkRegistry.TargetPoint(dualVertical.getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64D));
                        }
                    }
                    ShellHandler.removeShell(dualVerticalBottom.getPlayerName(), dualVerticalBottom);
                    //If sync is in progress, cancel and kill the player syncing
                    if (dualVerticalBottom.resyncPlayer > 25 && dualVerticalBottom.resyncPlayer < 120) {
                        ShellHandler.syncInProgress.remove(dualVerticalBottom.getPlayerName());
                        //Need to let dualVertical know sync is cancelled
                        EntityPlayerMP syncingPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(dualVertical.getPlayerName());
                        if (syncingPlayer != null) {
                            String name = DamageSource.outOfWorld.damageType;
                            DamageSource.outOfWorld.damageType = "syncFail";
                            syncingPlayer.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
                            DamageSource.outOfWorld.damageType = name;
                        }
                    }
                }
            }
        }
        else if (tileEntity instanceof TileEntityTreadmill) {
            TileEntityTreadmill treadmill = (TileEntityTreadmill) tileEntity;
            BlockPos toClean = new BlockPos(treadmill.back ? (treadmill.face == EnumFacing.WEST ? pos.getX() + 1 : treadmill.face == EnumFacing.EAST ? pos.getX() - 1 : pos.getX()) : (treadmill.face == EnumFacing.WEST ? pos.getX() - 1 : treadmill.face == EnumFacing.EAST ? pos.getX() + 1 : pos.getX()), pos.getY(), treadmill.back ? (treadmill.face == EnumFacing.SOUTH ? pos.getZ() - 1 : treadmill.face == EnumFacing.NORTH ? pos.getZ() + 1 : pos.getZ()) : (treadmill.face == EnumFacing.SOUTH ? pos.getZ() + 1 : treadmill.face == EnumFacing.NORTH ? pos.getZ() - 1 : pos.getZ()));
            TileEntity tileEntityPair = world.getTileEntity(toClean);

            if (tileEntityPair instanceof TileEntityTreadmill) {
                TileEntityTreadmill treadmillPair = (TileEntityTreadmill)tileEntityPair;
                if (treadmillPair.pair == treadmill) {
                    world.playEvent(2001, pos, Block.getIdFromBlock(Sync.blockDualVertical));
                    world.setBlockToAir(toClean);
                }

                if (!treadmillPair.back && !world.isRemote) {
                    float f = 0.5F;
                    double d = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d, (double)pos.getY() + d1, (double)pos.getZ() + d2, new ItemStack(Sync.itemTreadmill, 1));
                    entityitem.setPickupDelay(10);
                    world.spawnEntity(entityitem);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return new ItemStack(EnumType.getItemForType(world.getBlockState(pos).getValue(BlockDualVertical.TYPE)), 1);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        if (world.getTileEntity(pos) instanceof TileEntityDualVertical) {
            TileEntityDualVertical tileEntityDualVertical = (TileEntityDualVertical) world.getTileEntity(pos);
            return (int) Math.floor(tileEntityDualVertical.getBuildProgress() / (Sync.config.shellConstructionPowerRequirement / 15));
        }
        else return 0;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDualVertical) {
            TileEntityDualVertical dualVertical = (TileEntityDualVertical) tileEntity;
            if (side == EnumFacing.DOWN || side == EnumFacing.UP) return true;
            if (!(tileEntity instanceof TileEntityShellConstructor)) {
                return side == dualVertical.face;
            }
        }
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        switch (state.getValue(TYPE)) {
            case CONSTRUCTOR:
                return 0;
            case STORAGE:
                return 1;
            case TREADMILL:
                return 2;
            default:
                throw new RuntimeException("Unknown state value " + state.getValue(TYPE));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (meta) {
            case 0:
                return this.getDefaultState().withProperty(TYPE, EnumType.CONSTRUCTOR);
            case 1:
                return this.getDefaultState().withProperty(TYPE, EnumType.STORAGE);
            case 2:
                return this.getDefaultState().withProperty(TYPE, EnumType.TREADMILL);
            default:
                throw new RuntimeException("Don't know how to convert " + meta + " to state");
        }
    }

    private static void notifyThisAndAbove(IBlockState oldState, EnumType newType, BlockPos thisPos, World world, boolean isTop) {
        BlockPos other = isTop ? thisPos.down() : thisPos.up();
        IBlockState above = world.getBlockState(other);
        world.notifyBlockUpdate(thisPos, oldState, oldState.withProperty(TYPE, newType), 3);
        world.notifyBlockUpdate(other, above, above.withProperty(TYPE, newType), 3);
    }

}
