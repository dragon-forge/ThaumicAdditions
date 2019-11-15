package com.zeitheron.thaumicadditions.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.items.ItemsTC;

public class ItemDisenchantingFabric extends Item
{
	public ItemDisenchantingFabric()
	{
		setTranslationKey("disenchant_fabric");
		setMaxStackSize(1);
		setMaxDamage(32);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		return !repair.isEmpty() && repair.getItem() == ItemsTC.fabric;
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack)
	{
		return !getContainerItem(stack).isEmpty();
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack)
	{
		itemStack = itemStack.copy();
		itemStack.setItemDamage(itemStack.getItemDamage() + 1);
		if(itemStack.getItemDamage() >= getMaxDamage(itemStack))
			return itemStack.EMPTY;
		return itemStack;
	}
}