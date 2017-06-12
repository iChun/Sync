package me.ichun.mods.sync.client.render.item;

import me.ichun.mods.sync.client.model.ModelTreadmill;
import me.ichun.mods.sync.client.render.TileRendererTreadmill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Tobias on 12.06.2017.
 */
public class RenderItemTreadmill extends TileEntitySpecialRenderer<RenderItemTreadmill.ItemTreadmillRenderHack> {
    private static final ModelTreadmill modelTreadmill = new ModelTreadmill();

    @Override
    public void renderTileEntityAt(RenderItemTreadmill.ItemTreadmillRenderHack entity, double x, double y, double z, float partialTicks, int destroyStage)
    {

        GlStateManager.scale(0.33F, 0.33F, 0.33F);
        GlStateManager.translate(1F, 1F, 2F);
//        GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.rotate(180, 1 ,0 ,0);
//        GlStateManager.translate(1.5F, 0.8F, 0.0F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TileRendererTreadmill.txTreadmill);

        modelTreadmill.render(0.0625F);
    }

    public static class ItemTreadmillRenderHack extends TileEntity {
    }
}
