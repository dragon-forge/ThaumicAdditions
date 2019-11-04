package com.zeitheron.thaumicadditions.blocks;

import com.zeitheron.hammercore.api.ITileBlock;
import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.tile.TileSyncable;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.thaumicadditions.tiles.TileShadowEnchanter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockShadowEnchanter extends Block implements ITileBlock<TileShadowEnchanter>
{
	public BlockShadowEnchanter()
	{
		super(Material.ROCK);
		setTranslationKey("shadow_enchanter");
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		GuiManager.openGui(playerIn, Cast.cast(worldIn.getTileEntity(pos), TileSyncable.class));
		return true;
	}
	
	@Override
	public Class<TileShadowEnchanter> getTileClass()
	{
		return TileShadowEnchanter.class;
	}
	
	static final AxisAlignedBB SHADOW_AABB = new AxisAlignedBB(0, 0, 0, 1, 9.5 / 16, 1);
	
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SHADOW_AABB;
	}
}