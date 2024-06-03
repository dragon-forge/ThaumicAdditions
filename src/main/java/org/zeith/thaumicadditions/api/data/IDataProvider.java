package org.zeith.thaumicadditions.api.data;

import javax.annotation.Nullable;

public interface IDataProvider<BASE>
{
	Class<BASE> getBaseType();
	
	@Nullable
	<T> T get(BASE base, DataType<T> type);
	
	static <BASE> IDataProvider<BASE> simple(Class<BASE> base, ISimpleDataProvider<BASE> provider)
	{
		return new IDataProvider<BASE>()
		{
			@Override
			public Class<BASE> getBaseType()
			{
				return base;
			}
			
			@Nullable
			@Override
			public <T> T get(BASE base, DataType<T> type)
			{
				return provider.get(base, type);
			}
		};
	}
}