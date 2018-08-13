package com.zeitheron.thaumicadditions.api.utils;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aura.AuraHelper;

public interface IOcasionalPositionedEvent
{
	IOcasionalPositionedEvent DAWN = (worldIn, pos, state, random) ->
	{
		float flux = AuraHelper.getFlux(worldIn, pos);
		float drainFlux = Math.min(flux, random.nextFloat() * .1F);
		if(drainFlux > 0F)
		{
			AuraHelper.drainFlux(worldIn, pos, drainFlux, false);
			if(random.nextInt(64) == 0)
				AuraHelper.polluteAura(worldIn, pos, 2 * random.nextFloat(), true);
		}
	};
	
	IOcasionalPositionedEvent TWILIGHT = (worldIn, pos, state, random) ->
	{
		float targetFlux = AuraHelper.getAuraBase(worldIn, pos) * 1.25F;
		float charge = Math.max(0, Math.min(.1F, targetFlux - AuraHelper.getFlux(worldIn, pos)));
		if(charge > 0F)
		{
			AuraHelper.polluteAura(worldIn, pos, charge, true);
			if(random.nextInt(8) == 0)
				AuraHelper.polluteAura(worldIn, pos, 4 * random.nextFloat(), true);
		}
	};
	
	void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random);
}