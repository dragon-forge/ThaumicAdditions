package org.zeith.thaumicadditions.entity;

import com.zeitheron.hammercore.utils.SoundUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.api.EdibleAspect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.awt.*;

public class EntityEssentiaShot
		extends EntityThrowable
{
	public static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityEssentiaShot.class, DataSerializers.VARINT);
	
	public final AspectList aspects;
	
	public EntityEssentiaShot(World worldIn, EntityLivingBase throwerIn, AspectList aspects)
	{
		super(worldIn, throwerIn);
		this.aspects = aspects;
	}
	
	public EntityEssentiaShot(World worldIn)
	{
		super(worldIn);
		this.aspects = new AspectList();
	}
	
	@Override
	protected void entityInit()
	{
		dataManager.register(COLOR, -1);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		aspects.writeToNBT(compound);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		aspects.readFromNBT(compound);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if(world.isRemote)
			particle();
		else
			dataManager.set(COLOR, AspectUtil.getColor(aspects, true));
	}
	
	@SideOnly(Side.CLIENT)
	public void particle()
	{
		int color = dataManager.get(COLOR);
		if(color == -1) return;
		
		Vec3d prev = new Vec3d(prevPosX, prevPosY, prevPosZ);
		Vec3d cur = getPositionVector();
		
		Vec3d vel = new Vec3d(motionX, motionY, motionZ);
		
		int seg = 4;
		for(int i = 0; i <= seg; i++)
		{
			float slide = i / (float) seg;
			TAReconstructed.proxy.getFX().spawnColorDrop(world, new Vec3d(
					MathHelper.clampedLerp(prevPosX, posX, slide),
					MathHelper.clampedLerp(prevPosY, posY, slide),
					MathHelper.clampedLerp(prevPosZ, posZ, slide)
			), vel, new Color(color));
		}
	}
	
	@Override
	protected void onImpact(RayTraceResult result)
	{
		if(ticksExisted < 2)
			return;
		if(result.entityHit instanceof EntityLivingBase && aspects != null)
		{
			AspectList aspects = new AspectList();
			for(Aspect a : this.aspects.getAspectsSortedByAmount())
				aspects.add(a, Math.round(this.aspects.getAmount(a) * 1.5F));
			EdibleAspect.execute((EntityLivingBase) result.entityHit, aspects);
		}
		if(!world.isRemote)
			SoundUtil.playSoundEffect(world, "entity.slime.squish", getPosition(), 1F, 1F, SoundCategory.PLAYERS);
		setDead();
	}
}