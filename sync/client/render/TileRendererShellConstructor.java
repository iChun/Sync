package sync.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import sync.client.model.ModelShellConstructor;
import sync.common.core.SessionState;
import sync.common.tileentity.TileEntityShellConstructor;

public class TileRendererShellConstructor extends TileEntitySpecialRenderer 
{

	public final ResourceLocation txShellConstructor = new ResourceLocation("sync", "textures/model/shellConstructor.png");
	
	public ModelShellConstructor model;
	
	public TileRendererShellConstructor()
	{
		model = new ModelShellConstructor();
	}
	
	
	public void renderShellConstructor(TileEntityShellConstructor sc, double d, double d1, double d2, float f) 
	{
		if(sc.top)
		{
			return;
		}
		GL11.glPushMatrix();
		
		GL11.glTranslated(d + 0.5D, d1 + 0.75, d2 + 0.5D);
		GL11.glScalef(-0.5F, -0.5F, 0.5F);
		
		GL11.glRotatef((sc.face * 90F), 0.0F, 1.0F, 0.0F);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
		
		float prog = MathHelper.clamp_float(sc.constructionProgress + (sc.isPowered() ? f * sc.powerAmount() : 0), 0.0F, SessionState.shellConstructionPowerRequirement) / (float)SessionState.shellConstructionPowerRequirement; 
		
		model.rand.setSeed(sc.playerName.hashCode());
		model.txBiped = sc.locationSkin;
		model.renderConstructionProgress(prog, 0.0625F); //0.95F;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
		model.render(0.0625F);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,	double d2, float f) 
	{
		this.renderShellConstructor((TileEntityShellConstructor)tileentity, d0, d1, d2, f);
	}

}
