package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@MainThreaded
public class PacketSyncEntity
		implements IPacket
{
	private UUID uuid;
	private NBTTagCompound nbt;
	private int id;

	public PacketSyncEntity()
	{
	}

	public PacketSyncEntity(Entity entity)
	{
		uuid = entity.getUniqueID();
		id = entity.getEntityId();
		nbt = entity.writeToNBT(new NBTTagCompound());
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