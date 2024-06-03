package org.zeith.thaumicadditions.utils;

import java.util.List;
import java.util.function.*;

public class ListHelper
{
	public static <K> int replace(List<K> list, Predicate<K> matcher, UnaryOperator<K> replacer)
	{
		int c = 0;
		for(int i = 0; i < list.size(); ++i)
		{
			K k = list.get(i);
			if(matcher.test(k))
			{
				K v = replacer.apply(k);
				if(v == null)
				{
					list.remove(i);
					--i;
				} else
					list.set(i, v);
				++c;
			}
		}
		return c;
	}
}