package sync.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import sync.client.model.ModelShellConstructor;
import sync.client.model.ModelShellStorage;
import sync.common.Sync;
import sync.common.core.SessionState;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;

public class TileRendererDualVertical extends TileEntitySpecialRenderer 
{

	public static final ResourceLocation txShellConstructor = new ResourceLocation("sync", "textures/model/shellConstructor.png");
	public static final ResourceLocation txShellStorage = new ResourceLocation("sync", "textures/model/shellStorage.png");
	
	public ModelShellConstructor modelConstructor;
	public ModelShellStorage modelStorage;
	
	public TileRendererDualVertical()
	{
		modelConstructor = new ModelShellConstructor();
		modelStorage = new ModelShellStorage();
	}
	
	public void renderDualVertical(TileEntityDualVertical dv, double d, double d1, double d2, float f) 
	{
		if(dv.top)
		{
			return;
		}
		GL11.glPushMatrix();
		
		GL11.glTranslated(d + 0.5D, d1 + 0.75, d2 + 0.5D);
		GL11.glScalef(-0.5F, -0.5F, 0.5F);
		
		GL11.glRotatef((dv.face * 90F), 0.0F, 1.0F, 0.0F);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		ResourceLocation rl = dv.locationSkin;
		
		if(rl == null)
		{
			rl = AbstractClientPlayer.locationStevePng;
		}
		
		if(dv instanceof TileEntityShellConstructor)
		{
			TileEntityShellConstructor sc = (TileEntityShellConstructor)dv;
			Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
			
			float prog = MathHelper.clamp_float(sc.constructionProgress + (sc.isPowered() ? f * sc.powerAmount() : 0), 0.0F, SessionState.shellConstructionPowerRequirement) / (float)SessionState.shellConstructionPowerRequirement;
			
			float doorProg = MathHelper.clamp_float(TileEntityDualVertical.animationTime - sc.doorTime + (sc.doorOpen && sc.doorTime < TileEntityShellStorage.animationTime ? -f : !sc.doorOpen && sc.doorTime > 0 ? f : 0.0F), 0.0F, TileEntityDualVertical.animationTime) / (float)TileEntityDualVertical.animationTime;
			
			modelConstructor.rand.setSeed(sc.playerName.hashCode());
			modelConstructor.txBiped = rl;
			modelConstructor.renderConstructionProgress(prog, 0.0625F); //0.95F;
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
			modelConstructor.render(doorProg, 0.0625F);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		else if(dv instanceof TileEntityShellStorage)
		{
			TileEntityShellStorage ss = (TileEntityShellStorage)dv;
			
			modelStorage.txBiped = rl;
//			modelStorage.renderPlayer(0.0625F);
			
			float prog = MathHelper.clamp_float(TileEntityDualVertical.animationTime - ss.occupationTime + (ss.syncing ? f : 0.0F), 0.0F, TileEntityDualVertical.animationTime) / (float)TileEntityDualVertical.animationTime;
			
			if(!ss.syncing && !ss.vacating)
			{
				prog = 0.0F;
			}
			if(ss.vacating)
			{
				prog = 1.0F - prog;
			}
			
			if(ss.playerInstance != null && ss.syncing)
			{
				GL11.glPushMatrix();
				
				GL11.glScalef(-2.0F, -2.0F, 2.0F);
				GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);

				ss.playerInstance.getDataWatcher().updateObject(16, Byte.valueOf((byte)(ss.playerInstance.getDataWatcher().getWatchableObjectByte(16) | 1 << 1)));
				
				ss.playerInstance.ticksExisted = 35;
				ss.playerInstance.prevRotationYaw = ss.playerInstance.rotationYaw = ss.playerInstance.prevRotationYawHead = ss.playerInstance.rotationYawHead = 0.0F;
				ss.playerInstance.prevRotationPitch = ss.playerInstance.rotationPitch;
				ss.playerInstance.rotationPitch = (float)MathHelper.clamp_float((float)Math.pow(prog, 2D) * 3.1F, 0.0F, 1.0F) * (ss.playerInstance.getCurrentArmor(3) == null ? 15F : 5F);
				
//				ss.playerInstance.prevRotationYawHead = ss.playerInstance.rotationYawHead = ss.playerInstance.rotationYaw + 90F;
//				ss.playerInstance.prevRotationPitch = ss.playerInstance.rotationPitch;
//				ss.playerInstance.setPosition(ss.xCoord + 0.5D, ss.yCoord + 0.0D, ss.zCoord + 0.5D);
//				faceEntity(ss.playerInstance, Minecraft.getMinecraft().thePlayer, 10F, 10F);
				
				ss.playerInstance.setPosition(0.0D, 500D, 0.0D);
				
				ItemStack is = ss.playerInstance.getCurrentEquippedItem();
				
				ss.playerInstance.setCurrentItemOrArmor(0, null);
				
				Sync.proxy.tickHandlerClient.forceRender = true; 
				RenderManager.instance.getEntityRenderObject(ss.playerInstance).doRender(ss.playerInstance, 0.0D, -0.72D, 0.0D, 1.0F, f); // posXYZ, rotYaw, renderTick
				Sync.proxy.tickHandlerClient.forceRender = false;
				
				ss.playerInstance.setCurrentItemOrArmor(0, is);
				
				GL11.glPopMatrix();
			}
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			Minecraft.getMinecraft().renderEngine.bindTexture(txShellStorage);

			modelStorage.powered = ss.isPowered();
			modelStorage.isHomeUnit = ss.isHomeUnit;
			modelStorage.renderInternals(prog, 0.0625F);
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			Minecraft.getMinecraft().renderEngine.bindTexture(txShellStorage);
			modelStorage.render(prog, 0.0625F);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glPopMatrix();
	}
	
    public void faceEntity(Entity ent, Entity par1Entity, float par2, float par3)
    {
        double d0 = par1Entity.posX - ent.posX;
        double d1 = par1Entity.posZ - ent.posZ;
        double d2;

        if (par1Entity instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)par1Entity;
            d2 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (ent.posY + (double)ent.getEyeHeight());
        }
        else
        {
            d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY) / 2.0D - (ent.posY + (double)ent.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        ent.rotationPitch = this.updateRotation(ent.rotationPitch, f3, par3);
        ent.rotationYaw = this.updateRotation(ent.rotationYaw, f2, par2);
    }

    private float updateRotation(float par1, float par2, float par3)
    {
        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (f3 > par3)
        {
            f3 = par3;
        }

        if (f3 < -par3)
        {
            f3 = -par3;
        }

        return par1 + f3;
    }
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,	double d2, float f) 
	{
		this.renderDualVertical((TileEntityDualVertical)tileentity, d0, d1, d2, f);
	}

}
