package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.thaumicadditions.entity.EntityChester;
import com.zeitheron.thaumicadditions.inventory.gui.GuiChester;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@MainThreaded
public class PacketSyncTrunk
		implements IPacket
{
	private UUID uuid;
	private NBTTagCompound nbt;
	private int id;

	public PacketSyncTrunk()
	{
	}

	public PacketSyncTrunk(Entity entity)
	{
		uuid = entity.getUniqueID();
		id = entity.getEntityId();
		nbt = entity.writeToNBT(new NBTTagCompound());
		nbt.removeTag("Pos");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IPacket executeOnClient(PacketContext net)
	{
		Entity e = Minecraft.getMinecraft().world.getEntityByID(id);
		if(e != null)
		{
			NBTTagList nbttaglist = new NBTTagList();
			for(double d0 : new double[]{
					e.posX,
					e.posY,
					e.posZ
			})
				nbttaglist.appendTag(new NBTTagDouble(d0));
			nbt.setTag("Pos", nbttaglist);
			e.readFromNBT(nbt);
		}

		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			if(e instanceof EntityChester)
			{
				GuiChester gui;
				Minecraft.getMinecraft().displayGuiScreen(gui = new GuiChester((EntityChester) e, Minecraft.getMinecraft().player));
				Minecraft.getMinecraft().player.openContainer = gui.inventorySlots;
			}
		});

		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		uuid = nbt.getUniqueId("p1");
		this.nbt = nbt.getCompoundTag("p2");
		id = nbt.getInteger("p3");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setUniqueId("p1", uuid);
		nbt.setTag("p2", this.nbt);
		nbt.setInteger("p3", id);
	}
}