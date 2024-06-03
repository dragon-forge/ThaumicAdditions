package org.zeith.thaumicadditions.items.tools;

import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.datas.MirrorData;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.blocks.IBlockFacing;
import thaumcraft.common.lib.SoundsTC;

import java.util.Optional;

public class ItemWormholeMirror
		extends Item
		implements IRechargable
{
	public ItemWormholeMirror()
	{
		setTranslationKey("wormhole_mirror");
		setMaxStackSize(1);
	}
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10, EnumHand hand)
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
		if(player instanceof EntityPlayerMP && tm != null)
		{
			Optional<MirrorData> first = DataProviderRegistry.first(tm, MirrorData.TYPE);
			if(!first.isPresent())
				return super.onItemUseFirst(player, world, pos, side, par8, par9, par10, hand);
			boolean used = first
					.map(md -> useMirror(player, world, mirror, md, tm.getPos()))
					.orElse(false);
			return used ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
		
		return EnumActionResult.PASS;
	}
	
	private boolean useMirror(EntityPlayer player, World world, ItemStack mirror, MirrorData data, BlockPos tilePos)
	{
		if(!data.linked)
		{
			SoundUtil.playSoundEffect(world, SoundsTC.jar.getRegistryName().toString(), player.getPosition(), 1F, .9F + world.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
			return false;
		}
		
		int linkDim = data.linkDimension;
		BlockPos linkPos = data.linkPos;
		
		Vec3d targetPos = new Vec3d(linkPos.getX() + .5, linkPos.getY() - .8F, linkPos.getZ() + .5);
		
		WorldServer ourWorld = Cast.cast(world, WorldServer.class);
		WorldServer remoteWorld = linkDim != ourWorld.provider.getDimension() ? ourWorld.getMinecraftServer().getWorld(linkDim) : ourWorld;
		if(ourWorld == null || remoteWorld == null) return false;
		
		AxisAlignedBB cbb = remoteWorld.getBlockState(linkPos.down()).getCollisionBoundingBox(remoteWorld, linkPos.down());
		if(cbb != null) cbb = cbb.offset(linkPos);
		if(cbb != null && cbb.intersects(
				new AxisAlignedBB(targetPos.x - player.width / 2, targetPos.y, targetPos.z - player.width / 2, targetPos.x + player.width / 2, targetPos.y + player.height, targetPos.z + player.width / 2)
		))
		{
			targetPos = targetPos.add(0, 0.8F, 0);
		}
		
		WorldUtil.teleportPlayer((EntityPlayerMP) player, linkDim, targetPos.x, targetPos.y, targetPos.z);
		
		SoundUtil.playSoundEffect(ourWorld, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), tilePos, 1F, .8F + ourWorld.rand.nextFloat() * .2F, SoundCategory.PLAYERS);
		
		IBlockState state = remoteWorld.getBlockState(linkPos);
		if(state.getPropertyKeys().contains(IBlockFacing.FACING))
		{
			EnumFacing face = state.getValue(IBlockFacing.FACING);
			if(face == EnumFacing.UP)
				WorldUtil.teleportPlayer((EntityPlayerMP) player, linkDim, linkPos.getX() + .5, linkPos.getY() + .2F, linkPos.getZ() + .5);
			else if(face.getAxis() != Axis.Y)
			{
				state = remoteWorld.getBlockState(linkPos.down());
				boolean air = remoteWorld.isAirBlock(linkPos.down());
				WorldUtil.teleportPlayer((EntityPlayerMP) player, linkDim, linkPos.getX() + .5, linkPos.getY() - .8F + (!air && state.getBlock().isCollidable() ? 1 : 0), linkPos.getZ() + .5);
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
		
		SoundUtil.playSoundEffect(remoteWorld, SoundEvents.ENTITY_ENDERMEN_TELEPORT.getRegistryName().toString(), linkPos, 1F, .8F + remoteWorld.rand.nextFloat() * .4F, SoundCategory.PLAYERS);
		
		RechargeHelper.consumeCharge(mirror, player, 5);
		return true;
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