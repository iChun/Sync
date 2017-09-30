package me.ichun.mods.sync.client.render.item;

import me.ichun.mods.sync.client.model.ModelShellConstructor;
import me.ichun.mods.sync.client.render.TileRendererDualVertical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * TODO replace with static model
 */
public class RenderItemShellConstructor extends TileEntitySpecialRenderer<RenderItemShellConstructor.ItemShellConstructorRenderHack> {
    private static final ModelShellConstructor modelConstructor = new ModelShellConstructor();

    @Override
    public void render(ItemShellConstructorRenderHack entity, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {

        GlStateManager.scale(0.33F, 0.33F, 0.33F);
        GlStateManager.translate(0F, 0.25F, 0F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellConstructor);

        modelConstructor.render(1.0F, 0.0625F, false);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellConstructorAlpha);

        modelConstructor.render(1.0F, 0.0625F, true);
    }

    public static class ItemShellConstructorRenderHack extends TileEntity {
    }
}
