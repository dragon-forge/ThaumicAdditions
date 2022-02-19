package org.zeith.thaumicadditions.blocks.decor;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingBlock;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

import java.util.Random;

public class BlockTaintkinLit
		extends BlockTaintkin
		implements IGlowingBlock
{
	public static final NoiseGeneratorSimplex BASE_SIMPLEX = new NoiseGeneratorSimplex();
	final Random rand = new Random();

	public BlockTaintkinLit()
	{
		setTranslationKey("taintkin_lit");
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return 9;
	}

	@Override
	public ColoredLight produceColoredLight(World world, BlockPos pos, IBlockState state, float partialTicks)
	{
		double time = (System.currentTimeMillis() % 10_000_000L) / 50D;
		rand.setSeed(pos.toLong());
		time += rand.nextInt(10_000_000) / 50D;

		float prog = (float) (Math.sin(time / 6D) + 1F) / 2F;
		int color = ColorHelper.interpolate(0xCD91FF, 0xBB63FF, prog);
		float rad = (float) (BASE_SIMPLEX.getValue(time / 128D, 0) * 0.5F + 1.5F) * 7.5F;
		return ColoredLight.builder().pos(pos).radius(rad).color(color, false).build();
	}
}