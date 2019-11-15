package com.zeitheron.thaumicadditions.init.guis;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.thaumicadditions.inventory.container.ContainerCrystalBag;
import com.zeitheron.thaumicadditions.inventory.gui.GuiCrystalBag;
import com.zeitheron.thaumicadditions.items.ItemCrystalBag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiCallbackCrystalBag implements IGuiCallback
{
	@Override
	public Object getServerGuiElement(EntityPlayer player, World world, BlockPos pos)
	{
		ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
		if(!held.isEmpty() && held.getItem() instanceof ItemCrystalBag)
			return new ContainerCrystalBag(player, EnumHand.MAIN_HAND);
		held = player.getHeldItem(EnumHand.OFF_HAND);
		if(!held.isEmpty() && held.getItem() instanceof ItemCrystalBag)
			return new ContainerCrystalBag(player, EnumHand.OFF_HAND);
		return null;
	}
	
	@Override
	public Object getClientGuiElement(EntityPlayer player, World world, BlockPos pos)
	{
		ContainerCrystalBag c = Cast.cast(getServerGuiElement(player, world, pos), ContainerCrystalBag.class);
		return c != null ? new GuiCrystalBag(c) : null;
	}
}