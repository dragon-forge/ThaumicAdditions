package org.zeith.thaumicadditions.net.fxh;

import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.TAReconstructed;

import java.awt.*;
import java.io.IOException;

public class PacketSpawnColorCloud
		extends BaseParticlePacket
{
	protected int color;
	protected boolean noclip;
	
	public PacketSpawnColorCloud(Vec3d pos, Vec3d motion, int color, boolean noclip)
	{
		super(pos, motion);
		this.color = color;
		this.noclip = noclip;
	}
	
	public PacketSpawnColorCloud()
	{
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		super.write(buf);
		buf.writeInt(color);
		buf.writeBoolean(noclip);
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		super.read(buf);
		color = buf.readInt();
		noclip = buf.readBoolean();
	}
	
	@Override
	protected void spawnParticle(World world, Vec3d pos, Vec3d motion)
	{
		TAReconstructed.proxy.getFX().spawnColorCloud(world, pos, motion, new Color(color), noclip);
	}
}