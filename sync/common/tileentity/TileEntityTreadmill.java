package sync.common.tileentity;

import sync.common.Sync;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityTreadmill extends TileEntity 
{
	public TileEntityTreadmill pair;
	
	public boolean back;
	
	public int face;
	
	public boolean resync;
	
	public TileEntityTreadmill()
	{
		pair = null;
		back = false;
		
		face = 0;
		
		resync = false;
	}
	
	@Override
	public void updateEntity()
	{
		if(resync)
		{
			
		}
		resync = false;
	}
	
	public float powerOutput()
	{
		if(back)
		{
			return pair.powerOutput();
		}
		return 0F;
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		readFromNBT(pkt.data);
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
    public void writeToNBT(NBTTagCompound tag)
    {
		super.writeToNBT(tag);
		tag.setBoolean("back", back);
		tag.setInteger("face", face);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		back = tag.getBoolean("back");
		face = tag.getInteger("face");
		
		resync = true;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
    }

	@Override
    public Block getBlockType()
    {
        return Sync.blockDualVertical;
    }
	
}
