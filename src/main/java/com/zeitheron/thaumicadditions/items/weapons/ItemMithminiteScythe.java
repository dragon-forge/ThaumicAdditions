package com.zeitheron.thaumicadditions.items.weapons;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.raytracer.RayTracer;
import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.animator.BaseItemAnimator;
import com.zeitheron.thaumicadditions.api.animator.IAnimatableItem;
import com.zeitheron.thaumicadditions.api.animator.ItemVanillaAnimator;
import com.zeitheron.thaumicadditions.api.items.ILeftClickItem;
import com.zeitheron.thaumicadditions.entity.EntityMithminiteScythe;
import com.zeitheron.thaumicadditions.init.SoundsTAR;
import com.zeitheron.thaumicadditions.net.PacketSyncTARTag;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemMithminiteScythe
		extends Item
		implements IAnimatableItem, ILeftClickItem
{
	public static final String TIMEOUT = "MithminiteScytheTimeout";

	public ItemMithminiteScythe()
	{
		setTranslationKey("mithminite_scythe");
		setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);

		addPropertyOverride(new ResourceLocation("vperp"), (stack, worldIn, entityIn) ->
		{
			return stack.hasTagCompound() && stack.getTagCompound().getBoolean("AsProjectile") ? 1 : 0;
		});
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		Material mat = state.getMaterial();
		return mat == Material.PLANTS || mat == Material.VINE || mat == Material.LEAVES ? 1F : 0F;
	}

	@Override
	public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
	{
		return false;
	}

	@Override
	public float overrideSwing(float amount, ItemStack stack, EntityPlayer player, float partialTime)
	{
		return Math.min(TAReconstructed.getPlayerTag(player).getInteger(TIMEOUT) + partialTime, 7) / 7F;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		return true;
	}

	@Override
	public BaseItemAnimator getAnimator(ItemStack stack)
	{
		return ItemVanillaAnimator.VANILLA;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(I18n.format("damage." + getRegistryName() + ".melee"));
		tooltip.add(I18n.format("damage." + getRegistryName() + ".ranged"));
	}

	static final UUID REACH_ID = UUID.fromString("16786798-6b7b-4a28-a20e-1a6a80be39d7");

	@SubscribeEvent
	public void playerTick(PlayerTickEvent e)
	{
		if(e.phase == Phase.START)
		{
			NBTTagCompound nbt = TAReconstructed.getPlayerTag(e.player);
			nbt.setInteger(TIMEOUT, Math.min(7, nbt.getInteger(TIMEOUT) + 1));
			if(nbt.getInteger(TIMEOUT) >= 7 && nbt.getBoolean("LeftClick"))
				Cast.optionally(e.player, EntityPlayerMP.class).ifPresent(mp ->
				{
					ItemStack stack = mp.getHeldItemMainhand();
					if(!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem)
						((ILeftClickItem) stack.getItem()).onLeftClick(stack, mp);
				});

			ItemStack main = e.player.getHeldItemMainhand();
			IAttributeInstance attr = e.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
			attr.removeModifier(REACH_ID);
			if(!main.isEmpty() && main.getItem() == this)
				attr.applyModifier(new AttributeModifier(REACH_ID, "TARReachMithminiteScythe", 3, 0));
		}
	}

	@Override
	public void onLeftClick(ItemStack stack, EntityPlayerMP player)
	{
		NBTTagCompound nbt = TAReconstructed.getPlayerTag(player);
		if(nbt.getInteger(TIMEOUT) >= 7)
		{
			nbt.setInteger(TIMEOUT, 0);
			PacketSyncTARTag.sync(player);
			HCNet.swingArm(player, EnumHand.MAIN_HAND);
			SoundsTAR.MITHMINITE_SCYTHE.playAt(new WorldLocation(player.world, player.getPosition()), 2F, 1F, SoundCategory.PLAYERS);
			RayTraceResult res = RayTracer.retraceFully(player);
			if(res != null && res.typeOfHit == Type.ENTITY && res.entityHit != null)
			{
				Entity ent = res.entityHit;
				if(ent instanceof EntityLivingBase)
				{
					EntityLivingBase elb = (EntityLivingBase) ent;
					elb.attackEntityFrom(DamageSource.causePlayerDamage(player), 12F);
				}
			}

			if(!player.world.isRemote)
			{
				double mx = player.motionX, my = player.motionY, mz = player.motionZ;
				player.motionX = player.motionY = player.motionZ = 0;
				EntityMithminiteScythe scythe = new EntityMithminiteScythe(player.world, player);
				scythe.shoot(player, player.rotationPitch, player.rotationYaw, 0, 0.5F, 0F);
				player.world.spawnEntity(scythe);
				player.motionX = mx;
				player.motionY = my;
				player.motionZ = mz;
			}
		}
	}
}