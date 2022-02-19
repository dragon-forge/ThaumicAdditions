package org.zeith.thaumicadditions.init;

import com.zeitheron.hammercore.utils.OnetimeCaller;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.zeith.thaumicadditions.fluids.FluidCrystalWater;

public class FluidsTAR
{
	public static final FluidCrystalWater CRYSTAL_WATER = new FluidCrystalWater();

	public static final OnetimeCaller init = new OnetimeCaller(FluidsTAR::$init);

	private static void $init()
	{
		addFluid(CRYSTAL_WATER);
	}

	private static void addFluid(Fluid fl)
	{
		FluidRegistry.registerFluid(fl);
		FluidRegistry.addBucketForFluid(fl);
	}
}