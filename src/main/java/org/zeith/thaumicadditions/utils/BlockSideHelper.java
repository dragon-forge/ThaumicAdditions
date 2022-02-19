package org.zeith.thaumicadditions.utils;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSideHelper
{
	public static boolean isPassable(World world, BlockPos pos, EnumFacing face)
	{
		if(world.isAirBlock(pos))
			return true;
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof BlockDoor || state.getBlock() instanceof BlockTrapDoor)
			return false;
		if(state.isFullBlock() && state.isNormalCube())
			return false;
		BlockFaceShape shape = state.getBlockFaceShape(world, pos, face);
		if(shape == BlockFaceShape.SOLID)
			return false;
		return !state.isSideSolid(world, pos, face);
	}
}