package me.ichun.mods.sync.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper; import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import me.ichun.mods.sync.client.model.ModelTreadmill;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;

public class TileRendererTreadmill extends TileEntitySpecialRenderer<TileEntityTreadmill>
{

	public static final ResourceLocation txTreadmill = new ResourceLocation("sync", "textures/model/treadmill.png");
	
	public ModelTreadmill modelTreadmill;
	
	public TileRendererTreadmill()
	{
		modelTreadmill = new ModelTreadmill();
	}

	@Override
	public void renderTileEntityAt(TileEntityTreadmill tm, double d, double d1, double d2, float partialTicks, int destroyStage)
	{
		if(tm.back)
		{
			return;
		}
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(d + 0.5D, d1 + 0.75, d2 + 0.5D);
		GlStateManager.scale(-0.5F, -0.5F, 0.5F);
		
		GlStateManager.rotate(tm.face.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
		
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(txTreadmill);
		modelTreadmill.render(0.0625F);

		if(tm.latchedEnt != null)
		{
			tm.latchedEnt.prevRenderYawOffset = tm.latchedEnt.renderYawOffset = tm.face.getOpposite().getHorizontalAngle();
			tm.latchedEnt.limbSwingAmount = 1.5F + 3.5F * MathHelper.clamp(((float)tm.timeRunning / 12000F), 0.0F, 1.0F);
		}
		
		GlStateManager.disableBlend();
		
		GlStateManager.popMatrix();
	}
}
