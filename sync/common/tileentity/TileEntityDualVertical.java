package sync.common.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import sync.common.Sync;
import sync.common.core.SessionState;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityDualVertical extends TileEntity 
{

	public TileEntityDualVertical pair;
	public boolean top;
	public int face;
	public boolean vacating;
	
	public String playerName;
	public String name;
	
	public int resyncPlayer;
	
	public NBTTagCompound playerNBT;

	public ResourceLocation locationSkin;
	
	public boolean resync;
	
	public final static int animationTime = 40;
	
	public TileEntityDualVertical()
	{
		pair = null;
		top = false;
		vacating = false;
		face = 0;
		playerName = "";
		name = "";
		
		resyncPlayer = 0;
		
		playerNBT = new NBTTagCompound();
		
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
			name = pair.name;
			vacating = pair.vacating;
		}
		if(!top && !worldObj.isRemote)
		{
			if(resyncPlayer > -10)
			{
				resyncPlayer--;
				if(resyncPlayer == 60)
				{
					if(this.getClass() == TileEntityShellStorage.class)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)this;
						
						ss.occupied = true;
					}
					
					EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName);
					if(player != null)
					{
						player.setLocationAndAngles(xCoord + 0.5D, yCoord, zCoord + 0.5D, (face - 2) * 90F, 0F);
						
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						DataOutputStream stream1 = new DataOutputStream(bytes);
						try
						{
							stream1.writeInt(xCoord);
							stream1.writeInt(yCoord);
							stream1.writeInt(zCoord);
							
							stream1.writeInt(worldObj.provider.dimensionId);
							
							stream1.writeInt(face);
							
							stream1.writeBoolean(true);
							
							PacketDispatcher.sendPacketToPlayer(new Packet131MapData((short)Sync.getNetId(), (short)4, bytes.toByteArray()), (Player)player);
						}
						catch(IOException e)
						{
						}
					}
				}
				if(resyncPlayer == 40)
				{
					vacating = true;
					
					if(this.getClass() == TileEntityShellStorage.class)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)this;
						
						ss.occupied = true;
						
						ss.occupationTime = TileEntityShellStorage.animationTime;
						
						worldObj.markBlockForUpdate(ss.xCoord, ss.yCoord, ss.zCoord);
						worldObj.markBlockForUpdate(ss.xCoord, ss.yCoord + 1, ss.zCoord);
					}
				}
				if(resyncPlayer == -10)
				{
					if(this.getClass() == TileEntityShellStorage.class)
					{
						TileEntityShellStorage ss = (TileEntityShellStorage)this;
						
						ss.occupied = true;
					}
				}
			}
		}
		resync = false;
	}
	
	public void setup(TileEntityDualVertical scPair, boolean isTop, int placeYaw)
	{
		pair = scPair;
		top = isTop;
		face = placeYaw;
	}
	
	public float powerAmount()
	{
		return 0F;
	}

	public float getBuildProgress()
	{
		return SessionState.shellConstructionPowerRequirement;
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
		tag.setBoolean("vacating", vacating);
		tag.setString("playerName", playerName);
		tag.setString("name", name);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		top = tag.getBoolean("top");
		face = tag.getInteger("face");
		vacating = tag.getBoolean("vacating");
		playerName = tag.getString("playerName");
		name = tag.getString("name");
		
		resync = true;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
    }

	public byte[] createShellStateData() 
	{
		if(top && pair != null)
		{
			return pair.createShellStateData();
		}
			
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try
		{
			stream.writeInt(xCoord);
			stream.writeInt(yCoord);
			stream.writeInt(zCoord);
			stream.writeInt(worldObj.provider.dimensionId);
			
			stream.writeFloat(getBuildProgress());
			stream.writeFloat(powerAmount());
			
			stream.writeUTF(name);
			stream.writeUTF(worldObj.provider.getDimensionName());
			
			stream.writeBoolean(this.getClass() == TileEntityShellConstructor.class);
			
			Sync.writeNBTTagCompound(playerNBT, stream);
		}
		catch(IOException e)
		{
		}
		return bytes.toByteArray();
	}
}
