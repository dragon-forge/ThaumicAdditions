package com.zeitheron.thaumicadditions.utils;

public class ThaumicMath
{
	public static double cap(double value, double mx)
	{
		return value < 0D ? Math.min(value, -mx) : Math.max(value, mx);
	}
	
	public static double mcap(double value, double mn)
	{
		return value < 0D ? Math.max(value, -mn) : Math.min(value, mn);
	}
}