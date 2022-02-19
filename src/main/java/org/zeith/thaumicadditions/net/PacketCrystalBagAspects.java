package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.inventory.gui.GuiCrystalBag;
import thaumcraft.api.aspects.AspectList;

@MainThreaded
public class PacketCrystalBagAspects
		implements IPacket
{
	AspectList list;

	public static PacketCrystalBagAspects create(AspectList aspects)
	{
		PacketCrystalBagAspects p = new PacketCrystalBagAspects();
		p.list = aspects;
		return p;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		list.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		list = new AspectList();
		list.readFromNBT(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		GuiCrystalBag bag = Cast.cast(Minecraft.getMinecraft().currentScreen, GuiCrystalBag.class);
		if(bag != null)
			bag.getContainer().aspects = list;
	}
}