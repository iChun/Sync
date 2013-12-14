package sync.client.core;

import sync.client.render.TileRendererDualVertical;
import sync.common.core.CommonProxy;
import sync.common.tileentity.TileEntityDualVertical;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy 
{

	@Override
	public void initMod()
	{
		super.initMod();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDualVertical.class, new TileRendererDualVertical());
	}
	
	@Override
	public void initTickHandlers()
	{
		super.initTickHandlers();
		tickHandlerClient = new TickHandlerClient();
		TickRegistry.registerTickHandler(tickHandlerClient, Side.CLIENT);
	}
	
}
