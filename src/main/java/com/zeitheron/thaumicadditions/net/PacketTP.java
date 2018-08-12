package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.net.internal.PacketTeleportPlayer;
import com.zeitheron.thaumicadditions.utils.TP;

import net.minecraft.client.Minecraft;
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
		TP.teleport(Minecraft.getMinecraft().player, target.x, target.y, target.z);
		return new PacketTeleportPlayer().withTarget(target);
	}
}