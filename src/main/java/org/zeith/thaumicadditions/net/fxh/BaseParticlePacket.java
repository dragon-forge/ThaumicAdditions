package org.zeith.thaumicadditions.net.fxh;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;

public abstract class BaseParticlePacket
		implements IPacket
{
	protected Vec3d pos;
	protected Vec3d motion;
	
	public BaseParticlePacket(Vec3d pos, Vec3d motion)
	{
		this.pos = pos;
		this.motion = motion;
	}
	
	public BaseParticlePacket()
	{
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeDouble(pos.x).writeDouble(pos.y).writeDouble(pos.z);
		buf.writeDouble(motion.x).writeDouble(motion.y).writeDouble(motion.z);
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		motion = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
	@Override
	public void executeOnClient2(PacketContext net)
	{
		EntityPlayer pl = net.getPlayer();
		if(pl == null) return;
		spawnParticle(pl.world, pos, motion);
	}
	
	protected abstract void spawnParticle(World world, Vec3d pos, Vec3d motion);
	
	@Override
	public boolean executeOnMainThread()
	{
		return true;
	}
}