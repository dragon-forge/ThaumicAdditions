package org.zeith.thaumicadditions.compat.jei;

import com.google.common.base.Function;
import com.zeitheron.hammercore.utils.base.Cast;
import mezz.jei.api.*;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.RecipesFluxConcentrator;
import org.zeith.thaumicadditions.compat.jei.fluxconc.FCRecipe;
import org.zeith.thaumicadditions.compat.jei.fluxconc.FluxConcentratorCategory;
import org.zeith.thaumicadditions.init.BlocksTAR;
import org.zeith.thaumicadditions.init.ItemsTAR;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@JEIPlugin
public class ThaumAddJEI
		implements IModPlugin
{
	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry)
	{
		ISubtypeInterpreter essenceInterp = stack ->
		{
			IEssentiaContainerItem it = Cast.cast(stack.getItem(), IEssentiaContainerItem.class);
			if(it == null) return ISubtypeInterpreter.NONE;
			
			StringBuilder sb = new StringBuilder();
			
			for(Entry<Aspect, Integer> entry : it.getAspects(stack).aspects.entrySet())
			{
				if(sb.length() > 0) sb.append("; ");
				sb.append(entry.getKey().getName()).append(" x").append(entry.getValue());
			}
			
			return sb.toString();
		};
		
		registry.registerSubtypeInterpreter(ItemsTAR.SEAL_SYMBOL, essenceInterp);
		registry.registerSubtypeInterpreter(ItemsTAR.SALT_ESSENCE, essenceInterp);
		registry.registerSubtypeInterpreter(ItemsTAR.VIS_POD, essenceInterp);
		registry.registerSubtypeInterpreter(BlocksTAR.CRYSTAL_BLOCK.itemBlock, essenceInterp);
	}
	
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