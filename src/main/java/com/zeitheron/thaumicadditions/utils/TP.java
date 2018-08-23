package com.zeitheron.thaumicadditions.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class TP
{
	public static double teleport(EntityPlayer player, double x, double y, double z)
	{
		double ox = player.posX, oz = player.posZ;
		float r = 3;
		player.setPositionAndUpdate(x, y, z);
		if(player instanceof EntityPlayerMP)
			((EntityPlayerMP) player).connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
		return Math.sqrt((ox - x) * (ox - x) + (oz - z) * (oz - z));
	}
}