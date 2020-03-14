package com.zeitheron.thaumicadditions.net;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MainThreaded
public class PacketBlockEvent
		implements IPacket
{
	public int id, type;
	public long pos;

	static
	{
		IPacket.handle(PacketBlockEvent.class, PacketBlockEvent::new);
	}

	public static void performBlockEvent(World world, BlockPos pos, int id, int type)
	{
		if(world.isRemote)
			world.addBlockEvent(pos, world.getBlockState(pos).getBlock(), id, type);
		else
		{
			PacketBlockEvent bpe = new PacketBlockEvent();
			bpe.id = id;
			bpe.type = type;
			bpe.pos = pos.toLong();
			HCNet.INSTANCE.sendToAllAround(bpe, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		EntityPlayer player = HammerCore.renderProxy.getClientPlayer();
		if(player != null)
		{
			World wc = player.world;
			BlockPos pos = BlockPos.fromLong(this.pos);
			wc.addBlockEvent(pos, wc.getBlockState(pos).getBlock(), id, type);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("a", id);
		nbt.setInteger("b", type);
		nbt.setLong("c", pos);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		id = nbt.getInteger("a");
		type = nbt.getInteger("b");
		pos = nbt.getLong("c");
	}
}