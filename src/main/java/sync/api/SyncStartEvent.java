package sync.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class SyncStartEvent extends PlayerEvent 
{

	/**
	 * 
	 * Event is triggered when a sync is started.
	 * Fields:
	 * prevPlayerTag - Equivalent to player.writeToNBT(NBTTagCompound); This will be read next time. Changing entityPlayer's information will not affect information in this NBTTagCompound.
	 * nextPlayerTag - NBTTagCompound to be read when the player finishes synching. Contains the inventory/experience/gamemode etc of what the player will become.
	 * x - next sync x position
	 * y - next sync y position
	 * z - next sync z position
	 * 
	 */
	
	public final NBTTagCompound prevPlayerTag;
	public final NBTTagCompound nextPlayerTag;
	public final int x;
	public final int y;
	public final int z;
	
	public SyncStartEvent(EntityPlayer player, NBTTagCompound prevtag, NBTTagCompound nexttag, int xCoord, int yCoord, int zCoord) 
	{
		super(player);
		this.prevPlayerTag = prevtag;
		this.nextPlayerTag = nexttag;
		this.x = xCoord;
		this.y = yCoord;
		this.z = zCoord;
	}

}
