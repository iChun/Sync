package sync.common.shell;

import sync.common.core.SessionState;
import net.minecraft.entity.player.EntityPlayer;

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
		if(buildProgress < SessionState.shellConstructionPowerRequirement)
		{
			buildProgress += powerReceived;
			if(buildProgress > SessionState.shellConstructionPowerRequirement)
			{
				buildProgress = SessionState.shellConstructionPowerRequirement;
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
			return dimName.compareTo(((ShellState)arg0).dimName);
		}
		return 0;
	}
}
