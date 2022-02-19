package org.zeith.thaumicadditions.inventory.container;

import com.zeitheron.hammercore.client.gui.impl.container.ItemTransferHelper.TransferableContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.init.ItemsTAR;
import org.zeith.thaumicadditions.inventory.SlotTexturable;
import org.zeith.thaumicadditions.tiles.TileAuraDisperser;

public class ContainerAuraDisperser
		extends TransferableContainer<TileAuraDisperser>
{
	public ContainerAuraDisperser(EntityPlayer player, TileAuraDisperser t)
	{
		super(player, t, 8, 84);
	}

	@Override
	protected void addCustomSlots()
	{
		for(int i = 0; i < 9; ++i)
			addSlotToContainer(new SlotTexturable(t.inventory, i, 62 + (i % 3) * 18, 12 + i / 3 * 18).setValidator(s -> s.getItem() == ItemsTAR.SALT_ESSENCE).setBackgroundTexture(new ResourceLocation(InfoTAR.MOD_ID, "textures/slots/salt.png")));
	}

	@Override
	protected void addTransfer()
	{

	}
}