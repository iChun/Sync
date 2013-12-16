package sync.client.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import sync.client.model.ModelPixel;

public class EntityShellDestruction extends EntityLivingBase 
{

	public ResourceLocation txLocation;
	
	public float yaw;
	public float renderYaw;
	public float pitch;
	public float limbSwingg;
	public float limbSwinggAmount;
	
	public int progress;
	
	public ModelPixel model;
	
	public EntityShellDestruction(World par1World) 
	{
		super(par1World);
		progress = 0;
		txLocation = null;
		model = new ModelPixel();
	}

	public EntityShellDestruction(World par1World, float yw, float ry, float p, float ls, float lsa, ResourceLocation tx) 
	{
		super(par1World);
		progress = 0;
		txLocation = tx;
		yaw = yw;
		renderYaw = ry;
		pitch = p;
		limbSwingg = ls;
		limbSwinggAmount = lsa;
		model = new ModelPixel();
	}
	
	@Override
	public void onUpdate()
	{
		progress++;
		if(progress > 110)
		{
			setDead();
			return;
		}
	}
	
	@Override
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }
	
	@Override
    public void setHealth(float par1)
    {
    }
	
	@Override
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
    	return false;
    }
	
	@Override
	protected void entityInit() 
	{
		super.entityInit();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public ItemStack getHeldItem() {
		return null;
	}

	@Override
	public ItemStack getCurrentItemOrArmor(int i) {
		return null;
	}

	@Override
	public void setCurrentItemOrArmor(int i, ItemStack itemstack) {
	}

	@Override
	public ItemStack[] getLastActiveItems() {
		return new ItemStack[0];
	}

}
