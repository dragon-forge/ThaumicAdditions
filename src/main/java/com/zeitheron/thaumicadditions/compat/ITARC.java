package com.zeitheron.thaumicadditions.compat;

import com.zeitheron.hammercore.mod.ILoadModule;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITARC extends ILoadModule
{
	void init();
	
	@SideOnly(Side.CLIENT)
	void initClient();
}