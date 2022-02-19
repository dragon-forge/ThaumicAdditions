package org.zeith.thaumicadditions.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.potions.PotionWarpWard;

public class ThaumicHelper
{
	public static boolean applyWarpWard(EntityPlayer player)
	{
		if(!player.world.isRemote && !player.isPotionActive(PotionWarpWard.instance))
		{
			int warp = ThaumcraftCapabilities.getWarp(player).get(EnumWarpType.PERMANENT);
			int div = 1;
			if(warp > 0)
			{
				div = (int) Math.sqrt(warp);
				if(div < 1)
					div = 1;
			}

			player.addPotionEffect(new PotionEffect(PotionWarpWard.instance, Math.min(32000, 200000 / div), 0, true, true));

			return true;
		}

		return false;
	}

	public static DamageSource createLivingDamageSource(EntityLivingBase e)
	{
		if(e instanceof EntityPlayer) return DamageSource.causePlayerDamage((EntityPlayer) e);
		else if(e != null) return DamageSource.causeMobDamage(e);
		return DamageSource.MAGIC;
	}
}