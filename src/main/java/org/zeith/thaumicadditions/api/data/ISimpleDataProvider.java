package org.zeith.thaumicadditions.api.data;

import com.zeitheron.hammercore.utils.base.Cast;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ISimpleDataProvider<BASE>
{
	@Nullable
	<T> T get(BASE base, DataType<T> type);
	
	default ISimpleDataProvider<BASE> or(ISimpleDataProvider<BASE> other)
	{
		ISimpleDataProvider<BASE> thiz = this;
		return new ISimpleDataProvider<BASE>()
		{
			@Nullable
			@Override
			public <T> T get(BASE base, DataType<T> type)
			{
				T t = thiz.get(base, type);
				return t != null ? t : other.get(base, type);
			}
		};
	}
	
	static <BASE, OUT> ISimpleDataProvider<BASE> simple(DataType<OUT> singleType, Function<BASE, OUT> obtainer)
	{
		return new ISimpleDataProvider<BASE>()
		{
			@Nullable
			@Override
			public <T> T get(BASE base, DataType<T> type)
			{
				return singleType == type ? Cast.cast(obtainer.apply(base)) : null;
			}
		};
	}
	
	static <BASE, OUT> ISimpleDataProvider<BASE> stable(DataType<OUT> singleType, OUT value)
	{
		return simple(singleType, base -> value);
	}
}