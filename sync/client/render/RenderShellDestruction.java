package sync.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import sync.client.entity.EntityShellDestruction;

public class RenderShellDestruction extends Render 
{

	public RenderShellDestruction() 
	{
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return AbstractClientPlayer.locationStevePng;
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) 
	{
		EntityShellDestruction sd = (EntityShellDestruction)entity;
		
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        sd.model.rand.setSeed(entity.hashCode());
        sd.model.renderPlayer(sd.progress, sd.renderYaw, sd.yaw, sd.pitch, sd.limbSwingg, sd.limbSwinggAmount, 0.0625F, f1);

        GL11.glPopMatrix();
	}
}
