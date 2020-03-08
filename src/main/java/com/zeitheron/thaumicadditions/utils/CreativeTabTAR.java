package com.zeitheron.thaumicadditions.utils;

import com.zeitheron.thaumicadditions.init.ItemsTAR;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Comparator;

public class CreativeTabTAR
		extends CreativeTabs
{
	public CreativeTabTAR(String label)
	{
		super(label);
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> items)
	{
		NonNullList<NonNullList<ItemStack>> blocksSemiSort = NonNullList.create();
		NonNullList<NonNullList<ItemStack>> itemsSemiSort = NonNullList.create();

		NonNullList<ItemStack> temp = NonNullList.create();
		for(Item item : Item.REGISTRY)
		{
			item.getSubItems(this, temp);
			if(!temp.isEmpty())
			{
				Block bk = Block.getBlockFromItem(item);
				if(bk != Blocks.AIR) blocksSemiSort.add(temp);
				else itemsSemiSort.add(temp);
				temp = NonNullList.create();
			}
		}

		itemsSemiSort.sort(Comparator.comparingInt(NonNullList::size));
		blocksSemiSort.sort(Comparator.<NonNullList<ItemStack>> comparingInt(NonNullList::size));

		blocksSemiSort.stream().flatMap(NonNullList::stream).forEach(items::add);
		itemsSemiSort.stream().flatMap(NonNullList::stream).forEach(items::add);
	}

	@Override
	public ItemStack createIcon()
	{
		return new ItemStack(ItemsTAR.SEAL_GLOBE);
	}
}