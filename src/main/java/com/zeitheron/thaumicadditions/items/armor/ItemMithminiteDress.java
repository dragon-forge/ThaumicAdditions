package com.zeitheron.thaumicadditions.items.armor;

import java.util.UUID;

import com.google.common.collect.Multimap;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.raytracer.RayTracer;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.events.LivingEventsTAR;
import com.zeitheron.thaumicadditions.init.PotionsTAR;
import com.zeitheron.thaumicadditions.items.baubles.ItemFragnantPendant;
import com.zeitheron.thaumicadditions.net.PacketRemovePotionEffect;
import com.zeitheron.thaumicadditions.utils.ThaumicHelper;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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

public class ItemMithminiteDress extends ItemArmor implements IVisDiscountGear, IGoggles
{
	public static final ArmorMaterial MITHMINITE = EnumHelper.addArmorMaterial("TAR_MITHMINITE", "tar_mithminite", 0, new int[] { 6, 10, 15, 8 }, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8F);
	
	public ItemMithminiteDress(EntityEquipmentSlot slot)
	{
		super(MITHMINITE, slot == EntityEquipmentSlot.LEGS ? 1 : 0, slot);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer mp, ItemStack itemStack)
	{
		switch(armorType)
		{
		case HEAD:
		{
			mp.addPotionEffect(new PotionEffect(PotionsTAR.SANITY_CHECKER, 2, 0, true, false));
			if(mp.isInWater() && mp.ticksExisted % 10 == 0)
				mp.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 31, 0, true, false));
			if(!mp.isPotionActive(PotionWarpWard.instance) && mp.ticksExisted % 40 == 0 && ItemFragnantPendant.ODOUR_POWDER.canConsume(mp.inventory))
			{
				ItemFragnantPendant.ODOUR_POWDER.consume(mp.inventory);
				ThaumicHelper.applyWarpWard(mp);
			}
			if(mp.ticksExisted % 10 == 0)
			{
				boolean nightVision = world.getLightBrightness(mp.getPosition()) * 16F < 7F;
				if(!nightVision)
				{
					RayTraceResult rtr = RayTracer.retrace(mp, 12);
					if(rtr != null && rtr.typeOfHit == Type.BLOCK)
						nightVision = world.getLightBrightness(rtr.getBlockPos().offset(rtr.sideHit)) * 16F < 7F;
				}
				if(nightVision)
					mp.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 299, 0, true, false));
				else if(mp.isPotionActive(MobEffects.NIGHT_VISION))
				{
					mp.removeActivePotionEffect(MobEffects.NIGHT_VISION);
					if(mp instanceof EntityPlayerMP && !world.isRemote)
						HCNet.INSTANCE.sendTo(new PacketRemovePotionEffect(MobEffects.NIGHT_VISION), (EntityPlayerMP) mp);
				}
			}
		}
		break;
	
		case CHEST:
		{
			mp.getEntityData().setBoolean("TAR_Flight", true);
			if(mp.isBurning())
			{
				mp.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 119, 0, true, false));
				mp.extinguish();
			}
		}
		break;
	
		case LEGS:
		{
			mp.getEntityData().setInteger("TAR_LockFOV", 5);
		}
		break;
	
		case FEET:
		{
			if(!mp.capabilities.isFlying && mp.moveForward > 0.0f)
			{
				if(mp.world.isRemote && !mp.isSneaking())
				{
					if(!LivingEventsTAR.prevStep.containsKey(mp.getEntityId()))
						LivingEventsTAR.prevStep.put(mp.getEntityId(), Float.valueOf(mp.stepHeight));
					mp.stepHeight = 1.0f;
				}
				if(mp.onGround)
				{
					float bonus = 0.06f;
					if(mp.isInWater())
						bonus /= 2.0f;
					mp.moveRelative(0.0f, 0.0f, bonus, 1.0f);
				} else
				{
					if(mp.isInWater())
						mp.moveRelative(0.0f, 0.0f, 0.03f, 1.0f);
					mp.jumpMovementFactor = 0.05f;
				}
			}
		}
		break;
	
		default:
		break;
		}
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
		if(slot == armorType)
		{
			if(slot == EntityEquipmentSlot.HEAD)
			{
				map.put("generic.luck", new AttributeModifier(UUID.fromString("de9fc7ce-b49f-21d8-a3db-8ecb26505405"), "TAR_MHEAD_LUCK", 1, 1));
			} else if(slot == EntityEquipmentSlot.CHEST)
			{
				map.put("generic.maxHealth", new AttributeModifier(UUID.fromString("6d9fc7ce-b49f-41d8-93db-8ecb26505405"), "TAR_MCHEST_HP", 20, 0));
			} else if(slot == EntityEquipmentSlot.LEGS)
			{
				map.put("generic.movementSpeed", new AttributeModifier(UUID.fromString("6e9fc7ce-b49b-21d8-a3da-8ecb26505423"), "TAR_MHEAD_LUCK", 1, 1));
				map.put("generic.flyingSpeed", new AttributeModifier(UUID.fromString("6e9fc7ce-b49b-46f6-a3da-8ecb26505423"), "TAR_MHEAD_LUCK", 1, 1));
			} else if(slot == EntityEquipmentSlot.FEET)
			{
				
			}
		}
		return map;
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
	
	private final int[] discounts = { 0, 0, 10, 15, 20, 15 };
	
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