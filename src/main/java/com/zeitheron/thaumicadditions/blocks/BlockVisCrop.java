package com.zeitheron.thaumicadditions.blocks;

import java.util.Random;

import com.zeitheron.hammercore.api.INoItemBlock;
import com.zeitheron.hammercore.api.ITileBlock;
import com.zeitheron.hammercore.tile.TileSyncable;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.api.seals.ISealFertilizable;
import com.zeitheron.thaumicadditions.api.seals.SealManager;
import com.zeitheron.thaumicadditions.items.ItemVisPod;
import com.zeitheron.thaumicadditions.items.ItemVisSeeds;
import com.zeitheron.thaumicadditions.tiles.TileVisCrop;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
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
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.tools.ItemElementalHoe;

public class BlockVisCrop extends BlockCrops implements INoItemBlock, ITileBlock<TileVisCrop>, ISealFertilizable
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
		TileVisCrop tvc = WorldUtil.cast(world.getTileEntity(pos), TileVisCrop.class);
		Aspect asp = tvc != null ? tvc.getAspect() : aspectContainer.get();
		if(tvc == null)
			aspectContainer.set(null);
		
		if(asp == null)
			return;
		
		drops.add(ItemVisSeeds.create(asp, 1));
		
		Random rand = world instanceof World ? ((World) world).rand : RANDOM;
		
		if(isMaxAge(state))
		{
			int count = 1 + rand.nextInt(2);
			for(int i = 0; i < count; i++)
				drops.add(ItemVisPod.create(asp, 1));
			for(int i = 0; i < Math.min(4, 1 + fortune); ++i)
				if(rand.nextInt(20) == 0)
					drops.add(ItemVisSeeds.create(asp, 1));
		}
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack held = playerIn.getHeldItem(hand);
		
		if(!held.isEmpty() && held.getItem() instanceof ItemElementalHoe)
		{
			int i = this.getAge(state) + 1 + worldIn.rand.nextInt(2);
			int j = this.getMaxAge();
			if(i > j)
			{
				i = j;
				return true;
			}
			
			if(!worldIn.isRemote)
			{
				TileEntity tvc = worldIn.getTileEntity(pos);
				
				worldIn.setBlockState(pos, this.withAge(i), 2);
				
				worldIn.removeTileEntity(pos);
				tvc.validate();
				worldIn.setTileEntity(pos, tvc);
				((TileSyncable) tvc).sendChangesToNearby();
				
				worldIn.playBroadcastSound(2005, pos, 0);
				playerIn.getHeldItem(hand).damageItem(3, playerIn);
			} else
				FXDispatcher.INSTANCE.drawBlockMistParticles(pos, 4259648);
			
			return true;
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
	public boolean fertilize(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		TileEntity tvc = world.getTileEntity(pos);
		
		int a = getAge(state);
		int na = Math.min(getMaxAge(), a + 1 + world.rand.nextInt(2));
		
		if(na != a)
		{
			world.setBlockState(pos, withAge(na), 2);
			world.removeTileEntity(pos);
			tvc.validate();
			world.setTileEntity(pos, tvc);
		}
		
		return na != a;
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