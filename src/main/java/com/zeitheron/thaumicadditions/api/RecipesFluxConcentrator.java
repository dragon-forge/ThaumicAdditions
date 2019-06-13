package com.zeitheron.thaumicadditions.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RecipesFluxConcentrator
{
	private static final Map<IBlockState, BiPredicate<World, BlockPos>> HANDLERS = new HashMap<>();
	
	public static void handle(IBlockState src, BiPredicate<World, BlockPos> target)
	{
		if(target == null)
			throw new NullPointerException("target must not be null.");
		HANDLERS.put(src, target);
	}
	
	public static boolean handle(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		if(HANDLERS.containsKey(state))
			return HANDLERS.get(state).test(world, pos);
		return false;
	}
	
	public static BiPredicate<World, BlockPos> output(IBlockState state)
	{
		return (world, pos) -> world.setBlockState(pos, state, 2);
	}
}