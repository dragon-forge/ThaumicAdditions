package com.zeitheron.thaumicadditions.utils;

import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.tiles.TileAspectCombiner;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

public class AspectOperator
{
	public final EnumAOP function;
	public final int ticks;
	public final Aspect a, b;
	
	public AspectOperator(int ticks, Aspect aspect)
	{
		this.a = aspect;
		this.b = null;
		this.function = EnumAOP.DISSOLVE;
		this.ticks = ticks;
	}
	
	public AspectOperator(int ticks, Aspect a, Aspect b)
	{
		this.a = a;
		this.b = b;
		this.function = EnumAOP.COMBINE;
		this.ticks = ticks;
	}
	
	public int ticksRunnin = 0;
	
	public AspectList update()
	{
		if(ticksRunnin >= ticks)
			return getResult();
		++ticksRunnin;
		return null;
	}
	
	public AspectList getResult()
	{
		AspectList al = new AspectList();
		if(function == EnumAOP.DISSOLVE)
		{
			if(a != null)
				al.add(a, 1);
			return AspectHelper.reduceToPrimals(al);
		} else if(function == EnumAOP.COMBINE)
		{
			Aspect res = TileAspectCombiner.getOutput(a, b);
			if(res != null)
				al.add(res, 1);
			else
			{
				if(a != null)
					al.add(a, 1);
				if(b != null)
					al.add(b, 1);
			}
		}
		return al;
	}
	
	public NBTTagCompound write()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		if(a != null)
			nbt.setString("a", a.getTag());
		if(b != null)
			nbt.setString("b", b.getTag());
		nbt.setInteger("t", ticksRunnin);
		nbt.setInteger("tt", ticks);
		return nbt;
	}
	
	public static AspectOperator read(NBTTagCompound nbt)
	{
		Aspect a = Aspect.getAspect(nbt.getString("a"));
		Aspect b = Aspect.getAspect(nbt.getString("b"));
		int time = nbt.getInteger("t");
		int tt = nbt.getInteger("tt");
		AspectOperator ao = a != null && b == null ? new AspectOperator(tt, a) : a != null && b != null ? new AspectOperator(tt, a, b) : null;
		if(ao != null)
			ao.ticksRunnin = time;
		return ao;
	}
	
	public enum EnumAOP
	{
		DISSOLVE, COMBINE;
	}
}