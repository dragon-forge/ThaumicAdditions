package com.zeitheron.thaumicadditions.items.weapons;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.util.EnumHelper;

public class ItemAdaminiteSword
		extends ItemSword
{
	public final int attackDamage = 10;

	public static final ToolMaterial ADAMINITE_SWORD_MAT = EnumHelper.addToolMaterial("TAR_ADAMINITE_SWORD", 0, 0, 5F, 12, 60);

	public ItemAdaminiteSword()
	{
		super(ADAMINITE_SWORD_MAT);
		setTranslationKey("adaminite_sword");
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}
}