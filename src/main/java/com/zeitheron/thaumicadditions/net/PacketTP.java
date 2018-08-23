package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.net.internal.PacketTeleportPlayer;
import com.zeitheron.thaumicadditions.utils.TP;
import com.zeitheron.thaumicadditions.utils.ThaumicScheduler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketTP implements IPacket
{
	static
	{
		IPacket.handle(PacketTP.class, PacketTP::new);
	}
	
	public Vec3d target;
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		IPacket.Helper.setVec3d(nbt, "Target", target);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		target = IPacket.Helper.getVec3d(nbt, "Target");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IPacket executeOnClient(PacketContext net)
	{
		if(TP.teleport(Minecraft.getMinecraft().player, target.x, target.y, target.z) > 100.)
			ThaumicScheduler.schedule(5, Minecraft.getMinecraft().renderGlobal::loadRenderers);
		return new PacketTeleportPlayer().withTarget(target);
	}
}