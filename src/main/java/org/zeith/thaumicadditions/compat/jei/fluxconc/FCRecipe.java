package org.zeith.thaumicadditions.compat.jei.fluxconc;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FCRecipe
		implements IRecipeWrapper
{
	public final Ingredient input;
	public final ItemStack output;

	public FCRecipe(Ingredient input, ItemStack output)
	{
		this.input = input;
		this.output = output;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputs(ItemStack.class, Arrays.stream(input.getMatchingStacks()).map(ItemStack::copy).collect(Collectors.toList()));
		ingredients.setOutput(ItemStack.class, output.copy());
	}
}