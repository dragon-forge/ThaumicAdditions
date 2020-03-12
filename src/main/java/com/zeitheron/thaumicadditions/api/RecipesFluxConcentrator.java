package com.zeitheron.thaumicadditions.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class RecipesFluxConcentrator
{
	private static final Map<IBlockState, FluxConcentratorOutput> HANDLERS = new HashMap<>();

	public static void handle(IBlockState src, FluxConcentratorOutput target)
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

	public static FluxConcentratorOutput output(IBlockState state)
	{
		return new FluxConcentratorOutput(state);
	}

	public static Stream<Entry<IBlockState, FluxConcentratorOutput>> listRecipes()
	{
		return HANDLERS.entrySet().stream();
	}

	public static class FluxConcentratorOutput
			implements BiPredicate<World, BlockPos>
	{
		protected final IBlockState state;

		public FluxConcentratorOutput(IBlockState state)
		{
			this.state = state;
		}

		public IBlockState getOutState()
		{
			return state;
		}

		public ItemStack getOutStack()
		{
			return stackFromState(getOutState());
		}

		@Override
		public boolean test(World world, BlockPos pos)
		{
			return world.setBlockState(pos, getOutState(), 2);
		}
	}

	public static ItemStack stackFromState(IBlockState state)
	{
		Item drop = state.getBlock().getItemDropped(state, ThreadLocalRandom.current(), 0);
		int meta = state.getBlock().damageDropped(state);
		if(drop != null && drop != Items.AIR) return new ItemStack(drop, 1, meta);
		return ItemStack.EMPTY;
	}
}