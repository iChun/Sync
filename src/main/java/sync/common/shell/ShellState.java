package sync.common.shell;

import net.minecraft.entity.player.EntityPlayer;
import sync.common.Sync;

public class ShellState 
	implements Comparable
{

	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	
	public final int dimension;
	
	public EntityPlayer playerState;

	public String name;
	public String dimName;
	
	public float buildProgress;
	
	public float powerReceived;
	
	public boolean isConstructor;
	
	public boolean isHome;
	
	public ShellState(int i, int j, int k, int dim)
	{
		xCoord = i;
		yCoord = j;
		zCoord = k;
		dimension = dim;
		name = "";
		dimName = "";
		isConstructor = false;
	}
	
	public void tick()
	{
		if(buildProgress < Sync.config.getSessionInt("shellConstructionPowerRequirement"))
		{
			buildProgress += powerReceived;
			if(buildProgress > Sync.config.getSessionInt("shellConstructionPowerRequirement"))
			{
				buildProgress = Sync.config.getSessionInt("shellConstructionPowerRequirement");
			}
		}
	}
	
	public boolean matches(ShellState state)
	{
		return state.xCoord == xCoord && state.yCoord == yCoord && state.zCoord == zCoord && state.dimension == dimension;
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
