package me.ichun.mods.sync.client.render;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.sync.client.model.ModelShellConstructor;
import me.ichun.mods.sync.client.model.ModelShellStorage;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper; import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import me.ichun.mods.sync.client.model.ModelShellConstructor;
import me.ichun.mods.sync.client.model.ModelShellStorage;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;

public class TileRendererDualVertical extends TileEntitySpecialRenderer<TileEntityDualVertical>
{

	public static final ResourceLocation txShellConstructor = new ResourceLocation("sync", "textures/model/shellConstructor.png");
	public static final ResourceLocation txShellStorage = new ResourceLocation("sync", "textures/model/shellStorage.png");
	
	public static final ResourceLocation txShellConstructorAlpha = new ResourceLocation("sync", "textures/model/shellConstructorAlpha.png");
	public static final ResourceLocation txShellStorageAlpha = new ResourceLocation("sync", "textures/model/shellStorageAlpha.png");
	
	public ModelShellConstructor modelConstructor;
	public ModelShellStorage modelStorage;
	
	public TileRendererDualVertical()
	{
		modelConstructor = new ModelShellConstructor();
		modelStorage = new ModelShellStorage();
	}

	@Override
	public void renderTileEntityAt(TileEntityDualVertical dv, double d, double d1, double d2, float f, int destroyStage)
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
			rl = DefaultPlayerSkin.getDefaultSkinLegacy();
		}
		
		if(dv instanceof TileEntityShellConstructor)
		{
			TileEntityShellConstructor sc = (TileEntityShellConstructor)dv;
			float doorProg = MathHelper.clamp(TileEntityDualVertical.animationTime - sc.doorTime + (sc.doorOpen && sc.doorTime < TileEntityShellStorage.animationTime ? -f : !sc.doorOpen && sc.doorTime > 0 ? f : 0.0F), 0.0F, TileEntityDualVertical.animationTime) / (float)TileEntityDualVertical.animationTime;
			
			if(BlockDualVertical.renderPass == 0)
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
				
				float prog = Sync.config.shellConstructionPowerRequirement > 0 ? MathHelper.clamp(sc.constructionProgress + (sc.isPowered() ? f * sc.powerAmount() : 0), 0.0F, Sync.config.shellConstructionPowerRequirement) / (float)Sync.config.shellConstructionPowerRequirement : 1.0F;
				
				
				modelConstructor.rand.setSeed(sc.getPlayerName().hashCode());
				modelConstructor.txBiped = rl;
				modelConstructor.renderConstructionProgress(prog, 0.0625F, true, !sc.getPlayerName().equalsIgnoreCase("")); //0.95F;
				
				GL11.glDisable(GL11.GL_CULL_FACE);
				Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
				modelConstructor.render(doorProg, 0.0625F, false);
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
			else
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructorAlpha);
				modelConstructor.render(doorProg, 0.0625F, true);
			}
		}
		else if(dv instanceof TileEntityShellStorage)
		{
			TileEntityShellStorage ss = (TileEntityShellStorage)dv;
			
			float prog = MathHelper.clamp(TileEntityDualVertical.animationTime - ss.occupationTime + (ss.syncing ? f : 0.0F), 0.0F, TileEntityDualVertical.animationTime) / (float)TileEntityDualVertical.animationTime;
			
			if(!ss.syncing && !ss.vacating)
			{
				prog = 0.0F;
			}
			if(ss.vacating)
			{
				prog = 1.0F - prog;
			}
			
			if(BlockDualVertical.renderPass == 0)
			{
				modelStorage.txBiped = rl;
				if(ss.playerInstance != null && ss.syncing)
				{
//					if (iChunUtil.hasMorphMod()) morph.api.Api.allowNextPlayerRender(); //Allow next render as we render a "player" for the shell; this API method does not exist yet.
					GL11.glPushMatrix();
					
					GL11.glScalef(-2.0F, -2.0F, 2.0F);
					GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
	
//					ss.playerInstance.getDataWatcher().updateObject(16, Byte.valueOf((byte)(ss.playerInstance.getDataWatcher().getWatchableObjectByte(16) | 1 << 1))); TODO what is this?
					
					ss.playerInstance.ticksExisted = 35;
					ss.playerInstance.prevRotationPitch = ss.playerInstance.rotationPitch;
	
					int randSeed = Minecraft.getMinecraft().player.ticksExisted - (Minecraft.getMinecraft().player.ticksExisted % 100);
					ss.playerInstance.getRNG().setSeed(randSeed);
						
					if((Minecraft.getMinecraft().player.getName().equalsIgnoreCase("direwolf20") || Minecraft.getMinecraft().player.getName().equalsIgnoreCase("soaryn") || (EventCalendar.isNewYear() || EventCalendar.isAFDay() || EventCalendar.isHalloween() || EventCalendar.isChristmas())) && ss.playerInstance.getRNG().nextFloat() < 0.5F)
					{
						ss.playerInstance.prevRotationYawHead = ss.playerInstance.rotationYawHead = ss.playerInstance.rotationYaw + 90F;
						ss.playerInstance.setPosition(ss.getPos().getX() + 0.5D, ss.getPos().getY() + 0.0D, ss.getPos().getZ() + 0.5D);
						EntityHelper.faceEntity(ss.playerInstance, Minecraft.getMinecraft().player, 0.5F, 0.5F);
					}
					else
					{
						ss.playerInstance.prevRotationYaw = ss.playerInstance.rotationYaw = ss.playerInstance.prevRotationYawHead = ss.playerInstance.rotationYawHead = 0.0F;
						ss.playerInstance.rotationPitch = MathHelper.clamp((float)Math.pow(prog, 2D) * 3.1F, 0.0F, 1.0F) * (ss.playerInstance.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == null ? 15F : 5F);
					}
					
					ss.playerInstance.setPosition(0.0D, 500D, 0.0D);
					
					ItemStack is = ss.playerInstance.getActiveItemStack();
					
					ss.playerInstance.setItemStackToSlot(ss.playerInstance.getActiveHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, null);
					
					Sync.eventHandlerClient.forceRender = true;
					Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(ss.playerInstance).doRender(ss.playerInstance, 0.0D, -0.72D, 0.0D, 1.0F, f); // posXYZ, rotYaw, renderTick
					Sync.eventHandlerClient.forceRender = false;
					
					ss.playerInstance.setItemStackToSlot(ss.playerInstance.getActiveHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, is);
					
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
				modelStorage.render(prog, 0.0625F, false);
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
			else
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(txShellStorageAlpha);
				modelStorage.render(prog, 0.0625F, true);
				
				if(!ss.getName().equalsIgnoreCase(""))
				{
					FontRenderer fontrenderer = this.getFontRenderer();
					float ff = 1.6F;
					float f1 = 0.016666668F * ff;
					GL11.glPushMatrix();
					GL11.glTranslatef(0.0F, -2.475F, -1.01F);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GL11.glScalef(f1, f1, f1);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glDepthMask(false);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					Tessellator tessellator = Tessellator.getInstance();
					byte b0 = 0;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					VertexBuffer buffer = tessellator.getBuffer();
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					int j = fontrenderer.getStringWidth(ss.getName()) / 2;
					buffer.color(0.0F, 0.0F, 0.0F, 0.25F);
					buffer.pos((double)(-j - 1), (double)(-1 + b0), 0.0D);
					buffer.pos((double)(-j - 1), (double)(8 + b0), 0.0D);
					buffer.pos((double)(j + 1), (double)(8 + b0), 0.0D);
					buffer.pos((double)(j + 1), (double)(-1 + b0), 0.0D);
					tessellator.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDepthMask(true);
					fontrenderer.drawString(ss.getName(), -fontrenderer.getStringWidth(ss.getName()) / 2, b0, -1);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glPopMatrix();
				}
			}
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glPopMatrix();
	}
}
