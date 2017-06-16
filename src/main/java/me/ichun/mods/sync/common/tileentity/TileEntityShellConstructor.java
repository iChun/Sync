package me.ichun.mods.sync.common.tileentity;

import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.shell.ShellHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

//@Optional.Interface(iface = "cofh.api.tileentity.IEnergyInfo", modid = "CoFHCore")
public class TileEntityShellConstructor extends TileEntityDualVertical<TileEntityShellConstructor> implements IEnergyStorage
{
	public float constructionProgress;
	public int doorTime;
	public int rfBuffer;
	public boolean doorOpen;
	public float prevPower;

	public TileEntityShellConstructor() {
		super();
		
		constructionProgress = 0.0F;
		
		doorTime = 0;
		
		doorOpen = false;
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if(top && pair != null)
		{
			constructionProgress = pair.constructionProgress;
			doorOpen = pair.doorOpen;
		}
		if(isPowered())
		{
			float power = powerAmount();
			if(world.getWorldTime() % 200L == 0 && prevPower != power)
			{
				prevPower = power;
				if(!top && !world.isRemote)
				{
					EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
					if(player != null)
					{
						ShellHandler.updatePlayerOfShells(player, null, true);
					}
				}
			}
			constructionProgress += power;
			if(constructionProgress > Sync.config.shellConstructionPowerRequirement)
			{
				constructionProgress = Sync.config.shellConstructionPowerRequirement;
			}

            /*
			if(world.isRemote && !top)
			{
				spawnParticles();
			}
			*/

			//Notifies neighbours of block update, used for comparator
			if (world.getWorldTime() % 40L == 0) world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(getPos()).getBlock());
		}
		if(!top)
		{
			if(doorOpen)
			{
				if(doorTime < animationTime)
				{
					doorTime++;
				}
				if(!world.isRemote && doorTime == animationTime)
				{
					BlockPos pos = getPos();
					List list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
					if(list.isEmpty())
					{
						doorOpen = false;

						IBlockState state = world.getBlockState(pos);
						world.notifyBlockUpdate(pos, state, state, 3);
					}
				}
			}
			else
			{
				if(doorTime > 0)
				{
					doorTime--;
				}
			}
			if(!world.isRemote && !playerName.equalsIgnoreCase("") && !ShellHandler.isShellAlreadyRegistered(this))
			{
				ShellHandler.addShell(playerName, this, true);
			}
		}
		if(!top && !world.isRemote)
		{
			rfBuffer += Math.abs(powReceived - rfIntake);
			//If buffer has exceeded 5% of shell build, or if rfIntake has changed more than 10% of previous tick's, resync
			if((float)rfBuffer / (float)Sync.config.shellConstructionPowerRequirement > 0.05F || Math.abs((float)(powReceived - rfIntake) / (float)powReceived) > 0.1F)
			{
				rfIntake = powReceived;
				rfBuffer = 0;
				IBlockState state = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, state, state, 3);
			}
			powReceived = 0;
		}
	}
	
	public boolean isPowered()
	{
		if(top && pair != null)
		{
			return pair.isPowered();
		}
		return !playerName.equalsIgnoreCase("");
	}

	@Override
	public float getBuildProgress()
	{
		return constructionProgress;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag = super.writeToNBT(tag);
		tag.setFloat("constructionProgress", constructionProgress);
		tag.setBoolean("doorOpen", doorOpen);
		return tag;
	}
	 
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		constructionProgress = tag.getFloat("constructionProgress");
		doorOpen = tag.getBoolean("doorOpen");
		resync = true;
	}

	@Override
	public void reset() {
		super.reset();
		this.constructionProgress = 0F;
	}

	// Energy

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY && !top)
			return true;
		return super.hasCapability(capability, facing);
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
		int powReq = Math.max((int)Math.ceil(Sync.config.shellConstructionPowerRequirement - constructionProgress), 0);
		if(powReq == 0 || playerName.equalsIgnoreCase(""))
		{
			return 0;
		}
		int pow = maxReceive;
		if(pow > 24)
		{
			pow = 24;
		}
		if(pow > powReq)
		{
			pow = powReq;
		}
		if(!simulate)
		{
			powReceived += (float)pow * (float)Sync.config.ratioRF;
		}
		return pow;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean doExtract) {
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
		return (int)Math.ceil(Sync.config.shellConstructionPowerRequirement - constructionProgress) > 0;
	}
}
