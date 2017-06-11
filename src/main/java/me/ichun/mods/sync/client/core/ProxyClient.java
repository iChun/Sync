package me.ichun.mods.sync.client.core;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.sync.client.entity.EntityShellDestruction;
import me.ichun.mods.sync.client.render.RenderShellDestruction;
import me.ichun.mods.sync.client.render.TileRendererDualVertical;
import me.ichun.mods.sync.client.render.TileRendererTreadmill;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.core.ProxyCommon;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityTreadmill;
import net.minecraft.client.Minecraft;
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
		
//		MinecraftForgeClient.registerItemRenderer(Sync.itemBlockPlacer, new RenderBlockPlacerItem());

		iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
		iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindAttack);

        Sync.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(Sync.eventHandlerClient);

		RenderingRegistry.registerEntityRenderingHandler(EntityShellDestruction.class, new RenderShellDestruction.RenderFactory());
	}
}
