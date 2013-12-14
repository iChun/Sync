package sync.common.tileentity;

import sync.client.entity.EntityPaintFX;
import sync.common.core.SessionState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TileEntityDualVertical extends TileEntity 
{

	public TileEntityDualVertical pair;
	public boolean top;
	public int face;
	public String playerName;

	public ResourceLocation locationSkin;
	
	public boolean resync;
	
	public TileEntityDualVertical()
	{
		pair = null;
		top = false;
		face = 0;
		playerName = "";
		
		resync = false;
	}
	
	@Override
	public void updateEntity()
	{
		if(resync)
		{
			TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + (top ? -1 : 1), zCoord);
			if(te.getClass() == this.getClass())
			{
				TileEntityDualVertical sc = (TileEntityDualVertical)te;
				sc.pair = this;
				pair = sc;
			}
			
			if(worldObj.isRemote)
			{
	            locationSkin = AbstractClientPlayer.getLocationSkin(playerName);
	            AbstractClientPlayer.getDownloadImageSkin(this.locationSkin, playerName);
			}
		}
		if(top && pair != null)
		{
			playerName = pair.playerName;
		}
		resync = false;
	}
	
	public void setup(TileEntityDualVertical scPair, boolean isTop, int placeYaw)
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

    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		top = tag.getBoolean("top");
		face = tag.getInteger("face");
		playerName = tag.getString("playerName");
		
		resync = true;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
    }
}
