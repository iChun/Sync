package me.ichun.mods.sync.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTreadmill extends ModelBase
{
	//fields
	public ModelRenderer track;
	public ModelRenderer footLegL;
	public ModelRenderer footLegR;
	public ModelRenderer foorFront;
	public ModelRenderer dashboard;
	public ModelRenderer supportR;
	public ModelRenderer supportL;
	public ModelRenderer skirtL;
	public ModelRenderer skirtR;
	public ModelRenderer sideGuardLTop;
	public ModelRenderer sideGuardLFront;
	public ModelRenderer sideGuardLBack;
	public ModelRenderer sideGuardRTop;
	public ModelRenderer sideGuardRFront;
	public ModelRenderer sideGuardRBack;
	public ModelRenderer sideGuardL;
	public ModelRenderer sideGuardR;

	public ModelTreadmill()
	{
		textureWidth = 256;
		textureHeight = 128;

		track = new ModelRenderer(this, 0, 0);
		track.addBox(-9F, 0F, -16F, 18, 4, 62);
		track.setRotationPoint(0F, 17F, 1F);
		track.setTextureSize(256, 128);
		track.mirror = true;
		setRotation(track, -0.0436332F, 0F, 0F);
		footLegL = new ModelRenderer(this, 0, 66);
		footLegL.addBox(0F, 0F, 0F, 1, 2, 34);
		footLegL.setRotationPoint(12.5F, 22F, -16F);
		footLegL.setTextureSize(256, 128);
		footLegL.mirror = true;
		setRotation(footLegL, 0F, 0F, 0F);
		footLegR = new ModelRenderer(this, 0, 66);
		footLegR.addBox(0F, 0F, 0F, 1, 2, 34);
		footLegR.setRotationPoint(-13.5F, 22F, -16F);
		footLegR.setTextureSize(256, 128);
		footLegR.mirror = true;
		setRotation(footLegR, 0F, 0F, 0F);
		foorFront = new ModelRenderer(this, 0, 0);
		foorFront.addBox(-12.5F, 0F, 0F, 25, 2, 1);
		foorFront.setRotationPoint(0F, 22F, -16F);
		foorFront.setTextureSize(256, 128);
		foorFront.mirror = true;
		setRotation(foorFront, 0F, 0F, 0F);
		dashboard = new ModelRenderer(this, 0, 3);
		dashboard.addBox(-13F, 0F, 0F, 26, 10, 2);
		dashboard.setRotationPoint(0F, 6F, -15F);
		dashboard.setTextureSize(256, 128);
		dashboard.mirror = true;
		setRotation(dashboard, 1.134464F, 0F, 0F);
		supportR = new ModelRenderer(this, 0, 66);
		supportR.addBox(0F, 0F, -1F, 1, 15, 2);
		supportR.setRotationPoint(-13.5F, 7F, -11F);
		supportR.setTextureSize(256, 128);
		supportR.mirror = true;
		setRotation(supportR, 0F, 0F, 0F);
		supportL = new ModelRenderer(this, 0, 66);
		supportL.addBox(0F, 0F, -1F, 1, 15, 2);
		supportL.setRotationPoint(12.5F, 7F, -11F);
		supportL.setTextureSize(256, 128);
		supportL.mirror = true;
		setRotation(supportL, 0F, 0F, 0F);
		skirtL = new ModelRenderer(this, 98, 3);
		skirtL.addBox(-2F, 0F, -16F, 4, 5, 64);
		skirtL.setRotationPoint(11F, 16.5F, 0F);
		skirtL.setTextureSize(256, 128);
		skirtL.mirror = true;
		setRotation(skirtL, -0.0436332F, 0F, 0F);
		skirtR = new ModelRenderer(this, 98, 3);
		skirtR.addBox(-2F, 0F, -16F, 4, 5, 64);
		skirtR.setRotationPoint(-11F, 16.5F, 0F);
		skirtR.setTextureSize(256, 128);
		skirtR.mirror = true;
		setRotation(skirtR, -0.0436332F, 0F, 0F);
		sideGuardLTop = new ModelRenderer(this, 70, 72);
		sideGuardLTop.addBox(0F, 2F, 1F, 1, 1, 32);
		sideGuardLTop.setRotationPoint(12.5F, 5F, 0F);
		sideGuardLTop.setTextureSize(256, 128);
		sideGuardLTop.mirror = true;
		setRotation(sideGuardLTop, -0.0436332F, 0F, 0F);
		sideGuardLFront = new ModelRenderer(this, 146, 0);
		sideGuardLFront.addBox(0F, 3F, 1F, 1, 11, 1);
		sideGuardLFront.setRotationPoint(12.5F, 5F, 0F);
		sideGuardLFront.setTextureSize(256, 128);
		sideGuardLFront.mirror = true;
		setRotation(sideGuardLFront, -0.0436332F, 0F, 0F);
		sideGuardLBack = new ModelRenderer(this, 146, 0);
		sideGuardLBack.addBox(0F, 3F, 32F, 1, 11, 1);
		sideGuardLBack.setRotationPoint(12.5F, 5F, 0F);
		sideGuardLBack.setTextureSize(256, 128);
		sideGuardLBack.mirror = true;
		setRotation(sideGuardLBack, -0.0436332F, 0F, 0F);
		sideGuardRTop = new ModelRenderer(this, 70, 72);
		sideGuardRTop.addBox(0F, 2F, 1F, 1, 1, 32);
		sideGuardRTop.setRotationPoint(-13.5F, 5F, 0F);
		sideGuardRTop.setTextureSize(256, 128);
		sideGuardRTop.mirror = true;
		setRotation(sideGuardRTop, -0.0436332F, 0F, 0F);
		sideGuardRFront = new ModelRenderer(this, 146, 0);
		sideGuardRFront.addBox(0F, 3F, 1F, 1, 11, 1);
		sideGuardRFront.setRotationPoint(-13.5F, 5F, 0F);
		sideGuardRFront.setTextureSize(256, 128);
		sideGuardRFront.mirror = true;
		setRotation(sideGuardRFront, -0.0436332F, 0F, 0F);
		sideGuardRBack = new ModelRenderer(this, 146, 0);
		sideGuardRBack.addBox(0F, 3F, 32F, 1, 11, 1);
		sideGuardRBack.setRotationPoint(-13.5F, 5F, 0F);
		sideGuardRBack.setTextureSize(256, 128);
		sideGuardRBack.mirror = true;
		setRotation(sideGuardRBack, -0.0436332F, 0F, 0F);
		sideGuardL = new ModelRenderer(this, 180, 0);
		sideGuardL.addBox(0F, 3F, 2F, 0, 10, 30);
		sideGuardL.setRotationPoint(13.1F, 5F, 0F);
		sideGuardL.setTextureSize(256, 128);
		sideGuardL.mirror = true;
		setRotation(sideGuardL, -0.0436332F, 0F, 0F);
		sideGuardR = new ModelRenderer(this, 180, 0);
		sideGuardR.addBox(0F, 3F, 2F, 0, 10, 30);
		sideGuardR.setRotationPoint(-13.1F, 5F, 0F);
		sideGuardR.setTextureSize(256, 128);
		sideGuardR.mirror = true;
		setRotation(sideGuardR, -0.0436332F, 0F, 0F);
	}

	public void render(float f5)
	{
		track.render(f5);
		footLegL.render(f5);
		footLegR.render(f5);
		foorFront.render(f5);
		dashboard.render(f5);
		supportR.render(f5);
		supportL.render(f5);
		skirtL.render(f5);
		skirtR.render(f5);
		sideGuardLTop.render(f5);
		sideGuardLFront.render(f5);
		sideGuardLBack.render(f5);
		sideGuardRTop.render(f5);
		sideGuardRFront.render(f5);
		sideGuardRBack.render(f5);
		sideGuardL.render(f5);
		sideGuardR.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
