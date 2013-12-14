package sync.client.core;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import sync.common.Sync;
import sync.common.shell.ShellState;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandlerClient implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		if(type.equals(EnumSet.of(TickType.RENDER)))
		{
			if(Minecraft.getMinecraft().theWorld != null)
			{
				preRenderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, (Float)tickData[0]);
			}
		}
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
		if(radialTime > 0)
		{
			radialTime--;
		}
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
			else if(!Keyboard.isKeyDown(Keyboard.KEY_GRAVE) && tempKeyDown)
			{
				radialShow = false;
			}
			tempKeyDown = Keyboard.isKeyDown(Keyboard.KEY_GRAVE);
		}
		else
		{
			radialShow = false;
		}
		
		if(clock != world.getWorldTime() || world.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
		{
			clock = world.getWorldTime();
			
			for(ShellState state : shells)
			{
				state.tick();
			}
		}
		
//		world.spawnParticle("explode", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 0.0D, 0.0D, 0.0D);
	}
	
	public void preRenderTick(Minecraft mc, WorldClient world, float renderTick)
	{
		if(radialShow)
		{
			Mouse.getDX();
			Mouse.getDY();
			mc.mouseHelper.deltaX = mc.mouseHelper.deltaY = 0;
			mc.renderViewEntity.prevRotationYawHead = mc.renderViewEntity.rotationYawHead = radialPlayerYaw;
			mc.renderViewEntity.prevRotationYaw = mc.renderViewEntity.rotationYaw = radialPlayerYaw;
			mc.renderViewEntity.prevRotationPitch = mc.renderViewEntity.rotationPitch = radialPlayerPitch;
		}	
	}

	public void renderTick(Minecraft mc, WorldClient world, float renderTick)
	{
		if(radialShow)
		{
			double mag = Math.sqrt(Sync.proxy.tickHandlerClient.radialDeltaX * Sync.proxy.tickHandlerClient.radialDeltaX + Sync.proxy.tickHandlerClient.radialDeltaY * Sync.proxy.tickHandlerClient.radialDeltaY);
			double magAcceptance = 0.8D;
			
			ScaledResolution reso = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			
			float prog = MathHelper.clamp_float((3F - radialTime + renderTick) / 3F, 0.0F, 1.0F);
			
			float rad = (mag > magAcceptance ? 0.85F : 0.82F) * prog;
			
			int radius = 80;
			radius *= Math.pow(prog, 0.5D);
			
			if(!mc.gameSettings.hideGUI)
			{
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPushMatrix();
				GL11.glLoadIdentity();
				
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glPushMatrix();
				GL11.glLoadIdentity();
				
				int roundness = 100;
				
				double zLev = 0.05D;
				
				if(hasStencilBits)
				{
					GL11.glEnable(GL11.GL_STENCIL_TEST);
					
					GL11.glColorMask(false, false, false, false);
					
					GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
					GL11.glStencilMask(0xFF);
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					
					rad = (mag > magAcceptance ? 0.85F : 0.82F) * prog * (257F / (float)reso.getScaledHeight());
					
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					
					GL11.glBegin(GL11.GL_TRIANGLE_FAN);
					GL11.glVertex3d(0, 0, zLev);
					for(int i = 0; i <= roundness; i++)
					{
						double angle = Math.PI * 2 * i / roundness;
						GL11.glVertex3d(Math.cos(angle) * reso.getScaledHeight_double() / reso.getScaledWidth_double() * rad, Math.sin(angle) * rad, zLev);
					}
					GL11.glEnd();
					
					GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
					
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					
					rad = 0.44F * prog * (257F / (float)reso.getScaledHeight());
					
					GL11.glBegin(GL11.GL_TRIANGLE_FAN);
					GL11.glVertex3d(0, 0, zLev);
					for(int i = 0; i <= roundness; i++)
					{
						double angle = Math.PI * 2 * i / roundness;
						GL11.glVertex3d(Math.cos(angle) * reso.getScaledHeight_double() / reso.getScaledWidth_double() * rad, Math.sin(angle) * rad, zLev);
					}
					GL11.glEnd();
					
					GL11.glStencilMask(0x00);
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
					
					GL11.glColorMask(true, true, true, true);
				}
				
				rad = (mag > magAcceptance ? 0.85F : 0.82F) * prog * (257F / (float)reso.getScaledHeight());
				
				GL11.glColor4f(0.0F, 0.0F, 0.0F, mag > magAcceptance ? 0.6F : 0.4F);
				
				GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glVertex3d(0, 0, zLev);
				for(int i = 0; i <= roundness; i++)
				{
					double angle = Math.PI * 2 * i / roundness;
					GL11.glVertex3d(Math.cos(angle) * reso.getScaledHeight_double() / reso.getScaledWidth_double() * rad, Math.sin(angle) * rad, zLev);
				}
				GL11.glEnd();
				
				if(hasStencilBits)
				{
					GL11.glDisable(GL11.GL_STENCIL_TEST);
				}
				
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				
				GL11.glPopMatrix();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPopMatrix();
				
				GL11.glDisable(GL11.GL_BLEND);
				
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
		}
	}
	
	public boolean tempKeyDown;
	
	public boolean radialShow;
	public float radialPlayerYaw;
	public float radialPlayerPitch;
	public double radialDeltaX;
	public double radialDeltaY;
	public int radialTime;
	public boolean renderCrosshair;
	
	public long clock;
	
	public ArrayList<ShellState> shells = new ArrayList<ShellState>();
	
	public static boolean hasStencilBits = MinecraftForgeClient.getStencilBits() > 0;
}
