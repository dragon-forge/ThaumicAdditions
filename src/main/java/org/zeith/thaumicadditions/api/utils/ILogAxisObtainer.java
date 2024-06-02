package org.zeith.thaumicadditions.api.utils;

import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

import java.util.Optional;

public interface ILogAxisObtainer<T extends Comparable<T>>
{
	IProperty<T> property();
	
	EnumAxis from(T t);
	
	default Optional<EnumAxis> tryObtain(IBlockState state)
	{
		IProperty<T> prop = property();
		return state.getPropertyKeys().contains(prop) ? Optional.ofNullable(from(state.getValue(prop))) : Optional.empty();
	}
}