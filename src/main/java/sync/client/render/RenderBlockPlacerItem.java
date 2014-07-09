package sync.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import sync.client.model.ModelShellConstructor;
import sync.client.model.ModelShellStorage;
import sync.client.model.ModelTreadmill;

public class RenderBlockPlacerItem implements IItemRenderer 
{

	private ModelShellConstructor modelConstructor;
	private ModelShellStorage modelStorage;
	private ModelTreadmill modelTreadmill;

	public RenderBlockPlacerItem()
	{
		modelConstructor = new ModelShellConstructor();
		modelStorage = new ModelShellStorage();
		modelTreadmill = new ModelTreadmill();
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) 
	{
		switch(type)
		{
		case ENTITY:
		case EQUIPPED_FIRST_PERSON:
		case EQUIPPED:
		case INVENTORY: return true;
		default: return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) 
	{
		switch(helper)
		{
		case ENTITY_ROTATION:
		case ENTITY_BOBBING: return true;
		default: return false;
		}
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) 
	{
		boolean isFirstPerson = false;
		int renderType = 0;
		switch(type)
		{
			case ENTITY:
			{
				renderType = 2;
			}
			case INVENTORY:
			{
				if(renderType == 0)
				{
					renderType = 1;
				}
			}
			case EQUIPPED_FIRST_PERSON:
			{
				if(renderType == 0)
				{
					isFirstPerson = true;
				}
			}
			case EQUIPPED:
			{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				
				GL11.glPushMatrix();
				
	            GL11.glEnable(GL11.GL_BLEND);
	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
	            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
	            
	            float scale = (renderType == 1 ? 6.2F : renderType == 2 ? 0.3F : 0.55F) * 0.5F;
	            if(renderType == 0)
	            {
	            	GL11.glScalef(scale, scale, scale);
	            	
	            	GL11.glRotatef(15F, 0.0F, 0.0F, 1.0F);
	            	
	            	GL11.glTranslatef(-3.0F, 1.0F, 0.0F);
	            	
	            	if(!isFirstPerson)
	            	{
	            		float scale1 = 0.33F;
	            		GL11.glTranslatef(-0.1F, -2.0F, 0.0F);
	            		GL11.glRotatef(-135F, 1.0F, 0.0F, 0.0F);	
	            		
	            		GL11.glScalef(scale1, scale1, scale1);
	            		GL11.glRotatef(-7.5F, 0.0F, 1.0F, 0.0F);	
	            		GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
	            		GL11.glRotatef(10F, 1.0F, 0.0F, 1.0F);
	            		
	            	}
	            }
	            else if(renderType == 1)
	            {
	            	GL11.glScalef(-scale, -scale, -scale);
	            	
	            	GL11.glRotatef(35F, 1.0F, 0.0F, 0.0F);
	            	
	            	GL11.glRotatef(-50F, 0.0F, 1.0F, 0.0F);
	            	
					GL11.glTranslatef(3.08F, 5.1F, -0.4F);
	            }
	            else if(renderType == 2)
	            {
            		GL11.glTranslatef(0.0F, -0.1F, 0.0F);
	            	GL11.glScalef(scale, scale, scale);
	            }
	            
	            switch(item.getItemDamage())
	            {
		            case 0:
		            {
			            Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellConstructor);
			            
			            modelConstructor.render(1.0F, 0.0625F, false);

			            Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellConstructorAlpha);
			            
			            modelConstructor.render(1.0F, 0.0625F, true);
			            break;
		            }
		            case 1:
		            {
			            Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellStorage);
			            
			            modelStorage.render(1.0F, 0.0625F, false);
			            
			            Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellStorageAlpha);
			            
			            modelStorage.render(1.0F, 0.0625F, true);
			            
			            break;
		            }
		            case 2:
		            {
			            if(renderType == 0)
			            {
			            	GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
			            	GL11.glTranslatef(0.0F, -2.5F, 0.0F);
			            	
			            	if(!isFirstPerson)
			            	{
			            		GL11.glTranslatef(0.0F, 0.6F, 0.0F);
			            	}
			            }
			            else if(renderType == 1)
			            {
							GL11.glTranslatef(1.5F, 0.8F, 0.0F);
			            }
			            Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererTreadmill.txTreadmill);
			            
			            modelTreadmill.render(0.0625F);
			            break;
		            }
	            }
	            
	            GL11.glDisable(GL11.GL_BLEND);
				
				GL11.glPopMatrix();
			}
			default:{}
		}
	}

}
