package me.ichun.mods.sync.common.shell;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterShell extends Teleporter 
{

	public int dimension;
	public BlockPos pos;
	public float playerYaw;
	public float playerPitch;
	
	public TeleporterShell(WorldServer par1WorldServer) 
	{
		super(par1WorldServer);
	}

	public TeleporterShell(WorldServer server, int dimensionId, BlockPos pos, float yaw, float pitch) {
		this(server);
		this.dimension = dimensionId;
		this.pos = pos;
		this.playerYaw = yaw;
		this.playerPitch = pitch;
	}

    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
        par1Entity.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, playerYaw, playerPitch);
        par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
    }
    
    @Override
    public void removeStalePortalLocations(long par1)
    {
    }
	
}
