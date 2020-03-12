package com.zeitheron.thaumicadditions.compat.jei;

import com.google.common.base.Function;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.api.RecipesFluxConcentrator;
import com.zeitheron.thaumicadditions.compat.jei.fluxconc.FCRecipe;
import com.zeitheron.thaumicadditions.compat.jei.fluxconc.FluxConcentratorCategory;
import com.zeitheron.thaumicadditions.init.BlocksTAR;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JEIPlugin
public class ThaumAddJEI
		implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		Map<IBlockState, List<IBlockState>> outToInMap = new HashMap<>();
		Function<IBlockState, List<IBlockState>> getter = out -> outToInMap.computeIfAbsent(out, o -> new ArrayList<>());
		RecipesFluxConcentrator.listRecipes().forEach(e -> getter.apply(e.getValue().getOutState()).add(e.getKey()));
		List<FCRecipe> fcRecipes = new ArrayList<>();
		outToInMap.entrySet().forEach(e ->
		{
			ItemStack out = RecipesFluxConcentrator.stackFromState(e.getKey());
			if(!out.isEmpty())
			{
				List<ItemStack> ins = e.getValue().stream().map(RecipesFluxConcentrator::stackFromState).filter(s -> !s.isEmpty()).collect(Collectors.toList());
				if(!ins.isEmpty())
				{
					Ingredient ing = Ingredient.fromStacks(ins.toArray(new ItemStack[ins.size()]));
					fcRecipes.add(new FCRecipe(ing, out));
				}
			}
		});
		registry.addRecipes(fcRecipes, InfoTAR.MOD_ID + ":flux_concentrator");

		registry.addRecipeCatalyst(new ItemStack(BlocksTAR.FLUX_CONCENTRATOR), InfoTAR.MOD_ID + ":flux_concentrator");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new FluxConcentratorCategory(registry.getJeiHelpers()));
	}
}