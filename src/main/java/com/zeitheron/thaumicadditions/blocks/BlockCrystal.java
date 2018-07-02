package com.zeitheron.thaumicadditions.blocks;

import com.zeitheron.hammercore.internal.blocks.IItemBlock;
import com.zeitheron.hammercore.internal.blocks.base.BlockDeviceHC;
import com.zeitheron.hammercore.internal.blocks.base.IBlockOrientable;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.tiles.TileCrystalBlock;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.IInfusionStabiliser;

public class BlockCrystal extends BlockDeviceHC<TileCrystalBlock> implements IBlockOrientable, IInfusionStabiliser, IItemBlock
{
	public final ItemCrystalBlock itemBlock = new ItemCrystalBlock();
	
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
	
	public int getColor(ItemStack stack, int tintIndex)
	{
		return AspectUtil.getAspectFromCrystalBlockStack(stack).getColor();
	}
	
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
		super.getSubBlocks(itemIn, items);
		for(Aspect a : Aspect.aspects.values())
			items.add(AspectUtil.crystalBlock(a));
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(IBlockOrientable.FACING, facing);
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		TileCrystalBlock tcb = WorldUtil.cast(worldIn.getTileEntity(pos), TileCrystalBlock.class);
		if(tcb == null)
			tcb = new TileCrystalBlock();
		tcb.setAspect(AspectUtil.getAspectFromCrystalBlockStack(stack));
		worldIn.setTileEntity(pos, tcb);
		
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		TileCrystalBlock tcb = WorldUtil.cast(world.getTileEntity(pos), TileCrystalBlock.class);
		return tcb != null ? AspectUtil.crystalBlock(tcb.getAspect()) : super.getPickBlock(state, target, world, pos, player);
	}
	
	@Override
	public boolean canStabaliseInfusion(World worldIn, BlockPos pos)
	{
		return true;
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return false;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_)
	{
		return BlockFaceShape.UNDEFINED;
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
	public ItemBlock getItemBlock()
	{
		return itemBlock;
	}
	
	public class ItemCrystalBlock extends ItemBlock
	{
		public ItemCrystalBlock()
		{
			super(BlockCrystal.this);
		}
		
		@Override
		public String getItemStackDisplayName(ItemStack stack)
		{
			return super.getItemStackDisplayName(stack).replace("@ASPECT", AspectUtil.getAspectFromCrystalBlockStack(stack).getName());
		}
	}
}