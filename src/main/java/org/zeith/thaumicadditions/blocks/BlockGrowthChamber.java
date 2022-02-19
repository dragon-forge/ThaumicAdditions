package org.zeith.thaumicadditions.blocks;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.internal.blocks.base.BlockDeviceHC;
import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.tiles.TileGrowthChamber;

public class BlockGrowthChamber
		extends BlockDeviceHC<TileGrowthChamber>
		implements IBlockHorizontal
{
	public BlockGrowthChamber()
	{
		super(Material.IRON, TileGrowthChamber.class, "growth_chamber");
		setHardness(3F);
		setSoundType(SoundType.METAL);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		GuiManager.openGui(playerIn, Cast.cast(worldIn.getTileEntity(pos), TileGrowthChamber.class));
		return true;
	}
}