package org.zeith.thaumicadditions.entity;

import com.zeitheron.hammercore.api.IProcess;
import com.zeitheron.hammercore.net.HCNet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.init.ItemsTAR;
import org.zeith.thaumicadditions.net.PacketTransfurmWolf;

import javax.annotation.Nullable;

public class EntityBlueWolf
		extends EntityAnimal
{
	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.createKey(EntityBlueWolf.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> IS_ANGRY = EntityDataManager.createKey(EntityBlueWolf.class, DataSerializers.BOOLEAN);
	/**
	 * Float used to smooth the rotation of the wolf head
	 */
	private float headRotationCourse;
	private float headRotationCourseOld;
	/**
	 * true is the wolf is wet else false
	 */
	private boolean isWet;
	/**
	 * True if the wolf is shaking else False
	 */
	private boolean isShaking;
	/**
	 * This time increases while wolf is shaking and emitting water particles.
	 */
	private float timeWolfIsShaking;
	private float prevTimeWolfIsShaking;

	public EntityBlueWolf(World worldIn)
	{
		super(worldIn);
		this.setSize(0.6F, 0.85F);
	}

	public static void trasfurmate(EntityWolf wolf)
	{
		if(!wolf.isTamed() && !wolf.isAIDisabled())
		{
			HCNet.INSTANCE.sendToAllAround(new PacketTransfurmWolf(wolf), new TargetPoint(wolf.world.provider.getDimension(), wolf.posX, wolf.posY, wolf.posZ, 128));
			wolf.setNoAI(true);
			IProcess proc = new IProcess()
			{
				int time = 0;

				@Override
				public void update()
				{
					++time;

					if(time >= 60 && !wolf.isDead)
					{
						if(wolf.getServer() != null)
							wolf.getServer().addScheduledTask(() ->
							{
								EntityBlueWolf zeith = new EntityBlueWolf(wolf.world);
								zeith.setPositionAndRotation(wolf.posX, wolf.posY, wolf.posZ, wolf.rotationYaw, wolf.rotationPitch);
								wolf.world.spawnEntity(zeith);
								wolf.setDead();
							});
					}
				}

				@Override
				public boolean isAlive()
				{
					return time < 60 && !wolf.isDead;
				}
			};
			proc.start();
		}
	}

	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(10, new EntityAILookIdle(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, AbstractSkeleton.class, false));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
	{
		super.setAttackTarget(entitylivingbaseIn);

		this.setAngry(entitylivingbaseIn != null);
	}

	@Override
	protected void updateAITasks()
	{
		this.dataManager.set(DATA_HEALTH_ID, getHealth());
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(DATA_HEALTH_ID, getHealth());
		this.dataManager.register(IS_ANGRY, false);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setBoolean("Angry", this.isAngry());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		this.setAngry(compound.getBoolean("Angry"));
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		if(this.isAngry())
		{
			return SoundEvents.ENTITY_WOLF_GROWL;
		} else if(this.rand.nextInt(3) == 0)
		{
			return SoundEvents.ENTITY_WOLF_PANT;
		} else
		{
			return SoundEvents.ENTITY_WOLF_AMBIENT;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return SoundEvents.ENTITY_WOLF_HURT;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_WOLF_DEATH;
	}

	@Override
	protected float getSoundVolume()
	{
		return 0.4F;
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable()
	{
		return LootTableList.ENTITIES_WOLF;
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if(!this.world.isRemote && this.isWet && !this.isShaking && !this.hasPath() && this.onGround)
		{
			this.isShaking = true;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
			this.world.setEntityState(this, (byte) 8);
		}

		if(!this.world.isRemote && this.getAttackTarget() == null && this.isAngry())
		{
			this.setAngry(false);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.headRotationCourseOld = this.headRotationCourse;

		this.headRotationCourse += (0.0F - this.headRotationCourse) * 0.4F;

		if(this.isWet())
		{
			this.isWet = true;
			this.isShaking = false;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
		} else if((this.isWet || this.isShaking) && this.isShaking)
		{
			if(this.timeWolfIsShaking == 0.0F)
			{
				this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
			this.timeWolfIsShaking += 0.05F;

			if(this.prevTimeWolfIsShaking >= 2.0F)
			{
				this.isWet = false;
				this.isShaking = false;
				this.prevTimeWolfIsShaking = 0.0F;
				this.timeWolfIsShaking = 0.0F;
			}

			if(this.timeWolfIsShaking > 0.4F)
			{
				float f = (float) this.getEntityBoundingBox().minY;
				int i = (int) (MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float) Math.PI) * 7.0F);

				for(int j = 0; j < i; ++j)
				{
					float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (double) f1, f + 0.8F, this.posZ + (double) f2, this.motionX, this.motionY, this.motionZ);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean isWolfWet()
	{
		return this.isWet;
	}

	@SideOnly(Side.CLIENT)
	public float getShadingWhileWet(float p_70915_1_)
	{
		return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70915_1_) / 2.0F * 0.25F;
	}

	@SideOnly(Side.CLIENT)
	public float getShakeAngle(float p_70923_1_, float p_70923_2_)
	{
		float f = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70923_1_ + p_70923_2_) / 1.8F;

		if(f < 0.0F)
		{
			f = 0.0F;
		} else if(f > 1.0F)
		{
			f = 1.0F;
		}

		return MathHelper.sin(f * (float) Math.PI) * MathHelper.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
	}

	@SideOnly(Side.CLIENT)
	public float getInterestedAngle(float p_70917_1_)
	{
		return (this.headRotationCourseOld + (this.headRotationCourse - this.headRotationCourseOld) * p_70917_1_) * 0.15F * (float) Math.PI;
	}

	@Override
	public float getEyeHeight()
	{
		return this.height * 0.8F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(this.isEntityInvulnerable(source))
		{
			return false;
		} else
		{
			Entity entity = source.getTrueSource();

			if(entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
			{
				amount = (amount + 1.0F) / 2.0F;
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn)
	{
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if(flag)
		{
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	public boolean canMateWith(EntityAnimal otherAnimal)
	{
		return false;
	}

	@Override
	public void setInLove(@Nullable EntityPlayer player)
	{
	}

	@Override
	public boolean isBreedingItem(ItemStack stack)
	{
		return false;
	}

	@Nullable
	@Override
	public EntityAgeable createChild(EntityAgeable ageable)
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id)
	{
		if(id == 8)
		{
			this.isShaking = true;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
		} else
		{
			super.handleStatusUpdate(id);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getTailRotation()
	{
		if(this.isAngry())
		{
			return 1.5393804F;
		} else
		{
			return (float) Math.PI / 5F;
		}
	}

	public boolean isAngry()
	{
		return this.dataManager.get(IS_ANGRY);
	}

	public void setAngry(boolean angry)
	{
		this.dataManager.set(IS_ANGRY, angry);
	}

	public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
	{
		if(!(target instanceof EntityCreeper) && !(target instanceof EntityGhast))
		{
			if(target instanceof EntityWolf)
			{
				EntityWolf entitywolf = (EntityWolf) target;

				if(entitywolf.isTamed() && entitywolf.getOwner() == owner)
				{
					return false;
				}
			}

			if(target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target))
			{
				return false;
			} else
			{
				return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
			}
		} else
		{
			return false;
		}
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		entityDropItem(new ItemStack(ItemsTAR.ZEITH_FUR, 1 + rand.nextInt(4)), 0.0F);
	}
}