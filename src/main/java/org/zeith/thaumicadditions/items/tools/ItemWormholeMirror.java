package org.zeith.thaumicadditions.items.tools;

import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;

public class ItemWormholeMirror
		extends Item
		implements IRechargable
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

					WorldServer ws = Cast.cast(world, WorldServer.class);
					if(ws != null)
					{
						SoundUtil.playSoundEffect(ws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), tmr.getPos(), 1F, .8F + ws.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
						WorldServer rws = tmr.linkDim != ws.provider.getDimension() ? ws.getMinecraftServer().getWorld(tmr.linkDim) : ws;
						if(rws != null)
						{
							BlockPos pos2 = new BlockPos(tmr.linkX, tmr.linkY, tmr.linkZ);
							IBlockState state = rws.getBlockState(pos2);
							if(state.getPropertyKeys().contains(IBlockFacing.FACING))
							{
								EnumFacing face = state.getValue(IBlockFacing.FACING);
								if(face == EnumFacing.UP)
									WorldUtil.teleportPlayer((EntityPlayerMP) player, tmr.linkDim, tmr.linkX + .5, tmr.linkY + .2F, tmr.linkZ + .5);
								else if(face.getAxis() != Axis.Y)
								{
									state = rws.getBlockState(pos2.down());
									boolean air = rws.isAirBlock(pos2.down());
									WorldUtil.teleportPlayer((EntityPlayerMP) player, tmr.linkDim, tmr.linkX + .5, tmr.linkY - .8F + (!air && state.getBlock().isCollidable() ? 1 : 0), tmr.linkZ + .5);
									float yaw = 0;
									if(face == EnumFacing.SOUTH)
										yaw = 0;
									if(face == EnumFacing.NORTH)
										yaw = 180;
									if(face == EnumFacing.WEST)
										yaw = 90;
									if(face == EnumFacing.EAST)
										yaw = -90;
									((EntityPlayerMP) player).connection.setPlayerLocation(player.posX, player.posY, player.posZ, yaw, 0);
								}
							}

							SoundUtil.playSoundEffect(rws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), pos2, 1F, .8F + rws.rand.nextFloat() * .4F, SoundCategory.PLAYERS);
						}
					}

					RechargeHelper.consumeCharge(mirror, player, 5);
				} else
					SoundUtil.playSoundEffect(world, SoundsTC.jar.getRegistryName().toString(), player.getPosition(), 1F, .9F + world.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
			}
			if(tm != null && tm instanceof TileMirrorEssentia && player instanceof EntityPlayerMP)
			{
				TileMirrorEssentia tmr = (TileMirrorEssentia) tm;
				if(tmr.linked)
				{
					WorldUtil.teleportPlayer((EntityPlayerMP) player, tmr.linkDim, tmr.linkX + .5, tmr.linkY - .8F, tmr.linkZ + .5);

					WorldServer ws = Cast.cast(world, WorldServer.class);
					if(ws != null)
					{
						SoundUtil.playSoundEffect(ws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), tmr.getPos(), 1F, .8F + ws.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
						WorldServer rws = tmr.linkDim != ws.provider.getDimension() ? ws.getMinecraftServer().getWorld(tmr.linkDim) : ws;
						if(rws != null)
						{
							BlockPos pos2 = new BlockPos(tmr.linkX, tmr.linkY, tmr.linkZ);
							IBlockState state = rws.getBlockState(pos2);
							if(state.getPropertyKeys().contains(IBlockFacing.FACING))
							{
								EnumFacing face = state.getValue(IBlockFacing.FACING);
								if(face == EnumFacing.UP)
									WorldUtil.teleportPlayer((EntityPlayerMP) player, tmr.linkDim, tmr.linkX + .5, tmr.linkY + .2F, tmr.linkZ + .5);
								else if(face.getAxis() != Axis.Y)
								{
									state = rws.getBlockState(pos2.down());
									boolean air = rws.isAirBlock(pos2.down());
									WorldUtil.teleportPlayer((EntityPlayerMP) player, tmr.linkDim, tmr.linkX + .5, tmr.linkY - .8F + (!air && state.getBlock().isCollidable() ? 1 : 0), tmr.linkZ + .5);
									float yaw = 0;
									if(face == EnumFacing.SOUTH)
										yaw = 0;
									if(face == EnumFacing.NORTH)
										yaw = 180;
									if(face == EnumFacing.WEST)
										yaw = 90;
									if(face == EnumFacing.EAST)
										yaw = -90;
									((EntityPlayerMP) player).connection.setPlayerLocation(player.posX, player.posY, player.posZ, yaw, 0);
								}
							}

							SoundUtil.playSoundEffect(rws, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), pos2, 1F, .8F + rws.rand.nextFloat() * .4F, SoundCategory.PLAYERS);
						}
					}

					RechargeHelper.consumeCharge(mirror, player, 5);
				} else
					SoundUtil.playSoundEffect(world, SoundsTC.jar.getRegistryName().toString(), player.getPosition(), 1F, .9F + world.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
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
		return EnumChargeDisplay.NORMAL;
	}
}