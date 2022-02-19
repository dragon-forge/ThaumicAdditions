package org.zeith.thaumicadditions.api.seals;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.tiles.TileSeal;

public class SealInstance
{
	public final TileSeal seal;

	public SealInstance(TileSeal seal)
	{
		this.seal = seal;
	}

	public String getUnlocalizedDescription()
	{
		return "seal." + InfoTAR.MOD_ID + ".unknown";
	}

	public void onEntityCollisionWithSeal(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{

	}

	public boolean onSealActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return false;
	}

	public void onSealBreak()
	{

	}

	public void readFromNBT(NBTTagCompound nbt)
	{

	}

	public void tick()
	{

	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		return nbt;
	}
}