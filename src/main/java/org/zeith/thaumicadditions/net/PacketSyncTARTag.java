package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.TAReconstructed;

import java.util.UUID;

public class PacketSyncTARTag
		implements IPacket
{
	NBTTagCompound tag;
	UUID id;

	public PacketSyncTARTag(EntityPlayerMP player)
	{
		this.tag = TAReconstructed.getPlayerTag(player);
		this.id = player.getUniqueID();
	}

	public PacketSyncTARTag()
	{
	}

	public static void sync(EntityPlayerMP mp)
	{
		HCNet.INSTANCE.sendToAllAround(new PacketSyncTARTag(mp), new TargetPoint(mp.world.provider.getDimension(), mp.posX, mp.posY, mp.posZ, 64));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("t", tag);
		nbt.setUniqueId("u", id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		tag = nbt.getCompoundTag("t");
		id = nbt.getUniqueId("u");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		WorldClient wc = Minecraft.getMinecraft().world;
		if(wc != null)
		{
			EntityPlayer player = wc.getPlayerEntityByUUID(id);
			if(player != null) TAReconstructed.setPlayerTag(player, tag);
		}
	}
}