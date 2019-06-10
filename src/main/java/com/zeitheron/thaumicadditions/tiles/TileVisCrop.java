package com.zeitheron.thaumicadditions.tiles;

import com.zeitheron.hammercore.tile.TileSyncable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import thaumcraft.api.aspects.Aspect;

public class TileVisCrop extends TileSyncable
{
	private Aspect aspect;
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		if(aspect != null)
			nbt.setString("Aspect", aspect.getTag());
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("Aspect", NBT.TAG_STRING))
			aspect = Aspect.getAspect(nbt.getString("Aspect"));
		else
			aspect = null;
	}
	
	public Aspect getAspect()
	{
		return aspect;
	}
	
	public void setAspect(Aspect aspect)
	{
		this.aspect = aspect;
		if(world != null && !world.isRemote)
			sendChangesToNearby();
	}
}