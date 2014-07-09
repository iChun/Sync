package sync.common.tileentity;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import sync.common.Sync;
import sync.common.shell.ShellHandler;

public class TileEntityShellStorage extends TileEntityDualVertical
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
	public void updateEntity()
	{
		if(resync)
		{
			if(worldObj.isRemote && !playerName.equalsIgnoreCase("") && !prevPlayerName.equals(playerName) && syncing)
			{
				playerInstance = createPlayer(worldObj, playerName);
				prevPlayerName = playerName;
				if(playerNBT.hasKey("Inventory"))
				{
					playerInstance.readFromNBT(playerNBT);
				}
				worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
			}
		}
		if(top && pair != null)
		{
			TileEntityShellStorage ss = (TileEntityShellStorage)pair;
			occupied = ss.occupied;
			syncing = ss.syncing;
			hasPower = ss.hasPower;
			
			playerInstance = ss.playerInstance;
			
			prevPlayerName = ss.prevPlayerName;
			occupationTime = ss.occupationTime;
		}
		super.updateEntity();
		
		if(!top && occupied && !worldObj.isRemote && !syncing)
		{
			EntityPlayer player = worldObj.getPlayerEntityByName(playerName);
			if(player != null)
			{
				double d3 = player.posX - (xCoord + 0.5D);
				double d4 = player.boundingBox.minY - yCoord;
				double d5 = player.posZ - (zCoord + 0.5D);
				double dist = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);

				if(dist > 0.75D)
				{
					occupied = false;
					playerName = "";
					
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
				}
			}
			else
			{
				occupied = false;
				playerName = "";
				
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
			}
		}
		if(syncing && occupationTime > 0)
		{
			occupationTime--;
			if(occupationTime == 0)
			{
				if(vacating)
				{
					if(!worldObj.isRemote && !top )
					{
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
						ShellHandler.removeShell(playerName, this);
					}
					vacating = false;
					occupied = false;
					syncing = false;
					prevPlayerName = playerName = "";
					playerNBT = new NBTTagCompound();
				}
				else if(!worldObj.isRemote && occupied && isPowered() && !playerName.equalsIgnoreCase("") && !top && !ShellHandler.isShellAlreadyRegistered(this))
				{
					ShellHandler.addShell(playerName, this, true);
					worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
				}
			}
		}
		if(!worldObj.isRemote && !top)
		{
			if(!isPowered() && ShellHandler.isShellAlreadyRegistered(this))
			{
				ShellHandler.removeShell(playerName, this);
				hasPower = false;
				worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
			}
			else if(playerNBT.hasKey("Inventory") && isPowered() && !playerName.equalsIgnoreCase("") && (!ShellHandler.isShellAlreadyRegistered(this))) {
				ShellHandler.addShell(playerName, this, true);
				hasPower = true;
				worldObj.func_96440_m(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
			}

			if (powerAmount() >= Sync.shellStoragePowerRequirement && !hasPower) {
				hasPower = true;
				ShellHandler.addShell(playerName, this, true);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			if (powerAmount() < Sync.shellStoragePowerRequirement && hasPower) {
				hasPower = false;
				ShellHandler.removeShell(playerName, this);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			powReceived = 0;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static EntityPlayer createPlayer(World world, String playerName) 
	{
		return new EntityOtherPlayerMP(world, playerName);
	}

	public boolean isPowered() {
		if (top && pair != null) {
			return ((TileEntityShellStorage)pair).isPowered();
		}
		return (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) || worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord + 1, zCoord)) && hasPower;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setBoolean("occupied", occupied);
		tag.setBoolean("syncing", canSavePlayer <= 0 && syncing);
		tag.setBoolean("hasPower", hasPower);
		tag.setInteger("occupationTime", occupationTime);
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

	@Override
	@Optional.Method(modid = "ThermalExpansion")
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (Sync.shellStoragePowerRequirement == 0) {
			return 0;
		}
		int pow = maxReceive;
		if (pow > Sync.shellStoragePowerRequirement) {
			pow = Sync.shellStoragePowerRequirement;
		}
		if (!simulate) {
			powReceived += (float)pow * (float)Sync.ratioRF;
		}
		return pow;
	}

	@Override
	@Optional.Method(modid = "ThermalExpansion")
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	@Optional.Method(modid = "ThermalExpansion")
	public boolean canInterface(ForgeDirection from) {
		return !top;
	}

	@Override
	@Optional.Method(modid = "ThermalExpansion")
	public int getEnergyStored(ForgeDirection from) {
		return 0;
	}

	@Override
	@Optional.Method(modid = "ThermalExpansion")
	public int getMaxEnergyStored(ForgeDirection from) {
		return 0;
	}
}
