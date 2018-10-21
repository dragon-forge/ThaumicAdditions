package com.zeitheron.thaumicadditions.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
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
				div = (int) Math.sqrt((double) warp);
				if(div < 1)
					div = 1;
			}
			
			player.addPotionEffect(new PotionEffect(PotionWarpWard.instance, Math.min(32000, 200000 / div), 0, true, true));
			
			return true;
		}
		
		return false;
	}
}