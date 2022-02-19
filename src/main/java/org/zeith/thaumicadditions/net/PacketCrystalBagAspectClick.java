package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.zeith.thaumicadditions.inventory.container.ContainerCrystalBag;
import thaumcraft.api.aspects.Aspect;

@MainThreaded
public class PacketCrystalBagAspectClick
		implements IPacket
{
	Aspect a;
	boolean shift;
	int btn;

	public static PacketCrystalBagAspectClick create(Aspect a, boolean shift, int button)
	{
		PacketCrystalBagAspectClick p = new PacketCrystalBagAspectClick();
		p.a = a;
		p.shift = shift;
		p.btn = button;
		return p;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if(a != null)
			nbt.setString("a", a.getTag());
		nbt.setBoolean("s", shift);
		nbt.setInteger("b", btn);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		a = Aspect.getAspect(nbt.getString("a"));
		shift = nbt.getBoolean("s");
		btn = nbt.getInteger("b");
	}

	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP mp = net.getSender();
		if(mp != null && mp.openContainer instanceof ContainerCrystalBag)
		{
			ContainerCrystalBag bag = (ContainerCrystalBag) mp.openContainer;
			bag.onAspectClick(mp, a, shift, btn);
		}
	}
}