package com.zeitheron.thaumicadditions.items.armor;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.raytracer.RayTracer;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.init.PotionsTAR;
import com.zeitheron.thaumicadditions.items.baubles.ItemFragnantPendant;
import com.zeitheron.thaumicadditions.net.PacketRemovePotionEffect;
import com.zeitheron.thaumicadditions.utils.ThaumicHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.lib.potions.PotionWarpWard;

public class ItemAdaminiteDress extends ItemArmor implements IVisDiscountGear, IGoggles
{
	public static final ArmorMaterial ADAMINITE = EnumHelper.addArmorMaterial("TAR_ADAMINITE", "tar_adaminite", 0, new int[] { 4, 6, 8, 5 }, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8F);
	
	public ItemAdaminiteDress(EntityEquipmentSlot slot)
	{
		super(ADAMINITE, slot == EntityEquipmentSlot.LEGS ? 1 : 0, slot);
	}
	
	@Override
	public ItemAdaminiteDress setTranslationKey(String key)
	{
		return (ItemAdaminiteDress) super.setTranslationKey(key);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		return slot == EntityEquipmentSlot.LEGS ? InfoTAR.MOD_ID + ":textures/armor/adaminite_1.png" : InfoTAR.MOD_ID + ":textures/armor/adaminite_0.png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
		return null;
	}
	
	private final int[] discounts = { 0, 0, 4, 8, 10, 8 };
	
	@Override
	public int getVisDiscount(ItemStack stack, EntityPlayer player)
	{
		return discounts[armorType.ordinal()];
	}
	
	@Override
	public boolean showIngamePopups(ItemStack stack, EntityLivingBase owner)
	{
		return armorType == EntityEquipmentSlot.HEAD;
	}
}