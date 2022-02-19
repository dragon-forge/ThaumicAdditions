package org.zeith.thaumicadditions.blocks.sink;

import com.zeitheron.hammercore.internal.blocks.base.BlockDeviceHC;
import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.api.items.IAspectChargableItem;
import org.zeith.thaumicadditions.init.BlocksTAR;
import org.zeith.thaumicadditions.tiles.TileEssentiaSink;

public class BlockEssentiaSink
		extends BlockDeviceHC<TileEssentiaSink>
		implements IBlockHorizontal
{
	public BlockEssentiaSink()
	{
		super(Material.ROCK, TileEssentiaSink.class, "essentia_sink");
		setSoundType(SoundType.WOOD);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			TileEssentiaSink sink = Cast.cast(worldIn.getTileEntity(pos), TileEssentiaSink.class);
			if(sink != null)
			{
				ItemStack held = playerIn.getHeldItem(hand);
				ItemStack inv = sink.inventory.getStackInSlot(0);

				if(inv.isEmpty() && !held.isEmpty())
				{
					if(held.getItem() instanceof IAspectChargableItem)
					{
						sink.inventory.setInventorySlotContents(0, held.splitStack(1));
						sink.sendChangesToNearby();
					}
				}
				if(!inv.isEmpty())
				{
					if(!playerIn.inventory.addItemStackToInventory(inv))
						WorldUtil.spawnItemStack(worldIn, pos, inv);
					sink.sendChangesToNearby();
				}
			}
		}
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		if(worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up()))
			return super.canPlaceBlockAt(worldIn, pos);
		if(worldIn.getBlockState(pos.down()).getBlock().isReplaceable(worldIn, pos.down()))
			return super.canPlaceBlockAt(worldIn, pos);
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up()))
		{
			worldIn.setBlockState(pos.up(), BlocksTAR.ESSENTIA_SINK_AUX.getDefaultState());
		} else if(worldIn.getBlockState(pos.down()).getBlock().isReplaceable(worldIn, pos.down()))
		{
			worldIn.setBlockState(pos, BlocksTAR.ESSENTIA_SINK_AUX.getDefaultState());
			worldIn.setBlockState(pos.down(), state);
		} else
			worldIn.destroyBlock(pos, true);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos frompos)
	{
		if(worldIn.getBlockState(pos.up()).getBlock() != BlocksTAR.ESSENTIA_SINK_AUX)
		{
			TileEssentiaSink sink = Cast.cast(worldIn.getTileEntity(pos), TileEssentiaSink.class);
			sink.inventory.drop(worldIn, pos);
			sink.inventory.clear();
			worldIn.destroyBlock(pos, true);
		}
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