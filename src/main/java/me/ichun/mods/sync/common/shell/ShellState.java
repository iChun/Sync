package me.ichun.mods.sync.common.shell;

import me.ichun.mods.sync.common.Sync;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ShellState 
	implements Comparable
{

	public final BlockPos pos;
	
	public final int dimension;
	
	public EntityPlayer playerState;

	public String name;
	public String dimName;
	
	public float buildProgress;
	
	public float powerReceived;
	
	public boolean isConstructor;
	
	public boolean isHome;
	
	public ShellState(int x, int y, int z, int dim)
	{
		this(new BlockPos(x, y, z), dim);
	}

	public ShellState(BlockPos pos, int dim) {
		this.pos = pos;
		this.dimension = dim;
		this.name = "";
		this.dimName = "";
		this.isConstructor = false;
	}
	
	public void tick()
	{
		if(buildProgress < Sync.config.shellConstructionPowerRequirement)
		{
			buildProgress += powerReceived;
			if(buildProgress > Sync.config.shellConstructionPowerRequirement)
			{
				buildProgress = Sync.config.shellConstructionPowerRequirement;
			}
		}
	}
	
	public boolean matches(ShellState state)
	{
		return state.pos.equals(this.pos) && state.dimension == dimension;
	}

	@Override
	public int compareTo(Object arg0) 
	{
		if(arg0 instanceof ShellState)
		{
			ShellState ss = (ShellState)arg0;
			int dimCompare = dimName.compareTo(ss.dimName);
			if(dimCompare >= 0)
			{
				if(isConstructor && !ss.isConstructor)
				{
					return -1;
				}
			}
			return dimCompare;
		}
		return 0;
	}
}
