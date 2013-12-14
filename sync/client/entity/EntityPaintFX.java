package sync.client.entity;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityPaintFX extends EntityFX
{
    public EntityPaintFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float r, float g, float b)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.motionX = par8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionY = par10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.motionZ = par12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
        this.particleRed = r * 0.3F + 2.7F;
        this.particleGreen = g * 0.3F + 2.7F;
        this.particleBlue = b * 0.3F + 2.7F;
        this.particleScale = this.rand.nextFloat() * this.rand.nextFloat() * 2.0F + 1.0F;
        this.particleMaxAge = (int)(16.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D)) + 2;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8999999761581421D;
        this.motionY *= 0.8999999761581421D;
        this.motionZ *= 0.8999999761581421D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}
