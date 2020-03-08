package com.zeitheron.thaumicadditions.tiles;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import com.zeitheron.hammercore.net.props.NetPropertyItemStack;
import com.zeitheron.hammercore.net.props.NetPropertyString;
import com.zeitheron.hammercore.tile.ITileDroppable;
import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.EnumRotation;
import com.zeitheron.thaumicadditions.api.seals.SealCombination;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.api.seals.SealManager;
import com.zeitheron.thaumicadditions.init.BlocksTAR;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

public class TileSeal extends TileSyncableTickable implements ITileDroppable, IGlowingEntity
{
	public final NetPropertyItemStack stack;
	private final NetPropertyString[] slots = new NetPropertyString[3];
	public EnumFacing orientation;
	public SealCombination combination;
	public SealInstance instance;
	public NBTTagCompound optInstNBT;
	public NetPropertyString placer;
	
	public boolean dirty = false;
	
	{
		stack = new NetPropertyItemStack(this, ItemStack.EMPTY);
		slots[0] = new NetPropertyString(this, null);
		slots[1] = new NetPropertyString(this, null);
		slots[2] = new NetPropertyString(this, null);
		placer = new NetPropertyString(this);
	}
	
	@Override
	public void createDrop(EntityPlayer player, World world, BlockPos pos)
	{
		if(!world.isRemote)
		{
			EntityItem ent = new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack.get().copy());
			double mod = .10000000149011612;
			ent.motionX = (player.posX - ent.posX) * mod;
			ent.motionY = (player.posY - ent.posY) * mod;
			ent.motionZ = (player.posZ - ent.posZ) * mod;
			world.spawnEntity(ent);
		}
		
		if(instance != null)
			instance.onSealBreak();
	}

	@Override
	public boolean atTickRate(int rate)
	{
		return ticksExisted % rate == 0;
	}

	public Aspect getSymbol(int slot)
	{
		return Aspect.getAspect(slots[slot].get());
	}
	
	@Override
	public ColoredLight produceColoredLight(float partialTicks)
	{
		return instance instanceof IGlowingEntity ? ((IGlowingEntity) instance).produceColoredLight(partialTicks) : null;
	}
	
	@Override
	public void markDirty()
	{
		dirty = true;
		super.markDirty();
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		if(instance != null)
			instance.readFromNBT(nbt.getCompoundTag("SealInstance"));
		else
			optInstNBT = nbt.getCompoundTag("SealInstance");
		dirty = true;
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		if(instance != null)
			nbt.setTag("SealInstance", instance.writeToNBT(new NBTTagCompound()));
	}
	
	public void setSymbol(int index, Aspect aspect)
	{
		if(aspect == null)
			slots[index].set(null);
		else
			slots[index].set(aspect.getTag());
		
		dirty = true;
		if(instance != null)
			instance.onSealBreak();
		instance = null;
		optInstNBT = null;
		combination = null;
	}
	
	@Override
	public void tick()
	{
		if(getLocation().getBlock() == BlocksTAR.SEAL)
			orientation = getLocation().getState().getValue(EnumRotation.EFACING);
		
		if(getLocation().getRedstone() > 0)
			--ticksExisted;
		
		if(dirty)
		{
			SealCombination oldCombo = combination;
			combination = SealManager.getCombination(this);
			if(combination != oldCombo)
			{
				SealInstance old = instance;
				instance = SealManager.makeInstance(this, combination, optInstNBT);
				if(old != null)
					old.onSealBreak();
			}
			optInstNBT = null;
			dirty = false;
		}
		
		if(instance != null)
			instance.tick();
		
		if(combination != null)
		{
			if(!combination.isValid(this))
			{
				combination = null;
				dirty = true;
			}
		} else if(atTickRate(20))
		{
			SealCombination oldCombo = combination;
			combination = SealManager.getCombination(this);
			if(combination != oldCombo)
				instance = SealManager.makeInstance(this, combination, null);
		}
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return super.getRenderBoundingBox().grow(4);
	}
}