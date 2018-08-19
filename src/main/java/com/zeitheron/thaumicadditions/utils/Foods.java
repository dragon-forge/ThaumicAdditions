package com.zeitheron.thaumicadditions.utils;

import com.zeitheron.thaumicadditions.init.BlocksTAR;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

public class Foods
{
	public static boolean isSpecialFood(Item item)
	{
		// Fruit of Grisaia
		if(item.getClass().getName().equals("vazkii.botania.common.item.relic.ItemInfiniteFruit"))
			return true;
		
		// Arcane Cake
		if(item.getRegistryName().equals(BlocksTAR.CAKE.getRegistryName()))
			return true;
		
		return false;
	}
	
	public static boolean isFood(Item item)
	{
		return item instanceof ItemFood || isSpecialFood(item);
	}
}