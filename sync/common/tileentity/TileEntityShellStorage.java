package sync.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import sync.common.core.SessionState;
import sync.common.item.ChunkLoadHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityShellStorage extends TileEntityDualVertical 
{
	
	public boolean occupied;
	public boolean vacating;
	
	public EntityPlayer playerInstance;
	
	public String prevPlayerName;
	
	public int occupationTime;
	
	public NBTTagCompound playerNBT;
	
	public final static int animationTime = 40;
	
	public TileEntityShellStorage()
	{
		super();
		occupied = false;
		vacating = false;
		
		playerInstance = null;
		
		prevPlayerName = "";
		
		occupationTime = 0;
		
		playerNBT = new NBTTagCompound();
	}
	
	@Override
	public void updateEntity()
	{
		if(resync)
		{
			if(worldObj.isRemote && !playerName.equalsIgnoreCase("") && !prevPlayerName.equals(playerName))
			{
				playerInstance = createPlayer(playerName);
				prevPlayerName = playerName;
				playerInstance.readFromNBT(playerNBT);
			}
		}
		super.updateEntity();
		
		if(occupationTime > 0)
		{
			occupationTime--;
			if(occupationTime == 0)
			{
				if(vacating)
				{
					vacating = false;
					occupied = false;
					if(!worldObj.isRemote && !top )
					{
						ChunkLoadHandler.removeShellAsChunkloader(this);
					}
				}
				else if(!worldObj.isRemote && occupied && isPowered() && !top && !ChunkLoadHandler.shellTickets.containsKey(this))
				{
					ChunkLoadHandler.addShellAsChunkloader(this);
				}
			}
		}
		if(!worldObj.isRemote && !top)
		{
			if(!isPowered() && ChunkLoadHandler.shellTickets.containsKey(this))
			{
				ChunkLoadHandler.removeShellAsChunkloader(this);
			}
			else if(isPowered() && !ChunkLoadHandler.shellTickets.containsKey(this))
			{
				ChunkLoadHandler.addShellAsChunkloader(this);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private EntityPlayer createPlayer(String playerName) 
	{
		return new EntityOtherPlayerMP(worldObj, playerName);
	}

	public boolean isPowered()
	{
		if(top && pair != null)
		{
			return ((TileEntityShellStorage)pair).isPowered();
		}
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) || worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord + 1, zCoord);
	}
	
	@Override
    public void writeToNBT(NBTTagCompound tag)
    {
		super.writeToNBT(tag);
		tag.setBoolean("occupied", occupied);
		tag.setBoolean("vacating", vacating);
		
		tag.setInteger("occupationTime", occupationTime);
		
		tag.setCompoundTag("playerNBT", playerNBT);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		
		occupied = tag.getBoolean("occupied");
		vacating = tag.getBoolean("vacating");
		
		occupationTime = tag.getInteger("occupationTime");
		
		playerNBT = tag.getCompoundTag("playerNBT");
		
		resync = true;
    }
	
}
