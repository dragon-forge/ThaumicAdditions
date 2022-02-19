package org.zeith.thaumicadditions.inventory.container;

import com.zeitheron.hammercore.client.gui.impl.container.ItemTransferHelper.TransferableContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.zeith.thaumicadditions.tiles.TileCrystalCrusher;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.container.slot.SlotOutput;

public class ContainerCrystalCrusher
		extends TransferableContainer<TileCrystalCrusher>
{
	public ContainerCrystalCrusher(EntityPlayer player, TileCrystalCrusher t)
	{
		super(player, t, 8, 82);
	}

	@Override
	protected void addCustomSlots()
	{
		addSlotToContainer(new SlotCrystalEssence(t, 0, 62 - 18, 32));
		addSlotToContainer(new SlotOutput(t, 1, 62 + 54, 32));
	}

	@Override
	protected void addTransfer()
	{
		transfer.addInTransferRule(0, stack -> stack.getItem() == ItemsTC.crystalEssence);
		transfer.addOutTransferRule(0, i -> i > 1);
		transfer.addOutTransferRule(1, i -> i > 1);
	}

	public static class SlotCrystalEssence
			extends Slot
	{
		public SlotCrystalEssence(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return !stack.isEmpty() && stack.getItem() == ItemsTC.crystalEssence;
		}
	}
}