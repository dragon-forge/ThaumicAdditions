package org.zeith.thaumicadditions.init;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import com.zeitheron.hammercore.internal.GuiManager;
import org.zeith.thaumicadditions.init.guis.GuiCallbackCrystalBag;
import org.zeith.thaumicadditions.init.guis.GuiCallbackEssentiaPistol;
import org.zeith.thaumicadditions.init.guis.GuiCallbackRepairVoid;

import java.lang.reflect.Field;

public class GuisTAR
{
	public static final IGuiCallback ESSENTIA_PISTOL = new GuiCallbackEssentiaPistol();
	public static final IGuiCallback VOID_ANVIL = new GuiCallbackRepairVoid();
	public static final IGuiCallback CRYSTAL_BAG = new GuiCallbackCrystalBag();

	public static void register()
	{
		for(Field f : GuisTAR.class.getDeclaredFields())
		{
			f.setAccessible(true);
			if(IGuiCallback.class.isAssignableFrom(f.getType()))
				try
				{
					GuiManager.registerGuiCallback((IGuiCallback) f.get(null));
				} catch(Throwable err)
				{
				}
		}
	}
}