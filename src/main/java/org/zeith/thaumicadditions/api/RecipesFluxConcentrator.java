package org.zeith.thaumicadditions.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.utils.BlockSideHelper;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public class RecipesFluxConcentrator
{
	private static final Map<IBlockState, FluxConcentratorOutput> HANDLERS = new HashMap<>();

	private static final Set<IBlockState> PASS_IN_BLOCKS = new HashSet<>();

	public static boolean isPassable(World world, BlockPos pos, EnumFacing face)
	{
		return PASS_IN_BLOCKS.contains(world.getBlockState(pos)) || BlockSideHelper.isPassable(world, pos, face);
	}

	public static void handle(IBlockState src, FluxConcentratorOutput target)
	{
		Objects.requireNonNull(target, "target must not be null.");
		HANDLERS.put(src, target);
		PASS_IN_BLOCKS.add(src);
	}

	public static boolean handle(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		return HANDLERS.containsKey(state) && HANDLERS.get(state).test(world, pos);
	}

	public static FluxConcentratorOutput output(IBlockState state)
	{
		return new FluxConcentratorOutput(state);
	}

	public static Stream<Entry<IBlockState, FluxConcentratorOutput>> listRecipes()
	{
		return HANDLERS.entrySet().stream();
	}

	public static ItemStack stackFromState(IBlockState state)
	{
		Item drop = state.getBlock().getItemDropped(state, ThreadLocalRandom.current(), 0);
		int meta = state.getBlock().damageDropped(state);
		if(drop != null && drop != Items.AIR) return new ItemStack(drop, 1, meta);
		return ItemStack.EMPTY;
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
}