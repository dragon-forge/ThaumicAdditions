package com.zeitheron.thaumicadditions.blocks.plants;

import com.zeitheron.hammercore.api.INoItemBlock;
import com.zeitheron.thaumicadditions.api.seals.ISealFertilizable;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.tools.ItemElementalHoe;

import java.util.Random;

public class BlockVoidCrop
		extends BlockCrops
		implements INoItemBlock, ISealFertilizable
{
	public static final PropertyInteger AGE_4 = PropertyInteger.create("age", 0, 3);
	public static final BlockVoidCrop CROP = new BlockVoidCrop();

	private BlockVoidCrop()
	{
		setTranslationKey("void_crop");
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		drops.add(new ItemStack(ItemsTAR.VOID_SEED));

		Random rand = world instanceof World ? ((World) world).rand : RANDOM;

		if(isMaxAge(state))
		{
			int count = 1 + rand.nextInt(2);
			for(int i = 0; i < count; i++)
				drops.add(new ItemStack(ItemsTC.voidSeed));
			for(int i = 0; i < Math.min(4, 1 + fortune); ++i)
				if(rand.nextInt(40) == 0)
					drops.add(new ItemStack(ItemsTAR.VOID_SEED));
			if(rand.nextInt(3) == 0)
				drops.add(new ItemStack(ItemsTAR.VOID_FRUIT));
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
				worldIn.setBlockState(pos, this.withAge(i), 2);
				worldIn.playBroadcastSound(2005, pos, 0);
				playerIn.getHeldItem(hand).damageItem(3, playerIn);
			} else
				FXDispatcher.INSTANCE.drawBlockMistParticles(pos, 4259648);

			return true;
		}

		return false;
	}

	@Override
	public boolean fertilize(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		int a = getAge(state);
		int na = Math.min(getMaxAge(), a + 1 + world.rand.nextInt(2));
		if(na != a)
			world.setBlockState(pos, withAge(na), 2);
		return na != a;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
	{
		return false;
	}

	@Override
	protected PropertyInteger getAgeProperty()
	{
		return AGE_4;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, getAgeProperty());
	}

	@Override
	public int getMaxAge()
	{
		return 3;
	}

	@Override
	protected int getBonemealAgeIncrease(World worldIn)
	{
		return 1;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(ItemsTAR.VOID_SEED);
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		if(!super.canBlockStay(worldIn, pos, state))
			return false;
		return this.canSustainBush(worldIn.getBlockState(pos.down()));
	}
}