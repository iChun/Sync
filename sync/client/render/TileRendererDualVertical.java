package sync.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import sync.client.model.ModelShellConstructor;
import sync.client.model.ModelShellStorage;
import sync.common.core.SessionState;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityShellConstructor;
import sync.common.tileentity.TileEntityShellStorage;

public class TileRendererDualVertical extends TileEntitySpecialRenderer 
{

	public final ResourceLocation txShellConstructor = new ResourceLocation("sync", "textures/model/shellConstructor.png");
	public final ResourceLocation txShellStorage = new ResourceLocation("sync", "textures/model/shellStorage.png");
	
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
			
			modelConstructor.rand.setSeed(sc.playerName.hashCode());
			modelConstructor.txBiped = rl;
			modelConstructor.renderConstructionProgress(prog, 0.0625F); //0.95F;
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
			modelConstructor.render(0.0625F);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		else if(dv instanceof TileEntityShellStorage)
		{
			TileEntityShellStorage ss = (TileEntityShellStorage)dv;
			
			modelStorage.txBiped = rl;
//			modelStorage.renderPlayer(0.0625F);
			
			float prog = MathHelper.clamp_float(TileEntityShellStorage.animationTime - ss.occupationTime + f, 0.0F, TileEntityShellStorage.animationTime) / (float)TileEntityShellStorage.animationTime;
			
			if(ss.vacating)
			{
				prog = 1.0F - prog;
			}
			else if(!ss.occupied)
			{
				prog = 0.0F;
			}
			
			if(ss.playerInstance != null)
			{
				GL11.glPushMatrix();
				
				GL11.glScalef(-2.0F, -2.0F, 2.0F);
				GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);

				ss.playerInstance.ticksExisted = 35;
				ss.playerInstance.prevRotationYaw = ss.playerInstance.rotationYaw = ss.playerInstance.prevRotationYawHead = ss.playerInstance.rotationYawHead = 0.0F; 
				ss.playerInstance.prevRotationPitch = ss.playerInstance.rotationPitch;
				ss.playerInstance.rotationPitch = (float)MathHelper.clamp_float((float)Math.pow(prog, 2D) * 3.1F, 0.0F, 1.0F) * 15F;
				
				RenderManager.instance.getEntityRenderObject(ss.playerInstance).doRender(ss.playerInstance, 0.0D, -0.72D, 0.0D, 1.0F, f); // posXYZ, rotYaw, renderTick
				
				GL11.glPopMatrix();
			}
			
			Minecraft.getMinecraft().renderEngine.bindTexture(txShellStorage);
			
			modelStorage.renderInternals(prog, 0.0625F);
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			Minecraft.getMinecraft().renderEngine.bindTexture(txShellStorage);
			modelStorage.render(0.0625F);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,	double d2, float f) 
	{
		this.renderDualVertical((TileEntityDualVertical)tileentity, d0, d1, d2, f);
	}

}
