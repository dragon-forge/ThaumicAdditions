package org.zeith.thaumicadditions.blocks;

import com.zeitheron.hammercore.api.ITileBlock;
import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.api.data.datas.GauntletData;
import org.zeith.thaumicadditions.tiles.TileShadowEnchanter;
import thaumcraft.common.items.casters.ItemCaster;

public class BlockShadowEnchanter
		extends Block
		implements ITileBlock<TileShadowEnchanter>
{
	static final AxisAlignedBB SHADOW_AABB = new AxisAlignedBB(0, 0, 0, 1, 9.5 / 16, 1);

	public BlockShadowEnchanter()
	{
		super(Material.ROCK);
		setTranslationKey("shadow_enchanter");
		setHardness(1.5F);
		setHarvestLevel("pickaxe", 2);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileShadowEnchanter ench = Cast.cast(worldIn.getTileEntity(pos), TileShadowEnchanter.class);
		if(ench != null)
		{
			ItemStack stack = playerIn.getHeldItem(hand);
			if(!stack.isEmpty() && GauntletData.isGauntlet(stack))
			{
				if(!ench.infusing)
					ench.startCraft();
				if(ench.infusing)
					return true;
			}
		}
		GuiManager.openGui(playerIn, ench);
		return true;
	}

	@Override
	public Class<TileShadowEnchanter> getTileClass()
	{
		return TileShadowEnchanter.class;
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SHADOW_AABB;
	}
}