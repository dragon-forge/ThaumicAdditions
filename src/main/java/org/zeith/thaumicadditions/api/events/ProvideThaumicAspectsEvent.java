package org.zeith.thaumicadditions.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

public class ProvideThaumicAspectsEvent
		extends Event
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