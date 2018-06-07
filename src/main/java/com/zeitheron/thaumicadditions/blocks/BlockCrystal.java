package com.zeitheron.thaumicadditions.blocks;

import com.pengu.hammercore.common.blocks.base.BlockDeviceHC;
import com.pengu.hammercore.common.blocks.base.iBlockOrientable;
import com.pengu.hammercore.common.utils.WorldUtil;
import com.zeitheron.thaumicadditions.tiles.TileCrystalBlock;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.IInfusionStabiliser;

public class BlockCrystal extends BlockDeviceHC<TileCrystalBlock> implements iBlockOrientable, IInfusionStabiliser
{
	public BlockCrystal()
	{
		super(Material.ROCK, TileCrystalBlock.class, "crystal_block");
	}
	
	public int getColor(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
	{
		TileCrystalBlock tcb = WorldUtil.cast(worldIn.getTileEntity(pos), TileCrystalBlock.class);
		if(tcb != null)
			return tcb.getAspect().getColor();
		return 0xFFFFFF;
	}
	
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
		super.getSubBlocks(itemIn, items);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		TileCrystalBlock tcb = new TileCrystalBlock();
		tcb.setAspect(Aspect.AURA);
		worldIn.setTileEntity(pos, tcb);
		
		return getDefaultState().withProperty(iBlockOrientable.FACING, facing);
	}
	
	@Override
	public boolean canStabaliseInfusion(World worldIn, BlockPos pos)
	{
		return true;
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
}