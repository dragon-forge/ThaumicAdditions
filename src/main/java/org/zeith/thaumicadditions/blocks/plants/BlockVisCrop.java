package org.zeith.thaumicadditions.blocks.plants;

import com.zeitheron.hammercore.api.INoItemBlock;
import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.hammercore.utils.IRegisterListener;
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
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.api.seals.ISealFertilizable;
import org.zeith.thaumicadditions.items.ItemVisPod;
import org.zeith.thaumicadditions.items.placeholder.ItemVisSeedsNew;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.tools.ItemElementalHoe;

import java.util.Random;

public class BlockVisCrop
		extends BlockCrops
		implements INoItemBlock, ISealFertilizable, IRegisterListener
{
	public static final PropertyInteger AGE_5 = PropertyInteger.create("age", 0, 4);

	public final Aspect aspect;
	public final ItemVisSeedsNew seed;

	public BlockVisCrop(Aspect aspect)
	{
		this.aspect = aspect;
		setTranslationKey("vis_crop/" + aspect.getTag());
		this.seed = new ItemVisSeedsNew(this);
	}

	@Override
	public void onRegistered()
	{
		SimpleRegistration.registerItem(seed, getRegistryName().getNamespace(), TAReconstructed.tab);
		ThaumcraftApi.registerSeed(this, new ItemStack(seed));
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		drops.add(new ItemStack(seed, 1));
		Random rand = world instanceof World ? ((World) world).rand : RANDOM;
		if(isMaxAge(state))
		{
			int count = 1 + rand.nextInt(2);
			for(int i = 0; i < count; i++)
				drops.add(ItemVisPod.create(aspect, 1));
			for(int i = 0; i < Math.min(4, 1 + fortune); ++i)
				if(rand.nextInt(20) == 0)
					drops.add(new ItemStack(seed, 1));
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

	public int getColor(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
	{
		if(tintIndex == 1) return aspect.getColor();
		return 0xFFFFFF;
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
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(seed, 1);
	}
}