package org.zeith.thaumicadditions.blocks.decor;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTaintkin
		extends BlockPumpkin
{
	public BlockTaintkin()
	{
		setTranslationKey("taintkin");
		setHardness(1F);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
	}
}