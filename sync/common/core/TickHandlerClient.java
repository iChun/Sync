package sync.common.core;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandlerClient implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		if(type.equals(EnumSet.of(TickType.CLIENT)))
		{
			if(Minecraft.getMinecraft().theWorld != null)
			{
				worldTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld);
			}
		}
		else if(type.equals(EnumSet.of(TickType.RENDER)))
		{
			if(Minecraft.getMinecraft().theWorld != null)
			{
				renderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, (Float)tickData[0]);
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.CLIENT, TickType.RENDER);
	}

	@Override
	public String getLabel() 
	{
		return "Sync_TickHandlerClient";
	}

	public void worldTick(Minecraft mc, WorldClient world)
	{
		if(mc.currentScreen == null)
		{
			if(Keyboard.isKeyDown(Keyboard.KEY_GRAVE) && !tempKeyDown)
			{
				radialShow = true;
				radialTime = 3;
				
				radialPlayerYaw = mc.renderViewEntity.rotationYaw;
				radialPlayerPitch = mc.renderViewEntity.rotationPitch;
				
				radialDeltaX = radialDeltaY = 0;
				
				renderCrosshair = GuiIngameForge.renderCrosshairs;
				GuiIngameForge.renderCrosshairs = false;
			}
		}
	}
	
	public void renderTick(Minecraft mc, WorldClient world, float renderTick)
	{
	}
	
	public boolean tempKeyDown;
	
	public boolean radialShow;
	public float radialPlayerYaw;
	public float radialPlayerPitch;
	public double radialDeltaX;
	public double radialDeltaY;
	public int radialTime;
	public boolean renderCrosshair;
	
	public static boolean hasStencilBits = MinecraftForgeClient.getStencilBits() > 0;
}
