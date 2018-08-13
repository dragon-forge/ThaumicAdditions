package com.zeitheron.thaumicadditions.blocks;

import java.util.Random;

import com.zeitheron.thaumicadditions.api.utils.IOcasionalPositionedEvent;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTotem extends Block
{
	public final IOcasionalPositionedEvent tick;
	
	public BlockTotem(String sub, IOcasionalPositionedEvent tick)
	{
		super(Material.WOOD);
		this.tick = tick;
		setSoundType(SoundType.WOOD);
		setTranslationKey(sub + "_totem");
		setTickRandomly(true);
		setHardness(1.5F);
		setResistance(3F);
		setHarvestLevel("axe", -1);
	}
	
	@Override
	public int tickRate(World worldIn)
	{
		return 200;
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random)
	{
		tick.updateTick(worldIn, pos, state, random);
	}
}