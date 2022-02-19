package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatAllowedCharacters;
import org.zeith.thaumicadditions.inventory.container.ContainerRepairVoid;

@MainThreaded
public class PacketSetVoidItemName
		implements IPacket
{
	String name;

	public static PacketSetVoidItemName create(String name)
	{
		PacketSetVoidItemName p = new PacketSetVoidItemName();
		p.name = name;
		return p;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("s", name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		name = nbt.getString("s");
	}

	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP player = net.getSender();
		if(player.openContainer instanceof ContainerRepairVoid)
		{
			ContainerRepairVoid containerrepair = (ContainerRepairVoid) player.openContainer;

			if(name != null && name.length() > 0)
			{
				String s5 = ChatAllowedCharacters.filterAllowedCharacters(name);

				if(s5.length() <= 35)
				{
					containerrepair.updateItemName(s5);
				}
			} else
			{
				containerrepair.updateItemName("");
			}
		}
	}
}