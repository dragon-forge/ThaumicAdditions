package com.zeitheron.thaumicadditions.recipes.ingr;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

public class NBTRespectfulIngredient extends Ingredient
{
	private final ItemStack[] matchingNBTStacks;
	
	public NBTRespectfulIngredient(ItemStack... stacks)
	{
		super(stacks);
		this.matchingNBTStacks = stacks;
	}
	
	@Override
	public boolean apply(@Nullable ItemStack stack)
	{
		if(stack == null || stack.isEmpty())
			return false;
		else
		{
			for(ItemStack itemstack : this.matchingNBTStacks)
			{
				if(itemstack.getItem() == stack.getItem())
				{
					int i = itemstack.getMetadata();
					
					if(i == OreDictionary.WILDCARD_VALUE || i == stack.getMetadata())
					{
						return Objects.equals(itemstack.getTagCompound(), stack.getTagCompound());
					}
				}
			}
			
			return false;
		}
	}
}