package com.zeitheron.thaumicadditions.init;

import java.util.function.Function;

import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.seals.SealCombination;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.api.seals.SealManager;
import com.zeitheron.thaumicadditions.seals.earth.SealTillSoil;
import com.zeitheron.thaumicadditions.seals.magic.SealPortal;
import com.zeitheron.thaumicadditions.seals.water.SealWaterHydrate;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import thaumcraft.api.aspects.Aspect;

public class SealsTAR
{
	private static int seals;
	
	public static void init()
	{
		TAReconstructed.LOG.info("Registering seals...");
		
		register(new SealCombination(Aspect.WATER, null, null).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":fertilesoil"), seal -> new SealWaterHydrate(seal));
		register(new SealCombination(Aspect.EARTH, Aspect.EARTH, Aspect.EARTH).withDescriptionKey("seal." + InfoTAR.MOD_ID + ":createsoil"), seal -> new SealTillSoil(seal));
		register(new SealPortal.PortalSealCombination(), seal -> new SealPortal(seal));
		
		TAReconstructed.LOG.info("-Registered " + seals + " seals.");
	}
	
	private static void register(SealCombination combo, Function<TileSeal, SealInstance> obtainer)
	{
		TAReconstructed.LOG.info(" -" + combo + "...");
		SealManager.registerCombination(combo, obtainer);
		++seals;
	}
}