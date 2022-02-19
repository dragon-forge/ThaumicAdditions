package org.zeith.thaumicadditions.items;

import com.zeitheron.hammercore.net.HCNet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.zeith.thaumicadditions.entity.EntityBlueWolf;

public class ItemBlueBone
		extends Item
{
	public ItemBlueBone()
	{
		setTranslationKey("blue_bone");
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
	{
		if(target instanceof EntityWolf && !target.isDead)
		{
			EntityBlueWolf.trasfurmate((EntityWolf) target);
			stack.shrink(1);
			HCNet.swingArm(playerIn, hand);
			return true;
		}
		return false;
	}
}