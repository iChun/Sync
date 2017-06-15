package me.ichun.mods.sync.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.math.MathHelper; import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Random;

public class ModelShellConstructor extends ModelBase
{
	public ModelRenderer base;
	public ModelRenderer ceiling;
	public ModelRenderer baseSkirtBack;
	public ModelRenderer ceilingSkirtBack;
	public ModelRenderer baseSkirtLeft;
	public ModelRenderer baseSkirtRight;
	public ModelRenderer ceilingSkirtRight;
	public ModelRenderer ceilingSkirtLeft;
	public ModelRenderer backWall;
	public ModelRenderer backPillarRight;
	public ModelRenderer backPillarLeft;
	public ModelRenderer sideWallLeft;
	public ModelRenderer sideWallRight;
	public ModelRenderer baseSkirtFront;
	public ModelRenderer ceilingSkirtFront;
	public ModelRenderer frontPillarRight;
	public ModelRenderer frontPillarLeft;
	public ModelRenderer bodyPixel;
	public ModelRenderer bodyLayer;
	public ModelRenderer armLayer;
	public ModelRenderer headLayer;
	public ModelRenderer printerR;
	public ModelRenderer printerL;
	public ModelRenderer sprayGStand;
	public ModelRenderer sprayRStand;
	public ModelRenderer sprayBStand;
	public ModelRenderer sprayerG;
	public ModelRenderer sprayerR;
	public ModelRenderer sprayerB;
	public ModelRenderer doorRight;
	public ModelRenderer doorLeft;
	public ModelRenderer stencilBuffer;
	
	public Random rand;
	
	public ArrayList<int[]> bodyPixelCoords;
	public ArrayList<int[]> armPixelCoords;
	public ArrayList<int[]> headPixelCoords;
	
	public ModelPlayer modelBiped;
	
	public ResourceLocation txBiped;

