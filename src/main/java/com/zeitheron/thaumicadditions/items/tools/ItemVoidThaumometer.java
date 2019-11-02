package com.zeitheron.thaumicadditions.items.tools;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

public class ItemVoidThaumometer extends Item
{
	public static WeakReference<EntityFluxRift> HOVERED_RIFT = null;
	
	public static EntityFluxRift getSelectedRift()
	{
		return HOVERED_RIFT != null ? HOVERED_RIFT.get() : null;
	}
	
	public ItemVoidThaumometer()
	{
		setMaxStackSize(1);
		setTranslationKey("void_thaumometer");
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.RARE;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer p, EnumHand hand)
	{
		if(world.isRemote)
		{
			this.drawFX(world, p);
			p.world.playSound(p.posX, p.posY, p.posZ, SoundsTC.scan, SoundCategory.PLAYERS, 0.5f, 1.0f, false);
		} else
			this.doScan(world, p);
		return new ActionResult(EnumActionResult.SUCCESS, (Object) p.getHeldItem(hand));
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		boolean held;
		boolean bl = held = isSelected || itemSlot == 0;
		if(held && !world.isRemote && entity.ticksExisted % 20 == 0 && entity instanceof EntityPlayerMP)
			this.updateAura(stack, world, (EntityPlayerMP) entity);
		if(held && world.isRemote && entity.ticksExisted % 5 == 0 && entity instanceof EntityPlayer)
		{
			RayTraceResult mop;
			Entity target = EntityUtils.getPointedEntity(world, entity, 1.0, 16.0, 5.0f, true);
			if(target != null && ScanningManager.isThingStillScannable((EntityPlayer) entity, target))
				FXDispatcher.INSTANCE.scanHighlight(target);
			if((mop = this.getRayTraceResultFromPlayerWild(world, (EntityPlayer) entity, true)) != null && mop.getBlockPos() != null && ScanningManager.isThingStillScannable((EntityPlayer) entity, (Object) mop.getBlockPos()))
				FXDispatcher.INSTANCE.scanHighlight(mop.getBlockPos());
			
			if(target instanceof EntityFluxRift)
			{
				if(getSelectedRift() != target)
					HOVERED_RIFT = new WeakReference<>((EntityFluxRift) target);
			} else if(getSelectedRift() != null)
				HOVERED_RIFT = null;
		}
	}
	
	protected RayTraceResult getRayTraceResultFromPlayerWild(World worldIn, EntityPlayer playerIn, boolean useLiquids)
	{
		float f = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) + (float) worldIn.rand.nextInt(25) - (float) worldIn.rand.nextInt(25);
		float f1 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) + (float) worldIn.rand.nextInt(25) - (float) worldIn.rand.nextInt(25);
		double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX);
		double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) + (double) playerIn.getEyeHeight();
		double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ);
		Vec3d vec3 = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos((float) ((-f1) * 0.017453292f - 3.1415927f));
		float f3 = MathHelper.sin((float) ((-f1) * 0.017453292f - 3.1415927f));
		float f4 = -MathHelper.cos((float) ((-f) * 0.017453292f));
		float f5 = MathHelper.sin((float) ((-f) * 0.017453292f));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 16.0;
		Vec3d vec31 = vec3.add((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
		return worldIn.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
	}
	
	private void updateAura(ItemStack stack, World world, EntityPlayerMP player)
	{
		BlockPos pos = player.getPosition();
		AuraChunk ac = AuraHandler.getAuraChunk(world.provider.getDimension(), pos.getX() >> 4, pos.getZ() >> 4);
		if(ac != null)
		{
			if(!(ac.getFlux() <= ac.getVis() && ac.getFlux() <= (float) (ac.getBase() / 3) || ThaumcraftCapabilities.knowsResearch((EntityPlayer) player, "FLUX")))
			{
				ResearchManager.startResearchWithPopup((EntityPlayer) player, "FLUX");
				player.sendStatusMessage(new TextComponentTranslation("research.FLUX.warn"), true);
			}
			PacketHandler.INSTANCE.sendTo(new PacketAuraToClient(ac), player);
		}
	}
	
	private void drawFX(World worldIn, EntityPlayer playerIn)
	{
		block3:
		{
			block2:
			{
				Entity target = EntityUtils.getPointedEntity(worldIn, (Entity) playerIn, 1.0, 9.0, 0.0f, true);
				if(target == null)
					break block2;
				for(int a = 0; a < 10; ++a)
					FXDispatcher.INSTANCE.blockRunes(target.posX - 0.5, target.posY + (double) (target.getEyeHeight() / 2.0f), target.posZ - 0.5, 0.3f + worldIn.rand.nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.rand.nextFloat() * 0.7f, (int) (target.height * 15.0f), 0.03f);
				break block3;
			}
			RayTraceResult mop = this.rayTrace(worldIn, playerIn, true);
			if(mop == null || mop.getBlockPos() == null)
				break block3;
			for(int a = 0; a < 10; ++a)
				FXDispatcher.INSTANCE.blockRunes(mop.getBlockPos().getX(), (double) mop.getBlockPos().getY() + 0.25, mop.getBlockPos().getZ(), 0.3f + worldIn.rand.nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.rand.nextFloat() * 0.7f, 15, 0.03f);
		}
	}
	
	public void doScan(World worldIn, EntityPlayer playerIn)
	{
		if(!worldIn.isRemote)
		{
			Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0f, true);
			if(target != null)
			{
				ScanningManager.scanTheThing(playerIn, (Object) target);
			} else
			{
				RayTraceResult mop = this.rayTrace(worldIn, playerIn, true);
				if(mop != null && mop.getBlockPos() != null)
				{
					ScanningManager.scanTheThing(playerIn, (Object) mop.getBlockPos());
				} else
				{
					ScanningManager.scanTheThing(playerIn, null);
				}
			}
		}
	}
}