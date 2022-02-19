package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.IPacket.Helper;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.net.internal.PacketTeleportPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.utils.TP;
import org.zeith.thaumicadditions.utils.ThaumicScheduler;

public class PacketTP
		implements IPacket
{
	static
	{
		IPacket.handle(PacketTP.class, PacketTP::new);
	}

	public Vec3d target;

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		Helper.setVec3d(nbt, "Target", target);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		target = Helper.getVec3d(nbt, "Target");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IPacket executeOnClient(PacketContext net)
	{
		if(TP.teleport(Minecraft.getMinecraft().player, target.x, target.y, target.z) > 100.)
			ThaumicScheduler.schedule(15, Minecraft.getMinecraft().renderGlobal::loadRenderers);
		return new PacketTeleportPlayer().withTarget(target);
	}
}