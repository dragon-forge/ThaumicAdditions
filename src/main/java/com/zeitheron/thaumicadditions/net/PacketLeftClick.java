package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.items.ILeftClickItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@MainThreaded
public class PacketLeftClick
		implements IPacket
{
	boolean val;

	public PacketLeftClick(boolean val)
	{
		this.val = val;
	}

	public PacketLeftClick()
	{
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("b", val);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		val = nbt.getBoolean("b");
	}

	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP mp = net.getSender();
		if(mp != null)
		{
			TAReconstructed.getPlayerTag(mp).setBoolean("LeftClick", val);
			if(val)
			{
				ItemStack stack = mp.getHeldItemMainhand();
				if(!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem)
					((ILeftClickItem) stack.getItem()).onLeftClick(stack, mp);
			}
		}
	}
}