package org.zeith.thaumicadditions.blocks.decor;

import com.zeitheron.hammercore.internal.blocks.base.BlockDeviceHC;
import com.zeitheron.hammercore.internal.blocks.base.IBlockEnableable;
import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.tiles.TileCrystalLamp;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.lib.SoundsTC;

public class BlockCrystalLamp
		extends BlockDeviceHC<TileCrystalLamp>
		implements IBlockHorizontal, IBlockEnableable
{
	public BlockCrystalLamp()
	{
		super(Material.IRON, TileCrystalLamp.class, "crystal_lamp");
		setSoundType(SoundType.WOOD);
		setHardness(2F);
		setHarvestLevel("axe", 0);
	}

	public static int getColor(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
	{
		TileCrystalLamp tcb = Cast.cast(worldIn.getTileEntity(pos), TileCrystalLamp.class);
		Aspect a;
		if(tcb != null && (a = tcb.getAspect()) != null)
			return a.getColor();
		return 0xFFFFFF;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileCrystalLamp tile = Cast.cast(worldIn.getTileEntity(pos), TileCrystalLamp.class);
		if(tile == null || tile.getAspect() == null)
			return state.withProperty(ENABLED, false);
		return state;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state.getValue(ENABLED) ? 12 : 0;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos frompos)
	{
		boolean enabled = state.getValue(ENABLED);

		TileCrystalLamp tile = Cast.cast(worldIn.getTileEntity(pos), TileCrystalLamp.class);
		boolean tp = tile != null && tile.getAspect() != null;

		boolean powered = !worldIn.isBlockPowered(pos) && tp;
		updateStateKeepTile(worldIn, pos, state = state.withProperty(ENABLED, powered));

		if(enabled != powered)
		{
			worldIn.setLightFor(EnumSkyBlock.BLOCK, pos, getLightValue(state, worldIn, pos));
			worldIn.checkLight(pos);
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileCrystalLamp tile = Cast.cast(worldIn.getTileEntity(pos), TileCrystalLamp.class);

		if(tile != null)
		{
			ItemStack held = playerIn.getHeldItem(hand);

			boolean flag = false;

			if(tile.getAspect() != null && playerIn.isSneaking())
			{
				if(!playerIn.isCreative())
					WorldUtil.spawnItemStack(worldIn, pos, AspectUtil.crystalEssence(tile.getAspect(), 1));
				tile.setAspect(null);
				flag = true;
			}

			if(held.getItem() == ItemsTC.crystalEssence)
			{
				AspectList aspects = ((ItemCrystalEssence) ItemsTC.crystalEssence).getAspects(held);
				if(aspects.getAspects().length == 1)
				{
					Aspect asp = aspects.getAspects()[0];
					if(asp != null)
					{
						Aspect prev = tile.setAspect(asp);
						if(!playerIn.isCreative())
						{
							if(prev != null)
								WorldUtil.spawnItemStack(worldIn, pos, AspectUtil.crystalEssence(prev, 1));
							held.shrink(1);
						}
						flag = true;
					}
				}
			}

			if(flag)
			{
				worldIn.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.crystal, SoundCategory.BLOCKS, 0.5f, 0.4f / (playerIn.getRNG().nextFloat() * 0.4f + 0.8f));
				updateStateKeepTile(worldIn, pos, getActualState(state.withProperty(ENABLED, true), worldIn, pos));
			}

			return flag;
		}

		return false;
	}
}
