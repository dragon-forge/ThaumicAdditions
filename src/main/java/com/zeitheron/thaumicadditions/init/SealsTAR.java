package com.zeitheron.thaumicadditions.init;

import java.util.function.Function;

import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.seals.SealCombination;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.api.seals.SealManager;
import com.zeitheron.thaumicadditions.seals.earth.SealTillSoil;
import com.zeitheron.thaumicadditions.seals.life.SealFertilizeCrops;
import com.zeitheron.thaumicadditions.seals.magic.SealPortal;
import com.zeitheron.thaumicadditions.seals.tool.SealHarvestCrops1;
import com.zeitheron.thaumicadditions.seals.tool.SealHarvestCrops2;
import com.zeitheron.thaumicadditions.seals.water.SealWaterHydrate;
import com.zeitheron.thaumicadditions.seals.woid.SealPickup;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import thaumcraft.api.aspects.Aspect;

public class SealsTAR
{
	private static int seals;
	
	public static void init()
	{
		TAReconstructed.LOG.info("Registering seals...");
		
		register(new SealCombination(Aspect.WATER, null, null).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":fertilesoil"), SealWaterHydrate::new);
		register(new SealCombination(Aspect.EARTH, Aspect.EARTH, Aspect.EARTH).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":createsoil"), SealTillSoil::new);
		register(new SealCombination(Aspect.LIFE, Aspect.PLANT, null).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":bonemealcrops"), SealFertilizeCrops::new);
		register(new SealCombination(Aspect.TOOL, Aspect.PLANT, null).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":harvestcrops1"), SealHarvestCrops1::new);
		register(new SealCombination(Aspect.TOOL, Aspect.PLANT, Aspect.ORDER).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":harvestcrops2"), SealHarvestCrops2::new);
		register(new SealCombination(Aspect.VOID, Aspect.VOID, Aspect.VOID).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":pickupitems"), SealPickup::new);
		register(new SealPortal.PortalSealCombination(), SealPortal::new);
		
		TAReconstructed.LOG.info("-Registered " + seals + " seals.");
	}
	
	private static void register(SealCombination combo, Function<TileSeal, SealInstance> obtainer)
	{
		TAReconstructed.LOG.info(" -" + combo + "...");
		SealManager.registerCombination(combo, obtainer);
		++seals;
	}
}