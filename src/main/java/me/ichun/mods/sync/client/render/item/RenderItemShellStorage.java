package me.ichun.mods.sync.client.render.item;

import me.ichun.mods.sync.client.model.ModelShellStorage;
import me.ichun.mods.sync.client.render.TileRendererDualVertical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * TODO replace with static model
 */
public class RenderItemShellStorage extends TileEntitySpecialRenderer<RenderItemShellStorage.ItemShellStorageRenderHack> {
    private static final ModelShellStorage modelStorage = new ModelShellStorage();

    @Override
    public void render(ItemShellStorageRenderHack entity, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {

        GlStateManager.scale(0.33F, 0.33F, 0.33F);
        GlStateManager.translate(0F, 0.25F, 0F);
        GlStateManager.rotate(-90, 0, 1,0);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellStorage);

        modelStorage.render(1.0F, 0.0625F, false);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererDualVertical.txShellStorageAlpha);

        modelStorage.render(1.0F, 0.0625F, true);
    }

    public static class ItemShellStorageRenderHack extends TileEntity {
    }
}
