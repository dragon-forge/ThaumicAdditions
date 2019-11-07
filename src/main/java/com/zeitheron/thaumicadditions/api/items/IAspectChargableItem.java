package com.zeitheron.thaumicadditions.api.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface IAspectChargableItem
{
	/**
	 * Returns all aspects found in given stack.
	 */
	AspectList getHeldAspects(ItemStack stack);
	
	/**
	 * Returns if this item can accept given type of essentia
	 */
	boolean canAcceptAspect(ItemStack stack, Aspect aspect);
	
	/**
	 * Returns the maximal amount of given type essentia that can be stored in
	 * specified stack.
	 */
	int getMaxAspectCount(ItemStack stack, Aspect aspect);
	
	/**
	 * Adds the given aspect with given amount to this stack. Returns the amount
	 * that WAS ACCEPTED.
	 */
	int acceptAspect(ItemStack stack, Aspect aspect, int amount);
	
	/**
	 * Extracts the given aspect with amount from this stack. Returns the amount
	 * that WAS EXTRACTED.
	 */
	int extractAspect(ItemStack stack, Aspect aspect, int amount);
	
	Aspect getCurrentRequest(ItemStack stack);
	
	class AspectChargableItemHelper
	{
		public static AspectList getAspects(ItemStack stack)
		{
			AspectList al = new AspectList();
			if(stack.hasTagCompound())
				al.readFromNBT(stack.getTagCompound());
			return al;
		}
		
		public static void setAspects(ItemStack stack, AspectList list)
		{
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			list.writeToNBT(stack.getTagCompound());
		}
	}
}