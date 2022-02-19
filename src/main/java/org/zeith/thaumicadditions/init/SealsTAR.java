package org.zeith.thaumicadditions.init;

import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.api.seals.SealCombination;
import org.zeith.thaumicadditions.api.seals.SealInstance;
import org.zeith.thaumicadditions.api.seals.SealManager;
import org.zeith.thaumicadditions.seals.earth.SealTillSoil;
import org.zeith.thaumicadditions.seals.life.SealFertilizeCrops;
import org.zeith.thaumicadditions.seals.magic.SealPortal;
import org.zeith.thaumicadditions.seals.magic.SealPortal.PortalSealCombination;
import org.zeith.thaumicadditions.seals.tool.SealHarvestCrops1;
import org.zeith.thaumicadditions.seals.tool.SealHarvestCrops2;
import org.zeith.thaumicadditions.seals.water.SealWaterHydrate;
import org.zeith.thaumicadditions.seals.woid.SealPickup;
import org.zeith.thaumicadditions.tiles.TileSeal;
import thaumcraft.api.aspects.Aspect;

import java.util.function.Function;

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
		register(new PortalSealCombination(), SealPortal::new);

		TAReconstructed.LOG.info("-Registered " + seals + " seals.");
	}

	private static void register(SealCombination combo, Function<TileSeal, SealInstance> obtainer)
	{
		TAReconstructed.LOG.info(" -" + combo + "...");
		SealManager.registerCombination(combo, obtainer);
		++seals;
	}
}