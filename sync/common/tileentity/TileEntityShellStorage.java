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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityShellStorage extends TileEntityDualVertical 
{
	
	public boolean occupied;
	public boolean syncing;
	
	public EntityPlayer playerInstance;
	
	public String prevPlayerName;
	
	public int occupationTime;
	
	public TileEntityShellStorage()
	{
		super();
		occupied = false;
		syncing = false;
		
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
			}
		}
		if(top && pair != null)
		{
			TileEntityShellStorage ss = (TileEntityShellStorage)pair;
			occupied = ss.occupied;
			syncing = ss.syncing;
			
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
		        }
			}
			else
			{
				occupied = false;
				playerName = "";
				
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		if(syncing && occupationTime > 0)
		{
			occupationTime--;
			if(occupationTime == 0)
			{
				if(vacating)
				{
					vacating = false;
					occupied = false;
					syncing = false;
					if(!worldObj.isRemote && !top )
					{
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						ChunkLoadHandler.removeShellAsChunkloader(this);
					}
				}
				else if(!worldObj.isRemote && occupied && isPowered() && !playerName.equalsIgnoreCase("") && !top && !ChunkLoadHandler.shellTickets.containsKey(this))
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
			else if(playerNBT.hasKey("Inventory") && isPowered() && !playerName.equalsIgnoreCase("") && !ChunkLoadHandler.shellTickets.containsKey(this))
			{
				ChunkLoadHandler.addShellAsChunkloader(this);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static EntityPlayer createPlayer(World world, String playerName) 
	{
		return new EntityOtherPlayerMP(world, playerName);
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
		tag.setBoolean("syncing", syncing);
		
		tag.setInteger("occupationTime", occupationTime);
		
		tag.setCompoundTag("playerNBT", playerNBT);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		
		occupied = tag.getBoolean("occupied");
		
		syncing = tag.getBoolean("syncing");
		
		occupationTime = tag.getInteger("occupationTime");
		
		playerNBT = tag.getCompoundTag("playerNBT");
		
		resync = true;
    }
	
}
