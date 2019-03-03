package me.ichun.mods.sync.common.tileentity;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.shell.ShellHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class TileEntityShellStorage extends TileEntityDualVertical<TileEntityShellStorage> implements IEnergyStorage
{
    public boolean occupied;
    public boolean syncing;
    public boolean hasPower;

    public EntityPlayer playerInstance;

    public String prevPlayerName;

    public int occupationTime;

    public TileEntityShellStorage()
    {
        super();
        occupied = false;
        syncing = false;
        hasPower = true;

        playerInstance = null;

        prevPlayerName = "";

        occupationTime = 0;
    }

    @Override
    public void update()
    {
        if(resync)
        {
            if(world.isRemote && !playerName.equalsIgnoreCase("") && !prevPlayerName.equals(playerName) && syncing)
            {
                playerInstance = createPlayer(world, playerUUID, playerName);
                prevPlayerName = playerName;
                if(playerNBT.hasKey("Inventory"))
                {
                    playerInstance.readFromNBT(playerNBT);
                }
                world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
            }
        }
        if(top && pair != null)
        {
            occupied = pair.occupied;
            syncing = pair.syncing;
            hasPower = pair.hasPower;

            playerInstance = pair.playerInstance;

            prevPlayerName = pair.prevPlayerName;
            occupationTime = pair.occupationTime;
        }
        super.update();

        if(!top && occupied && !world.isRemote && !syncing)
        {
            EntityPlayer player = world.getPlayerEntityByName(playerName);
            if(player != null)
            {
                double d3 = player.posX - (pos.getX() + 0.5D);
                double d4 = player.getEntityBoundingBox().minY - pos.getY();
                double d5 = player.posZ - (pos.getZ() + 0.5D);
                double dist = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                if(dist > 0.75D)
                {
                    occupied = false;
                    playerName = "";

                    IBlockState state = world.getBlockState(pos);
                    world.notifyBlockUpdate(pos, state, state, 3);
                    world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
                }
            }
            else
            {
                occupied = false;
                playerName = "";

                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
                world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
            }
        }
        if(syncing && occupationTime > 0)
        {
            occupationTime--;
            if(occupationTime == 0)
            {
                if(vacating)
                {
                    if(!world.isRemote && !top )
                    {
                        IBlockState state = world.getBlockState(pos);
                        world.notifyBlockUpdate(pos, state, state, 3);
                        world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
                        ShellHandler.removeShell(playerName, this);
                    }
                    vacating = false;
                    occupied = false;
                    syncing = false;
                    prevPlayerName = playerName = "";
                    playerNBT = new NBTTagCompound();
                }
                else if(!world.isRemote && occupied && isPowered() && !playerName.equalsIgnoreCase("") && !top && !ShellHandler.isShellAlreadyRegistered(this))
                {
                    ShellHandler.addShell(playerName, this, true);
                    world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
                }
            }
        }
        if(!world.isRemote && !top)
        {
            if(!isPowered() && ShellHandler.isShellAlreadyRegistered(this))
            {
                ShellHandler.removeShell(playerName, this);
                hasPower = false;
                world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
            }
            else if(playerNBT.hasKey("Inventory") && isPowered() && !playerName.equalsIgnoreCase("") && (!ShellHandler.isShellAlreadyRegistered(this))) {
                ShellHandler.addShell(playerName, this, true);
                hasPower = true;
                world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
            }

            if (powerAmount() >= Sync.config.shellStoragePowerRequirement && !hasPower) {
                hasPower = true;
                ShellHandler.addShell(playerName, this, true);
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
            if (powerAmount() < Sync.config.shellStoragePowerRequirement && hasPower) {
                hasPower = false;
                ShellHandler.removeShell(playerName, this);
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
            powReceived = 0;
        }
    }

    @SideOnly(Side.CLIENT)
    public static EntityPlayer createPlayer(World world, UUID uuid, String playerName)
    {
        return new EntityOtherPlayerMP(world, uuid == null ? EntityHelper.getGameProfile(playerName) : EntityHelper.getGameProfile(uuid, playerName));
    }

    public boolean isPowered() {
        if (top && pair != null) {
            return pair.isPowered();
        }
        return (world.getRedstonePowerFromNeighbors(pos) != 0 || world.getRedstonePowerFromNeighbors(pos.up()) != 0) && hasPower;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag = super.writeToNBT(tag);
        tag.setBoolean("occupied", occupied);
        tag.setBoolean("syncing", canSavePlayer <= 0 && syncing);
        tag.setBoolean("hasPower", hasPower);
        tag.setInteger("occupationTime", occupationTime);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        occupied = tag.getBoolean("occupied");
        syncing = tag.getBoolean("syncing");
        hasPower = tag.getBoolean("hasPower");
        occupationTime = tag.getInteger("occupationTime");
        resync = true;
    }

    @Override
    public void reset() {
        super.reset();
        this.syncing = false;
        this.playerInstance = null;
        this.vacating = false;
        this.occupied = false;
        this.occupationTime = 0;
        this.prevPlayerName = "";
    }

    //----------ENERGY METHODS----------

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY && !top || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            //noinspection unchecked
            return (T) this;
        return super.getCapability(capability, facing);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (Sync.config.shellStoragePowerRequirement == 0) {
            return 0;
        }
        int pow = maxReceive;
        if (pow > Sync.config.shellStoragePowerRequirement) {
            pow = Sync.config.shellStoragePowerRequirement;
        }
        if (!simulate) {
            powReceived += (float)pow * (float)Sync.config.ratioRF;
        }
        return pow;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return !top && Sync.config.shellStoragePowerRequirement != 0;
    }
}
