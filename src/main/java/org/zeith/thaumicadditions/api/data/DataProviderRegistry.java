package org.zeith.thaumicadditions.api.data;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.tileentity.TileEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class DataProviderRegistry
{
	private static final Map<DataType<?>, List<IDataProvider<?>>> PROVIDERS = new ConcurrentHashMap<>();
	
	public static <T extends TileEntity, DATA> void register(DataType<DATA> type, IDataProvider<T> provider)
	{
		PROVIDERS.computeIfAbsent(type, o -> new ArrayList<>()).add(provider);
	}
	
	public static <DATA> Stream<DATA> all(Object base, DataType<DATA> type)
	{
		return PROVIDERS.getOrDefault(type, Collections.emptyList())
				.stream()
				.map(p -> get(p, base, type))
				.filter(Objects::nonNull);
	}
	
	public static <DATA> Optional<DATA> first(TileEntity tile, DataType<DATA> type)
	{
		return all(tile, type).findFirst();
	}
	
	private static <BASE, DATA> DATA get(IDataProvider<BASE> data, Object base, DataType<DATA> type)
	{
		BASE baseChecked = Cast.cast(base, data.getBaseType());
		if(baseChecked == null) return null;
		return data.get(baseChecked, type);
	}
}