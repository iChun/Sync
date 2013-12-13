package sync.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

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

	public ModelShellConstructor()
	{
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
	}

	public void render(float f5)
	{
		sprayGStand.render(f5);
		sprayRStand.render(f5);
		sprayBStand.render(f5);

		sprayerG.render(f5);
		sprayerR.render(f5);
		sprayerB.render(f5);
		
		printerR.render(f5);
		printerL.render(f5);
		
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
		
		backWall.render(f5);
		sideWallLeft.render(f5);
		sideWallRight.render(f5);
		doorRight.render(f5);
		doorLeft.render(f5);
	}
	
	public void renderConstructionProgress(float f5)
	{
		bodyPixel.render(f5);
		bodyLayer.render(f5);
		armLayer.render(f5);
		headLayer.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
