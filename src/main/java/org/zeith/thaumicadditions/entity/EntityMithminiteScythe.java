package org.zeith.thaumicadditions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.client.fx.ParticleFX;
import org.zeith.thaumicadditions.utils.ThaumicHelper;

import java.util.List;

public class EntityMithminiteScythe
		extends EntityThrowable
{
	public static final DataParameter<Integer> DISPOSE_TIME = EntityDataManager.createKey(EntityMithminiteScythe.class, DataSerializers.VARINT);
	public static final DataParameter<Float> SHOOT_YAW = EntityDataManager.createKey(EntityMithminiteScythe.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> SHOOT_PITCH = EntityDataManager.createKey(EntityMithminiteScythe.class, DataSerializers.FLOAT);
	int maxDisposeTime = 15;
	boolean hasHitEntity;

	public EntityMithminiteScythe(World worldIn)
	{
		super(worldIn);
	}

	public EntityMithminiteScythe(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
	}

	public EntityMithminiteScythe(World worldIn, EntityLivingBase throwerIn)
	{
		super(worldIn, throwerIn);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		setSize(2, 2);
		dataManager.register(DISPOSE_TIME, 120);
		dataManager.register(SHOOT_YAW, 0F);
		dataManager.register(SHOOT_PITCH, 0F);
	}

	@Override
	public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
	{
		dataManager.set(SHOOT_YAW, rotationYawIn);
		dataManager.set(SHOOT_PITCH, rotationPitchIn);
		super.shoot(entityThrower, rotationPitchIn, rotationYawIn, pitchOffset, velocity, inaccuracy);
	}

	public float getShootYaw()
	{
		return dataManager.get(SHOOT_YAW);
	}

	public float getShootPitch()
	{
		return dataManager.get(SHOOT_PITCH);
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0F;
	}

	@Override
	public void onUpdate()
	{
		Entity entity = null;
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
		double d0 = 0.0D;
		boolean flag = false;

		for(int i = 0; i < list.size(); ++i)
		{
			Entity entity1 = list.get(i);

			if(entity1.canBeCollidedWith())
			{
				if(entity1 == this.ignoreEntity)
				{
					flag = true;
				} else if(this.thrower != null && this.ticksExisted < 2 && this.ignoreEntity == null)
				{
					this.ignoreEntity = entity1;
					flag = true;
				} else
				{
					flag = false;
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
					boolean intersection = axisalignedbb.intersects(getEntityBoundingBox().grow(1));

					if(intersection)
					{
						onImpact(new RayTraceResult(entity1));
						break;
					}
				}
			}
		}

		super.onUpdate();
		if(world.isRemote) fx();
		if(ticksExisted > dataManager.get(DISPOSE_TIME))
			setDead();
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key)
	{
		super.notifyDataManagerChange(key);
		if(key == DISPOSE_TIME)
		{
			maxDisposeTime = Math.min(dataManager.get(DISPOSE_TIME) - ticksExisted, 15);
		}
	}

	public float getAlpha(float partialTime)
	{
		if(maxDisposeTime == 0) return 0F;
		int time = dataManager.get(DISPOSE_TIME);
		return MathHelper.clamp(time - ticksExisted, 0, maxDisposeTime) / (float) maxDisposeTime;
	}

	@Override
	protected void onImpact(RayTraceResult result)
	{
		if(world.isRemote) return;
		if(result.entityHit instanceof EntityLivingBase && !hasHitEntity)
		{
			EntityLivingBase thrower = getThrower();
			if(thrower != null && thrower.getEntityId() == result.entityHit.getEntityId()) return;
			if(((EntityLivingBase) result.entityHit).getHealth() > 0)
			{
				result.entityHit.attackEntityFrom(ThaumicHelper.createLivingDamageSource(thrower).setDamageBypassesArmor(), 14F);
				hasHitEntity = true;
				dataManager.set(DISPOSE_TIME, Math.min(dataManager.get(DISPOSE_TIME), Math.min(ticksExisted + 15, 60)));
			}
		}
		if(result.typeOfHit == Type.BLOCK)
		{
			hasHitEntity = true;
			dataManager.set(DISPOSE_TIME, Math.min(dataManager.get(DISPOSE_TIME), Math.min(ticksExisted + 15, 60)));
		}
	}

	@SideOnly(Side.CLIENT)
	public void fx()
	{
		boolean disappear = getAlpha(0F) < 1F;

		if(disappear || ticksExisted % 2 == 0)
		{
			for(int i = 0; i < (disappear ? 4 : 1); ++i)
			{
				ParticleFX fx = new ParticleFX(world, getPositionVector().add((rand.nextFloat() - rand.nextFloat()), (rand.nextFloat() - rand.nextFloat()), (rand.nextFloat() - rand.nextFloat())), 1);
				fx.lightStrength = disappear ? 3F : 1.5F;
				fx.lightColor = new Vec3d(1F, 0.5F, 1F);
				fx.spawn();
			}
		}
	}
}