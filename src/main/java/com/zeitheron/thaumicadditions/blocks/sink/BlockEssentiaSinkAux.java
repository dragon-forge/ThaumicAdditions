package com.zeitheron.thaumicadditions.blocks.sink;

import com.zeitheron.hammercore.api.INoItemBlock;
import com.zeitheron.hammercore.api.blocks.INoBlockstate;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.thaumicadditions.init.BlocksTAR;
import com.zeitheron.thaumicadditions.tiles.TileEssentiaSink;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEssentiaSinkAux extends Block implements INoBlockstate, INoItemBlock
{
	public BlockEssentiaSinkAux()
	{
		super(Material.ROCK);
		setHardness(2F);
		setSoundType(SoundType.WOOD);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(worldIn.getBlockState(pos.down()).getBlock() != BlocksTAR.ESSENTIA_SINK)
			worldIn.destroyBlock(pos, false);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return (state = worldIn.getBlockState(pos.down())).getBlock().onBlockActivated(worldIn, pos.down(), state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}
}