package org.zeith.thaumicadditions.tiles;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import com.zeitheron.hammercore.internal.blocks.base.IBlockEnableable;
import com.zeitheron.hammercore.net.props.NetPropertyString;
import com.zeitheron.hammercore.tile.TileSyncable;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.blocks.decor.BlockAmberLamp;
import org.zeith.thaumicadditions.init.BlocksTAR;
import thaumcraft.api.aspects.Aspect;

import java.util.Random;

public class TileCrystalLamp
		extends TileSyncable
		implements IGlowingEntity, IInventory
{
	public final NetPropertyString crystal;

	public TileCrystalLamp()
	{
		this.crystal = new NetPropertyString(this, "");
	}

	public Aspect getAspect()
	{
		if(getLocation().getRedstone() > 0)
			return null;
		String s = crystal.get();
		return s.isEmpty() ? null : Aspect.getAspect(s);
	}

	public Aspect setAspect(Aspect a)
	{
		Aspect pr = getAspect();
		if(a != null)
		{
			crystal.set(a.getTag());
			inventory.setInventorySlotContents(0, AspectUtil.crystalEssence(a, 1));
		} else
		{
			crystal.set("");
			inventory.setInventorySlotContents(0, ItemStack.EMPTY);
		}
		return pr;
	}

	static final Random rand = new Random();

	@Override
	public ColoredLight produceColoredLight(float partialTicks)
	{
		IBlockState state = world.getBlockState(pos);
		Aspect as;
		if(state.getBlock() != BlocksTAR.CRYSTAL_LAMP || !state.getValue(IBlockEnableable.ENABLED) || (as = getAspect()) == null)
			return null;

		double time = (System.currentTimeMillis() % 10_000_000L) / 50D;
		rand.setSeed(pos.toLong());
		time += rand.nextInt(10_000_000) / 50D;

		float prog = (float) (Math.sin(time / 6D) + 1F) / 2F;

		int color = as.getColor();

		float rad = (float) (BlockAmberLamp.BASE_SIMPLEX.getValue(time / 128D, 0) * 0.5F + 2.25F) * 6F;
		return ColoredLight.builder().pos(pos).color(color, false).radius(rad).build();
	}

	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		inventory.writeToNBT(nbt);
	}

	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		inventory.readFromNBT(nbt);
	}

	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	public final InventoryDummy inventory = new InventoryDummy(1);

	{
		inventory.inventoryStackLimit = 1;
	}

	@Override
	public boolean isEmpty()
	{
		return inventory.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return inventory.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return inventory.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		inventory.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return false;
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		inventory.clear();
	}

	@Override
	public String getName()
	{
		return inventory.getName();
	}

	@Override
	public boolean hasCustomName()
	{
		return inventory.hasCustomName();
	}
}