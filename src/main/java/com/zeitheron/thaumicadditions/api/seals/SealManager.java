package com.zeitheron.thaumicadditions.api.seals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.nbt.NBTTagCompound;

public class SealManager
{
	private static final List<SealCombination> combinations = new ArrayList<>();
	private static final Map<SealCombination, Function<TileSeal, SealInstance>> instances = new HashMap<>();
	
	public static SealCombination getCombination(TileSeal seal)
	{
		for(int i = 0; i < combinations.size(); ++i)
			if(combinations.get(i).isValid(seal))
				return combinations.get(i);
		return null;
	}
	
	public static SealInstance makeInstance(TileSeal seal, SealCombination combo, @Nullable NBTTagCompound nbt)
	{
		Function<TileSeal, SealInstance> instanceMaker = instances.get(combo);
		if(instanceMaker != null)
		{
			SealInstance inst = instanceMaker.apply(seal);
			if(nbt != null)
				inst.readFromNBT(nbt);
			return inst;
		}
		return null;
	}
	
	public static void registerCombination(SealCombination combination, Function<TileSeal, SealInstance> instanceMaker)
	{
		combinations.add(combination);
		instances.put(combination, instanceMaker);
	}
}