package org.zeith.thaumicadditions.api.utils;

public interface IChangeListener
{
	void update(int id);

	default void update(ListenerList list, int id)
	{
		if(valid())
			update(id);
	}

	boolean valid();
}