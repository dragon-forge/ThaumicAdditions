package org.zeith.thaumicadditions.init;

import com.zeitheron.hammercore.utils.OnetimeCaller;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.thaumicadditions.effect.PotionSanityChecker;
import org.zeith.thaumicadditions.effect.PotionSoundSensivity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PotionsTAR
{
	public static final PotionSoundSensivity SOUND_SENSIVITY = new PotionSoundSensivity();
	public static final PotionSanityChecker SANITY_CHECKER = new PotionSanityChecker();

	public static final OnetimeCaller register = new OnetimeCaller(PotionsTAR::$register);

	private static void $register()
	{
		IForgeRegistry<Potion> reg = GameRegistry.findRegistry(Potion.class);

		for(Field f : PotionsTAR.class.getDeclaredFields())
		{
			f.setAccessible(true);
			if(Potion.class.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()))
				try
				{
					reg.register((Potion) f.get(null));
				} catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
		}
	}
}