package com.zeitheron.thaumicadditions.items;

import com.zeitheron.thaumicadditions.init.ItemsTAR;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants.NBT;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class ItemVisPod extends Item implements IEssentiaContainerItem
{
	public static final int ASPECT_COUNT = 5;
	
	public ItemVisPod()
	{
		setTranslationKey("vis_pod");
	}
	
	public static int getColor(ItemStack stack, int layer)
	{
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Aspect", NBT.TAG_STRING))
		{
			Aspect a = Aspect.getAspect(stack.getTagCompound().getString("Aspect"));
			if(a != null)
				return a.getColor();
		}
		return 0xFFFFFF;
	}
	
	public static ItemStack create(Aspect aspect, int count)
	{
		ItemStack stack = new ItemStack(ItemsTAR.VIS_POD, count);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setString("Aspect", aspect.getTag());
		return stack;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(isInCreativeTab(tab))
			for(Aspect a : Aspect.aspects.values())
				items.add(create(a, 1));
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String an = "Unknown";
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Aspect", NBT.TAG_STRING))
		{
			Aspect a = Aspect.getAspect(stack.getTagCompound().getString("Aspect"));
			if(a != null)
				an = a.getName();
		}
		return I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name", an).trim();
	}
	
	@Override
	public AspectList getAspects(ItemStack stack)
	{
		AspectList al = new AspectList();
		if(stack.hasTagCompound())
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("Aspect", NBT.TAG_STRING))
				al.add(Aspect.getAspect(nbt.getString("Aspect")), ASPECT_COUNT);
		}
		return al;
	}
	
	@Override
	public boolean ignoreContainedAspects()
	{
		return false;
	}
	
	@Override
	public void setAspects(ItemStack stack, AspectList list)
	{
		if(list.getAspects().length > 0)
		{
			Aspect a = list.getAspects()[0];
			int ac = list.getAmount(a) / ASPECT_COUNT;
			stack.setCount(ac);
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setString("Aspect", a.getTag());
		}
	}
}