package org.zeith.thaumicadditions.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import org.zeith.thaumicadditions.init.BlocksTAR;

public class Foods
{
	public static boolean isSpecialFood(Item item)
	{
		// Fruit of Grisaia
		if(item.getClass().getName().equals("vazkii.botania.common.item.relic.ItemInfiniteFruit"))
			return true;

		// Arcane Cake
		return item.getRegistryName().equals(BlocksTAR.CAKE.getRegistryName());
	}

	public static boolean isFood(Item item)
	{
		return item instanceof ItemFood || isSpecialFood(item);
	}
}