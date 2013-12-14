package sync.common.core;

import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;
import sync.common.Sync;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandler 
{

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onMouseEvent(MouseEvent event)
	{
		if(Sync.proxy.tickHandlerClient.radialShow && !Sync.proxy.tickHandlerClient.shells.isEmpty())
		{
			Sync.proxy.tickHandlerClient.radialDeltaX += event.dx / 100D;
			Sync.proxy.tickHandlerClient.radialDeltaY += event.dy / 100D;
			
			double mag = Math.sqrt(Sync.proxy.tickHandlerClient.radialDeltaX * Sync.proxy.tickHandlerClient.radialDeltaX + Sync.proxy.tickHandlerClient.radialDeltaY * Sync.proxy.tickHandlerClient.radialDeltaY);
			if(mag > 1.0D)
			{
				Sync.proxy.tickHandlerClient.radialDeltaX /= mag;
				Sync.proxy.tickHandlerClient.radialDeltaY /= mag;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onRenderPlayer(RenderPlayerEvent.Pre event)
	{
		if(Sync.proxy.tickHandlerClient.refusePlayerRender.containsKey(event.entityPlayer.username) && !Sync.proxy.tickHandlerClient.forceRender && Sync.proxy.tickHandlerClient.refusePlayerRender.get(event.entityPlayer.username) < 118)
		{
			event.entityPlayer.lastTickPosX = event.entityPlayer.prevPosX = event.entityPlayer.posX;
			event.entityPlayer.lastTickPosY = event.entityPlayer.prevPosY = event.entityPlayer.posY;
			event.entityPlayer.lastTickPosZ = event.entityPlayer.prevPosZ = event.entityPlayer.posZ;
			event.entityPlayer.renderYawOffset = event.entityPlayer.rotationYaw;
			event.setCanceled(true);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onRenderPlayerSpecials(RenderPlayerEvent.Specials.Pre event)
	{
//		if(Sync.proxy.tickHandlerClient.refusePlayerRender.containsKey(event.entityPlayer.username) && !Sync.proxy.tickHandlerClient.forceRender)
//		{
//			event.setCanceled(true);
//		}
	}
}
