package com.zeitheron.thaumicadditions.items.armor;

import com.zeitheron.thaumicadditions.InfoTAR;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IVisDiscountGear;

import javax.annotation.Nullable;

public class ItemBlueWolfSuit
		extends ItemArmor
		implements IVisDiscountGear
{
	public static final ArmorMaterial BLUE_WOLF = EnumHelper.addArmorMaterial("TAR_BLUE_WOLF", "tar_blue_wolf", 0, new int[]{
			6,
			12,
			16,
			8
	}, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8F);

	public ItemBlueWolfSuit(EntityEquipmentSlot slot)
	{
		super(BLUE_WOLF, slot == EntityEquipmentSlot.LEGS ? 1 : 0, slot);
	}

	private final int[] discounts = new int[]{
			0,
			0,
			10,
			15,
			20,
			15
	};

	@Override
	public int getVisDiscount(ItemStack stack, EntityPlayer player)
	{
		return discounts[armorType.ordinal()];
	}

	@Override
	public ItemBlueWolfSuit setTranslationKey(String key)
	{
		return (ItemBlueWolfSuit) super.setTranslationKey(key);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		return slot == EntityEquipmentSlot.LEGS ? InfoTAR.MOD_ID + ":textures/armor/blue_wolf_1.png" : InfoTAR.MOD_ID + ":textures/armor/blue_wolf_0.png";
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
		return null;
	}
}
