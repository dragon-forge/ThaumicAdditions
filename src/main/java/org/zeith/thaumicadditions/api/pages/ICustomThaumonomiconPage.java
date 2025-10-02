package org.zeith.thaumicadditions.api.pages;

import thaumcraft.client.gui.GuiResearchPage;

public interface ICustomThaumonomiconPage<T>
{
	Class<T> getRecipeBaseType();
	
	Object getRecipeResult(T recipe);
	
	void render(GuiResearchPage gui, int x, int y, int mx, int my, T recipe);
}