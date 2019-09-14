package com.zeitheron.thaumicadditions.items.seed;

import com.zeitheron.thaumicadditions.blocks.plants.BlockVoidCrop;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;

public class ItemVoidSeed extends ItemSeeds
{
	public ItemVoidSeed()
	{
		super(BlockVoidCrop.CROP, Blocks.FARMLAND);
		setTranslationKey("void_seed");
	}
}