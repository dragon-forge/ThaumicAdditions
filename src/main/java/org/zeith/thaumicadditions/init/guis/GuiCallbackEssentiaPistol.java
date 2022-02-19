package org.zeith.thaumicadditions.init.guis;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.inventory.container.ContainerEssentiaPistol;
import org.zeith.thaumicadditions.inventory.gui.GuiEssentiaPistol;
import org.zeith.thaumicadditions.items.weapons.ItemEssentiaPistol;

public class GuiCallbackEssentiaPistol
		implements IGuiCallback
{
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world, BlockPos pos)
	{
		ContainerEssentiaPistol c = Cast.cast(getServerGuiElement(player, world, pos), ContainerEssentiaPistol.class);
		return c != null ? new GuiEssentiaPistol(c) : null;
	}

	@Override
	public Object getServerGuiElement(EntityPlayer player, World world, BlockPos pos)
	{
		ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
		if(!held.isEmpty() && held.getItem() instanceof ItemEssentiaPistol)
			return new ContainerEssentiaPistol(player, EnumHand.MAIN_HAND);
		held = player.getHeldItem(EnumHand.OFF_HAND);
		if(!held.isEmpty() && held.getItem() instanceof ItemEssentiaPistol)
			return new ContainerEssentiaPistol(player, EnumHand.OFF_HAND);
		return null;
	}
}