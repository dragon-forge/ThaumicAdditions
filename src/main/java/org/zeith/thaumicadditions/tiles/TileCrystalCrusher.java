package org.zeith.thaumicadditions.tiles;

import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.FrictionRotator;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.init.ItemsTAR;
import org.zeith.thaumicadditions.inventory.container.ContainerCrystalCrusher;
import org.zeith.thaumicadditions.inventory.gui.GuiCrystalCrusher;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.events.EssentiaHandler;

import java.util.Objects;
import java.util.Random;

public class TileCrystalCrusher
		extends TileSyncableTickable
		implements ISidedInventory
{
	public final FrictionRotator rotator = new FrictionRotator();
	private final int[] slots = new int[] {
			0,
			1
	};
	public InventoryDummy inv = new InventoryDummy(2);
	public int crushes, craftTime;
	
	{
		rotator.degree = new Random().nextFloat() * 360;
	}
	
	@Override
	public void tick()
	{
		rotator.friction = .5F;
		if(world.isRemote)
			rotator.update();
		
		if(world.getRedstonePowerFromNeighbors(pos) > 0)
			return;
		
		if(!world.isRemote && crushes <= 0 && EssentiaHandler.drainEssentia(this, Aspect.MECHANISM, null, 12, 1))
		{
			crushes += 3;
			sync();
		}
		
		if(canCraft())
		{
			++craftTime;
			if(world.isRemote)
			{
				rotator.speedup(1F);
				
				TAReconstructed.proxy.getFX().spawnItemCrack(world,
						new Vec3d(pos.getX() + .1 + getRNG().nextFloat() * .8, pos.getY() + 4 / 16D, pos.getZ() + .1 + getRNG().nextFloat() * .8),
						new Vec3d((getRNG().nextFloat() - getRNG().nextFloat()) * .3, 0, (getRNG().nextFloat() - getRNG().nextFloat()) * .3),
						inv.getStackInSlot(0)
				);
			}
			if(!world.isRemote && atTickRate(10))
				sendChangesToNearby();
			if(!world.isRemote && craftTime >= 100)
				craft();
		} else if(craftTime > 0)
			--craftTime;
	}
	
	public boolean canCraft()
	{
		if(crushes <= 0) return false;
		ItemStack in = inv.getStackInSlot(0);
		if(in.isEmpty() || in.getItem() != ItemsTC.crystalEssence) return false;
		ItemStack out = inv.getStackInSlot(1);
		return out.isEmpty()
			   || (out.getItem() == ItemsTAR.SALT_ESSENCE
				   && Objects.equals(in.getTagCompound(), out.getTagCompound())
				   && out.getCount() < out.getMaxStackSize()
			   );
	}
	
	public void craft()
	{
		NBTTagCompound nbt = inv.getStackInSlot(0).getTagCompound();
		if(nbt != null)
			nbt = nbt.copy();
		
		if(inv.getStackInSlot(1).isEmpty())
		{
			inv.getStackInSlot(0).shrink(1);
			ItemStack stack = new ItemStack(ItemsTAR.SALT_ESSENCE);
			stack.setTagCompound(nbt);
			inv.setInventorySlotContents(1, stack);
			crushes--;
		} else if(inv.getStackInSlot(1).getItem() == ItemsTAR.SALT_ESSENCE && Objects.equals(inv.getStackInSlot(0).getTagCompound(), inv.getStackInSlot(1).getTagCompound()))
		{
			inv.getStackInSlot(0).shrink(1);
			inv.getStackInSlot(1).grow(1);
			crushes--;
		}
		
		craftTime = 0;
		sync();
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Fuel", crushes);
		nbt.setInteger("CraftTime", craftTime);
		nbt.setTag("Items", inv.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		crushes = nbt.getInteger("Fuel");
		craftTime = nbt.getInteger("CraftTime");
		inv.readFromNBT(nbt.getCompoundTag("Items"));
	}
	
	@Override
	public boolean hasGui()
	{
		return true;
	}
	
	@Override
	public Object getClientGuiElement(EntityPlayer player)
	{
		return new GuiCrystalCrusher(player, this);
	}
	
	@Override
	public Object getServerGuiElement(EntityPlayer player)
	{
		return new ContainerCrystalCrusher(player, this);
	}
	
	@Override
	public int getSizeInventory()
	{
		return inv.getSizeInventory();
	}
	
	@Override
	public boolean isEmpty()
	{
		return inv.isEmpty();
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return inv.getStackInSlot(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return inv.decrStackSize(index, count);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return inv.removeStackFromSlot(index);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		inv.setInventorySlotContents(index, stack);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return inv.getInventoryStackLimit();
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return inv.isUsableByPlayer(player, pos);
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
		return index == 0 && stack.getItem() == ItemsTC.crystalEssence;
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
		inv.clear();
	}
	
	@Override
	public String getName()
	{
		return "";
	}
	
	@Override
	public boolean hasCustomName()
	{
		return false;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return slots;
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		return isItemValidForSlot(index, itemStackIn);
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return index == 1;
	}
}