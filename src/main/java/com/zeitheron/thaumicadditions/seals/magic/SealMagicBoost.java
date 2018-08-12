package com.zeitheron.thaumicadditions.seals.magic;

import java.util.Random;

import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.thaumicadditions.api.seals.SealCombination;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;

public class SealMagicBoost extends SealInstance
{
	public static class MagicBoostSealCombination extends SealCombination
	{
		public MagicBoostSealCombination()
		{
			super(null, null, null);
		}
		
		@Override
		public boolean isValid(final TileSeal seal)
		{
			final Aspect s = Aspect.MAGIC;
			final Aspect i1 = seal.getSymbol(0);
			final Aspect i2 = seal.getSymbol(1);
			final Aspect i3 = seal.getSymbol(2);
			return i1 == s && i2 == null && i3 == null || i1 == s && i2 == s && i3 == null || i1 == s && i2 == s && i3 == s;
		}
		
		@Override
		public String toString()
		{
			String m = Aspect.MAGIC.getName();
			return m + ", ~" + m + ", ~" + m;
		}
	}
	
	public int cooldown;
	
	public SealMagicBoost(final TileSeal seal)
	{
		super(seal);
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound nbt)
	{
		cooldown = nbt.getInteger("Cooldown");
	}
	
	@Override
	public void tick()
	{
		if(seal.getWorld().isRemote)
			return;
		if(cooldown > 0)
			--cooldown;
		if(seal.getLocation().getRedstone() > 0)
			return;
		int delay = 20;
		final Aspect s = Aspect.MAGIC;
		final Aspect i1 = seal.getSymbol(0);
		final Aspect i2 = seal.getSymbol(1);
		final Aspect i3 = seal.getSymbol(2);
		if(i1 == s && i2 == null && i3 == null)
			delay = 20;
		if(i1 == s && i2 == s && i3 == null)
			delay = 15;
		if(i1 == s && i2 == s && i3 == s)
			delay = 10;
		if(cooldown <= 0)
		{
			final boolean worked = AuraHelper.getAuraBase(seal.getWorld(), seal.getPos()) * 1.5 < AuraHelper.getVis(seal.getWorld(), seal.getPos());
			
			if(worked)
			{
				AuraHelper.addVis(seal.getWorld(), seal.getPos(), seal.getRNG().nextFloat());
				cooldown = delay;
				final WorldLocation loc = seal.getLocation();
				final Random rand = loc.getWorld().rand;
				final EnumFacing to = seal.orientation;
				final EnumFacing from = to.getOpposite();
				final float mult = 0.7f;
				final float modX = (rand.nextFloat() - rand.nextFloat()) * 0.5f;
				final float modY = (rand.nextFloat() - rand.nextFloat()) * 0.5f;
				final float modZ = (rand.nextFloat() - rand.nextFloat()) * 0.5f;
				AxisAlignedBB thus = loc.getState().getBoundingBox(loc.getWorld(), loc.getPos());
				
				Vec3d pos = new Vec3d(thus.minX + (thus.maxX - thus.minX) * 0.5D, thus.minY + (thus.maxY - thus.minY) * 0.5D, thus.minZ + (thus.maxZ - thus.minZ) * 0.5D).add(new Vec3d(loc.getPos()));
				pos = pos.add(from.getXOffset() * 0.05, from.getYOffset() * 0.05, from.getZOffset() * 0.05);
				final Vec3d end = pos.add(to.getXOffset() * mult + modX, to.getYOffset() * mult + modY, to.getZOffset() * mult + modZ);
				
				// HCNetwork.manager.sendToAllAround(new PacketFXWisp2(pos.x,
				// pos.y, pos.z, end.x, end.y, end.z, 0.5f, 0),
				// loc.getPointWithRad(48));
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbt)
	{
		nbt.setInteger("Cooldown", cooldown);
		return nbt;
	}
}