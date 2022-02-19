package org.zeith.thaumicadditions.api.fx;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ParticleHelperTAR
{
	public static void spawnItemCrack(World world, double x, double y, double z, double motionX, double motionY, double motionZ, ItemStack stack)
	{
		HCNet.spawnParticle(world, TARParticleTypes.ITEMSTACK_CRACK, x, y, z, motionX, motionY, motionZ, NBTUtils.toIA(stack.serializeNBT()));
	}

	public static void spawnPollution(World world, double x, double y, double z)
	{
		HCNet.spawnParticle(world, TARParticleTypes.POLLUTION, x, y, z, 0, 0, 0);
	}
}