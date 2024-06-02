package org.zeith.thaumicadditions.net.fxh;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.TAReconstructed;

import java.awt.*;
import java.io.IOException;

public class PacketSpawnColorDrop
		extends BaseParticlePacket
{
	protected int color;
	
	public PacketSpawnColorDrop(Vec3d pos, Vec3d motion, int color)
	{
		super(pos, motion);
		this.color = color;
	}
	
	public PacketSpawnColorDrop()
	{
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		super.write(buf);
		buf.writeInt(color);
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		super.read(buf);
		color = buf.readInt();
	}
	
	@Override
	protected void spawnParticle(World world, Vec3d pos, Vec3d motion)
	{
		TAReconstructed.proxy.getFX().spawnColorDrop(world, pos, motion, new Color(color));
	}
}