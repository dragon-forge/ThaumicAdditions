package com.zeitheron.thaumicadditions.api.seals;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISealFertilizable
{
	boolean fertilize(World world, BlockPos pos);
}