package com.zeitheron.thaumicadditions.compat.thaumicwands;

import com.zeitheron.hammercore.mod.ModuleLoader;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.compat.ITARC;
import com.zeitheron.thaumicadditions.init.ItemsTAR;

import de.zpenguin.thaumicwands.api.ThaumicWandsAPI;
import net.minecraft.item.ItemStack;

@ModuleLoader(requiredModid="thaumicwands")
public class TARCThaumicWands implements ITARC {

	@Override
	public void init()
	{
		ThaumicWandsAPI.registerWandCap("mithrillium", new TARWandCap("mithrillium", 0.75F, AspectUtil.primals(1), new ItemStack(ItemsTAR.MITHRILLIUM_CAP), 30));
		ThaumicWandsAPI.registerWandCap("adaminite", new TARWandCap("adaminite", 0.7F, AspectUtil.primals(2), new ItemStack(ItemsTAR.ADAMINITE_CAP), 35));
		ThaumicWandsAPI.registerWandCap("mithminite", new TARWandCap("mithminite", 0.6F, AspectUtil.primals(3), new ItemStack(ItemsTAR.MITHMINITE_CAP), 40));

		ThaumicWandsAPI.registerWandRod("adaminitewood", new TARWandRod("adaminitewood", 3200, new ItemStack(ItemsTAR.ADAMINITEWOOD_ROD), 40));

	}

	@Override
	public void initClient()
	{
	}


}
