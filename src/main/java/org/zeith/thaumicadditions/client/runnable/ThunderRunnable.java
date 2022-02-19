package org.zeith.thaumicadditions.client.runnable;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.net.internal.thunder.Thunder;
import com.zeitheron.hammercore.net.internal.thunder.Thunder.Layer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThunderRunnable
		implements Runnable
{
	World world;
	Vec3d start, end;
	long seed;
	int age;
	float fractMod;
	Layer core, aura;

	public ThunderRunnable(World world, Vec3d start, Vec3d end, long seed, int age, float fractMod, Layer core, Layer aura)
	{
		this.world = world;
		this.start = start;
		this.end = end;
		this.seed = seed;
		this.age = age;
		this.fractMod = fractMod;
		this.core = core;
		this.aura = aura;
	}

	@Override
	public void run()
	{
		HammerCore.particleProxy.spawnSimpleThunder(world, start, end, seed, age, fractMod, core, aura);
	}
}