package org.zeith.thaumicadditions.init.guis;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.inventory.container.ContainerRepairVoid;
import org.zeith.thaumicadditions.inventory.gui.GuiRepairVoid;

public class GuiCallbackRepairVoid
		implements IGuiCallback
{
	@Override
	public Object getServerGuiElement(EntityPlayer player, World world, BlockPos pos)
	{
		return new ContainerRepairVoid(player.inventory, world, pos, player);
	}

	@Override
	public Object getClientGuiElement(EntityPlayer player, World world, BlockPos pos)
	{
		return new GuiRepairVoid(player.inventory, world, pos);
	}
}