	public ModelShellConstructor()
	{
		rand = new Random();
		
		bodyPixelCoords = new ArrayList<>();
		
		for(int x = -7; x <= 7; x += 2)
		{
			for(int z = -3; z <= 3; z += 2)
			{
				bodyPixelCoords.add(new int[] { x, z });
			}
		}
		
		armPixelCoords = new ArrayList<>();
		
		for(int x = 9; x <= 15; x += 2)
		{
			for(int z = -3; z <= 3; z += 2)
			{
				armPixelCoords.add(new int[] { x, z });
			}
		}
		
		headPixelCoords = new ArrayList<>();
		
		for(int x = -7; x <= 7; x += 2)
		{
			for(int z = -7; z <= 7; z += 2)
			{
				headPixelCoords.add(new int[] { x, z });
			}
		}

		modelBiped = new ModelPlayer(1F, false);
		modelBiped.isChild = false;
		
		txBiped = DefaultPlayerSkin.getDefaultSkinLegacy();
		
		textureWidth = 256;
		textureHeight = 256;

		base = new ModelRenderer(this, 64, 62);
		base.addBox(-16F, 0F, -16F, 32, 1, 32);
		base.setRotationPoint(0F, 23F, 0F);
		base.setTextureSize(256, 256);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		ceiling = new ModelRenderer(this, 0, 0);
		ceiling.addBox(-16F, 0F, -16F, 32, 2, 32);
		ceiling.setRotationPoint(0F, -40F, 0F);
		ceiling.setTextureSize(256, 256);
		ceiling.mirror = true;
		setRotation(ceiling, 0F, 0F, 0F);
		baseSkirtBack = new ModelRenderer(this, 0, 34);
		baseSkirtBack.addBox(-16F, 0F, -1F, 32, 1, 3);
		baseSkirtBack.setRotationPoint(0F, 22F, 14F);
		baseSkirtBack.setTextureSize(256, 256);
		baseSkirtBack.mirror = true;
		setRotation(baseSkirtBack, 0F, 0F, 0F);
		ceilingSkirtBack = new ModelRenderer(this, 0, 34);
		ceilingSkirtBack.addBox(-16F, 0F, -1F, 32, 1, 3);
		ceilingSkirtBack.setRotationPoint(0F, -38F, 14F);
		ceilingSkirtBack.setTextureSize(256, 256);
		ceilingSkirtBack.mirror = true;
		setRotation(ceilingSkirtBack, 0F, 0F, 0F);
		baseSkirtLeft = new ModelRenderer(this, 0, 38);
		baseSkirtLeft.addBox(-1F, 0F, -16F, 3, 1, 27);
		baseSkirtLeft.setRotationPoint(14F, 22F, 2F);
		baseSkirtLeft.setTextureSize(256, 256);
		baseSkirtLeft.mirror = true;
		setRotation(baseSkirtLeft, 0F, 0F, 0F);
		baseSkirtRight = new ModelRenderer(this, 0, 38);
		baseSkirtRight.addBox(-1F, 0F, -16F, 3, 1, 27);
		baseSkirtRight.setRotationPoint(-15F, 22F, 2F);
		baseSkirtRight.setTextureSize(256, 256);
		baseSkirtRight.mirror = true;
		setRotation(baseSkirtRight, 0F, 0F, 0F);
		ceilingSkirtRight = new ModelRenderer(this, 0, 38);
		ceilingSkirtRight.addBox(-1F, 0F, -16F, 3, 1, 27);
		ceilingSkirtRight.setRotationPoint(-15F, -38F, 2F);
		ceilingSkirtRight.setTextureSize(256, 256);
		ceilingSkirtRight.mirror = true;
		setRotation(ceilingSkirtRight, 0F, 0F, 0F);
		ceilingSkirtLeft = new ModelRenderer(this, 0, 38);
		ceilingSkirtLeft.addBox(-1F, 0F, -16F, 3, 1, 27);
		ceilingSkirtLeft.setRotationPoint(14F, -38F, 2F);
		ceilingSkirtLeft.setTextureSize(256, 256);
		ceilingSkirtLeft.mirror = true;
		setRotation(ceilingSkirtLeft, 0F, 0F, 0F);
		backWall = new ModelRenderer(this, 0, 66);
		backWall.addBox(-16F, 0F, 0F, 26, 59, 1);
		backWall.setRotationPoint(3F, -37F, 14F);
		backWall.setTextureSize(256, 256);
		backWall.mirror = true;
		setRotation(backWall, 0F, 0F, 0F);
		backPillarRight = new ModelRenderer(this, 0, 126);
		backPillarRight.addBox(0F, 0F, 0F, 3, 59, 3);
		backPillarRight.setRotationPoint(-16F, -37F, 13F);
		backPillarRight.setTextureSize(256, 256);
		backPillarRight.mirror = true;
		setRotation(backPillarRight, 0F, 0F, 0F);
		backPillarLeft = new ModelRenderer(this, 0, 126);
		backPillarLeft.addBox(0F, 0F, 0F, 3, 59, 3);
		backPillarLeft.setRotationPoint(13F, -37F, 13F);
		backPillarLeft.setTextureSize(256, 256);
		backPillarLeft.mirror = true;
		setRotation(backPillarLeft, 0F, 0F, 0F);
		sideWallLeft = new ModelRenderer(this, 12, 126);
		sideWallLeft.addBox(0F, 0F, 0F, 1, 59, 27);
		sideWallLeft.setRotationPoint(15.05F, -37F, -14F);
		sideWallLeft.setTextureSize(256, 256);
		sideWallLeft.mirror = true;
		setRotation(sideWallLeft, 0F, 0F, 0F);
		sideWallRight = new ModelRenderer(this, 12, 126);
		sideWallRight.addBox(0F, 0F, 0F, 1, 59, 27);
		sideWallRight.setRotationPoint(-16.05F, -37F, -14F);
		sideWallRight.setTextureSize(256, 256);
		sideWallRight.mirror = true;
		setRotation(sideWallRight, 0F, 0F, 0F);
		baseSkirtFront = new ModelRenderer(this, 70, 34);
		baseSkirtFront.addBox(-16F, 0F, -1F, 26, 1, 3);
		baseSkirtFront.setRotationPoint(3F, 22F, -13F);
		baseSkirtFront.setTextureSize(256, 256);
		baseSkirtFront.mirror = true;
		setRotation(baseSkirtFront, 0F, 0F, 0F);
		ceilingSkirtFront = new ModelRenderer(this, 70, 34);
		ceilingSkirtFront.addBox(-16F, 0F, -1F, 26, 1, 3);
		ceilingSkirtFront.setRotationPoint(3F, -38F, -13F);
		ceilingSkirtFront.setTextureSize(256, 256);
		ceilingSkirtFront.mirror = true;
		setRotation(ceilingSkirtFront, 0F, 0F, 0F);
		frontPillarRight = new ModelRenderer(this, 60, 38);
		frontPillarRight.addBox(0F, -7F, 0F, 1, 61, 1);
		frontPillarRight.setRotationPoint(-16F, -31F, -15F);
		frontPillarRight.setTextureSize(256, 256);
		frontPillarRight.mirror = true;
		setRotation(frontPillarRight, 0F, 0F, 0F);
		frontPillarLeft = new ModelRenderer(this, 60, 38);
		frontPillarLeft.addBox(0F, -7F, 0F, 1, 61, 1);
		frontPillarLeft.setRotationPoint(15F, -31F, -15F);
		frontPillarLeft.setTextureSize(256, 256);
		frontPillarLeft.mirror = true;
		setRotation(frontPillarLeft, 0F, 0F, 0F);
		bodyPixel = new ModelRenderer(this, 192, 200);
		bodyPixel.addBox(-1F, -1F, -1F, 2, 2, 2);
		bodyPixel.setRotationPoint(22F, 0F, 0F);
		bodyPixel.setTextureSize(256, 256);
		bodyPixel.mirror = true;
		setRotation(bodyPixel, 0F, 0F, 0F);
		bodyLayer = new ModelRenderer(this, 192, 200);
		bodyLayer.addBox(-8F, -1F, -4F, 16, 2, 8);
		bodyLayer.setRotationPoint(35F, 0F, 0F);
		bodyLayer.setTextureSize(256, 256);
		bodyLayer.mirror = true;
		setRotation(bodyLayer, 0F, 0F, 0F);
		armLayer = new ModelRenderer(this, 192, 200);
		armLayer.addBox(-4F, -1F, -4F, 8, 2, 8);
		armLayer.setRotationPoint(49F, 0F, 0F);
		armLayer.setTextureSize(256, 256);
		armLayer.mirror = true;
		setRotation(armLayer, 0F, 0F, 0F);
		headLayer = new ModelRenderer(this, 192, 200);
		headLayer.addBox(-8F, -1F, -8F, 16, 2, 16);
		headLayer.setRotationPoint(35F, 8F, 0F);
		headLayer.setTextureSize(256, 256);
		headLayer.mirror = true;
		setRotation(headLayer, 0F, 0F, 0F);
		printerR = new ModelRenderer(this, 54, 66);
		printerR.addBox(0F, -1F, -1F, 1, 2, 2);
		printerR.setRotationPoint(-14F, 21F, 13F);
		printerR.setTextureSize(256, 256);
		printerR.mirror = true;
		setRotation(printerR, 0F, 0.7853982F, 0F);
		printerL = new ModelRenderer(this, 54, 66);
		printerL.addBox(-1F, -1F, -1F, 1, 2, 2);
		printerL.setRotationPoint(14F, 21F, 13F);
		printerL.setTextureSize(256, 256);
		printerL.mirror = true;
		setRotation(printerL, 0F, -0.7853982F, 0F);
		sprayGStand = new ModelRenderer(this, 128, 0);
		sprayGStand.addBox(-0.5F, -0.5F, -0.5F, 1, 32, 1);
		sprayGStand.setRotationPoint(11F, -8F, -9F);
		sprayGStand.setTextureSize(256, 256);
		sprayGStand.mirror = true;
		setRotation(sprayGStand, 0F, -0.7853982F, 0F);
		sprayRStand = new ModelRenderer(this, 128, 0);
		sprayRStand.addBox(-0.5F, -0.5F, -0.5F, 1, 32, 1);
		sprayRStand.setRotationPoint(-11F, -8F, -9F);
		sprayRStand.setTextureSize(256, 256);
		sprayRStand.mirror = true;
		setRotation(sprayRStand, 0F, 0.7853982F, 0F);
		sprayBStand = new ModelRenderer(this, 128, 0);
		sprayBStand.addBox(-0.5F, -0.5F, -0.5F, 1, 32, 1);
		sprayBStand.setRotationPoint(0F, -8F, 12F);
		sprayBStand.setTextureSize(256, 256);
		sprayBStand.mirror = true;
		setRotation(sprayBStand, 0F, 0F, 0F);
		sprayerG = new ModelRenderer(this, 132, 0);
		sprayerG.addBox(-1F, 0F, -0.55F, 2, 1, 2);
		sprayerG.setRotationPoint(11F, -9F, -9F);
		sprayerG.setTextureSize(256, 256);
		sprayerG.mirror = true;
		setRotation(sprayerG, 0F, -0.7853982F, 0F);
		sprayerR = new ModelRenderer(this, 132, 0);
		sprayerR.addBox(-1F, 0F, -0.55F, 2, 1, 2);
		sprayerR.setRotationPoint(-11F, -9F, -9F);
		sprayerR.setTextureSize(256, 256);
		sprayerR.mirror = true;
		setRotation(sprayerR, 0F, 0.7853982F, 0F);
		sprayerB = new ModelRenderer(this, 132, 0);
		sprayerB.addBox(-1F, 0F, -1.45F, 2, 1, 2);
		sprayerB.setRotationPoint(0F, -9F, 12F);
		sprayerB.setTextureSize(256, 256);
		sprayerB.mirror = true;
		setRotation(sprayerB, 0F, 0F, 0F);
		doorRight = new ModelRenderer(this, 140, 0);
		doorRight.addBox(-0.5F, -30F, 0F, 15, 61, 1);
		doorRight.setRotationPoint(-14.5F, -8F, -15F);
		doorRight.setTextureSize(256, 256);
		doorRight.mirror = true;
		setRotation(doorRight, 0F, 0F, 0F);
		doorLeft = new ModelRenderer(this, 140, 0);
		doorLeft.addBox(-14.5F, -30F, 0F, 15, 61, 1);
		doorLeft.setRotationPoint(14.5F, -8F, -15F);
		doorLeft.setTextureSize(256, 256);
		doorLeft.mirror = true;
		setRotation(doorLeft, 0F, 0F, 0F);
		stencilBuffer = new ModelRenderer(this, 0, 0);
		stencilBuffer.addBox(-17F, 0.5F, -8.5F, 34, 60, 17);
		stencilBuffer.setRotationPoint(0F, -9F, 0F);
		stencilBuffer.setTextureSize(256, 256);
		stencilBuffer.mirror = true;
		setRotation(stencilBuffer, 0F, 0F, 0F);
	}

