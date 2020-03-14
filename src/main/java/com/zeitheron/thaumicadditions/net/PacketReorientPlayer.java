package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.thaumicadditions.utils.ThaumicScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MainThreaded
public class PacketReorientPlayer
		implements IPacket
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
	public void executeOnClient2(PacketContext net)
	{
		ThaumicScheduler.schedule(5, () -> Minecraft.getMinecraft().player.rotationYaw = yaw);
	}
}