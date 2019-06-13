package com.zeitheron.thaumicadditions.items;

import com.zeitheron.hammercore.utils.color.ColorHelper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.items.RechargeHelper;

public class ItemVisScribingTools extends Item implements IScribeTools, IRechargable
{
	public static final int RATIO = 3;
	
	public ItemVisScribingTools()
	{
		setTranslationKey("vis_scribing_tools");
		setMaxStackSize(1);
		setMaxDamage(150);
		setHasSubtypes(false);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(isInCreativeTab(tab))
		{
			ItemStack stack = new ItemStack(this);
			stack.setItemDamage(stack.getMaxDamage());
			items.add(stack);
			
			stack = new ItemStack(this);
			stack.setItemDamage(0);
			items.add(stack);
		}
	}
	
	@Override
	public void setDamage(ItemStack stack, int damage)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("tc.charge", (stack.getMaxDamage() - damage) / RATIO);
		super.setDamage(stack, damage);
	}
	
	@Override
	public int getDamage(ItemStack stack)
	{
		stack.setItemDamage(stack.getMaxDamage() - RechargeHelper.getCharge(stack) * RATIO);
		return super.getDamage(stack);
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return 0xFF00E5;
	}
	
	@Override
	public int getMaxCharge(ItemStack stack, EntityLivingBase ent)
	{
		stack.setItemDamage(stack.getMaxDamage() - RechargeHelper.getCharge(stack) * RATIO);
		return getMaxDamage(stack) / RATIO;
	}
	
	@Override
	public EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase ent)
	{
		return EnumChargeDisplay.NORMAL;
	}
}