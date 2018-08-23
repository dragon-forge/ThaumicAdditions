package com.zeitheron.thaumicadditions.events;

import com.zeitheron.hammercore.annotations.MCFBus;
import com.zeitheron.hammercore.event.FoodEatenEvent;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.thaumicadditions.api.EdibleAspect;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.utils.Foods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;

@MCFBus
public class LivingEventsTAR
{
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void foodEaten(FoodEatenEvent e)
	{
		if(e.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayerMP mp = (EntityPlayerMP) e.getEntityPlayer();
			ItemStack item = e.getOriginStack();
			AspectList al;
			if(!item.isEmpty() && Foods.isFood(item.getItem()) && (al = EdibleAspect.getSalt(item)).visSize() > 0)
				EdibleAspect.execute(mp, al);
		}
	}
	
	@SubscribeEvent
	public void itemPickupEvent(EntityItemPickupEvent ev)
	{
		float fall = ev.getEntityPlayer().fallDistance;
		if(ev.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayerMP mp = (EntityPlayerMP) ev.getEntityPlayer();
			if(mp.getEntityBoundingBox() != null && fall >= 3F && ThaumcraftCapabilities.knowsResearch(mp, "TAR_ESSENCE_SALT@2"))
			{
				EntityItem e = ev.getItem();
				ItemStack stack = e.getItem();
				if(!stack.isEmpty() && stack.getItem() == ItemsTC.crystalEssence)
				{
					NBTTagCompound nbt = e.getEntityData();
					float crack = nbt.getFloat("CrystalCrack");
					crack += fall - 1;
					
					int shrinks = 0;
					while(crack > 4 && !stack.isEmpty())
					{
						++shrinks;
						crack -= 4;
						stack.shrink(1);
						ItemStack salt = new ItemStack(ItemsTAR.SALT_ESSENCE);
						salt.setTagCompound(stack.getTagCompound().copy());
						EntityItem ni = new EntityItem(e.world, e.posX, e.posY, e.posZ, salt);
						ni.motionX = e.motionX;
						ni.motionY = e.motionY;
						ni.motionZ = e.motionZ;
						if(stack.isEmpty())
						{
							e.setDead();
							return;
						}
						e.world.spawnEntity(ni);
						SoundUtil.playSoundEffect(e.world, SoundsTC.crystal.getRegistryName().toString(), e.getPosition(), 1F, .8F, SoundCategory.PLAYERS);
					}
					
					nbt.setFloat("CrystalCrack", crack);
					
					if(shrinks == 0)
						SoundUtil.playSoundEffect(e.world, SoundsTC.crystal.getRegistryName().toString(), e.getPosition(), 1F, .2F, SoundCategory.PLAYERS);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void livingDeath(LivingDeathEvent lde)
	{
		EntityLivingBase el = lde.getEntityLiving();
		if(el instanceof EntityPlayerMP)
		{
			EntityPlayerMP mp = (EntityPlayerMP) el;
			if(mp.getGameProfile().getName().equalsIgnoreCase("zeitheron"))
			{
				int scales = 1 + mp.world.rand.nextInt(6);
				EntityItem ei = mp.dropItem(new ItemStack(ItemsTAR.ZEITH_SCALES, scales), true, false);
				ei.motionX *= .2;
				ei.motionZ *= .2;
			}
		}
	}
}