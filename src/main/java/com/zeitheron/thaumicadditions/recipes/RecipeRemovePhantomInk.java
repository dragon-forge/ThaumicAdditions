package com.zeitheron.thaumicadditions.recipes;

import java.lang.reflect.Field;

import com.zeitheron.thaumicadditions.init.ItemsTAR;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import thaumcraft.api.items.ItemsTC;

public class RecipeRemovePhantomInk extends Impl<IRecipe> implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		ItemStack phial = ItemStack.EMPTY;
		ItemStack armor = ItemStack.EMPTY;
		
		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof ItemArmor && stack.hasTagCompound() && stack.getTagCompound().getBoolean("TAR_PHANTOM"))
				{
					if(!armor.isEmpty())
						return false;
					armor = stack;
				} else if(stack.getItem() == ItemsTC.phial && !stack.hasTagCompound())
				{
					if(!phial.isEmpty())
						return false;
					phial = stack;
				} else
					return false;
			}
		}
		
		return !phial.isEmpty() && !armor.isEmpty();
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack phial = ItemStack.EMPTY;
		ItemStack armor = ItemStack.EMPTY;
		
		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof ItemArmor && stack.hasTagCompound() && stack.getTagCompound().getBoolean("TAR_PHANTOM"))
				{
					if(!armor.isEmpty())
						return ItemStack.EMPTY;
					armor = stack;
				} else if(stack.getItem() == ItemsTC.phial && !stack.hasTagCompound())
				{
					if(!phial.isEmpty())
						return ItemStack.EMPTY;
					phial = stack;
				} else
					return ItemStack.EMPTY;
			}
		}
		
		if(!phial.isEmpty() && !armor.isEmpty())
		{
			ItemStack cr = armor.copy();
			if(cr.hasTagCompound())
			{
				cr.getTagCompound().removeTag("TAR_PHANTOM");
				if(cr.getTagCompound().isEmpty())
					cr.setTagCompound(null);
			}
			return cr;
		}
		
		return ItemStack.EMPTY;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		Container inventory = null;
		try
		{
			Field f = InventoryCrafting.class.getDeclaredFields()[3];
			f.setAccessible(true);
			inventory = (Container) f.get(inv);
		} catch(Throwable err)
		{
		}
		if(inventory != null)
		{
			InventoryPlayer ip = null;
			for(Slot slot : inventory.inventorySlots)
				if(slot.inventory instanceof InventoryPlayer)
				{
					ip = ((InventoryPlayer) slot.inventory);
					break;
				}
			if(ip != null)
			{
				ip.addItemStackToInventory(new ItemStack(ItemsTAR.PHANTOM_INK_PHIAL));
			}
		}
		return IRecipe.super.getRemainingItems(inv);
	}
	
	@Override
	public boolean canFit(int width, int height)
	{
		return width * height > 1;
	}
	
	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isDynamic()
	{
		return true;
	}
}