package sync.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class ModelShellStorage extends ModelBase
{
	public ModelRenderer base;
	public ModelRenderer ceiling;
	public ModelRenderer baseSkirtBack;
	public ModelRenderer ceilingSkirtBack;
	public ModelRenderer baseSkirtLeft;
	public ModelRenderer baseSkirtRight;
	public ModelRenderer ceilingSkirtRight;
	public ModelRenderer ceilingSkirtLeft;
	public ModelRenderer backSupport1;
	public ModelRenderer backPillarRight;
	public ModelRenderer backPillarLeft;
	public ModelRenderer sideWallLeft;
	public ModelRenderer sideWallRight;
	public ModelRenderer baseSkirtFront;
	public ModelRenderer ceilingSkirtFront;
	public ModelRenderer frontPillarRight;
	public ModelRenderer frontPillarLeft;
	public ModelRenderer doorRight;
	public ModelRenderer doorLeft;
	public ModelRenderer bracketFeetFront;
	public ModelRenderer bracketFeetBack;
	public ModelRenderer bracketFeetRight;
	public ModelRenderer bracketFeetLeft;
	public ModelRenderer bracketFeetSupportRight;
	public ModelRenderer bracketFeetSupportLeft;
	public ModelRenderer bracketShoulderSupportL;
	public ModelRenderer bracketShoulderSupportL2;
	public ModelRenderer bracketShoulderSupportR;
	public ModelRenderer bracketShoulderSupportR2;
	public ModelRenderer headJackLeft;
	public ModelRenderer headJackTop;
	public ModelRenderer headJackBottom;
	public ModelRenderer headJackRight;
	public ModelRenderer headJackCore;
	public ModelRenderer backWall;
	public ModelRenderer backSupport2;
	public ModelRenderer backSupport3;
	public ModelRenderer backSupport4;
	public ModelRenderer indicatorBottom;
	public ModelRenderer indicatorTop;
	public ModelRenderer indicatorRight;
	public ModelRenderer indicatorLeft;
	public ModelRenderer indicator2;
	
	public ModelBiped modelBiped;
	
	public ResourceLocation txBiped;
	
	public boolean powered;
	public boolean isHomeUnit;

	public ModelShellStorage()
	{
		textureWidth = 256;
		textureHeight = 256;

		modelBiped = new ModelBiped();
		modelBiped.isChild = false;

		txBiped = AbstractClientPlayer.locationStevePng;
		
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
		backSupport1 = new ModelRenderer(this, 88, 128);
		backSupport1.addBox(-16F, 0F, 0F, 3, 59, 1);
		backSupport1.setRotationPoint(5.5F, -37F, 13F);
		backSupport1.setTextureSize(256, 256);
		backSupport1.mirror = true;
		setRotation(backSupport1, 0F, 0F, 0F);
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
		bracketFeetFront = new ModelRenderer(this, 70, 100);
		bracketFeetFront.addBox(-9F, 17F, 0F, 18, 3, 1);
		bracketFeetFront.setRotationPoint(0F, 0F, -5F);
		bracketFeetFront.setTextureSize(256, 256);
		bracketFeetFront.mirror = true;
		setRotation(bracketFeetFront, 0F, 0F, 0F);
		bracketFeetBack = new ModelRenderer(this, 70, 100);
		bracketFeetBack.addBox(-9F, 17F, 0F, 18, 3, 1);
		bracketFeetBack.setRotationPoint(0F, 0F, 4F);
		bracketFeetBack.setTextureSize(256, 256);
		bracketFeetBack.mirror = true;
		setRotation(bracketFeetBack, 0F, 0F, 0F);
		bracketFeetRight = new ModelRenderer(this, 70, 104);
		bracketFeetRight.addBox(-9F, 17F, -4F, 1, 3, 8);
		bracketFeetRight.setRotationPoint(0F, 0F, 0F);
		bracketFeetRight.setTextureSize(256, 256);
		bracketFeetRight.mirror = true;
		setRotation(bracketFeetRight, 0F, 0F, 0F);
		bracketFeetLeft = new ModelRenderer(this, 70, 104);
		bracketFeetLeft.addBox(8F, 17F, -4F, 1, 3, 8);
		bracketFeetLeft.setRotationPoint(0F, 0F, 0F);
		bracketFeetLeft.setTextureSize(256, 256);
		bracketFeetLeft.mirror = true;
		setRotation(bracketFeetLeft, 0F, 0F, 0F);
		bracketFeetSupportRight = new ModelRenderer(this, 70, 115);
		bracketFeetSupportRight.addBox(-9.25F, 16.75F, -1F, 1, 9, 2);
		bracketFeetSupportRight.setRotationPoint(0F, 0F, 0F);
		bracketFeetSupportRight.setTextureSize(256, 256);
		bracketFeetSupportRight.mirror = true;
		setRotation(bracketFeetSupportRight, 0F, 0F, 0F);
		bracketFeetSupportLeft = new ModelRenderer(this, 70, 115);
		bracketFeetSupportLeft.addBox(8.25F, 16.75F, -1F, 1, 9, 2);
		bracketFeetSupportLeft.setRotationPoint(0F, 0F, 0F);
		bracketFeetSupportLeft.setTextureSize(256, 256);
		bracketFeetSupportLeft.mirror = true;
		setRotation(bracketFeetSupportLeft, 0F, 0F, 0F);
		bracketShoulderSupportL = new ModelRenderer(this, 70, 107);
		bracketShoulderSupportL.addBox(-0.5F, -1F, -19F, 1, 1, 19);
		bracketShoulderSupportL.setRotationPoint(7F, -15F, 14F);
		bracketShoulderSupportL.setTextureSize(256, 256);
		bracketShoulderSupportL.mirror = true;
		setRotation(bracketShoulderSupportL, 0F, 0F, 0F);
		bracketShoulderSupportL2 = new ModelRenderer(this, 78, 115);
		bracketShoulderSupportL2.addBox(-0.5F, -4F, -19F, 1, 4, 1);
		bracketShoulderSupportL2.setRotationPoint(7F, -15F, 14F);
		bracketShoulderSupportL2.setTextureSize(256, 256);
		bracketShoulderSupportL2.mirror = true;
		setRotation(bracketShoulderSupportL2, 0F, 0F, 0F);
		bracketShoulderSupportR = new ModelRenderer(this, 70, 107);
		bracketShoulderSupportR.addBox(-0.5F, -1F, -19F, 1, 1, 19);
		bracketShoulderSupportR.setRotationPoint(-7F, -15F, 14F);
		bracketShoulderSupportR.setTextureSize(256, 256);
		bracketShoulderSupportR.mirror = true;
		setRotation(bracketShoulderSupportR, 0F, 0F, 0F);
		bracketShoulderSupportR2 = new ModelRenderer(this, 78, 115);
		bracketShoulderSupportR2.addBox(-0.5F, -4F, -19F, 1, 4, 1);
		bracketShoulderSupportR2.setRotationPoint(-7F, -15F, 14F);
		bracketShoulderSupportR2.setTextureSize(256, 256);
		bracketShoulderSupportR2.mirror = true;
		setRotation(bracketShoulderSupportR2, 0F, 0F, 0F);
		headJackLeft = new ModelRenderer(this, 70, 127);
		headJackLeft.addBox(0.5F, -2F, -6F, 1, 1, 6);
		headJackLeft.setRotationPoint(0F, -27F, 15F);
		headJackLeft.setTextureSize(256, 256);
		headJackLeft.mirror = true;
		setRotation(headJackLeft, 0F, 0F, 0F);
		headJackTop = new ModelRenderer(this, 70, 134);
		headJackTop.addBox(-1.5F, -3F, -6F, 3, 1, 6);
		headJackTop.setRotationPoint(0F, -27F, 15F);
		headJackTop.setTextureSize(256, 256);
		headJackTop.mirror = true;
		setRotation(headJackTop, 0F, 0F, 0F);
		headJackBottom = new ModelRenderer(this, 70, 134);
		headJackBottom.addBox(-1.5F, -1F, -6F, 3, 1, 6);
		headJackBottom.setRotationPoint(0F, -27F, 15F);
		headJackBottom.setTextureSize(256, 256);
		headJackBottom.mirror = true;
		setRotation(headJackBottom, 0F, 0F, 0F);
		headJackRight = new ModelRenderer(this, 70, 127);
		headJackRight.addBox(-1.5F, -2F, -6F, 1, 1, 6);
		headJackRight.setRotationPoint(0F, -27F, 15F);
		headJackRight.setTextureSize(256, 256);
		headJackRight.mirror = true;
		setRotation(headJackRight, 0F, 0F, 0F);
		headJackCore = new ModelRenderer(this, 70, 141);
		headJackCore.addBox(-0.5F, -2F, -5F, 1, 1, 5);
		headJackCore.setRotationPoint(0F, -27F, 15F);
		headJackCore.setTextureSize(256, 256);
		headJackCore.mirror = true;
		setRotation(headJackCore, 0F, 0F, 0F);
		backWall = new ModelRenderer(this, 96, 128);
		backWall.addBox(-16F, 0F, 0F, 26, 59, 2);
		backWall.setRotationPoint(3F, -37F, 14F);
		backWall.setTextureSize(256, 256);
		backWall.mirror = true;
		setRotation(backWall, 0F, 0F, 0F);
		backSupport2 = new ModelRenderer(this, 88, 128);
		backSupport2.addBox(-16F, 0F, 0F, 3, 59, 1);
		backSupport2.setRotationPoint(11.5F, -37F, 13F);
		backSupport2.setTextureSize(256, 256);
		backSupport2.mirror = true;
		setRotation(backSupport2, 0F, 0F, 0F);
		backSupport3 = new ModelRenderer(this, 88, 128);
		backSupport3.addBox(-16F, 0F, 0F, 3, 59, 1);
		backSupport3.setRotationPoint(17.5F, -37F, 13F);
		backSupport3.setTextureSize(256, 256);
		backSupport3.mirror = true;
		setRotation(backSupport3, 0F, 0F, 0F);
		backSupport4 = new ModelRenderer(this, 88, 128);
		backSupport4.addBox(-16F, 0F, 0F, 3, 59, 1);
		backSupport4.setRotationPoint(23.5F, -37F, 13F);
		backSupport4.setTextureSize(256, 256);
		backSupport4.mirror = true;
		setRotation(backSupport4, 0F, 0F, 0F);

		indicatorBottom = new ModelRenderer(this, 0, 0);
		indicatorBottom.addBox(-2.5F, 7F, 0.3F, 5, 1, 1);
		indicatorBottom.setRotationPoint(10.5F, 16F, 12F);
		indicatorBottom.setTextureSize(256, 256);
		indicatorBottom.mirror = true;
		setRotation(indicatorBottom, -0.4833219F, 0F, 0F);
		indicatorTop = new ModelRenderer(this, 0, 0);
		indicatorTop.addBox(-2.5F, 0F, 0.3F, 5, 1, 1);
		indicatorTop.setRotationPoint(10.5F, 16F, 12F);
		indicatorTop.setTextureSize(256, 256);
		indicatorTop.mirror = true;
		setRotation(indicatorTop, -0.4833219F, 0F, 0F);
		indicatorRight = new ModelRenderer(this, 0, 0);
		indicatorRight.addBox(-2.5F, 1F, 0.3F, 1, 6, 1);
		indicatorRight.setRotationPoint(10.5F, 16F, 12F);
		indicatorRight.setTextureSize(256, 256);
		indicatorRight.mirror = true;
		setRotation(indicatorRight, -0.4833219F, 0F, 0F);
		indicatorLeft = new ModelRenderer(this, 0, 0);
		indicatorLeft.addBox(1.5F, 1F, 0.3F, 1, 6, 1);
		indicatorLeft.setRotationPoint(10.5F, 16F, 12F);
		indicatorLeft.setTextureSize(256, 256);
		indicatorLeft.mirror = true;
		setRotation(indicatorLeft, -0.4833219F, 0F, 0F);
		
		indicator2 = new ModelRenderer(this, 0, 9);
		indicator2.addBox(-2.5F, 1F, 0.5F, 5, 8, 1);
		indicator2.setRotationPoint(10.5F, 16F, 12F);
		indicator2.setTextureSize(256, 256);
		indicator2.mirror = true;
		setRotation(indicator2, -0.4833219F, 0F, 0F);
	}

	public void renderPlayer(float prog, float f5)
	{
		GL11.glPushMatrix();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glScalef(2.0F * 0.9375F, 2.0F * 0.9375F, 2.0F * 0.9375F);
		GL11.glTranslated(0.0D, -0.72D, 0.0D);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(txBiped);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		modelBiped.bipedHead.rotateAngleX = (float)Math.toRadians(15F);
		modelBiped.bipedHead.render(f5);
		modelBiped.bipedBody.render(f5);
		modelBiped.bipedRightArm.render(f5);
		modelBiped.bipedLeftArm.render(f5);
		modelBiped.bipedRightLeg.render(f5);
		modelBiped.bipedLeftLeg.render(f5);
		
		GL11.glPopMatrix();
	}
	
	public void renderInternals(float prog, float f5)
	{
		GL11.glPushMatrix();
		
		float legClampProg = MathHelper.clamp_float(prog / 0.6F, 0.0F, 1.0F);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glScalef(0.9375F, 0.9375F, 0.9375F);
		
		bracketFeetFront.rotationPointY = bracketFeetBack.rotationPointY = bracketFeetRight.rotationPointY = bracketFeetLeft.rotationPointY = bracketFeetSupportRight.rotationPointY = bracketFeetSupportLeft.rotationPointY = 6F - (6F * legClampProg);
		
		bracketFeetFront.render(f5);
		bracketFeetBack.render(f5);
		bracketFeetRight.render(f5);
		bracketFeetLeft.render(f5);
		bracketFeetSupportRight.render(f5);
		bracketFeetSupportLeft.render(f5);
		
		GL11.glPopMatrix();
		
		float armClampProg = MathHelper.clamp_float(prog / 0.4F, 0.0F, 1.0F);
		
		bracketShoulderSupportL.rotateAngleX = bracketShoulderSupportL2.rotateAngleX = (float)Math.toRadians(90F) - (float)Math.toRadians(90F) * armClampProg;
		bracketShoulderSupportR.rotateAngleX = bracketShoulderSupportR2.rotateAngleX = (float)Math.toRadians(90F) - (float)Math.toRadians(90F) * armClampProg;
		
		bracketShoulderSupportL.rotateAngleY = bracketShoulderSupportL2.rotateAngleY = (float)Math.toRadians(-2F) * armClampProg;
		bracketShoulderSupportR.rotateAngleY = bracketShoulderSupportR2.rotateAngleY = (float)Math.toRadians(2F) * armClampProg;
		
		bracketShoulderSupportL.renderWithRotation(f5);
		bracketShoulderSupportL2.renderWithRotation(f5);
		bracketShoulderSupportR.renderWithRotation(f5);
		bracketShoulderSupportR2.renderWithRotation(f5);
		
		float headClampProg = MathHelper.clamp_float((prog - 0.5F) / 0.3F, 0.0F, 1.0F);
		
		headJackLeft.rotateAngleX = headJackTop.rotateAngleX = headJackBottom.rotateAngleX = headJackRight.rotateAngleX = headJackCore.rotateAngleX = (float)Math.toRadians(90F) - (float)Math.toRadians(90F) * headClampProg;

		float headJackProg = MathHelper.clamp_float((prog - 0.8F) / 0.2F, 0.0F, 1.0F);
		
		headJackCore.rotationPointZ = 14F + -5F * headJackProg;
		
		headJackLeft.render(f5);
		headJackTop.render(f5);
		headJackBottom.render(f5);
		headJackRight.render(f5);
		headJackCore.render(f5);
		
	    indicatorBottom.render(f5);
	    indicatorTop.render(f5);
	    indicatorRight.render(f5);
	    indicatorLeft.render(f5);
		
		if(powered)
		{
			if(isHomeUnit)
			{
				GL11.glColor4f(0.0F, 1.0F, 1.0F, 1.0F);
			}
			else
			{
				GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);				
			}
		}
		else
		{
			GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
		}

		indicator2.render(f5);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public void render(float prog, float f5)
	{
		base.render(f5);
		ceiling.render(f5);
		baseSkirtBack.render(f5);
		ceilingSkirtBack.render(f5);
		baseSkirtLeft.render(f5);
		baseSkirtRight.render(f5);
		ceilingSkirtRight.render(f5);
		ceilingSkirtLeft.render(f5);
		backWall.render(f5);
		backSupport1.render(f5);
		backSupport2.render(f5);
		backSupport3.render(f5);
		backSupport4.render(f5);
		backPillarRight.render(f5);
		backPillarLeft.render(f5);
		sideWallLeft.render(f5);
		sideWallRight.render(f5);
		baseSkirtFront.render(f5);
		ceilingSkirtFront.render(f5);
		frontPillarRight.render(f5);
		frontPillarLeft.render(f5);
		
		float retractProg = MathHelper.clamp_float(prog / 0.4F, 0.0F, 1.0F);
		float swingProg = MathHelper.clamp_float((prog - 0.3F) / 0.4F, 0.0F, 1.0F);

		doorLeft.rotateAngleY = (float)Math.toRadians(-90F) - (float)Math.toRadians(-90F) * swingProg;
		doorRight.rotateAngleY = (float)Math.toRadians(90F) - (float)Math.toRadians(90F) * swingProg;
		doorLeft.rotationPointZ = doorRight.rotationPointZ = -1 + (-14 * retractProg);
		
//		rotY = R = 90 L = -90
//		rotZ = -1, -15;
		doorRight.render(f5);
		doorLeft.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
