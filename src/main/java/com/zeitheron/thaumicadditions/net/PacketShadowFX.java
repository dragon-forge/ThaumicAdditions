package com.zeitheron.thaumicadditions.net;

import java.util.Random;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.hammercore.utils.math.MathHelper;
import com.zeitheron.thaumicadditions.tiles.TileShadowEnchanter;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;

public class PacketShadowFX implements IPacket
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
		float xr = (float) pos.getX() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * .5F;
		float yr = (float) pos.getY() + 0.7f + rand.nextFloat() * 0.1F;
		float zr = (float) pos.getZ() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.5F;
		
		float xm = ((pos.getX() + 0.5F) - xr) / 10F;
		float ym = ((pos.getY() + 0.75F) - yr) / 10F;
		float zm = ((pos.getZ() + 0.5F) - zr) / 10F;
		
		xm = MathHelper.clip(xm, -0.1F, 0.1F);
		ym = MathHelper.clip(ym, -0.1F, 0.1F);
		zm = MathHelper.clip(zm, -0.1F, 0.1F);
		
		FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, 0, 0, 0, 10, ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), 0.001F);
	}
}