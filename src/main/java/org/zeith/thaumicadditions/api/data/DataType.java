package org.zeith.thaumicadditions.api.data;

import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class DataType<T>
{
	private final ResourceLocation id;
	private final Class<T> dataType;
	
	public DataType(ResourceLocation id, Class<T> dataType)
	{
		this.id = id;
		this.dataType = dataType;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		DataType<?> that = (DataType<?>) o;
		return Objects.equals(id, that.id) && Objects.equals(dataType, that.dataType);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(id, dataType);
	}
	
	@Override
	public String toString()
	{
		return "TileDataType{" +
			   "id=" + id +
			   ", dataType=" + dataType +
			   '}';
	}
}