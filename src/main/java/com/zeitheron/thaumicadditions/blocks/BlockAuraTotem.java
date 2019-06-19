package com.zeitheron.thaumicadditions.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAuraTotem extends Block
{
	public static final PropertyEnum<EnumTotemVariant> VARIANT = PropertyEnum.create("variant", EnumTotemVariant.class);
	
	public BlockAuraTotem()
	{
		super(Material.WOOD);
		setTranslationKey("aura_totem");
		setHardness(2F);
		setHarvestLevel("axe", 0);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if(getMetaFromState(state) % 2 == 1)
			return new AxisAlignedBB(2 / 16D, 0, 2 / 16D, 14 / 16D, 1, 14 / 16D);
		return super.getBoundingBox(state, source, pos);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		IBlockState low = world.getBlockState(pos.down());
		IBlockState high = world.getBlockState(pos.up());
		// if(low.getBlock() == this && high.getBlock() == this)
		// return getStateFromMeta(0);
		if(low.getBlock() == this)
			return getStateFromMeta((getMetaFromState(low) + 1) % 5);
		if(high.getBlock() == this)
			return getStateFromMeta(getMetaFromState(high) == 0 ? 4 : (getMetaFromState(high) - 1) % 5);
		return getStateFromMeta(0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		IBlockState low = world.getBlockState(pos.down());
		IBlockState high = world.getBlockState(pos.up());
		
		// if(low.getBlock() == this && high.getBlock() == this &&
		// getMetaFromState(high) - getMetaFromState(low) != 2)
		// {
		// world.destroyBlock(pos, true);
		// return;
		// }
		
		if(low.getBlock() == this)
		{
			int j = 0, k = 4;
			IBlockState tmps;
			for(int i = 0; i < 4; ++i)
				if((tmps = world.getBlockState(pos.down(1 + i))).getBlock() == this && getMetaFromState(tmps) < k)
				{
					k = getMetaFromState(tmps);
					j++;
				} else
					break;
			if(getMetaFromState(state) != j)
			{
				world.setBlockState(pos, getStateFromMeta(j), 3);
			}
		}
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, VARIANT);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(VARIANT).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(VARIANT, EnumTotemVariant.values()[meta % 5]);
	}
	
	public enum EnumTotemVariant implements IStringSerializable
	{
		BASE, //
		BASE_TO_CENTER, //
		CENTER, //
		CENTER_TO_TOP, //
		TOP;
		
		@Override
		public String getName()
		{
			return "v0" + ordinal();
		}
	}
}