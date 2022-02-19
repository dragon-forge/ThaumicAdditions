package org.zeith.thaumicadditions.init;

import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.hammercore.utils.SoundObject;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.TAReconstructed;

import java.lang.reflect.Field;

public class SoundsTAR
{
	public static final SoundObject RUNE_SET = new SoundObject(InfoTAR.MOD_ID, "rune_set");
	public static final SoundObject PORTAL_OPEN = new SoundObject(InfoTAR.MOD_ID, "popen");
	public static final SoundObject PORTAL_CLOSE = new SoundObject(InfoTAR.MOD_ID, "pclose");
	public static final SoundObject FIZZ = new SoundObject(InfoTAR.MOD_ID, "fizz");
	public static final SoundObject SHADOW_BEAM = new SoundObject(InfoTAR.MOD_ID, "shadow_beam");
	public static final SoundObject ESSENTIA_PISTOL_SHOOT = new SoundObject(InfoTAR.MOD_ID, "essentia_pistol_shoot");
	public static final SoundObject MITHMINITE_SCYTHE = new SoundObject(InfoTAR.MOD_ID, "mithminite_scythe");

	public static void register()
	{
		Field[] fs = SoundsTAR.class.getDeclaredFields();
		for(Field f : fs)
			if(SoundObject.class.isAssignableFrom(f.getType()))
				try
				{
					f.setAccessible(true);
					SoundObject so = (SoundObject) f.get(null);
					SimpleRegistration.registerSound(so);
					TAReconstructed.LOG.info("[SOUNDS] Registered sound " + so.name);
				} catch(Throwable err)
				{
					err.printStackTrace();
				}
	}
}