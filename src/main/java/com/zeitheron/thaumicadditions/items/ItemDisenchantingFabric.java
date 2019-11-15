package com.zeitheron.thaumicadditions.items;

import com.zeitheron.hammercore.internal.items.ICustomEnchantColorItem;
import com.zeitheron.hammercore.utils.color.Rainbow;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;

public class ItemDisenchantingFabric extends Item implements ICustomEnchantColorItem
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
	
	@Override
    @SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getEnchantEffectColor(ItemStack stack)
	{
		return Rainbow.doIt(0, 5000L);
	}
}