package me.ichun.mods.sync.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class ModelPixel extends ModelBase
{
	public Random rand = new Random();

	public ModelRenderer[] modelHead;
	public ModelRenderer[] modelBody;
	public ModelRenderer[] modelLimb;
	public ModelRenderer[] modelLimbMirrored;

	public ModelPixel()
	{
		modelHead = new ModelRenderer[8 * 8 * 8];
		modelBody = new ModelRenderer[12 * 4 * 8];
		modelLimb = new ModelRenderer[12 * 4 * 4];
		modelLimbMirrored = new ModelRenderer[12 * 4 * 4];


		int sizeX = 8;
		int sizeY = 8;
		int sizeZ = 8;

		//
		//		else if(limb == 4)
		//        {
		//        	sizeX = 8;
		//        }
		//        else if(limb == 5)
		//        {
		//        	sizeX = 8;
		//        	sizeY = 8;
		//        	sizeZ = 8;
		//        }

		textureWidth = 48;
		textureHeight = 24;

		for(int i = 0; i < sizeX; i++)
		{
			for(int j = 0; j < sizeY; j++)
			{
				for(int k = 0; k < sizeZ; k++)
				{
					if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
					{
						int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
						int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

						ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
						modelHead[i + j * sizeX + k * sizeY * sizeX] = pixel;
						pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
					}
				}
			}
		}

		sizeX = 8;
		sizeY = 12;
		sizeZ = 4;

		for(int i = 0; i < sizeX; i++)
		{
			for(int j = 0; j < sizeY; j++)
			{
				for(int k = 0; k < sizeZ; k++)
				{
					if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
					{
						int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
						int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

						ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
						modelBody[i + j * sizeX + k * sizeY * sizeX] = pixel;
						pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
					}
				}
			}
		}

		sizeX = 4;
		sizeY = 12;
		sizeZ = 4;

		for(int i = 0; i < sizeX; i++)
		{
			for(int j = 0; j < sizeY; j++)
			{
				for(int k = 0; k < sizeZ; k++)
				{
					if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
					{
						int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
						int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

						ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
						modelLimb[i + j * sizeX + k * sizeY * sizeX] = pixel;
						pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
					}
				}
			}
		}

		for(int i = 0; i < sizeX; i++)
		{
			for(int j = 0; j < sizeY; j++)
			{
				for(int k = 0; k < sizeZ; k++)
				{
					if(i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1)
					{
						int x = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeX + sizeZ + i : k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k;
						int y = j == sizeY - 1 && !(i == 0 || i == sizeX - 1 || k == 0 || k == sizeZ - 1) ? sizeZ - k : sizeZ + sizeY - j - 2;

						ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
						pixel.mirror = true;
						modelLimbMirrored[i + j * sizeX + k * sizeY * sizeX] = pixel;
						pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
					}
				}
			}
		}

	}

	public void renderPlayer(int prog, float renderYaw, float rotationYaw, float rotationPitch, float limbSwing, float limbSwingAmount, float f5, float renderTick, int[] skins)
	{
		//		prog = 0;
		GlStateManager.pushMatrix();

		if(skins == null)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.color(0.7F, 0.7F, 0.7F, 0.8F);
		}
		else
		{
			GlStateManager.color(0.95F, 0.95F, 0.95F, 0.9F);
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.translate(0.0F, -0.7F, 0.0F);

		GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);

		for(int i = 0; i < 6; i++)
		//        for(int i = 5; i < 6; i++)
		{
			if(skins != null)
			{
				if(i < 2)
				{
					GlStateManager.bindTexture(skins[0]);
				}
				else if(i < 4)
				{
					GlStateManager.bindTexture(skins[1]);
				}
				else
				{
					GlStateManager.bindTexture(skins[i - 2]);
				}
			}
			renderLimb(prog, i, renderYaw, rotationYaw, rotationPitch, limbSwing, limbSwingAmount, f5, renderTick);
		}

		GlStateManager.disableBlend();

		if(skins == null)
		{
			GlStateManager.enableTexture2D();
		}

		GlStateManager.popMatrix();
	}

	public void renderLimb(int progInt, int limb, float renderYaw, float rotationYaw, float rotationPitch, float limbSwing, float limbSwingAmount, float f5, float renderTick)
	{
		GlStateManager.pushMatrix();
		//0 = left leg
		//1 = right leg
		//2 = left arm
		//3 = right arm
		//4 = body
		//5 = head

		float prog = MathHelper.clamp((progInt + renderTick) / 100F, 0.0F, 1.0F);

		float shatterProg = MathHelper.clamp((float)(Math.pow(1.0F - MathHelper.clamp(((prog - 0.05F) / 0.125F), 0.0F, 1.0F), 3D)), 0.0F, 1.0F);
		float properShatterProg = 1.0F - MathHelper.clamp((float)(Math.pow(1.0F - MathHelper.clamp(((prog - 0.025F) / 0.2F), 0.0F, 1.0F), 2D)), 0.0F, 1.0F);

		rotationPitch *= shatterProg;

		if(limb != 5)
		{
			GlStateManager.rotate(renderYaw, 0.0F, 1.0F, 0.0F);
		}
		else
		{
			GlStateManager.rotate(rotationYaw, 0.0F, 1.0F, 0.0F);
		}


		float f8 = limbSwing - limbSwingAmount;

		int sizeX = 4;
		int sizeY = 12;
		int sizeZ = 4;

		float offsetX = 0.0F;
		float offsetY = 0.0F;
		float offsetZ = 0.0F;

		float spread = 0.7F;

		if(limb == 4)
		{
			sizeX = 8;
		}
		else if(limb == 5)
		{
			sizeX = 8;
			sizeY = 8;
			sizeZ = 8;
		}

		for(int i = -(sizeX / 2); i < (sizeX / 2); i++)
		{
			for(int j = -sizeY; j < 0; j++)
			{
				for(int k = -(sizeZ / 2); k < (sizeZ / 2); k++)
				{
					if(i == -(sizeX / 2) || i == (sizeX / 2) - 1 || j == -sizeY || j == -1 || k == -(sizeZ / 2) || k == (sizeZ / 2) - 1)
					//                	if(i == -(sizeX / 2) && j == -sizeY && k == -(sizeZ / 2))
					{
						GlStateManager.pushMatrix();

						ModelRenderer[] list;
						if(limb < 4)
						{
							list = (limb % 2 == 0) ? modelLimbMirrored : modelLimb;
						}
						else if(limb == 4)
						{
							list = modelBody;
						}
						else
						{
							list = modelHead;
						}

						ModelRenderer pixel = list[((sizeX / 2) + i) + ((sizeY + j) * sizeX) + ((((sizeZ / 2) + k) * (sizeX * sizeY)))];
						//i + j * 8 + k * 64

						pixel.rotationPointX = 0;
						pixel.rotationPointY = 0;
						pixel.rotationPointZ = 0;

						if(limb == 0)
						{
							GlStateManager.translate(2F/16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), 0.0F, 0.0F);
						}
						else if(limb == 1)
						{
							GlStateManager.translate(-2F/16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), 0.0F, 0.0F);
						}
						else if(limb == 2)
						{
							GlStateManager.translate(4F/16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), -10F/16F * shatterProg, 0.0F);
							pixel.rotationPointX = 2F;
							pixel.rotationPointY = -2F * shatterProg;
						}
						else if(limb == 3)
						{
							GlStateManager.translate(-4F/16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), -10F/16F * shatterProg, 0.0F);
							pixel.rotationPointX = -2F;
							pixel.rotationPointY = -2F * shatterProg;
						}
						else if(limb == 4)
						{
							sizeX = 8;
							GlStateManager.translate(0.0F, -12F/16F * shatterProg, 0.0F);
						}
						else if(limb == 5)
						{
							sizeX = 8;
							sizeY = 8;
							sizeZ = 8;
							GlStateManager.translate(0.0F, -12F/16F * shatterProg, 0.0F);

							GlStateManager.rotate(rotationPitch, 1.0F, 0.0F, 0.0F);
							pixel.rotationPointY = -8F * shatterProg;
						}

						if(limb < 4)
						{
							if(limb == 0 || limb == 3)
							{
								GL11.glRotated((Math.toDegrees(Math.cos(f8 * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount)) * shatterProg, 1.0F, 0.0F, 0.0F);
							}
							if(limb == 1 || limb == 2)
							{
								GL11.glRotated((Math.toDegrees(Math.cos(f8 * 0.6662F) * 1.4F * limbSwingAmount)) * shatterProg, 1.0F, 0.0F, 0.0F);
							}
						}

						GlStateManager.translate(0.0F, 11F/16F * (1.0F - shatterProg) + rand.nextFloat() * 0.01F, 0.0F);

						GlStateManager.translate(-(offsetX + (i + 0.5F)) / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg), (-(offsetY + (j + 0.5F)) / 16F) * shatterProg, -(offsetZ + (k + 0.5F)) / 16F + ((rand.nextFloat() - 0.5F) * spread * properShatterProg));
						if(prog < rand.nextFloat())
						{
							pixel.render(f5);
						}
						GlStateManager.popMatrix();
					}
				}
			}
		}
		GlStateManager.popMatrix();
	}
}
