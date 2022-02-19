package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.tiles.TileShadowEnchanter;
import thaumcraft.client.fx.FXDispatcher;

import java.util.Random;

public class PacketShadowFX
		implements IPacket
{
	private long pos;
	private int color;

	public static PacketShadowFX create(TileShadowEnchanter tile, int color)
	{
		PacketShadowFX fx = new PacketShadowFX();
		fx.pos = tile.getPos().toLong();
		fx.color = color;
		return fx;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setLong("p", pos);
		nbt.setInteger("c", color);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		pos = nbt.getLong("p");
		color = nbt.getInteger("c");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		BlockPos pos = BlockPos.fromLong(this.pos);
		Random rand = ThreadLocalRandom.current();
		Vec3d center = new Vec3d(pos.getX() + .5F, pos.getY() + 1F, pos.getZ() + .5F);
		Vec3d point = center.add((rand.nextFloat() - rand.nextFloat()) * .5F, (rand.nextFloat() - rand.nextFloat()) * .75F, (rand.nextFloat() - rand.nextFloat()) * .5F);
		Vec3d move = center.subtract(point).scale(0.05);
		FXDispatcher.INSTANCE.drawWispyMotes(point.x, point.y, point.z, move.x, move.y, move.z, 40, ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), 0.001F);
	}
}