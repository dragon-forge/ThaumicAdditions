package org.zeith.thaumicadditions.api.data.datas;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.DataType;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class JarData
{
	public static final DataType<JarData> TYPE = new DataType<>(InfoTAR.id("jar_data"), JarData.class);
	
	@Nullable
	public final TileJarFillable tcJarTile;
	
	public final int capacity;
	
	public final boolean voidsExcess;
	
	@Nullable
	public final BiFunction<JarData, ItemStack, JarStorage> getStorage;
	
	@Nullable
	public final BiFunction<ItemStack, JarStorage, ItemStack> setStorage;
	
	public JarData(@Nullable TileJarFillable tcJarTile, int capacity, boolean voidsExcess, @Nullable BiFunction<JarData, ItemStack, JarStorage> getStorage, @Nullable BiFunction<ItemStack, JarStorage, ItemStack> setStorage)
	{
		this.tcJarTile = tcJarTile;
		this.capacity = capacity;
		this.voidsExcess = voidsExcess;
		this.getStorage = getStorage;
		this.setStorage = setStorage;
	}
	
	@Nullable
	public JarStorage getStorage(ItemStack stack)
	{
		if(getStorage != null) return getStorage.apply(this, stack);
		return null;
	}
	
	public ItemStack setStorage(ItemStack stack, JarStorage storage)
	{
		if(setStorage != null) return setStorage.apply(stack, storage);
		return stack;
	}
	
	public static JarData fromTile(TileEntity tile)
	{
		return DataProviderRegistry.first(tile, TYPE).orElse(null);
	}
	
	public static JarData fromStack(ItemStack stack)
	{
		return DataProviderRegistry.first(stack.getItem(), TYPE).orElse(null);
	}
	
	public static class JarStorage
	{
		public final JarData jar;
		public @Nullable Aspect aspect, aspectFilter;
		public int amount;
		
		public JarStorage(JarData jar, @Nullable Aspect aspect, @Nullable Aspect aspectFilter, int amount)
		{
			this.jar = jar;
			this.aspect = aspect;
			this.aspectFilter = aspectFilter;
			this.amount = amount;
		}
		
		public ItemStack set(ItemStack stack)
		{
			return jar.setStorage(stack, this);
		}
		
		public AspectList asList()
		{
			AspectList al = new AspectList();
			if(aspect != null && amount > 0) al.add(aspect, amount);
			return al;
		}
	}
}