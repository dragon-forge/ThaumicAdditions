package org.zeith.thaumicadditions.api;

import com.zeitheron.hammercore.utils.color.Rainbow;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

import java.util.function.Function;

public class CustomColoredAspect
		extends Aspect
{
	public static final Function<CustomColoredAspect, Integer> RAINBOW = a -> Rainbow.doIt(a.hashCode() * a.hashCode(), 20000L);

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