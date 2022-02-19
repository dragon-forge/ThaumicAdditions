package org.zeith.thaumicadditions.api.animator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAnimatableItem
{
	BaseItemAnimator getAnimator(ItemStack stack);

	@SideOnly(Side.CLIENT)
	default float overrideSwing(float amount, ItemStack stack, EntityPlayer player, float partialTime)
	{
		return amount;
	}
}
