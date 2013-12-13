package sync.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntityShellConstructor extends TileEntity 
{

	public TileEntityShellConstructor pair;
	public boolean top;
	public int face;
	public String playerName;
	public float constructionProgress;
	
	public boolean resync;
	
	public TileEntityShellConstructor()
	{
		pair = null;
		top = false;
		face = 0;
		playerName = "";
		constructionProgress = 0.0F;
		
		resync = false;
	}
	
	@Override
	public void updateEntity()
	{
		if(resync)
		{
			TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + (top ? -1 : 1), zCoord);
			if(te instanceof TileEntityShellConstructor)
			{
				TileEntityShellConstructor sc = (TileEntityShellConstructor)te;
				sc.pair = this;
				pair = sc;
			}
		}
	}
	
	public void setup(TileEntityShellConstructor scPair, boolean isTop, int placeYaw)
	{
		pair = scPair;
		top = isTop;
		face = placeYaw;
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
		tag.setBoolean("top", top);
		tag.setInteger("face", face);
		tag.setString("playerName", playerName);
		tag.setFloat("constructionProgress", constructionProgress);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		top = tag.getBoolean("top");
		face = tag.getInteger("face");
		playerName = tag.getString("playerName");
		constructionProgress = tag.getFloat("constructionProgress");
		
		resync = true;
    }
}
