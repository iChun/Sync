package sync.common.tileentity;

import cofh.api.core.IEnergyInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import sync.common.Sync;
import sync.common.shell.ShellHandler;

import java.util.List;

@Optional.Interface(iface = "cofh.api.tileentity.IEnergyInfo", modid = "ThermalExpansion")
public class TileEntityShellConstructor extends TileEntityDualVertical implements IEnergyInfo
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
	public void updateEntity()
	{
		super.updateEntity();
		
		if(top && pair != null)
		{
			constructionProgress = ((TileEntityShellConstructor)pair).constructionProgress;
			doorOpen = ((TileEntityShellConstructor)pair).doorOpen;
		}
		if(isPowered())
		{
			float power = powerAmount();
			if(worldObj.getWorldTime() % 200L == 0 && prevPower != power)
			{
				prevPower = power;
				if(!top && !worldObj.isRemote)
				{
					EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName);
					if(player != null)
					{
						ShellHandler.updatePlayerOfShells(player, null, true);
					}
				}
			}
			constructionProgress += power;
			if(constructionProgress > Sync.config.getSessionInt("shellConstructionPowerRequirement"))
			{
				constructionProgress = Sync.config.getSessionInt("shellConstructionPowerRequirement");
			}

            /*
			if(worldObj.isRemote && !top)
			{
				spawnParticles();
			}
			*/

			//Notifies neighbours of block update, used for comparator
			if (worldObj.getWorldTime() % 40L == 0) worldObj.func_147453_f(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
		}
		if(!top)
		{
			if(doorOpen)
			{
				if(doorTime < TileEntityShellStorage.animationTime)
				{
					doorTime++;
				}
				if(!worldObj.isRemote && doorTime == TileEntityShellStorage.animationTime)
				{
					List list = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
					if(list.isEmpty())
					{
						doorOpen = false;
						
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
			if(!worldObj.isRemote && !playerName.equalsIgnoreCase("") && !ShellHandler.isShellAlreadyRegistered(this))
			{
				ShellHandler.addShell(playerName, this, true);
			}
		}
		if(!top && !worldObj.isRemote) 
		{
			rfBuffer += Math.abs(powReceived - rfIntake);
			//If buffer has exceeded 5% of shell build, or if rfIntake has changed more than 10% of previous tick's, resync
			if((float)rfBuffer / (float)Sync.config.getSessionInt("shellConstructionPowerRequirement") > 0.05F || Math.abs((float)(powReceived - rfIntake) / (float)powReceived) > 0.1F)
			{
				rfIntake = powReceived;
				rfBuffer = 0;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			powReceived = 0;
		}
	}
	
	public void setup(TileEntityDualVertical scPair, boolean isTop, int placeYaw)
	{
		pair = scPair;
		top = isTop;
		face = placeYaw;
	}
	
	public boolean isPowered()
	{
		if(top && pair != null)
		{
			return ((TileEntityShellConstructor)pair).isPowered();
		}
		return !playerName.equalsIgnoreCase("");
	}

	@Override
	public float getBuildProgress()
	{
		return constructionProgress;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setFloat("constructionProgress", constructionProgress);
		tag.setBoolean("doorOpen", doorOpen);
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
	
	// TE methods
	@Override
	@Optional.Method(modid = "CoFHCore")
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		int powReq = Math.max((int)Math.ceil(Sync.config.getSessionInt("shellConstructionPowerRequirement") - constructionProgress), 0);
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
			powReceived += (float)pow * (float)Sync.config.getInt("ratioRF");
		}
		return pow;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean doExtract)
	{
		return 0;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return !top;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public int getEnergyStored(ForgeDirection from)
	{
		return 0;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return 0;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public int getInfoEnergyPerTick() {
		return powReceived;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public int getInfoMaxEnergyPerTick() {
		return 24;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public int getInfoEnergyStored() {
		return 0;
	}

	@Override
	@Optional.Method(modid = "CoFHCore")
	public int getInfoMaxEnergyStored() {
		return 0;
	}
}
