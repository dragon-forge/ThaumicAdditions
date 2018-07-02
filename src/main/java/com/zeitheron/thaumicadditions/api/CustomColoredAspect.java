package com.zeitheron.thaumicadditions.api;

import java.util.function.Function;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class CustomColoredAspect extends Aspect
{
	public final Function<CustomColoredAspect, Integer> getColor;
	
	public CustomColoredAspect(String tag, int color, Aspect[] components, ResourceLocation image, int blend, Function<CustomColoredAspect, Integer> getcolor)
	{
		super(tag, color, components, image, blend);
		this.getColor = getcolor;
	}
	
	@Override
	public int getColor()
	{
		return getColor.apply(this);
	}
}