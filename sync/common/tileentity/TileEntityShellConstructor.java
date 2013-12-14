package sync.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import sync.common.core.SessionState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityShellConstructor extends TileEntityDualVertical 
{
	public float constructionProgress;

	public TileEntityShellConstructor()
	{
		super();
		
		constructionProgress = 0.0F;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if(top && pair != null)
		{
			constructionProgress = ((TileEntityShellConstructor)pair).constructionProgress;
		}
		if(isPowered())
		{
			constructionProgress += powerAmount();
			if(constructionProgress > SessionState.shellConstructionPowerRequirement)
			{
				constructionProgress = SessionState.shellConstructionPowerRequirement;
			}
			
			if(worldObj.isRemote && !top)
			{
				spawnParticles();
			}
		}
	}
	
	public void setup(TileEntityDualVertical scPair, boolean isTop, int placeYaw)
	{
		pair = scPair;
		top = isTop;
		face = placeYaw;
	}
	
	public boolean isPowered()
	{
		if(top && pair != null)
		{
			return ((TileEntityShellConstructor)pair).isPowered();
		}
		if(playerName.equalsIgnoreCase(""))
		{
			return false;
		}
		return true;
	}
	
	public float powerAmount()
	{
		return 40F;
	}
	
	@SideOnly(Side.CLIENT)
	public void spawnParticles()
	{
//		float prog = MathHelper.clamp_float(this.constructionProgress, 0.0F, SessionState.shellConstructionPowerRequirement) / (float)SessionState.shellConstructionPowerRequirement;
//		if(prog > 0.95F)
//		{
//			float angle = 0;
//			
//			System.out.println(face);
//			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityPaintFX(worldObj, xCoord + 0.5D , yCoord + 0.5D, zCoord + 0.5D, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 1.0F));
//		}
	}

	@Override
    public void writeToNBT(NBTTagCompound tag)
    {
		super.writeToNBT(tag);
		tag.setFloat("constructionProgress", constructionProgress);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		constructionProgress = tag.getFloat("constructionProgress");
		
		resync = true;
    }
	
}
