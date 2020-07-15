package com.zeitheron.thaumicadditions.compat;

import com.zeitheron.hammercore.mod.ILoadModule;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITARC
		extends ILoadModule
{
	default void preInit()
	{

	}

	default void init()
	{
	}

	@SideOnly(Side.CLIENT)
	default void initClient()
	{
	}

	default void addResearches()
	{
	}

	default void addArcaneRecipes()
	{
	}
}