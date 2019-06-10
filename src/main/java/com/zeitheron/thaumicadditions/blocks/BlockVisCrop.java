package com.zeitheron.thaumicadditions.blocks;

import java.util.Random;

import com.zeitheron.hammercore.api.INoItemBlock;
import com.zeitheron.hammercore.api.ITileBlock;
import com.zeitheron.hammercore.tile.TileSyncable;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.items.ItemVisPod;
import com.zeitheron.thaumicadditions.items.ItemVisSeeds;
import com.zeitheron.thaumicadditions.tiles.TileVisCrop;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.tools.ItemElementalHoe;

public class BlockVisCrop extends BlockCrops implements INoItemBlock, ITileBlock<TileVisCrop>
{
	public static final PropertyInteger AGE_5 = PropertyInteger.create("age", 0, 4);
	
	public BlockVisCrop()
	{
		setTranslationKey("vis_crop");
	}
	
	ThreadLocal<Aspect> aspectContainer = ThreadLocal.withInitial(() -> null);
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileVisCrop tvc = WorldUtil.cast(worldIn.getTileEntity(pos), TileVisCrop.class);
		aspectContainer.set(tvc != null ? tvc.getAspect() : null);
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		Aspect asp = aspectContainer.get();
		aspectContainer.set(null);
		
		if(asp == null)
			return;
		
		Random rand = world instanceof World ? ((World) world).rand : RANDOM;
		
		int count = rand.nextInt(3);
		for(int i = 0; i < count; i++)
			if(isMaxAge(state))
				drops.add(ItemVisPod.create(asp, 1));
			else
				drops.add(ItemVisSeeds.create(asp, 1));
			
		int age = getAge(state);
		if(age >= getMaxAge())
		{
			boolean atl = false;
			for(int i = 0; i < 2; ++i)
				if(rand.nextInt(25) == 0 || !atl)
					atl = drops.add(ItemVisSeeds.create(asp, 1));
		}
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack held = playerIn.getHeldItem(hand);
		
		if(!held.isEmpty() && held.getItem() == ItemsTC.elementalHoe)
		{
			TileEntity tvc = worldIn.getTileEntity(pos);
			
			if(!worldIn.isRemote)
			{
				int i = this.getAge(state) + 1 + worldIn.rand.nextInt(2);
		        int j = this.getMaxAge();

		        if (i > j)
		        {
		            i = j;
		        }

		        worldIn.setBlockState(pos, this.withAge(i), 2);
			}
			
			worldIn.removeTileEntity(pos);
			tvc.validate();
			worldIn.setTileEntity(pos, tvc);
			((TileSyncable) tvc).sendChangesToNearby();
		}
		
		return false;
	}
	
	public int getColor(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
	{
		TileVisCrop tcb = WorldUtil.cast(worldIn.getTileEntity(pos), TileVisCrop.class);
		if(tcb != null && tcb.getAspect() != null)
			return tcb.getAspect().getColor();
		return 0xFFFFFF;
	}
	
	@Override
	public void grow(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tvc = worldIn.getTileEntity(pos);
		super.grow(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
		tvc.validate();
		worldIn.setTileEntity(pos, tvc);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		TileEntity tvc = worldIn.getTileEntity(pos);
		super.updateTick(worldIn, pos, state, rand);
		worldIn.removeTileEntity(pos);
		tvc.validate();
		worldIn.setTileEntity(pos, tvc);
	}
	
	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
	{
		return false;
	}
	
	@Override
	protected PropertyInteger getAgeProperty()
	{
		return AGE_5;
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, getAgeProperty());
	}
	
	@Override
	public int getMaxAge()
	{
		return 4;
	}
	
	@Override
	protected int getBonemealAgeIncrease(World worldIn)
	{
		return 1;
	}
	
	@Override
	public Class<TileVisCrop> getTileClass()
	{
		return TileVisCrop.class;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		TileVisCrop tvc = WorldUtil.cast(world.getTileEntity(pos), TileVisCrop.class);
		Aspect asp = tvc != null ? tvc.getAspect() : null;
		return asp != null ? ItemVisSeeds.create(asp, 1) : ItemStack.EMPTY;
	}
}