package com.zeitheron.thaumicadditions.items.armor;

import com.zeitheron.thaumicadditions.InfoTAR;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IVisDiscountGear;

public class ItemMithminiteDress extends ItemArmor implements IVisDiscountGear
{
	public static final ArmorMaterial MITHMINITE = EnumHelper.addArmorMaterial("TAR_MITHMINITE", "tar_mithminite", 88, new int[] { 6, 10, 15, 8 }, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8F);
	
	public ItemMithminiteDress(EntityEquipmentSlot slot)
	{
		super(MITHMINITE, slot == EntityEquipmentSlot.LEGS ? 1 : 0, slot);
	}
	
	@Override
	public ItemMithminiteDress setTranslationKey(String key)
	{
		return (ItemMithminiteDress) super.setTranslationKey(key);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		return slot == EntityEquipmentSlot.LEGS ? InfoTAR.MOD_ID + ":textures/armor/mithminite_1.png" : InfoTAR.MOD_ID + ":textures/armor/mithminite_0.png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
		return null;
	}

	@Override
	public int getVisDiscount(ItemStack stack, EntityPlayer player)
	{
		return 0;
	}
}