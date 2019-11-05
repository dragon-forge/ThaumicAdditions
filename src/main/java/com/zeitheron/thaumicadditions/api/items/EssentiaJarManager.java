package com.zeitheron.thaumicadditions.api.items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Predicates;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.thaumicadditions.blocks.BlockAbstractEssentiaJar.BlockAbstractJarItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.essentia.BlockJarItem;

public class EssentiaJarManager
{
	private static final Map<Item, Function<ItemStack, IJar>> JARS = new HashMap<>();
	
	static
	{
		Function<ItemStack, IJar> vanillaJar;
		registerJar(Item.getItemFromBlock(BlocksTC.jarNormal), vanillaJar = stack ->
		{
			BlockJarItem item = Cast.cast(stack.getItem(), BlockJarItem.class);
			return new IJar()
			{
				@Override
				public AspectList getEssentia(ItemStack stack)
				{
					return item.getAspects(stack);
				}
				
				@Override
				public int drain(ItemStack stack, Aspect aspect, int amount)
				{
					AspectList list = getEssentia(stack);
					int d;
					if((d = list.getAmount(aspect)) > 0)
					{
						amount = Math.min(d, amount);
						list.remove(aspect, amount);
						item.setAspects(stack, list);
						return amount;
					}
					return 0;
				}
				
				@Override
				public int fill(ItemStack stack, Aspect aspect, int amount)
				{
					AspectList list = getEssentia(stack);
					int d;
					if((d = list.getAmount(aspect)) < 250)
					{
						amount = Math.min(250 - d, amount);
						list.add(aspect, amount);
						item.setAspects(stack, list);
						return amount;
					}
					return 0;
				}
				
				@Override
				public int capacity(ItemStack jarStack)
				{
					return 250;
				}
			};
		});
		registerJar(Item.getItemFromBlock(BlocksTC.jarVoid), vanillaJar);
		
		ForgeRegistries.ITEMS.getValuesCollection().stream().filter(Predicates.instanceOf(BlockAbstractJarItem.class)).forEach(ji ->
		{
			final BlockAbstractJarItem item = (BlockAbstractJarItem) ji;
			final int capacity = item.block.capacity;
			registerJar(ji, stack ->
			{
				return new IJar()
				{
					@Override
					public AspectList getEssentia(ItemStack stack)
					{
						return item.getAspects(stack);
					}
					
					@Override
					public int drain(ItemStack stack, Aspect aspect, int amount)
					{
						AspectList list = getEssentia(stack);
						int d;
						if((d = list.getAmount(aspect)) > 0)
						{
							amount = Math.min(d, amount);
							list.remove(aspect, amount);
							item.setAspects(stack, list);
							return amount;
						}
						return 0;
					}
					
					@Override
					public int fill(ItemStack stack, Aspect aspect, int amount)
					{
						AspectList list = getEssentia(stack);
						int d;
						if((d = list.getAmount(aspect)) < capacity)
						{
							amount = Math.min(capacity - d, amount);
							list.add(aspect, amount);
							item.setAspects(stack, list);
							return amount;
						}
						return 0;
					}
					
					@Override
					public int capacity(ItemStack jarStack)
					{
						return capacity;
					}
				};
			});
		});
	}
	
	public static void registerJar(Item item, IJar jar)
	{
		JARS.put(item, stack -> jar);
	}
	
	public static void registerJar(Item item, Function<ItemStack, IJar> jar)
	{
		JARS.put(item, jar);
	}
	
	public static IJar fromStack(ItemStack stack)
	{
		if(stack.isEmpty())
			return null;
		Function<ItemStack, IJar> jarsup = JARS.get(stack.getItem());
		return jarsup == null ? null : jarsup.apply(stack);
	}
	
	public static interface IJar
	{
		AspectList getEssentia(ItemStack stack);
		
		int drain(ItemStack stack, Aspect aspect, int amount);
		
		int fill(ItemStack stack, Aspect aspect, int amount);
		
		int capacity(ItemStack jarStack);
	}
}