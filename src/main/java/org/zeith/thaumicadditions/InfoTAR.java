package org.zeith.thaumicadditions;

import net.minecraft.util.ResourceLocation;

public class InfoTAR
{
	public static final String MOD_ID = "thaumadditions";
	public static final String MOD_VERSION = "@VERSION@";
	public static final String MOD_NAME = "Thaumic Additions: Reconstructed";
	
	public static ResourceLocation id(String id)
	{
		return new ResourceLocation(MOD_ID, id);
	}
}