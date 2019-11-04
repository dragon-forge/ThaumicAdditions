package com.zeitheron.thaumicadditions.api.fx;

import com.zeitheron.hammercore.client.utils.ParticleHelper;

import net.minecraft.util.EnumParticleTypes;

public class TARParticleTypes
{
	public static final EnumParticleTypes ITEMSTACK_CRACK = ParticleHelper.newParticleType("TAR_ITEMSTACK_CRACK", "itemstack_crack", false, 0);
	public static final EnumParticleTypes POLLUTION = ParticleHelper.newParticleType("TAR_POLLUTION", "tc.pollution", false, 0);
	public static final EnumParticleTypes COLOR_CLOUD = ParticleHelper.newParticleType("TAR_COLOR_CLOUD", "tar.color_cloud", false, 0);
	public static final EnumParticleTypes COLOR_DROP = ParticleHelper.newParticleType("TAR_COLOR_DROP", "tar.color_drop", false, 1);
}