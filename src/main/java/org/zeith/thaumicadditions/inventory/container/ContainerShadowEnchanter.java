package org.zeith.thaumicadditions.inventory.container;

import com.zeitheron.hammercore.client.gui.impl.container.ItemTransferHelper.TransferableContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.zeith.thaumicadditions.tiles.TileShadowEnchanter;
import thaumcraft.common.container.slot.SlotOutput;

public class ContainerShadowEnchanter
		extends TransferableContainer<TileShadowEnchanter>
{
	public ContainerShadowEnchanter(EntityPlayer player, TileShadowEnchanter tile)
	{
		super(player, tile, 8, 104);
	}

	@Override
	protected void addCustomSlots()
	{
		addSlotToContainer(new Slot(t.items, 0, 8, 17)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return !stack.isEmpty() && stack.isItemEnchantable();
			}
		});

		addSlotToContainer(new SlotOutput(t.items, 1, 152, 17));
	}

	@Override
	protected void addTransfer()
	{
		transfer.addInTransferRule(0, inventorySlots.get(0)::isItemValid);
		transfer.addOutTransferRule(0, s -> s > 1);
		transfer.addOutTransferRule(1, s -> s > 1);
	}
}