package sync.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import sync.common.core.SessionState;
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
	
	public final static int animationTime = 40;
	
	public TileEntityShellStorage()
	{
		super();
		occupied = false;
		vacating = false;
		
		playerInstance = null;
		
		prevPlayerName = "";
		
		occupationTime = 0;
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
			}
		}
		super.updateEntity();
		
		if(occupationTime > 0)
		{
			occupationTime--;
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
		if(playerName.equalsIgnoreCase(""))
		{
			return false;
		}
		return true;
	}
	
	@Override
    public void writeToNBT(NBTTagCompound tag)
    {
		super.writeToNBT(tag);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		
		resync = true;
    }
	
}
