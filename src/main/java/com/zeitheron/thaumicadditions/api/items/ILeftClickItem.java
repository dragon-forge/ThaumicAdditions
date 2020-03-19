package com.zeitheron.thaumicadditions.api.items;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface ILeftClickItem
{
	void onLeftClick(ItemStack stack, EntityPlayerMP player);
}