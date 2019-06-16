package com.zeitheron.thaumicadditions.items.baubles;

import com.zeitheron.hammercore.utils.ConsumableItem;
import com.zeitheron.hammercore.utils.IRegisterListener;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.utils.ThaumicHelper;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.potions.PotionWarpWard;

public class ItemFragnantPendant extends Item implements IBauble, IRenderBauble, IRegisterListener
{
	public static ConsumableItem ODOUR_POWDER;
	
	public ItemFragnantPendant()
	{
		setTranslationKey("fragnant_pendant");
		setMaxStackSize(1);
	}
	
	@Override
	public void onRegistered()
	{
		ODOUR_POWDER = new ConsumableItem(1, Ingredient.fromItem(ItemsTAR.ODOUR_POWDER));
	}
	
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player)
	{
		EntityPlayerMP mp = WorldUtil.cast(player, EntityPlayerMP.class);
		if(mp != null && !mp.world.isRemote && !mp.isPotionActive(PotionWarpWard.instance) && mp.ticksExisted % 40 == 0 && ODOUR_POWDER.canConsume(mp.inventory))
		{
			ODOUR_POWDER.consume(mp.inventory);
			ThaumicHelper.applyWarpWard(mp);
		}
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float partialTicks)
	{
	}
}