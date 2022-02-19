package org.zeith.thaumicadditions.client;

import com.zeitheron.hammercore.utils.FinalFieldHelper;
import thaumcraft.client.fx.particles.FXGeneric;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ParticleHooksTAR
{
	private static final ReFl<Integer> gridSize = ReMg.getf(int.class, FXGeneric.class, "gridSize");
	private static final ReFl<float[]> scaleFrames = ReMg.getf(float[].class, FXGeneric.class, "scaleFrames");

	public static boolean isSoundingFX(FXGeneric fx)
	{
		float[] sf;
		return fx.particleMaxAge == 44 && gridSize.get(fx) == 16 && (sf = scaleFrames.get(fx)).length == 45 && sf[0] == 9F;
	}

	static class ReMg
	{
		private static <T> ReFl<T> getf(Class<T> retType, Class<?> cls, String fn)
		{
			try
			{
				return new ReFl<>(cls.getDeclaredField(fn));
			} catch(NoSuchFieldException | SecurityException e)
			{
			}
			return null;
		}
	}

	static class ReFl<T>
	{
		private final Field f;
		private final boolean fn;

		public ReFl(Field f)
		{
			this.f = f;
			this.fn = Modifier.isFinal(f.getModifiers());
			this.f.setAccessible(true);
		}

		boolean set(Object inst, T val)
		{
			try
			{
				if(this.fn)
					return FinalFieldHelper.setFinalField(f, inst, val);
				else
				{
					f.set(inst, val);
					return true;
				}
			} catch(ReflectiveOperationException e)
			{
			}
			return false;
		}

		T get(Object inst)
		{
			try
			{
				return (T) f.get(inst);
			} catch(IllegalArgumentException | IllegalAccessException e)
			{
			}
			return null;
		}
	}
}