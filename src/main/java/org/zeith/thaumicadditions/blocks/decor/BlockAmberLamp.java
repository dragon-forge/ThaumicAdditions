package org.zeith.thaumicadditions.blocks.decor;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingBlock;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

import java.util.Random;

public class BlockAmberLamp
		extends Block
		implements IGlowingBlock
{
	public static final NoiseGeneratorSimplex BASE_SIMPLEX = new NoiseGeneratorSimplex();
	final Random rand = new Random();

	public BlockAmberLamp()
	{
		super(Material.IRON);
		setTranslationKey("amber_lamp");
		setSoundType(SoundType.METAL);
		setHardness(3F);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return 12;
	}

	@Override
	public ColoredLight produceColoredLight(World world, BlockPos pos, IBlockState state, float partialTicks)
	{
		double time = (System.currentTimeMillis() % 10_000_000L) / 50D;
		rand.setSeed(pos.toLong());
		time += rand.nextInt(10_000_000) / 50D;

		float prog = (float) (Math.sin(time / 6D) + 1F) / 2F;
		int color = ColorHelper.interpolate(0xFF7547, 0xFFF8AB, prog);
		float rad = (float) (BASE_SIMPLEX.getValue(time / 128D, 0) * 0.5F + 1.5F) * 6F;
		return ColoredLight.builder().pos(pos).color(color, false).radius(rad).build();
	}
}
