package me.ichun.mods.sync.client.render;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.sync.client.model.ModelShellConstructor;
import me.ichun.mods.sync.client.model.ModelShellStorage;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.block.BlockDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellConstructor;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class TileRendererDualVertical extends TileEntitySpecialRenderer<TileEntityDualVertical>
{

    public static final ResourceLocation txShellConstructor = new ResourceLocation("sync", "textures/model/shell_constructor.png");
    public static final ResourceLocation txShellStorage = new ResourceLocation("sync", "textures/model/shell_storage.png");

    public static final ResourceLocation txShellConstructorAlpha = new ResourceLocation("sync", "textures/model/shell_constructor_alpha.png");
    public static final ResourceLocation txShellStorageAlpha = new ResourceLocation("sync", "textures/model/shell_storage_alpha.png");

    public ModelShellConstructor modelConstructor;
    public ModelShellStorage modelStorage;

    public TileRendererDualVertical() {
        modelConstructor = new ModelShellConstructor();
        modelStorage = new ModelShellStorage();
    }



    @Override
    public void render(TileEntityDualVertical dv, double d, double d1, double d2, float f, int destroyStage, float alpha)
    {
        if(dv.top)
        {
            return;
        }
        GlStateManager.pushMatrix();

        GlStateManager.translate(d + 0.5D, d1 + 0.75, d2 + 0.5D);
        GlStateManager.scale(-0.5F, -0.5F, 0.5F);

        GlStateManager.rotate(dv.face.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

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

                GlStateManager.disableCull();
                Minecraft.getMinecraft().renderEngine.bindTexture(txShellConstructor);
                modelConstructor.render(doorProg, 0.0625F, false);
                GlStateManager.enableCull();
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
                    GlStateManager.pushMatrix();

                    GlStateManager.scale(-2.0F, -2.0F, 2.0F);
                    GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);

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
                        ss.playerInstance.rotationPitch = MathHelper.clamp((float)Math.pow(prog, 2D) * 3.1F, 0.0F, 1.0F) * (ss.playerInstance.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty() ? 15F : 5F);
                    }

                    ss.playerInstance.setPosition(0.0D, 500D, 0.0D);

                    ItemStack is = ss.playerInstance.getHeldItem(ss.playerInstance.getActiveHand());

                    ss.playerInstance.setItemStackToSlot(ss.playerInstance.getActiveHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);

                    Sync.eventHandlerClient.forceRender = true;
                    Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(ss.playerInstance).doRender(ss.playerInstance, 0.0D, -0.72D, 0.0D, 1.0F, 0); // posXYZ, rotYaw, renderTick
                    Sync.eventHandlerClient.forceRender = false;

                    ss.playerInstance.setItemStackToSlot(ss.playerInstance.getActiveHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, is);

                    GlStateManager.popMatrix();
                }

                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                Minecraft.getMinecraft().renderEngine.bindTexture(txShellStorage);

                modelStorage.powered = ss.isPowered();
                modelStorage.isHomeUnit = ss.isHomeUnit;
                modelStorage.renderInternals(prog, 0.0625F);

                GlStateManager.disableCull();
                Minecraft.getMinecraft().renderEngine.bindTexture(txShellStorage);
                modelStorage.render(prog, 0.0625F, false);
                GlStateManager.enableCull();
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
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F, -2.475F, -1.01F);
                    GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(f1, f1, f1);
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    Tessellator tessellator = Tessellator.getInstance();
                    byte b0 = 0;
                    GlStateManager.disableTexture2D();
                    BufferBuilder buffer = tessellator.getBuffer();
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    int j = fontrenderer.getStringWidth(ss.getName()) / 2;
                    buffer.pos((double)(-j - 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    buffer.pos((double)(-j - 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    buffer.pos((double)(j + 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    buffer.pos((double)(j + 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableDepth();
                    fontrenderer.drawString(ss.getName(), -fontrenderer.getStringWidth(ss.getName()) / 2, b0, -1);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                }
            }
        }

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }
}
