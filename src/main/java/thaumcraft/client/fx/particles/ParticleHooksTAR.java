package thaumcraft.client.fx.particles;

import scala.actors.threadpool.Arrays;

public class ParticleHooksTAR
{
	public static boolean isSoundingFX(FXGeneric fx)
	{
		return fx.particleMaxAge == 44 && fx.gridSize == 16 && fx.scaleFrames.length == 45 && fx.scaleFrames[0] == 9F;
	}
}