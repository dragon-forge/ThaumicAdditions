package com.zeitheron.thaumicadditions.init.guis;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import com.zeitheron.thaumicadditions.inventory.container.ContainerRepairVoid;
import com.zeitheron.thaumicadditions.inventory.gui.GuiRepairVoid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiCallbackRepairVoid implements IGuiCallback
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