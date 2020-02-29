package com.zeitheron.thaumicadditions.utils;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

public class LootHelper
{
	private static Field pools, lootEntries;

	public static Stream<LootEntry> lootEntryStream(LootTable table)
	{
		if(pools == null)
		{
			pools = LootTable.class.getDeclaredFields()[2];
			pools.setAccessible(true);
		}
		if(lootEntries == null)
		{
			lootEntries = LootPool.class.getDeclaredFields()[0];
			lootEntries.setAccessible(true);
		}

		try
		{
			List<LootPool> poolList = List.class.cast(pools.get(table));
			return poolList.stream().flatMap(pool ->
			{
				try
				{
					List<LootEntry> lootEntryList = List.class.cast(lootEntries.get(pool));
					return lootEntryList.stream();
				} catch(IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			});
		} catch(Throwable err)
		{
			if(err instanceof RuntimeException) throw (RuntimeException) err;
			throw new RuntimeException(err);
		}
	}
}