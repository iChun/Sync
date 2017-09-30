package me.ichun.mods.sync.client.core;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.sync.client.entity.EntityShellDestruction;
import me.ichun.mods.sync.client.render.RenderShellDestruction;
import me.ichun.mods.sync.client.render.TileRendererDualVertical;
import me.ichun.mods.sync.client.render.TileRendererTreadmill;
import me.ichun.mods.sync.client.render.item.RenderItemShellConstructor;
import me.ichun.mods.sync.client.render.item.RenderItemShellStorage;
import me.ichun.mods.sync.client.render.item.RenderItemTreadmill;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.core.ProxyCommon;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
	@Override
	public void preInitMod()
	{
		super.preInitMod();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDualVertical.class, new TileRendererDualVertical());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTreadmill.class, new TileRendererTreadmill());

		iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
		iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindAttack);

        Sync.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(Sync.eventHandlerClient);

		RenderingRegistry.registerEntityRenderingHandler(EntityShellDestruction.class, new RenderShellDestruction.RenderFactory());

		if (!Minecraft.getMinecraft().getFramebuffer().isStencilEnabled()) //We use stencil, make sure it's enabled
			Minecraft.getMinecraft().getFramebuffer().enableStencil();
	}
}
