package com.zeitheron.thaumicadditions.tiles;

import com.zeitheron.hammercore.tile.TileSyncable;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.api.EdibleAspect;
import com.zeitheron.thaumicadditions.items.ItemSaltEssence;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.AspectList;

public class TileArcaneCake extends TileSyncable
{
	public final AspectList aspects = new AspectList();
	
	public int getRGB()
	{
		return aspects.visSize() > 0 ? AspectUtil.getColor(aspects, true) : 0xFF0000;
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		aspects.writeToNBT(nbt, "Aspects");
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		aspects.readFromNBT(nbt, "Aspects");
	}
}