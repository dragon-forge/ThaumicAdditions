package com.zeitheron.thaumicadditions.blocks;

import java.util.List;
import java.util.Random;

import com.zeitheron.hammercore.internal.blocks.base.BlockDeviceHC;
import com.zeitheron.hammercore.internal.blocks.base.IBlockOrientable;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.tiles.TileFluxConcentrator;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

public class BlockFluxConcentrator extends BlockDeviceHC<TileFluxConcentrator> implements IBlockOrientable
{
	public BlockFluxConcentrator()
	{
		super(Material.IRON, TileFluxConcentrator.class, "flux_concentrator");
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return WorldUtil.getFacing(state).getOpposite() == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return WorldUtil.getFacing(world.getBlockState(pos)).getOpposite() == side;
	}
}