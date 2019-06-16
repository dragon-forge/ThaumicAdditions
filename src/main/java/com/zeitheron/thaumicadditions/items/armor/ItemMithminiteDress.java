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

public class ItemMithminiteDress extends ItemArmor implements IVisDiscountGear, IGoggles
{
	public static final ArmorMaterial MITHMINITE = EnumHelper.addArmorMaterial("TAR_MITHMINITE", "tar_mithminite", 0, new int[] { 6, 10, 15, 8 }, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8F);
	
	public ItemMithminiteDress(EntityEquipmentSlot slot)
	{
		super(MITHMINITE, slot == EntityEquipmentSlot.LEGS ? 1 : 0, slot);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{
		EntityPlayerMP mp = WorldUtil.cast(player, EntityPlayerMP.class);
		if(mp == null || world.isRemote)
			return;
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
				else
					if(mp.isPotionActive(MobEffects.NIGHT_VISION))
					{
						mp.removeActivePotionEffect(MobEffects.NIGHT_VISION);
						HCNet.INSTANCE.sendTo(new PacketRemovePotionEffect(MobEffects.NIGHT_VISION), mp);
					}
			}
		}
		break;
	
		case CHEST:
		{
			
		}
		break;
	
		case LEGS:
		{
			
		}
		break;
	
		case FEET:
		{
			
		}
		break;
	
		default:
		break;
		}
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