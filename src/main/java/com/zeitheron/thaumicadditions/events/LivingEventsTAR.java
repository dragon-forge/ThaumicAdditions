package com.zeitheron.thaumicadditions.events;

import com.zeitheron.hammercore.annotations.MCFBus;
import com.zeitheron.hammercore.event.FoodEatenEvent;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.thaumicadditions.api.EdibleAspect;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.items.armor.ItemMithminiteDress;
import com.zeitheron.thaumicadditions.utils.Foods;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.HashMap;
import java.util.UUID;

@MCFBus
public class LivingEventsTAR
{
	public static HashMap<Integer, Float> prevStep = new HashMap<>();

	@SubscribeEvent
	public void playerTick(PlayerTickEvent e)
	{
		if(e.phase != Phase.END)
			return;
		NBTTagCompound nbt = e.player.getEntityData();
		if(nbt.getBoolean("TAR_Flight"))
		{
			e.player.capabilities.allowFlying = true;
			nbt.setBoolean("TAR_Flight", false);
		} else if(nbt.hasKey("TAR_Flight"))
		{
			if(!e.player.capabilities.isCreativeMode)
			{
				e.player.capabilities.allowFlying = false;
				e.player.capabilities.isFlying = false;
			}
			nbt.removeTag("TAR_Flight");
		}
		if(nbt.getInteger("TAR_LockFOV") > 0)
		{
			int nl;
			nbt.setInteger("TAR_LockFOV", nl = nbt.getInteger("TAR_LockFOV") - 1);
			if(nl == 0)
				nbt.removeTag("TAR_LockFOV");
		}
		handleSpeedMods(e.player);
		IPlayerKnowledge ipk = ThaumcraftCapabilities.getKnowledge(e.player);
		if(ipk.isResearchComplete("BASEELDRITCH") && !ipk.isResearchComplete("TAR_ELDRITCH"))
			ResearchManager.completeResearch(e.player, "TAR_ELDRITCH", true);
		ItemStack chestplate = e.player.inventory.armorInventory.get(2);
		if(chestplate.isEmpty() || chestplate.getItem() != ItemsTAR.MITHMINITE_ROBE)
		{
			UUID id = UUID.fromString("6d9fc7ce-b49f-41d8-93db-8ecb26505405");
			IAttributeInstance health = e.player.getAttributeMap()
					.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
			if(health.getModifier(id) != null)
			{
				health.removeModifier(id);
				e.player.setHealth(Math.min(e.player.getHealth(), e.player.getMaxHealth()));
			}
		}
	}

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
				int scales = 2 + mp.world.rand.nextInt(7);
				EntityItem ei = mp.dropItem(new ItemStack(ItemsTAR.ZEITH_FUR, scales), true, false);
				if(ei != null)
				{
					ei.motionX *= .2;
					ei.motionZ *= .2;
				}
			}
		}
	}

	@SubscribeEvent
	public void pickupXP(PlayerPickupXpEvent e)
	{
		EntityPlayerMP mp = Cast.cast(e.getEntityPlayer(), EntityPlayerMP.class);
		if(mp != null && !mp.world.isRemote && e.getOrb() != null)
		{
			int xp = e.getOrb().getXpValue();
			ItemStack stack = mp.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			if(!stack.isEmpty() && stack.getItem() instanceof ItemMithminiteDress)
				for(EnumHand hand : EnumHand.values())
				{
					ItemStack held = mp.getHeldItem(hand);
					if(!held.isEmpty() && held.getItem().isDamageable() && held.getItem().isDamaged(held))
						held.setItemDamage(Math.max(0, held.getItemDamage() - xp));
				}
		}
	}

	@SubscribeEvent
	public void fall(LivingFallEvent e)
	{
		ItemStack boots = e.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if(!boots.isEmpty() && boots.getItem() instanceof ItemMithminiteDress)
		{
			e.setDamageMultiplier(0);
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void hurt(LivingHurtEvent e)
	{
		if(e.getEntityLiving() instanceof EntityLiving)
		{
			EntityLiving living = (EntityLiving) e.getEntityLiving();
			if(living.deathLootTable != null && !living.getEntityData().hasKey("HC_CDLT"))
				living.getEntityData().setString("HC_CDLT", living.deathLootTable.toString());
		}

		DamageSource ds = e.getSource();
		if(ds != null && ds.isFireDamage())
		{
			ItemStack chest = e.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if(!chest.isEmpty() && chest.getItem() instanceof ItemMithminiteDress)
			{
				e.setCanceled(true);
				e.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 119, 0, true, false));
			}
		}
	}

	@SubscribeEvent
	public void playerJumps(LivingJumpEvent event)
	{
		ItemStack is;
		if(event.getEntity() instanceof EntityPlayer && ((EntityPlayer) event.getEntity()).getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemMithminiteDress)
			event.getEntityLiving().motionY += 0.2750000059604645;
	}

	private static void handleSpeedMods(EntityPlayer player)
	{
		if(player.world.isRemote && (player.isSneaking() || !((player.getItemStackFromSlot(EntityEquipmentSlot.FEET)).getItem() instanceof ItemMithminiteDress)) && prevStep.containsKey(player.getEntityId()))
		{
			player.stepHeight = prevStep.get(player.getEntityId()).floatValue();
			prevStep.remove(player.getEntityId());
		}
	}
}