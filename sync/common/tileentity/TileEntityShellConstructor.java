package sync.common.tileentity;

import cofh.api.energy.IEnergyHandler;
import cofh.api.tileentity.IEnergyInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import sync.common.Sync;
import sync.common.core.ChunkLoadHandler;
import sync.common.core.SessionState;
import sync.common.shell.ShellHandler;

import java.util.List;

public class TileEntityShellConstructor extends TileEntityDualVertical 
	implements IEnergyHandler, IEnergyInfo
{
	public float constructionProgress;
	
	public int doorTime;
	public int powReceived;
	
	public int rfIntake;
	
	public int rfBuffer;
	
	public boolean doorOpen;
	
	public float prevPower;

	public TileEntityShellConstructor()
	{
		super();
		
		constructionProgress = 0.0F;
		
		doorTime = 0;
		
		doorOpen = false;
		powReceived = 0;

		rfIntake = 0;
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
			if(constructionProgress > SessionState.shellConstructionPowerRequirement)
			{
				constructionProgress = SessionState.shellConstructionPowerRequirement;
			}
			
			if(worldObj.isRemote && !top)
			{
				spawnParticles();
			}

			//Notifies neighbours of block update, used for comparator
			if (worldObj.getWorldTime() % 40L == 0) worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
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
					List list = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
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
			if(!worldObj.isRemote && !playerName.equalsIgnoreCase("") && !ChunkLoadHandler.shellTickets.containsKey(this))
			{
				ShellHandler.addShell(playerName, this, true);
			}
		}
		if(!top && !worldObj.isRemote) 
		{
			rfBuffer += Math.abs(powReceived - rfIntake);
			if((float)rfBuffer / (float)SessionState.shellConstructionPowerRequirement > 0.05F || Math.abs((float)(powReceived - rfIntake) / (float)powReceived) > 0.1F) // If buffer has exceeded 5% of shell build, or if rfIntake has changed more than 10% of previous tick's, resync
			{
				rfIntake = powReceived;
				rfBuffer = 0;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		powReceived = 0;
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

	@Override
	public float getBuildProgress()
	{
		return constructionProgress;
	}
	
	@SideOnly(Side.CLIENT)
	public void spawnParticles()
	{
//		float prog = MathHelper.clamp_float(this.constructionProgress, 0.0F, SessionState.shellConstructionPowerRequirement) / (float)SessionState.shellConstructionPowerRequirement;
//		if(prog > 0.95F)
//		{
//			float angle = 0;
//			
//			System.out.println(face);
//			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityPaintFX(worldObj, xCoord + 0.5D , yCoord + 0.5D, zCoord + 0.5D, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 1.0F));
//		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setFloat("constructionProgress", constructionProgress);
		tag.setBoolean("doorOpen", doorOpen);
		tag.setInteger("rfIntake", rfIntake);
	}
	 
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		constructionProgress = tag.getFloat("constructionProgress");
		doorOpen = tag.getBoolean("doorOpen");
		rfIntake = tag.getInteger("rfIntake");
		
		resync = true;
	}
	
	// TE methods
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		int powReq = Math.max((int)Math.ceil(SessionState.shellConstructionPowerRequirement - constructionProgress), 0);
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
			powReceived += (float)pow * (float)Sync.ratioRF;
		}
		return pow;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean doExtract)
	{
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from)
	{
		return !top;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return 0;
	}

	@Override
	public int getEnergyPerTick() {
		return powReceived;
	}

	@Override
	public int getMaxEnergyPerTick() {
		return 24;
	}

	@Override
	public int getEnergy() {
		return 0;
	}

	@Override
	public int getMaxEnergy() {
		return 0;
	}
}
