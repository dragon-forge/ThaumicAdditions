package org.zeith.thaumicadditions.entity;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.InterItemStack;
import com.zeitheron.hammercore.utils.ItemInsertionUtil;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.init.ItemsTAR;
import org.zeith.thaumicadditions.inventory.container.ContainerChester;
import org.zeith.thaumicadditions.items.tools.ItemBoneEye;
import org.zeith.thaumicadditions.net.PacketSyncEntity;
import org.zeith.thaumicadditions.net.PacketSyncTrunk;
import thaumcraft.api.aura.AuraHelper;

import java.util.List;
import java.util.UUID;

public class EntityChester
		extends EntityAnimal
{
	public static final DataParameter<Float> LID_ROTATION = EntityDataManager.createKey(EntityChester.class, DataSerializers.FLOAT);
	public final InventoryDummy inventory = new InventoryDummy(36);

	public boolean open;
	public int angerLevel, attackTime;
	public boolean staying;
	public float prevLidRotation;
	public UUID owner;
	public float field_768_a;
	public float field_767_b;
	private int jumpDelay;
	private int eatDelay;

	public EntityChester(World worldIn)
	{
		super(worldIn);
		setSize(.8F, .8F);
	}

	public EntityChester(World world, EntityPlayer player, double x, double y, double z)
	{
		this(world);
		owner = player.getGameProfile().getId();
		setPositionAndUpdate(x, y, z);
	}

	public float getLidRotation()
	{
		return this.dataManager.get(LID_ROTATION).floatValue();
	}

	public void setLidRotation(float lidRotation)
	{
		this.dataManager.set(LID_ROTATION, lidRotation);
	}

	public float getCurrentLidRotation(float partialTime)
	{
		float c = getLidRotation();
		return prevLidRotation + (c - prevLidRotation) * partialTime;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(LID_ROTATION, 0F);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		staying = nbt.getBoolean("Stay");
		inventory.readFromNBT(nbt.getCompoundTag("Items"));
		owner = nbt.getUniqueId("Owner");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("Stay", staying);
		nbt.setTag("Items", inventory.writeToNBT(new NBTTagCompound()));
		nbt.setUniqueId("Owner", owner);
		return super.writeToNBT(nbt);
	}

	public void resizeInventory(int slots)
	{
		NonNullList<ItemStack> nev = NonNullList.withSize(slots, ItemStack.EMPTY);
		int min = Math.min(inventory.getSizeInventory(), slots);
		for(int i = 0; i < min; ++i)
			nev.set(i, inventory.inventory.get(i));
		inventory.inventory = nev;
	}

	@Override
	public boolean isChild()
	{
		return false;
	}

	@Override
	public void addPotionEffect(PotionEffect potioneffectIn)
	{
	}

	@Override
	protected float applyPotionDamageCalculations(DamageSource source, float damage)
	{
		return 0F;
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public boolean canMateWith(EntityAnimal otherAnimal)
	{
		return false;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable)
	{
		return null;
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier)
	{
		inventory.drop(world, getPosition());
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(source == DamageSource.OUT_OF_WORLD && !isDead)
			setDead();
		return false;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		return source != DamageSource.OUT_OF_WORLD;
	}

	@Override
	protected void despawnEntity()
	{
		// Empty to prevent ANY POSSIBLE despawning of the entity
	}

	@Override
	public void setDead()
	{
		if(!isDead)
		{
			inventory.drop(world, getPosition());
			WorldUtil.spawnItemStack(world, posX, posY, posZ, new ItemStack(ItemsTAR.CHESTER));
		}
		super.setDead();
	}

	public void eatItems()
	{
		if(getHealth() <= 25 && eatDelay == 0)
			for(int a = 0; a < inventory.getSizeInventory(); ++a)
			{
				if(inventory.getStackInSlot(a).isEmpty() || !(inventory.getStackInSlot(a).getItem() instanceof ItemFood))
					continue;
				ItemFood itemfood = (ItemFood) inventory.getStackInSlot(a).getItem();
				// heal(itemfood.getHealAmount(inventory.getStackInSlot(a)));
				inventory.getStackInSlot(a).shrink(1);
				eatDelay = 10 + rand.nextInt(15);
				if(getHealth() == 50)
					HammerCore.audioProxy.playSoundAt(world, "random.burp", getPosition(), .5F, rand.nextFloat() * .5F + .5F, SoundCategory.AMBIENT);
				else
					HammerCore.audioProxy.playSoundAt(world, "random.eat", getPosition(), .5F, rand.nextFloat() * .5F + .5F, SoundCategory.AMBIENT);
				showHeartsOrSmokeFX(true);
				setLidRotation(.15F);
				break;
			}

		// if(hasUpgrade(ItemUpgrade.idFromItem(ItemsLT.STABILIZED_SINGULARITY)))
		// pullItems();
	}

	private void pullItems()
	{
		if(eatDelay > 0)
			return;

		List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(posX - .5, posY - .5, posZ - .5, posX + .5, posY + .5, posZ + .5));

		for(EntityItem ei : items)
		{
			ItemStack leftover = ItemInsertionUtil.putStackInInventoryAllSlots(inventory, ei.getItem().copy(), EnumFacing.UP);
			boolean changed = InterItemStack.isStackNull(leftover) || !leftover.isItemEqual(ei.getItem());
			if(InterItemStack.isStackNull(leftover))
				ei.setDead();
			else
				ei.setItem(leftover);
			if(changed)
			{
				eatDelay += 2 + rand.nextInt(4);
				return;
			}
		}
	}

	@Override
	public void onUpdate()
	{
		field_767_b = field_768_a;
		boolean flag = onGround;
		super.onUpdate();
		updateEntity();
		eatItems();
		if(onGround && !flag)
			field_768_a = -0.5f;
		field_768_a *= 0.6f;
		prevLidRotation = getLidRotation();
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		if(!player.getGameProfile().getId().equals(owner))
			if(!world.isRemote)
			{
				player.sendMessage(new TextComponentTranslation("chat." + InfoTAR.MOD_ID + ":chester.not_master"));
				return true;
			}

		ItemStack held = player.getHeldItem(hand);

		if(!held.isEmpty() && held.getItem() instanceof ItemFood && getHealth() < 50F)
		{
			// heal(((ItemFood) held.getItem()).getHealAmount(held));
			held.shrink(1);
			if(getHealth() == 50)
				HammerCore.audioProxy.playSoundAt(world, "random.burp", getPosition(), .5F, rand.nextFloat() * .5F + .5F, SoundCategory.AMBIENT);
			else
				HammerCore.audioProxy.playSoundAt(world, "random.eat", getPosition(), .5F, rand.nextFloat() * .5F + .5F, SoundCategory.AMBIENT);
			showHeartsOrSmokeFX(true);
			setLidRotation(.15F);

			return true;
		}

		// if(player.isSneaking() && InteractionEvents.isWandReversal(held))
		// {
		// if(!dropUpgrade(player))
		// {
		// if(!world.isRemote)
		// {
		// inventory.drop(world, getPosition());
		// setDead();
		// }
		// WorldUtil.spawnItemStack(world, posX, posY, posZ,
		// EnumMultiMaterialType.TRAVELING_TRUNK.stack());
		// }
		// ItemWand.removeVis(held, .1F);
		// if(!world.isRemote)
		// HammerCore.audioProxy.playSoundAt(world, InfoLT.MOD_ID + ":zap",
		// getPosition(), .5F, 1F, SoundCategory.PLAYERS);
		// player.swingArm(hand);
		// setTrunkType();
		// return true;
		// }

		if(!held.isEmpty() && held.getItem() instanceof ItemBoneEye && !world.isRemote)
		{
			if(player.isSneaking())
			{
				setDead();
				HammerCore.audioProxy.playSoundAt(world, "thaumcraft:zap", getPosition(), .5F, 1F, SoundCategory.PLAYERS);
			} else
			{
				staying = !staying;
				HammerCore.audioProxy.playSoundAt(world, "thaumcraft:key", getPosition(), .5F, 1F, SoundCategory.PLAYERS);
				player.sendStatusMessage(new TextComponentTranslation("chat." + InfoTAR.MOD_ID + ":chester." + (staying ? "stay" : "follow")), true);
				HCNet.INSTANCE.sendToAllAround(new PacketSyncEntity(this), new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32));
			}
			player.swingArm(hand);
			return true;
		}

		// if(!held.isEmpty() && held.getItem() instanceof ItemUpgrade)
		// {
		// int up = ItemUpgrade.idFromItem((ItemUpgrade) held.getItem());
		// if(canAcceptUpgrade(up))
		// if(setUpgrade(up))
		// {
		// if(!world.isRemote)
		// HammerCore.audioProxy.playSoundAt(world, InfoLT.MOD_ID + ":upgrade",
		// getPosition(), .5F, 1F, SoundCategory.PLAYERS);
		// player.swingArm(hand);
		// player.getHeldItem(hand).shrink(1);
		// setTrunkType();
		// }
		// return true;
		// }

		open = true;

		if(player instanceof EntityPlayerMP && !world.isRemote)
		{
			HCNet.INSTANCE.sendTo(new PacketSyncTrunk(this), (EntityPlayerMP) player);
			HammerCore.audioProxy.playSoundAt(world, "block.chest.open", getPosition(), .1F, rand.nextFloat() * .1F + .9F, SoundCategory.AMBIENT);
		}

		player.openContainer = new ContainerChester(this, player);
		player.swingArm(hand);

		return true;
	}

	void showHeartsOrSmokeFX(boolean flag)
	{
		EnumParticleTypes s = EnumParticleTypes.HEART;
		int amount = 1;

		if(!flag)
		{
			s = EnumParticleTypes.EXPLOSION_NORMAL;
			amount = 7;
		}

		for(int i = 0; i < amount; ++i)
		{
			double d = rand.nextGaussian() * 0.02;
			double d1 = rand.nextGaussian() * 0.02;
			double d2 = rand.nextGaussian() * 0.02;
			world.spawnParticle(s, posX + rand.nextFloat() * width * 2 - width, posZ + .5 + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2 - width, d, d1, d2);
		}
	}

	protected void updateEntity()
	{
		boolean sync = false;

		if(angerLevel > 0)
			--angerLevel;
		if(eatDelay > 0)
			--eatDelay;
		if(attackTime > 0)
			--attackTime;

		MinecraftServer mc = getServer();
		fallDistance = 0F;
		EntityPlayer entityplayer = mc != null ? mc.getPlayerList().getPlayerByUUID(owner) : null;
		if(entityplayer != null)
		{
			if(!(entityplayer.openContainer instanceof ContainerChester) && open)
			{
				open = false;
				HammerCore.audioProxy.playSoundAt(world, "block.chest.close", getPosition(), .1F, rand.nextFloat() * .1F + .9F, SoundCategory.AMBIENT);
			}

			EntityLivingBase entity;
			List<EntityLivingBase> list;

			tp:
			if(!staying && entityplayer != null && (getDistance(entityplayer) > 20 || (inWater && getDistance(entityplayer) > 8 && !entityplayer.isInWater())))
			{
				int i = MathHelper.floor(entityplayer.posX) - 2;
				int j = MathHelper.floor(entityplayer.posZ) - 2;
				int k = MathHelper.floor(entityplayer.posY);

				for(int l = 0; l <= 4; ++l)
					for(int i1 = 0; i1 <= 4; ++i1)
					{
						if(l >= 1 && i1 >= 1 && l <= 3 && i1 <= 3 || !world.isBlockNormalCube(new BlockPos(i + l, k - 1, j + i1), true) || world.isBlockNormalCube(new BlockPos(i + l, k, j + i1), true) || world.isBlockNormalCube(new BlockPos(i + l, k + 1, j + i1), true))
							continue;
						HammerCore.audioProxy.playSoundAt(world, "entity.endermen.teleport", i + l + .5, k, j + i1 + .5, .5F, 1, SoundCategory.AMBIENT);
						setPositionAndUpdate(i + l + .5, k, j + i1 + .5);
						showHeartsOrSmokeFX(false);
						setAttackTarget(null);
						angerLevel = 0;
						AuraHelper.polluteAura(world, getPosition(), .1F, true);
						break tp;
					}
			}

			// if((angerLevel == 0 || getAttackingEntity() == null) &&
			// hasUpgrade(ItemUpgrade.idFromItem(ItemsLT.STABILIZED_SINGULARITY))
			// && !(list = world.getEntitiesWithinAABB(EntityLivingBase.class,
			// new AxisAlignedBB(posX, posY, posZ, posX + 1, posY + 1, posZ +
			// 1).expand(16.0, 4.0, 16.0))).isEmpty() && (entity =
			// list.get(rand.nextInt(list.size()))) instanceof IMob &&
			// canEntityBeSeen(entity))
			// {
			// angerLevel = 600;
			// setAttackTarget(entity);
			// }

			boolean move = false;

			if(angerLevel > 0 && getAttackTarget() != null && getAttackTarget() != entityplayer)
			{
				faceEntity(getAttackTarget(), 10, 20);
				move = true;

				if(attackTime <= 0 && getDistanceSq(getAttackTarget()) < 1.5 && getAttackTarget().getEntityBoundingBox().maxY > getEntityBoundingBox().minY && getAttackTarget().getEntityBoundingBox().minY < getEntityBoundingBox().maxY)
				{
					float damage = 0;
					attackTime = 10 + rand.nextInt(5);
					// float by = 4 + upgrades[0] != -1 ? 1 : (byte0 = 0 +
					// upgrades[1] != -1 ? 1 : 0);
					// if(hasUpgrade(ItemUpgrade.idFromItem(ItemsLT.HARNESSED_RAGE)))
					// damage = damage + 1;
					if(damage > 0F)
						getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), damage);
					setLidRotation(getLidRotation() + .015F);
					HammerCore.audioProxy.playSoundAt(world, "entity.blaze.hit", getPosition(), .5F, rand.nextFloat() * .1F + .9F, SoundCategory.HOSTILE);
				}

				if(getAttackTarget().isDead)
				{
					setAttackTarget(null);
					angerLevel = 5;
				}
			}

			if(entityplayer != null && getDistance(entityplayer) > 4 && angerLevel == 0 && !staying)
			{
				faceEntity(entityplayer, 10, 20);
				move = true;
			}

			if(onGround && jumpDelay-- <= 0 && move)
			{
				jumpDelay = rand.nextInt(10) + 5;
				jumpDelay /= 3;

				isJumping = true;
				field_768_a = 1;

				// moveStrafing = 1 - rand.nextFloat() * 2;
				// moveForward = 6 +
				// (hasUpgrade(ItemUpgrade.idFromItem(ItemsLT.QUICKSILVER_CORE))
				// ? 2 : 0);

				moveStrafing = 0;
				moveForward = 0;

				jumpMovementFactor = .03F;

				double div = .2;
				motionX = com.zeitheron.hammercore.utils.math.MathHelper.clip((entityplayer.posX - posX) / 16D, -div, div);
				motionZ = com.zeitheron.hammercore.utils.math.MathHelper.clip((entityplayer.posZ - posZ) / 16D, -div, div);

				jump();

				HammerCore.audioProxy.playSoundAt(world, "block.chest.close", getPosition(), .1F, rand.nextFloat() * .1F + .9F, SoundCategory.AMBIENT);
			} else
			{
				isJumping = false;
				if(motionY < 0.0 || open)
					if(getLidRotation() < .5F)
					{
						setLidRotation(getLidRotation() + .015F);
						sync = true;
					}
				if(getLidRotation() > .5F)
				{
					setLidRotation(.5F);
					sync = true;
				}
				if(onGround)
				{
					moveForward = 0;
					moveStrafing = 0;
					if(!open)
					{
						if(getLidRotation() > 0)
						{
							setLidRotation(getLidRotation() - .1F);
							sync = true;
						}

						if(getLidRotation() < 0F)
						{
							setLidRotation(0);
							sync = true;
						}
					}
				}
			}

			if(open)
			{
				setLidRotation(getLidRotation() + .035F);
				sync = true;
			}

			if(getLidRotation() > .5F)
			{
				setLidRotation(.5F);
				sync = true;
			}
		}

		if(sync && !world.isRemote)
			HCNet.INSTANCE.sendToAllAround(new PacketSyncEntity(this), new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32));
	}

	private float updateRotation(float f, float f1, float f2)
	{
		float f3;
		for(f3 = f1 - f; f3 < -180; f3 += 360)
			;
		while(f3 >= 180)
			f3 -= 360;
		if(f3 > f2)
			f3 = f2;
		if(f3 < -f2)
			f3 = -f2;
		return f + f3;
	}
}