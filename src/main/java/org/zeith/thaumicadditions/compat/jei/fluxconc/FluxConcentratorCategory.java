package org.zeith.thaumicadditions.compat.jei.fluxconc;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.init.BlocksTAR;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FluxConcentratorCategory
		implements IRecipeCategory<FCRecipe>
{
	IDrawable bg;

	public FluxConcentratorCategory(IJeiHelpers helpers)
	{
		bg = helpers.getGuiHelper().createDrawable(new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/jei.png"), 0, 0, 56, 46);
	}

	@Override
	public String getUid()
	{
		return InfoTAR.MOD_ID + ":flux_concentrator";
	}

	@Override
	public String getTitle()
	{
		return I18n.format(BlocksTAR.FLUX_CONCENTRATOR.getTranslationKey() + ".name");
	}

	@Override
	public String getModName()
	{
		return InfoTAR.MOD_NAME;
	}

	@Override
	public IDrawable getBackground()
	{
		return bg;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, FCRecipe recipeWrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup items = recipeLayout.getItemStacks();

		items.init(0, true, 1, 14);
		items.init(1, false, 37, 14);

		items.set(0, Arrays.stream(recipeWrapper.input.getMatchingStacks()).collect(Collectors.toList()));
		items.set(1, recipeWrapper.output);
	}
}