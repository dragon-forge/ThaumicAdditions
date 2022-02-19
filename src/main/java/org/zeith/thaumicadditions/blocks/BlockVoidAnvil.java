package org.zeith.thaumicadditions.blocks;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.init.GuisTAR;

public class BlockVoidAnvil
		extends BlockFalling
{
	protected static final AxisAlignedBB X_AXIS_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.125D, 1.0D, 1.0D, 0.875D);
	protected static final AxisAlignedBB Z_AXIS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.0D, 0.875D, 1.0D, 1.0D);

	public BlockVoidAnvil()
	{
		super(Material.IRON);
		setHardness(2F);
		setHarvestLevel("pickaxe", 2);
		setSoundType(SoundType.ANVIL);
		setTranslationKey("void_anvil");
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return state.getValue(IBlockHorizontal.FACING).getAxis() == Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		GuiManager.openGuiCallback(GuisTAR.VOID_ANVIL, playerIn, worldIn, pos);
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		EnumFacing enumfacing = placer.getHorizontalFacing().rotateY();
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(IBlockHorizontal.FACING, enumfacing);
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
	protected void onStartFalling(EntityFallingBlock fallingEntity)
	{
		fallingEntity.setHurtEntities(true);
	}

	@Override
	public void onEndFalling(World worldIn, BlockPos pos, IBlockState fallingState, IBlockState hitState)
	{
		worldIn.playEvent(1031, pos, 0);
	}

	@Override
	public void onBroken(World worldIn, BlockPos pos)
	{
		worldIn.playEvent(1029, pos, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return true;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.getBlock() != this ? state : state.withProperty(IBlockHorizontal.FACING, rot.rotate(state.getValue(IBlockHorizontal.FACING)));
	}
}