package org.zeith.thaumicadditions.net.fxh;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.TAReconstructed;

import java.io.IOException;

@MainThreaded
public class PacketSpawnItemCrack
		extends BaseParticlePacket
{
	static
	{
		IPacket.handle(PacketSpawnItemCrack.class, PacketSpawnItemCrack::new);
	}
	
	ItemStack stack;
	
	public PacketSpawnItemCrack(Vec3d pos, Vec3d motion, ItemStack stack)
	{
		super(pos, motion);
		this.stack = stack;
	}
	
	public PacketSpawnItemCrack()
	{
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		super.write(buf);
		buf.writeItemStack(stack);
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		super.read(buf);
		stack = buf.readItemStack();
	}
	
	@Override
	protected void spawnParticle(World world, Vec3d pos, Vec3d motion)
	{
		TAReconstructed.proxy.getFX().spawnItemCrack(world, pos, motion, stack);
	}
}