package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.items.weapons.ItemShadowBeamStaff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class PacketSyncRotationAndShootShadowBeamStaff implements IPacket
{
	float pitch, yaw;
	EnumHand hand;
	
	public static PacketSyncRotationAndShootShadowBeamStaff create(EntityPlayer player, EnumHand hand)
	{
		PacketSyncRotationAndShootShadowBeamStaff p = new PacketSyncRotationAndShootShadowBeamStaff();
		p.pitch = player.rotationPitch;
		p.yaw = player.rotationYaw;
		p.hand = hand;
		return p;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setFloat("p", pitch);
		nbt.setFloat("y", yaw);
		nbt.setBoolean("h", hand == EnumHand.MAIN_HAND);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		pitch = nbt.getFloat("p");
		yaw = nbt.getFloat("y");
		hand = nbt.getBoolean("h") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP mp = net.getSender();
		if(mp != null)
		{
			mp.rotationYaw = yaw;
			mp.rotationPitch = pitch;
			ItemStack held = mp.getHeldItem(hand);
			if(!held.isEmpty() && held.getItem() instanceof ItemShadowBeamStaff)
				ItemsTAR.SHADOW_BEAM_STAFF.handleRightClick(mp, hand);
		}
	}
}