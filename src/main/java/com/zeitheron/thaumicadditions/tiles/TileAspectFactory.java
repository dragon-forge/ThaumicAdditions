package com.zeitheron.thaumicadditions.tiles;

import java.util.List;
import java.util.Map;

import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.hammercore.lib.zlib.utils.IndexedMap;
import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.PositionedSearching;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.utils.AspectOperator;
import com.zeitheron.thaumicadditions.utils.AspectRule;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public class TileAspectFactory extends TileSyncableTickable implements IAspectContainer
{
	public final AspectList contained = new AspectList();
	public final AspectList toDissolve = new AspectList();
	public final IndexedMap<Aspect, AspectRule> rules = new IndexedMap<>();
	public PositionedSearching<TileJarFillable> jars;
	public AspectOperator currentOperation = null;
	public int speed = 40;
	
	@Override
	public void tick()
	{
		if(jars == null)
		{
			jars = new PositionedSearching<>(pos -> WorldUtil.cast(world.getTileEntity(pos), TileJarFillable.class), te -> te != null && !te.isInvalid(), TileJarFillable.class);
			jars.setCenter(pos);
			jars.setRadius(8, 3, 8);
		}
		
		jars.update(4);
		
		if(atTickRate(20))
		{
			// Try to fill the jars with the vis
			AspectList left = addAspects(jars.located, contained, rules);
			contained.aspects.clear();
			contained.add(left);
			
			AspectList al = getAspects(jars.located);
			
			AspectRule ar;
			for(Aspect a : al.getAspectsSortedByAmount())
				if((ar = rules.get(a)) != null)
				{
					int amt = al.getAmount(a);
					
					if(amt > ar.max)
					{
						boolean cd = !a.isPrimal();
						
						if(ar.voidExcess || cd)
						{
							int overfill = amt - ar.max;
							int newOverfill = takeAspect(jars.located, a, overfill);
							int taken = overfill - newOverfill;
							if(cd)
								toDissolve.add(a, taken);
						}
					} else if(amt < ar.min && !a.isPrimal())
					{
						
					}
				}
		}
		
		if(currentOperation == null)
		{
			if(toDissolve != null && toDissolve.visSize() > 0)
			{
				Aspect[] as = toDissolve.getAspectsSortedByAmount();
				if(as != null && as.length > 0)
				{
					Aspect dissolven = as[as.length - 1];
					toDissolve.remove(dissolven, 1);
					currentOperation = new AspectOperator(speed, dissolven);
				}
			}
		}
		
		if(currentOperation != null)
		{
			AspectList al = currentOperation.update();
			if(al != null)
			{
				currentOperation = null;
				contained.add(al);
			}
		}
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		contained.writeToNBT(nbt, "Aspects");
		toDissolve.writeToNBT(nbt, "Disolve");
		if(currentOperation != null)
			nbt.setTag("CurrentOperation", currentOperation.write());
		
		NBTTagList list = new NBTTagList();
		for(Aspect a : rules.getKeys())
		{
			AspectRule ar = rules.get(a);
			if(ar != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("Aspect", a.getTag());
				ar.write(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Rules", list);
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		contained.readFromNBT(nbt, "Aspects");
		toDissolve.readFromNBT(nbt, "Disolve");
		if(nbt.hasKey("CurrentOperation"))
			currentOperation = AspectOperator.read(nbt.getCompoundTag("CurrentOperation"));
		
		rules.clear();
		NBTTagList list = nbt.getTagList("Rules", NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			Aspect a = Aspect.getAspect(tag.getString("Aspect"));
			if(a != null)
				rules.put(a, new AspectRule().read(tag));
		}
	}
	
	@Override
	public AspectList getAspects()
	{
		return contained.copy().add(toDissolve);
	}
	
	@Override
	public void setAspects(AspectList aspects)
	{
	}
	
	@Override
	public boolean doesContainerAccept(Aspect tag)
	{
		return false;
	}
	
	@Override
	public int addToContainer(Aspect tag, int amount)
	{
		return amount;
	}
	
	@Override
	public boolean takeFromContainer(Aspect tag, int amount)
	{
		return false;
	}
	
	@Override
	public boolean takeFromContainer(AspectList ot)
	{
		return false;
	}
	
	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount)
	{
		return contained.getAmount(tag) >= amount;
	}
	
	@Override
	public boolean doesContainerContain(AspectList ot)
	{
		for(Aspect a : ot.aspects.keySet())
			if(!doesContainerContainAmount(a, ot.getAmount(a)))
				return false;
		return true;
	}
	
	@Override
	public int containerContains(Aspect tag)
	{
		return contained.getAmount(tag);
	}
	
	public static AspectList getAspects(List<TileJarFillable> jars)
	{
		AspectList al = new AspectList();
		for(TileJarFillable tjf : jars)
			if(tjf.aspect != null && tjf.amount > 0)
				al.add(tjf.aspect, tjf.amount);
		return al;
	}
	
	public static int takeAspect(List<TileJarFillable> jars, Aspect a, int amount)
	{
		for(TileJarFillable tjf : jars)
			if(tjf.aspect != null && tjf.amount > 0)
			{
				int aboutToTake = Math.min(tjf.amount, amount);
				tjf.amount -= aboutToTake;
				amount -= aboutToTake;
				if(tjf.amount <= 0)
				{
					tjf.aspect = null;
					tjf.amount = 0;
				}
				if(amount <= 0)
					return 0;
			}
		return amount;
	}
	
	/**
	 * Optimized to iterate once for all aspects instead of 1 iteration for each
	 * aspect.
	 */
	public static AspectList takeAspects(List<TileJarFillable> jars, AspectList list)
	{
		list = list.copy();
		for(TileJarFillable tjf : jars)
			if(tjf.aspect != null && tjf.amount > 0 && list.getAmount(tjf.aspect) > 0)
			{
				int aboutToTake = Math.min(tjf.amount, list.getAmount(tjf.aspect));
				tjf.amount -= aboutToTake;
				list.remove(tjf.aspect, aboutToTake);
				if(tjf.amount <= 0)
				{
					tjf.aspect = null;
					tjf.amount = 0;
				}
			}
		return list;
	}
	
	public static int addAspect(List<TileJarFillable> jars, Aspect a, int amount, AspectRule rule)
	{
		for(TileJarFillable tjf : jars)
		{
			int left = tjf.addToContainer(a, amount);
			amount = left;
			if(left <= 0)
				break;
		}
		return (rule != null && rule.voidExcess) ? 0 : amount;
	}
	
	public static AspectList addAspects(List<TileJarFillable> jars, AspectList list, Map<Aspect, AspectRule> rules)
	{
		list = list.copy();
		
		fill:
		{
			for(TileJarFillable tjf : jars)
			{
				if(tjf.aspect != null && list.getAmount(tjf.aspect) > 0)
				{
					int amount = list.getAmount(tjf.aspect);
					int left = tjf.addToContainer(tjf.aspect, amount);
					int used = amount - left;
					list.remove(tjf.aspect, used);
				}
				
				if(list.visSize() <= 0)
					break fill;
			}
			
			AspectRule ar;
			for(Aspect key : list.aspects.keySet())
				if((ar = rules.get(key)) != null && ar.voidExcess)
					list.aspects.remove(key);
				
			for(Aspect a : list.getAspectsSortedByAmount())
			{
				int pval = list.getAmount(a);
				int val = addAspect(jars, a, pval, rules.get(a));
				int use = pval - val;
				list.remove(a, use);
			}
		}
		
		return list;
	}
}