package me.ichun.mods.sync.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class SyncStartEvent extends PlayerEvent 
{

	/**
	 * 
	 * Event is triggered when a sync is started.
	 * Fields:
	 * prevPlayerTag - Equivalent to player.writeToNBT(NBTTagCompound); This will be read next time. Changing entityPlayer's information will not affect information in this NBTTagCompound.
	 * nextPlayerTag - NBTTagCompound to be read when the player finishes synching. Contains the inventory/experience/gamemode etc of what the player will become.
	 * pos - next sync position
	 *
	 */
	
	private final NBTTagCompound prevPlayerTag;
	private final NBTTagCompound nextPlayerTag;
	private final BlockPos pos;

	public SyncStartEvent(EntityPlayer player, NBTTagCompound prevtag, NBTTagCompound nexttag, BlockPos pos)
	{
		super(player);
		this.prevPlayerTag = prevtag;
		this.nextPlayerTag = nexttag;
		this.pos = pos;
	}

	public NBTTagCompound getPrevPlayerTag()
    {
        return prevPlayerTag;
    }

    public NBTTagCompound getNextPlayerTag()
    {
        return nextPlayerTag;
    }

    public BlockPos getPos()
    {
        return pos;
    }

}
