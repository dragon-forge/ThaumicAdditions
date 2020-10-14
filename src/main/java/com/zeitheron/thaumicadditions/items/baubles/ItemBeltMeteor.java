package com.zeitheron.thaumicadditions.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.zeitheron.hammercore.utils.AABBUtils;
import com.zeitheron.thaumicadditions.events.LivingEventsTAR;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;

import java.util.List;

public class ItemBeltMeteor
		extends Item
		implements IBauble, IRechargable, IRarity
{
	public ItemBeltMeteor()
	{
		setTranslationKey("meteor_belt");
		setMaxStackSize(1);
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.BELT;
	}
	
	@Override
	public IRarity getForgeRarity(ItemStack stack)
	{
		return this;
	}
	
	@Override
	public TextFormatting getColor()
	{
		return TextFormatting.RED;
	}
	
	@Override
	public String getName()
	{
		return "Outer";
	}
	
	@Override
	public void onWornTick(ItemStack itemStack, EntityLivingBase base)
	{
		EntityPlayer asPlayer = null;
		if(base instanceof EntityPlayer && (asPlayer = (EntityPlayer) base).inventory.armorInventory.get(0).getItem() == ItemsTC.travellerBoots)
			return;
		
		boolean hasCharge = RechargeHelper.getCharge(itemStack) > 0;
		if(!base.world.isRemote && base.ticksExisted % 20 == 0)
		{
			int e = 0;
			if(itemStack.hasTagCompound()) e = itemStack.getTagCompound().getInteger("energy");
			
			if(e > 0)
			{
				if(asPlayer == null || !asPlayer.capabilities.isCreativeMode)
					--e;
			} else if((asPlayer != null && asPlayer.capabilities.isCreativeMode) || RechargeHelper.consumeCharge(itemStack, base, 1))
				e = 60;
			
			itemStack.setTagInfo("energy", new NBTTagInt(e));
		}
		
		boolean isFlying = false;
		if(base instanceof EntityPlayer) isFlying = ((EntityPlayer) base).capabilities.isFlying;
		
		if(hasCharge && !isFlying && base.moveForward > 0.0F)
		{
			if(base.world.isRemote && !base.isSneaking())
			{
				if(!LivingEventsTAR.prevStep.containsKey(base.getEntityId()))
					LivingEventsTAR.prevStep.put(base.getEntityId(), Float.valueOf(base.stepHeight));
				base.stepHeight = 1.0f;
			}
			
			if(base.onGround)
			{
				float bonus = 0.2F;
				
				if(base.isInWater())
				{
					bonus /= 4.0F;
				}
				
				base.moveRelative(0.0F, 0.0F, bonus, 1.0F);
			} else
			{
				if(base.isInWater())
				{
					base.moveRelative(0.0F, 0.0F, 0.025F, 1.0F);
				}
				
				base.jumpMovementFactor = 0.12F;
			}
		}
		
		if(!base.onGround)
		{
			int m = 0;
			if(itemStack.hasTagCompound()) m = itemStack.getTagCompound().getInteger("meteoring");
			
			if(base.isSneaking())
			{
				base.motionY -= 0.15F;
				
				AxisAlignedBB aabb = base.getEntityBoundingBox().grow(0.25F);
				for(int i = 0; i < 3; ++i)
				{
					Vec3d p = AABBUtils.randomPosWithin(aabb, base.getRNG());
					base.world.spawnParticle(EnumParticleTypes.FLAME, p.x, p.y, p.z, 0, 0, 0);
				}
				
				++m;
			} else m = 0;
			
			itemStack.setTagInfo("meteoring", new NBTTagInt(m));
		} else
		{
			if(itemStack.hasTagCompound())
			{
				if(itemStack.getTagCompound().getInteger("meteoring") > 5)
				{
					itemStack.getTagCompound().removeTag("meteoring");
					
					double x = base.posX, y = base.posY, z = base.posZ;
					base.world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 0.8F + (1.0F + (base.world.rand.nextFloat() - base.world.rand.nextFloat()) * 0.2F) * 0.7F);
					base.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 1.0D, 0.0D, 0.0D);
					
					if(!base.world.isRemote)
					{
						Vec3d vec3d = new Vec3d(x, y, z);
						float f3 = 8F;
						int k1 = MathHelper.floor(x - (double) f3 - 1.0D);
						int l1 = MathHelper.floor(x + (double) f3 + 1.0D);
						int i2 = MathHelper.floor(y - (double) f3 - 1.0D);
						int i1 = MathHelper.floor(y + (double) f3 + 1.0D);
						int j2 = MathHelper.floor(z - (double) f3 - 1.0D);
						int j1 = MathHelper.floor(z + (double) f3 + 1.0D);
						List<Entity> list = base.world.getEntitiesWithinAABBExcludingEntity(base, new AxisAlignedBB(k1, i2, j2, l1, i1, j1));
						
						for(int k2 = 0; k2 < list.size(); ++k2)
						{
							Entity entity = list.get(k2);
							
							if(!entity.isImmuneToExplosions())
							{
								double d12 = entity.getDistance(x, y, z) / (double) f3;
								
								if(d12 <= 1.0D)
								{
									double d5 = entity.posX - x;
									double d7 = entity.posY + (double) entity.getEyeHeight() - y;
									double d9 = entity.posZ - z;
									double d13 = MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
									
									if(d13 != 0.0D)
									{
										double d14 = base.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
										double d10 = (1.0D - d12) * d14;
										entity.attackEntityFrom(asPlayer != null ? DamageSource.causePlayerDamage(asPlayer) : DamageSource.causeMobDamage(base), (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f3 + 1.0D)));
									}
								}
							}
						}
					}
				} else
					itemStack.getTagCompound().removeTag("meteoring");
			}
		}
	}
	
	@Override
	public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase)
	{
		return 300;
	}
	
	@Override
	public EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase)
	{
		return EnumChargeDisplay.PERIODIC;
	}
}
