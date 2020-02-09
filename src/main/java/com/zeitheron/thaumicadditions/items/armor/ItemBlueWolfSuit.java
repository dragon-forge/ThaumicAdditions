package com.zeitheron.thaumicadditions.items.armor;

import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.client.models.ModelBlueWolfHead;
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

import static net.minecraft.inventory.EntityEquipmentSlot.*;

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
		super(BLUE_WOLF, slot == LEGS ? 1 : 0, slot);
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
		switch(slot)
		{
			case CHEST:
				return InfoTAR.MOD_ID + ":textures/armor/blue_wolf_body.png";
			case LEGS:
				return InfoTAR.MOD_ID + ":textures/armor/blue_wolf_feet.png";
			case FEET:
				return InfoTAR.MOD_ID + ":textures/armor/blue_wolf_feetpaws.png";
			case HEAD:
			default:
				return InfoTAR.MOD_ID + ":textures/armor/blue_wolf_head.png";
		}
	}

	@SideOnly(Side.CLIENT)
	public ModelBiped model;

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
//		model = null;

		if(model == null)
		{
			if(armorType == HEAD) model = new ModelBlueWolfHead(1F, true, false, false, false);
			else if(armorType == CHEST) model = new ModelBlueWolfHead(1F, false, true, false, false);
			else if(armorType == LEGS) model = new ModelBlueWolfHead(1F, false, false, true, false);
			else model = new ModelBlueWolfHead(1F, false, false, false, true);

			this.model.bipedHead.showModel = (armorType == HEAD);
			this.model.bipedHeadwear.showModel = (armorType == HEAD);
			this.model.bipedBody.showModel = ((armorType == CHEST) || (armorType == LEGS));
			this.model.bipedLeftArm.showModel = (armorType == CHEST);
			this.model.bipedRightArm.showModel = (armorType == CHEST);
			this.model.bipedLeftLeg.showModel = (armorType == LEGS || armorType == FEET);
			this.model.bipedRightLeg.showModel = (armorType == LEGS || armorType == FEET);
		}

		if(entityLiving == null)
		{
			return model;
		}

		this.model.isSneak = entityLiving.isSneaking();
		this.model.isRiding = entityLiving.isRiding();
		this.model.isChild = entityLiving.isChild();

		this.model.bipedHeadwear.showModel = (armorType == HEAD);
		this.model.bipedBody.showModel = ((armorType == CHEST) || (armorType == LEGS));
		this.model.bipedLeftArm.showModel = (armorType == CHEST);
		this.model.bipedRightArm.showModel = (armorType == CHEST);
		this.model.bipedLeftLeg.showModel = (armorType == LEGS || armorType == FEET);
		this.model.bipedRightLeg.showModel = (armorType == LEGS || armorType == FEET);


		return model;
	}
}
