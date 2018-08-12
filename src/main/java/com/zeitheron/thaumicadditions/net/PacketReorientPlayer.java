package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketReorientPlayer implements IPacket
{
	static
	{
		IPacket.handle(PacketReorientPlayer.class, PacketReorientPlayer::new);
	}
	
	public float yaw;
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setFloat("Yaw", yaw);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		yaw = nbt.getFloat("Yaw");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IPacket executeOnClient(PacketContext net)
	{
		Minecraft.getMinecraft().player.rotationYaw = yaw;
		return null;
	}
}