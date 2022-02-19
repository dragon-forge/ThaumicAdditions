package org.zeith.thaumicadditions.tiles;

import com.zeitheron.hammercore.tile.ITileDroppable;
import com.zeitheron.hammercore.tile.TileSyncable;
import com.zeitheron.hammercore.utils.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import org.zeith.thaumicadditions.api.AspectUtil;
import thaumcraft.api.aspects.Aspect;

import java.util.Map;
import java.util.Random;

public class TileCrystalBlock
		extends TileSyncable
		implements ITileDroppable
{
	private Aspect aspect;

	public Aspect getAspect()
	{
		if(aspect == null)
		{
			Aspect[] asps = Aspect.aspects.values().toArray(new Aspect[0]);
			aspect = asps[new Random().nextInt(asps.length)];
		}

		return aspect;
	}

	public void setAspect(Aspect aspect)
	{
		this.aspect = aspect;
		getAspect();
		sendChangesToNearby();
	}

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
		getAspect();
	}

	@Override
	public void createDrop(EntityPlayer player, World world, BlockPos pos)
	{
		if(!world.isRemote && (player == null || !player.capabilities.isCreativeMode) && aspect != null)
			WorldUtil.spawnItemStack(world, pos, AspectUtil.crystalEssence(getAspect(), 9));
	}

	@Override
	public void addProperties(Map<String, Object> properties, RayTraceResult trace)
	{
		properties.put("Aspect", getAspect().getTag());
	}
}