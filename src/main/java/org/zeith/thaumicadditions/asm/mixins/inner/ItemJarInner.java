package org.zeith.thaumicadditions.asm.mixins.inner;

import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.asm.minmixin.annotations.Copy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public abstract class ItemJarInner
		extends Item
		implements IEssentiaContainerItem
{
	public static void init() {}
	
	private ItemJarInner()
	{
	}
	
	@Copy
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		AspectList al = this.getAspects(stack);
		
		if(al != null)
		{
			float percent = (float) getDurabilityForDisplay(stack);
			int rgb = AspectUtil.getColor(al, true);
			
			float r = ColorHelper.getRed(rgb);
			float g = ColorHelper.getGreen(rgb);
			float b = ColorHelper.getBlue(rgb);
			
			return ColorHelper.packRGB(r, g, b);
		}
		
		return super.getRGBDurabilityForDisplay(stack);
	}
}