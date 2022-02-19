package org.zeith.thaumicadditions.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.zeith.thaumicadditions.entity.EntityChester;

public class ContainerChester
		extends Container
{
	public final EntityChester chester;

	public ContainerChester(EntityChester chester, EntityPlayer player)
	{
		this.chester = chester;

		for(int l = 0; l < 9; ++l)
			addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 168));

		for(int k = 0; k < 3; ++k)
			for(int j1 = 0; j1 < 9; ++j1)
				addSlotToContainer(new Slot(player.inventory, j1 + k * 9 + 9, 8 + j1 * 18, 110 + k * 18));

		for(int j = 0; j < chester.inventory.getSizeInventory() / 9; ++j)
			for(int i1 = 0; i1 < 9; ++i1)
				addSlotToContainer(new Slot(chester.inventory, i1 + j * 9, 8 + i1 * 18, 17 + j * 18));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return chester.inventory.isUsableByPlayer(playerIn, chester.getPosition());
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = getSlot(index);
		if(slot != null)
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if(index >= 0 && index < 36 && !mergeItemStack(itemstack1, 36, inventorySlots.size(), false))
				return ItemStack.EMPTY;
			if(index >= 36 && !mergeItemStack(itemstack1, 0, 36, false))
				return ItemStack.EMPTY;
			if(!itemstack1.isEmpty())
				slot.onSlotChanged();
			if(itemstack1.getCount() != itemstack.getCount())
				slot.putStack(itemstack1);
			else
				return ItemStack.EMPTY;
		}
		return itemstack;
	}
}