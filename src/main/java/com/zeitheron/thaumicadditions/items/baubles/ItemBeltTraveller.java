package com.zeitheron.thaumicadditions.items.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.zeitheron.hammercore.utils.IRegisterListener;
import com.zeitheron.thaumicadditions.events.LivingEventsTAR;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;

public class ItemBeltTraveller
		extends Item
		implements IBauble, IRechargable, IRegisterListener
{
	public ItemBeltTraveller()
	{
		setTranslationKey("traveller_belt");
		setMaxStackSize(1);
	}

	@Override
	public void onRegistered()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.BELT;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.RARE;
	}

	@Override
	public void onWornTick(ItemStack itemStack, EntityLivingBase player)
	{
		if(player instanceof EntityPlayer && ((EntityPlayer) player).inventory.armorInventory.get(0).getItem() == ItemsTC.travellerBoots)
			return;

		boolean hasCharge = RechargeHelper.getCharge(itemStack) > 0;
		if(!player.world.isRemote && player.ticksExisted % 20 == 0)
		{
			int e = 0;
			if(itemStack.hasTagCompound()) e = itemStack.getTagCompound().getInteger("energy");
			if(e > 0) --e;
			else if(RechargeHelper.consumeCharge(itemStack, player, 1)) e = 60;
			itemStack.setTagInfo("energy", new NBTTagInt(e));
		}

		boolean isFlying = false;
		if(player instanceof EntityPlayer) isFlying = ((EntityPlayer) player).capabilities.isFlying;

		if(hasCharge && !isFlying && player.moveForward > 0.0F)
		{
			if(player.world.isRemote && !player.isSneaking())
			{
				if(!LivingEventsTAR.prevStep.containsKey(player.getEntityId()))
					LivingEventsTAR.prevStep.put(player.getEntityId(), Float.valueOf(player.stepHeight));
				player.stepHeight = 1.0f;
			}

			if(player.onGround)
			{
				float bonus = 0.05F;
				if(player.isInWater())
				{
					bonus /= 4.0F;
				}

				player.moveRelative(0.0F, 0.0F, bonus, 1.0F);
			} else
			{
				if(player.isInWater())
				{
					player.moveRelative(0.0F, 0.0F, 0.025F, 1.0F);
				}

				player.jumpMovementFactor = 0.05F;
			}
		}
	}

	@Override
	public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase)
	{
		return 240;
	}

	@Override
	public EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase)
	{
		return EnumChargeDisplay.PERIODIC;
	}

	@SubscribeEvent
	public void playerJumps(LivingJumpEvent event)
	{
		if(event.getEntity() instanceof EntityPlayer && ((EntityPlayer) event.getEntity()).inventory.armorInventory.get(0).getItem() == ItemsTC.travellerBoots)
			return;

		IBaublesItemHandler h;
		if(event.getEntity() instanceof EntityPlayer && (h = BaublesApi.getBaublesHandler((EntityPlayer) event.getEntity())) != null)
		{
			ItemStack is = h.getStackInSlot(BaubleType.BELT.getValidSlots()[0]);
			if(RechargeHelper.getCharge(is) > 0 && !is.isEmpty() && is.getItem() instanceof ItemBeltTraveller)
			{
				EntityLivingBase var10000 = event.getEntityLiving();
				var10000.motionY += 0.2750000059604645D;
			}
		}
	}
}