	public void render(float doorProg, float f5, boolean alpha)
	{
		if(!alpha)
		{
			base.render(f5);
			ceiling.render(f5);
			
			baseSkirtBack.render(f5);
			baseSkirtLeft.render(f5);
			baseSkirtRight.render(f5);
			baseSkirtFront.render(f5);
			
			ceilingSkirtBack.render(f5);
			ceilingSkirtLeft.render(f5);
			ceilingSkirtRight.render(f5);
			ceilingSkirtFront.render(f5);
			
			backPillarRight.render(f5);
			backPillarLeft.render(f5);
			frontPillarRight.render(f5);
			frontPillarLeft.render(f5);
		}
		
		backWall.render(f5);
		
		sideWallLeft.render(f5);
		sideWallRight.render(f5);
		
		float retractProg = MathHelper.clamp(doorProg / 0.4F, 0.0F, 1.0F);
		float swingProg = MathHelper.clamp((doorProg - 0.3F) / 0.4F, 0.0F, 1.0F);

		doorLeft.rotateAngleY = (float)Math.toRadians(-90F) - (float)Math.toRadians(-90F) * swingProg;
		doorRight.rotateAngleY = (float)Math.toRadians(90F) - (float)Math.toRadians(90F) * swingProg;
		doorLeft.rotationPointZ = doorRight.rotationPointZ = -1 + (-14 * retractProg);

		doorRight.render(f5);
		doorLeft.render(f5);
	}
	
