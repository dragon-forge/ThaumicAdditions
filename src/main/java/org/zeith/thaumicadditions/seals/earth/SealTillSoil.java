package org.zeith.thaumicadditions.seals.earth;

import com.zeitheron.hammercore.utils.WorldLocation;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zeith.thaumicadditions.api.seals.SealInstance;
import org.zeith.thaumicadditions.tiles.TileSeal;
import thaumcraft.api.aura.AuraHelper;

import java.util.Random;

public class SealTillSoil
		extends SealInstance
{
	public SealTillSoil(TileSeal seal)
	{
		super(seal);
	}

	@Override
	public void tick()
	{
		if(seal.atTickRate(10))
		{
			WorldLocation loc = seal.getLocation();
			Random rand = loc.getWorld().rand;

			if(loc.getRedstone() > 0)
				return;

			EnumFacing to = seal.orientation;
			EnumFacing from = to.getOpposite();

			float mult = .7F;

			float modX = (rand.nextFloat() - rand.nextFloat()) * .5F;
			float modY = (rand.nextFloat() - rand.nextFloat()) * .5F;
			float modZ = (rand.nextFloat() - rand.nextFloat()) * .5F;

			AxisAlignedBB thus = loc.getState().getBoundingBox(loc.getWorld(), loc.getPos());
			Vec3d pos = new Vec3d(thus.minX + (thus.maxX - thus.minX) * 0.5D, thus.minY + (thus.maxY - thus.minY) * 0.5D, thus.minZ + (thus.maxZ - thus.minZ) * 0.5D).add(new Vec3d(loc.getPos()));
			pos = pos.add(from.getXOffset() * .05, from.getYOffset() * .05, from.getZOffset() * .05);
			Vec3d end = pos.add(to.getXOffset() * mult + modX, to.getYOffset() * mult + modY, to.getZOffset() * mult + modZ);

			BlockPos center0 = loc.getPos().offset(to, 4);
			if(to.getAxis() == Axis.Y)
				center0 = loc.getPos().offset(to, to == EnumFacing.UP ? 2 : 4);
			int rad = 5;
			int tries = 40;

			int driest = 7;
			WorldLocation drps = null;

			while(tries-- > 0)
			{
				WorldLocation l = new WorldLocation(loc.getWorld(), center0.add(rand.nextInt(rad) - rand.nextInt(rad), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(rad) - rand.nextInt(rad)));
				WorldLocation up = l.offset(EnumFacing.UP);

				if(l.getBlock() == Blocks.DIRT || l.getBlock() == Blocks.GRASS && up.getBlock().isAir(up.getState(), up.getWorld(), up.getPos()))
				{
					if(rand.nextInt(100) < 5)
						AuraHelper.polluteAura(seal.getWorld(), seal.getPos(), .1F, true);

					l.setState(Blocks.FARMLAND.getDefaultState());
//					HCNetwork.manager.sendToAllAround(new PacketFXWisp2(pos.x, pos.y, pos.z, end.x, end.y, end.z, .5F, 3), loc.getPointWithRad(48));

					int r = 8 + rand.nextInt(9);

					for(int i = 0; i < r; ++i)
					{
						Vec3d start = new Vec3d(l.getPos()).add(rand.nextDouble(), .8, rand.nextDouble());
						Vec3d end2 = start.add(0, .7 + rand.nextFloat() * .5F, 0);
//						HCNetwork.manager.sendToAllAround(new PacketFXWisp2(start.x, start.y, start.z, end2.x, end2.y, end2.z, 1F, 3), loc.getPointWithRad(48));
					}

					break;
				}
			}
		}
	}
}