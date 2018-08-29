package com.zeitheron.thaumicadditions.utils;

import net.minecraft.nbt.NBTTagCompound;

public class AspectRule
{
	public int min, max;
	public boolean voidExcess;
	
	public NBTTagCompound write(NBTTagCompound nbt)
	{
		nbt.setInteger("Min", min);
		nbt.setInteger("Max", max);
		nbt.setBoolean("Void", voidExcess);
		return nbt;
	}
	
	public AspectRule read(NBTTagCompound nbt)
	{
		min = nbt.getInteger("Min");
		max = nbt.getInteger("Max");
		voidExcess = nbt.getBoolean("Void");
		return this;
	}
}