package org.zeith.thaumicadditions.tiles.jars;

import net.minecraft.util.EnumFacing;
import org.zeith.thaumicadditions.tiles.TileAbstractJarFillable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;

public class TileMithminiteJar
		extends TileAbstractJarFillable
{
	@Override
	public int getCapacity()
	{
		return 4005;
	}
	
	@Override
	public boolean voidsExcess()
	{
		return true;
	}
	
	@Override
	public int addToContainer(Aspect tt, int am)
	{
		if(aspect == null)
		{
			aspect = tt;
			amount = am;
		}
		boolean up = this.amount < getCapacity();
		if(am == 0)
			return am;
		if(tt == this.aspect || this.amount == 0)
		{
			this.aspect = tt;
			this.amount += am;
			am = 0;

			int overfill = amount - 4000;
			if(overfill > 0)
			{
				if(this.world.rand.nextInt(250 - overfill) == 0)
					AuraHelper.polluteAura(getWorld(), getPos(), 1F, true);
				amount -= overfill;
			}
		}
		if(up)
		{
			syncTile(false);
			markDirty();
		}
		return am;
	}

	@Override
	public int getMinimumSuction()
	{
		return aspectFilter != null ? 48 : 32;
	}

	@Override
	public int getSuctionAmount(EnumFacing loc)
	{
		if(aspectFilter != null && amount < 4000)
			return 48;
		return 32;
	}
}