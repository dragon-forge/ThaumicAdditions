package org.zeith.thaumicadditions.api.animator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BaseItemAnimator
{
	@SideOnly(Side.CLIENT)
	public boolean transformHand(RenderSpecificHandEvent e, float progress)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean transformHandItem(RenderSpecificHandEvent e, float progress)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public boolean rendersHand(EntityPlayer player, EnumHand hand, EnumHandSide side)
	{
		return false;
	}
}