	public void renderConstructionProgress(float prog, float f5, boolean renderMachineParts, boolean renderPlayer)
	{
		float printProg = -54F;
		
		if(renderMachineParts)
		{
			boolean renderSprayStand = false;
			
			if(prog <= 0.95F)
			{
				if(prog >= 0.940F)
				{
					renderSprayStand = 22F + (-55F * MathHelper.clamp((float)Math.pow((prog - 0.940F) / 0.005F , 0.5D), 0.0F, 1.0F)) < -8F;
				}
			}
			else
			{
				renderSprayStand = 22F + (-55F * (1.0F - prog) / 0.05F) < -8F;
			}
			if(renderSprayStand)
			{
				sprayRStand.rotationPointY = sprayGStand.rotationPointY = sprayBStand.rotationPointY = -8F;
				
				sprayGStand.render(f5);
				sprayRStand.render(f5);
				sprayBStand.render(f5);
			}
			if(prog <= 0.95F)
			{
				if(prog >= 0.75F)
				{
					printerL.rotationPointY = printerR.rotationPointY = 21.0F + (printProg * prog / 0.95F) - (printProg * ((prog - 0.75F) / 0.3F));
					
					printerR.render(f5);
					printerL.render(f5);
				}
				printerL.rotationPointY = printerR.rotationPointY = 21.0F + (printProg * prog / 0.95F);
				
				if(prog >= 0.940F)
				{
					sprayRStand.rotationPointY = sprayGStand.rotationPointY = sprayBStand.rotationPointY = 22F + (-58F * MathHelper.clamp((float)Math.pow((prog - 0.940F) / 0.005F , 0.5D), 0.0F, 1.0F));
					sprayerR.rotationPointY = sprayerG.rotationPointY = sprayerB.rotationPointY = 21F + (-58F * MathHelper.clamp((float)Math.pow((prog - 0.940F) / 0.005F , 0.5D), 0.0F, 1.0F));
				}
				else
				{
					sprayRStand.rotationPointY = sprayGStand.rotationPointY = sprayBStand.rotationPointY = 22F;  
					sprayerR.rotationPointY = sprayerG.rotationPointY = sprayerB.rotationPointY = 21F;
				}
			}
			else
			{
				printerL.rotationPointY = printerR.rotationPointY = 21.0F + ((printProg + 36F) * (1.0F - prog) / 0.05F);
				
				printerR.render(f5);
				printerL.render(f5);
				
				printerL.rotationPointY = printerR.rotationPointY = 21.0F + (printProg * (1.0F - prog) / 0.05F);
				
				sprayRStand.rotationPointY = sprayGStand.rotationPointY = sprayBStand.rotationPointY = 22F + (-58F * (1.0F - prog) / 0.05F);  
				sprayerR.rotationPointY = sprayerG.rotationPointY = sprayerB.rotationPointY = 21F + (-58F * (1.0F - prog) / 0.05F);
			}
			
			printerR.render(f5);
			printerL.render(f5);
			
			sprayGStand.render(f5); //-8
			sprayRStand.render(f5);
			sprayBStand.render(f5);
	
			sprayerG.render(f5); //-9
			sprayerR.render(f5);
			sprayerB.render(f5);
		}
		
		if(renderPlayer)
		{
			GlStateManager.pushMatrix();
			
			float scale = 0.9375F;
			GlStateManager.scale(scale, scale, scale);
			
			float brightness = 0.7F;
			GlStateManager.color(brightness, brightness, brightness, 1.0F);
			
			GlStateManager.disableTexture2D();
			
			bodyLayer.rotationPointX = armLayer.rotationPointX = headLayer.rotationPointX = 0.0F;
			
			if(prog < 0.75F)
			{
				ArrayList<int[]> bodyPix = new ArrayList<>(bodyPixelCoords);
				
				float progPerLayer = (0.75F * (1F/24F));
				
				float pixProg = prog % progPerLayer;
				
				int pixelCount = 0;
				
				int level = 0;
				for(float f = 1F/24F * 0.75F; f < prog; f += 1F/24F * 0.75F)
				{
					rand.nextInt();
					level++;
				}
				
				for(float f = 0; f < pixProg; f += progPerLayer / 32F)
				{
					pixelCount++;
				}
				
				while(bodyPix.size() > pixelCount)
				{
					bodyPix.remove(rand.nextInt(bodyPix.size()));
				}

                for (int[] coord : bodyPix) {
                    bodyPixel.rotationPointX = coord[0];
                    bodyPixel.rotationPointZ = coord[1];
                    bodyPixel.rotationPointY = 24.0F + (-2 * level);
                    bodyPixel.render(f5);
                }
			}
			else if(prog < 0.95F)
			{
				ArrayList<int[]> armPix = new ArrayList<>(armPixelCoords);
				
				float progPerArmLayer = (0.20F * (1F/12F));
				
				float pixArmProg = (prog - 0.75F) % progPerArmLayer;
				
				int pixelArmCount = 0;
				
				for(float f = 0; f < pixArmProg; f += progPerArmLayer / 16F)
				{
					pixelArmCount++;
				}
				
				int armLevel = 0;
				for(float f = 0.75F + 1F/12F * 0.20F; f < prog; f += 1F/12F * 0.20F)
				{
					rand.nextInt();
					armLevel++;
				}
				
				while(armPix.size() > pixelArmCount)
				{
					armPix.remove(armLevel == 0 ? armPix.size() - 1 : rand.nextInt(armPix.size()));
				}

                for (int[] coord : armPix) {
                    bodyPixel.rotationPointX = coord[0];
                    bodyPixel.rotationPointZ = coord[1];
                    bodyPixel.rotationPointY = -22.0F + (2 * armLevel);
                    bodyPixel.render(f5);

                    bodyPixel.rotationPointX = -coord[0];
                    bodyPixel.rotationPointZ = -coord[1];
                    bodyPixel.render(f5);
                }
				
				rand.setSeed("headConstructor".hashCode());
				
				ArrayList<int[]> headPix = new ArrayList<>(headPixelCoords);
				
				float progPerHeadLayer = (0.20F * (1F/8F));
				
				float pixHeadProg = (prog - 0.75F) % progPerHeadLayer;
				
				int pixelHeadCount = 0;
				
				for(float f = 0; f < pixHeadProg; f += progPerHeadLayer / 64F)
				{
					pixelHeadCount++;
				}
				
				int headLevel = 0;
				for(float f = 0.75F + 1F/8F * 0.20F; f < prog; f += 1F/8F * 0.20F)
				{
					headLevel++;
				}
				
				while(headPix.size() > pixelHeadCount)
				{
					headPix.remove(headLevel == 0 ? headPix.size() - 1 : rand.nextInt(headPix.size()));
				}

                for (int[] coord : headPix) {
                    bodyPixel.rotationPointX = coord[0];
                    bodyPixel.rotationPointZ = coord[1];
                    bodyPixel.rotationPointY = -24.0F + (-2 * headLevel);
                    bodyPixel.render(f5);
                }
			}
			
			if(prog < 0.95F)
			{
				for(float f = 1F/24F * 0.75F; f < prog; f += 1F/24F * 0.75F)
				{
					if(f > 0.75F)
					{
						break;
					}
					bodyLayer.rotationPointY = 26.0F + (-2 * f / (1F/24F * 0.75F)); 
					bodyLayer.render(f5);
				}
				
				for(float f = 0.75F + 1F/8F * 0.20F; f < prog; f += 1F/8F * 0.20F)
				{
					if(f > 0.95F)
					{
						break;
					}
					headLayer.rotationPointY = 38.0F + (-2 * f / (1F/8F * 0.20F)); 
					headLayer.render(f5);
				}
				
				for(float f = 0.75F + 1F/12F * 0.20F; f < prog; f += 1F/12F * 0.20F)
				{
					if(f > 0.95F)
					{
						break;
					}
					armLayer.rotationPointX = 12F;
					armLayer.rotationPointY = -24F + (2 * (f - 0.75F) / (1F/12F * 0.20F)); 
					armLayer.render(f5);
					
					armLayer.rotationPointX = -12F;
					armLayer.render(f5);
				}
				
				GlStateManager.enableTexture2D();
			}
			else
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				GlStateManager.translate(0.0D, -0.72D, 0.0D);
				final int stencilBit = MinecraftForgeClient.reserveStencilBit();
				
				if(stencilBit >= 0 && prog < 1.0F)
				{
					GL11.glDepthMask(false);
					
					GL11.glEnable(GL11.GL_STENCIL_TEST);
					
					GlStateManager.colorMask(false, false, false, false);
					
					final int stencilMask = 1 << stencilBit;
					
					GL11.glStencilMask(stencilMask);
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					GL11.glStencilFunc(GL11.GL_ALWAYS, 0, stencilMask);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
					
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	
					modelBiped.bipedHead.render(f5);
                    modelBiped.bipedHeadwear.render(f5);
					modelBiped.bipedBody.render(f5);
					modelBiped.bipedRightArm.render(f5);
					modelBiped.bipedLeftArm.render(f5);
					modelBiped.bipedRightLeg.render(f5);
					modelBiped.bipedLeftLeg.render(f5);
					
					GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
					
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					
					GlStateManager.pushMatrix();
					
					GlStateManager.scale(0.5F, 0.5F, 0.5F);
					stencilBuffer.rotationPointY = 48F + (-64F * (1.0F - prog) / 0.05F);
					stencilBuffer.render(f5);
					
					GlStateManager.popMatrix();
					
					GL11.glStencilMask(0x00);
					GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
	
					GlStateManager.colorMask(true, true, true, true);
	
					GL11.glDepthMask(true);
	
					
					if(prog < 1.0F)
					{
						modelBiped.bipedHead.render(f5);
                        modelBiped.bipedHeadwear.render(f5);
						modelBiped.bipedBody.render(f5);
						modelBiped.bipedRightArm.render(f5);
						modelBiped.bipedLeftArm.render(f5);
						modelBiped.bipedRightLeg.render(f5);
						modelBiped.bipedLeftLeg.render(f5);
					}
					
					GL11.glDepthMask(false);
					
					GlStateManager.colorMask(false, false, false, false);
					
					GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
					GL11.glStencilMask(stencilMask);
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	
					modelBiped.bipedHead.render(f5);
                    modelBiped.bipedHeadwear.render(f5);
					modelBiped.bipedBody.render(f5);
					modelBiped.bipedRightArm.render(f5);
					modelBiped.bipedLeftArm.render(f5);
					modelBiped.bipedRightLeg.render(f5);
					modelBiped.bipedLeftLeg.render(f5);
					
					GL11.glStencilFunc(GL11.GL_ALWAYS, 0, stencilMask);
					
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					
					GlStateManager.pushMatrix();
					
					GlStateManager.scale(0.5F, 0.5F, 0.5F);
					stencilBuffer.rotationPointY = 48F + (-64F * (1.0F - prog) / 0.05F);
					stencilBuffer.render(f5);
					
					GlStateManager.popMatrix();
					
					GL11.glStencilMask(0x00);
					GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
	
					GlStateManager.colorMask(true, true, true, true);
	
					GL11.glDepthMask(true);
	
					GlStateManager.enableTexture2D();
					Minecraft.getMinecraft().renderEngine.bindTexture(txBiped);
					
					GlStateManager.scale(1.001F, 1.001F, 1.001F);
					GlStateManager.translate(0.0D, -0.00005D, 0.0D);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					
					modelBiped.bipedHead.render(f5);
                    modelBiped.bipedHeadwear.render(f5);
					modelBiped.bipedBody.render(f5);
					modelBiped.bipedRightArm.render(f5);
					modelBiped.bipedLeftArm.render(f5);
					modelBiped.bipedRightLeg.render(f5);
					modelBiped.bipedLeftLeg.render(f5);
	
					GL11.glStencilMask(0);
					GL11.glDisable(GL11.GL_STENCIL_TEST);
				}
				else
				{
					if(prog < 1.0F)
					{
						modelBiped.bipedHead.render(f5);
                        modelBiped.bipedHeadwear.render(f5);
						modelBiped.bipedBody.render(f5);
						modelBiped.bipedRightArm.render(f5);
						modelBiped.bipedLeftArm.render(f5);
						modelBiped.bipedRightLeg.render(f5);
						modelBiped.bipedLeftLeg.render(f5);
					}
	
					GlStateManager.enableTexture2D();
					Minecraft.getMinecraft().renderEngine.bindTexture(txBiped);
					
					GlStateManager.scale(1.001F, 1.001F, 1.001F);
					GlStateManager.translate(0.0D, -0.00005D, 0.0D);
					GlStateManager.color(1.0F, 1.0F, 1.0F, (prog - 0.95F) / 0.05F);
					
					modelBiped.bipedHead.render(f5);
                    modelBiped.bipedHeadwear.render(f5);
					modelBiped.bipedBody.render(f5);
					modelBiped.bipedRightArm.render(f5);
					modelBiped.bipedLeftArm.render(f5);
					modelBiped.bipedRightLeg.render(f5);
					modelBiped.bipedLeftLeg.render(f5);
				}
				
				MinecraftForgeClient.releaseStencilBit(stencilBit);
			}
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
