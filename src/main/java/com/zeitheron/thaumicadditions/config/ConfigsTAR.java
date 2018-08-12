package com.zeitheron.thaumicadditions.config;

import com.zeitheron.hammercore.cfg.HCModConfigurations;
import com.zeitheron.hammercore.cfg.IConfigReloadListener;
import com.zeitheron.hammercore.cfg.fields.ModConfigPropertyBool;
import com.zeitheron.thaumicadditions.InfoTAR;

@HCModConfigurations(modid = InfoTAR.MOD_ID)
public class ConfigsTAR implements IConfigReloadListener
{
	@ModConfigPropertyBool(category = "Client", name = "Seethrough Portal", comment = "Should portal seal be see-throught?", defaultValue = true)
	public static boolean portalGfx;
}