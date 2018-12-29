package com.zeitheron.thaumicadditions.items;

import com.zeitheron.thaumicadditions.entity.EntityChester;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemChester extends Item
{
	public ItemChester()
	{
		setTranslationKey("chester");
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack held = player.getHeldItem(hand);
		BlockPos sp = pos.offset(facing);
		if(!worldIn.isRemote)
			worldIn.spawnEntity(new EntityChester(worldIn, player, sp.getX() + .5, sp.getY(), sp.getZ() + .5));
		held.shrink(1);
		player.swingArm(hand);
		return EnumActionResult.SUCCESS;
	}
}