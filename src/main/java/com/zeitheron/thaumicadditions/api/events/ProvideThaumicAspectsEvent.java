package com.zeitheron.thaumicadditions.api.events;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import thaumcraft.api.aspects.Aspect;

public class ProvideThaumicAspectsEvent extends Event
{
	public final List<Aspect> aspects = new ArrayList<Aspect>();
	
	public ProvideThaumicAspectsEvent()
	{
		aspects.addAll(Aspect.aspects.values());
	}
	
	public void add(Aspect aspect)
	{
		if(!aspects.contains(aspect))
			this.aspects.add(aspect);
	}
	
	public List<Aspect> getAspects()
	{
		return aspects;
	}
}