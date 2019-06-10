package com.zeitheron.thaumicadditions.seals.tool;

import java.util.Random;

import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.IPlantable;
import thaumcraft.api.aura.AuraHelper;

public class SealHarvestCrops2 extends SealInstance
{
	public SealHarvestCrops2(TileSeal seal)
	{
		super(seal);
	}
	
	@Override
	public void tick()
	{
		if(!seal.atTickRate(40))
			return;
		
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
			
			IGrowable grow = WorldUtil.cast(l.getBlock(), IGrowable.class);
			
			if(!l.getWorld().isRemote && grow != null && !grow.canGrow(l.getWorld(), l.getPos(), l.getState(), l.getWorld().isRemote))
			{
				drps = l;
				
				IBlockState state = null;
				NonNullList<ItemStack> drops = NonNullList.create();
				
				int attempts = 20;
				glob: while(--attempts > 0)
				{
					drops.clear();
					l.getBlock().getDrops(drops, l.getWorld(), l.getPos(), l.getState(), 0);
					for(ItemStack i : drops)
						if(i.getItem() instanceof IPlantable)
						{
							IPlantable plant = (IPlantable) i.getItem();
							state = plant.getPlant(l.getWorld(), l.getPos());
							i.shrink(1);
							break glob;
						}
				}
				
				TileEntity te = l.getTile();
				
				l.destroyBlock(false);
				
				if(state != null)
				{
					l.setState(state, 3);
					if(te != null)
					{
						te.validate();
						l.setTile(te);
					}
				}
				
				for(ItemStack i : drops)
					if(!i.isEmpty())
						WorldUtil.spawnItemStack(l, i);
					
				break;
			}
		}
		
		if(drps != null)
		{
			WorldLocation l = drps;
			
			if(rand.nextInt(100) < 5)
				AuraHelper.polluteAura(seal.getWorld(), seal.getPos(), .1F, true);
				
			// HCNetwork.manager.sendToAllAround(new PacketFXWisp2(pos.x, pos.y,
			// pos.z, end.x, end.y, end.z, .5F, 2), loc.getPointWithRad(48));
			
			int r = 8 + rand.nextInt(9);
			
			for(int i = 0; i < r; ++i)
			{
				Vec3d start = new Vec3d(l.getPos()).add(rand.nextDouble(), .8, rand.nextDouble());
				Vec3d end2 = start.add(0, .7 + rand.nextFloat() * .5F, 0);
				// HCNetwork.manager.sendToAllAround(new PacketFXWisp2(start.x,
				// start.y, start.z, end2.x, end2.y, end2.z, 1F, 2),
				// loc.getPointWithRad(48));
			}
		}
	}
}