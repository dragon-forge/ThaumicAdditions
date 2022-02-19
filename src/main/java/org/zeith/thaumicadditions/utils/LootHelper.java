package org.zeith.thaumicadditions.utils;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LootHelper
{
	private static Field pools, lootEntries;
	private static Method getLootTable;
	private static Field itemF, functionsF, tableF;

	public static boolean hasDropped(LivingDropsEvent e, Predicate<ItemStack> drop)
	{
		return e.getDrops().stream().map(EntityItem::getItem).anyMatch(drop);
	}

	public static LootContext generateDropContent(LivingDropsEvent e)
	{
		if(e.getEntityLiving().world.isRemote) return null;
		EntityLivingBase c = e.getEntityLiving();
		Builder ctx = (new Builder((WorldServer) c.world)).withLootedEntity(c).withDamageSource(e.getSource());
		if(e.isRecentlyHit() && e.getSource() != null && e.getSource().getTrueSource() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) e.getSource().getTrueSource();
			ctx = ctx.withPlayer(player).withLuck(player.getLuck());
		}
		return ctx.build();
	}

	public static ResourceLocation getDeathTablePath(EntityLiving c)
	{
		if(getLootTable == null)
		{
			for(Method m : EntityLiving.class.getDeclaredMethods())
				if(m.getParameterCount() == 0 && ResourceLocation.class.isAssignableFrom(m.getReturnType()))
				{
					getLootTable = m;
					getLootTable.setAccessible(true);
					break;
				}
		}
		ResourceLocation lootTable = c.deathLootTable;

		// Load from custom override
		if(lootTable == null && c.getEntityData().hasKey("HC_CDLT", NBT.TAG_STRING))
			lootTable = new ResourceLocation(c.getEntityData().getString("HC_CDLT"));

		if(lootTable == null) try
		{
			lootTable = (ResourceLocation) getLootTable.invoke(c);
		} catch(IllegalAccessException | InvocationTargetException ex)
		{
		}
		return lootTable;
	}

	public static LootTable getDeathTable(EntityLiving c)
	{
		LootTable lt = c.world.getLootTableManager().getLootTableFromLocation(getDeathTablePath(c));
		if(lt == null) lt = LootTable.EMPTY_LOOT_TABLE;
		return lt;
	}

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
			List<LootPool> poolList = (List) pools.get(table);
			return poolList.stream().flatMap(pool ->
			{
				try
				{
					List<LootEntry> lootEntryList = (List) lootEntries.get(pool);
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

	public static NonNullList<ItemStack> getEntryItems(LootEntry entry, Random rand, LootContext context)
	{
		NonNullList<ItemStack> items = NonNullList.create();

		if(itemF == null)
		{
			itemF = LootEntryItem.class.getDeclaredFields()[0];
			itemF.setAccessible(true);
		}

		if(functionsF == null)
		{
			functionsF = LootEntryItem.class.getDeclaredFields()[1];
			functionsF.setAccessible(true);
		}

		try
		{
			if(entry instanceof LootEntryItem)
			{
				Item item = (Item) itemF.get(entry);
				LootFunction[] functions = (LootFunction[]) functionsF.get(entry);
				ItemStack itemstack = new ItemStack(item);
				for(LootFunction lootfunction : functions)
				{
					if(lootfunction instanceof SetCount) continue;
					if(LootConditionManager.testAllConditions(lootfunction.getConditions(), rand, context))
					{
						itemstack = lootfunction.apply(itemstack, rand, context);
					}
				}
				items.add(itemstack);
			} else if(entry instanceof LootEntryTable)
			{
				if(tableF == null)
				{
					tableF = LootEntryTable.class.getDeclaredFields()[0];
					tableF.setAccessible(true);
				}
				LootTable table = context.getLootTableManager().getLootTableFromLocation((ResourceLocation) tableF.get(entry));
				lootEntryStream(table).forEach(entry2 -> items.addAll(getEntryItems(entry2, rand, context)));
			} else entry.addLoot(items, rand, context);
		} catch(Throwable err)
		{
		}

		return items;
	}
}