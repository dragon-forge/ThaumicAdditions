package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

import java.util.Random;

@EventBusSubscriber
public class PacketTransfurmWolf
		implements IPacket
{
	static IntList wolves = new IntArrayList();
	int id;

	public PacketTransfurmWolf(EntityWolf wolf)
	{
		id = wolf.getEntityId();
	}

	public PacketTransfurmWolf()
	{
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void clientTick(ClientTickEvent e)
	{
		if(e.phase == Phase.START)
		{
			wolves.removeIf(id ->
			{
				Entity ent = Minecraft.getMinecraft().world.getEntityByID(id);
				if(ent instanceof EntityWolf)
				{
					Random rng = ((EntityWolf) ent).getRNG();
					AxisAlignedBB aabb = ent.getEntityBoundingBox().grow(0.125F);
					int amt = 2 + rng.nextInt(10);
					for(int i = 0; i < amt; ++i)
						FXDispatcher.INSTANCE.spark((float) (aabb.minX + (aabb.maxX - aabb.minX) * rng.nextFloat()), (float) (aabb.minY + (aabb.maxY - aabb.minY) * rng.nextFloat()), (float) (aabb.minZ + (aabb.maxZ - aabb.minZ) * rng.nextFloat()), 1F, 0.1F, 1F, 1F, 1F);
				}
				return ent == null || ent.isDead;
			});
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("i", id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		id = nbt.getInteger("i");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		wolves.add(id);
	}
}