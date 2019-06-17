package com.zeitheron.thaumicadditions.items;

import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;

public class ItemWormholeMirror extends Item implements IRechargable
{
	public ItemWormholeMirror()
	{
		setTranslationKey("wormhole_mirror");
		setMaxStackSize(1);
	}
	
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10, EnumHand hand)
	{
		Block bi = world.getBlockState(pos).getBlock();
		if(bi == BlocksTC.mirror || bi == BlocksTC.mirrorEssentia)
		{
			if(world.isRemote)
			{
				player.swingArm(hand);
				return super.onItemUseFirst(player, world, pos, side, par8, par9, par10, hand);
			}
			
			ItemStack mirror = player.getHeldItem(hand);
			if(RechargeHelper.getCharge(mirror) < 5)
				return super.onItemUseFirst(player, world, pos, side, par8, par9, par10, hand);
			
			TileEntity tm = world.getTileEntity(pos);
			if(tm != null && tm instanceof TileMirror && player instanceof EntityPlayerMP)
			{
				TileMirror tmr = (TileMirror) tm;
				if(tmr.linked)
				{
					WorldUtil.teleportPlayer((EntityPlayerMP) player, tmr.linkDim, tmr.linkX + .5, tmr.linkY - .8F, tmr.linkZ + .5);
					
					WorldServer ws = WorldUtil.cast(world, WorldServer.class);
					if(ws != null)
					{
						SoundUtil.playSoundEffect(ws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), tmr.getPos(), 1F, .8F + ws.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
						WorldServer rws = tmr.linkDim != ws.provider.getDimension() ? ws.getMinecraftServer().getWorld(tmr.linkDim) : ws;
						if(rws != null)
							SoundUtil.playSoundEffect(rws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), new BlockPos(tmr.linkX, tmr.linkY, tmr.linkZ), 1F, .8F + rws.rand.nextFloat() * .4F, SoundCategory.PLAYERS);
					}
				}
				
				RechargeHelper.consumeCharge(mirror, player, 5);
			}
			if(tm != null && tm instanceof TileMirrorEssentia && player instanceof EntityPlayerMP)
			{
				TileMirrorEssentia tmr = (TileMirrorEssentia) tm;
				if(tmr.linked)
				{
					WorldUtil.teleportPlayer((EntityPlayerMP) player, tmr.linkDim, tmr.linkX + .5, tmr.linkY - .8F, tmr.linkZ + .5);
					
					WorldServer ws = WorldUtil.cast(world, WorldServer.class);
					if(ws != null)
					{
						SoundUtil.playSoundEffect(ws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), tmr.getPos(), 1F, .8F + ws.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
						WorldServer rws = tmr.linkDim != ws.provider.getDimension() ? ws.getMinecraftServer().getWorld(tmr.linkDim) : ws;
						if(rws != null)
							SoundUtil.playSoundEffect(rws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), new BlockPos(tmr.linkX, tmr.linkY, tmr.linkZ), 1F, .8F + rws.rand.nextFloat() * .4F, SoundCategory.PLAYERS);
					}
				}
				
				RechargeHelper.consumeCharge(mirror, player, 5);
			}
			return EnumActionResult.PASS;
		}
		return super.onItemUseFirst(player, world, pos, side, par8, par9, par10, hand);
	}

	@Override
	public int getMaxCharge(ItemStack var1, EntityLivingBase var2)
	{
		return 250;
	}

	@Override
	public EnumChargeDisplay showInHud(ItemStack var1, EntityLivingBase var2)
	{
		return EnumChargeDisplay.PERIODIC;
	}
}