package sync.client.core;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import sync.client.entity.EntityShellDestruction;
import sync.client.render.RenderBlockPlacerItem;
import sync.client.render.RenderShellDestruction;
import sync.client.render.TileRendererDualVertical;
import sync.client.render.TileRendererTreadmill;
import sync.common.Sync;
import sync.common.core.CommonProxy;
import sync.common.tileentity.TileEntityDualVertical;
import sync.common.tileentity.TileEntityTreadmill;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void initMod()
	{
		super.initMod();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDualVertical.class, new TileRendererDualVertical());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTreadmill.class, new TileRendererTreadmill());
		
		MinecraftForgeClient.registerItemRenderer(Sync.itemBlockPlacer, new RenderBlockPlacerItem());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityShellDestruction.class, new RenderShellDestruction());
	}
	
	@Override
	public void initTickHandlers()
	{
		super.initTickHandlers();
		tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);
	}
	
}
