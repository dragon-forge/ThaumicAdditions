package org.zeith.thaumicadditions.items.seed;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import org.zeith.thaumicadditions.blocks.plants.BlockVoidCrop;

public class ItemVoidSeed
		extends ItemSeeds
{
	public ItemVoidSeed()
	{
		super(BlockVoidCrop.CROP, Blocks.FARMLAND);
		setTranslationKey("void_seed");
	}
}