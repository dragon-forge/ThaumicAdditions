package org.zeith.thaumicadditions.blocks;

import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;

public class BlockThaumicLectern
		extends Block
{
	public BlockThaumicLectern()
	{
		super(Material.WOOD);
		setTranslationKey("thaumic_lectern");
		setSoundType(SoundType.WOOD);
		setHardness(1.5F);
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
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemsTC.thaumonomicon.onItemRightClick(worldIn, playerIn, hand);
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, IBlockHorizontal.FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(IBlockHorizontal.FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(IBlockHorizontal.FACING, EnumFacing.HORIZONTALS[meta % 4]);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(IBlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
	}
}