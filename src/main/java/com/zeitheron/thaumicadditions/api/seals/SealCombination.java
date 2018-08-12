package com.zeitheron.thaumicadditions.api.seals;

import com.zeitheron.thaumicadditions.tiles.TileSeal;

import thaumcraft.api.aspects.Aspect;

public class SealCombination
{
	public final Aspect[] slots = new Aspect[3];
	
	public SealCombination(Aspect i, Aspect j, Aspect k)
	{
		slots[0] = i;
		slots[1] = j;
		slots[2] = k;
	}
	
	/**
	 * Used to invoke a static method via reflection. <br>
	 * Format: com.package.RenderClass.methodName
	 */
	public String getRender(TileSeal seal, int index)
	{
		return "com.pengu.lostthaumaturgy.client.render.seal.LTSealRenders.renderNone";
	}
	
	public boolean isValid(TileSeal seal)
	{
		for(int i = 0; i < 3; ++i)
			if(slots[i] != seal.getSymbol(i))
				return false;
		return true;
	}
	
	private String nameAspect(int i)
	{
		return slots[i] != null ? slots[i].getName() : "None";
	}
	
	@Override
	public String toString()
	{
		return nameAspect(0) + ", " + nameAspect(1) + ", " + nameAspect(2);
	}
}