package com.zeitheron.thaumicadditions.entity;

import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.api.EdibleAspect;
import com.zeitheron.thaumicadditions.api.fx.TARParticleTypes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class EntityEssentiaShot extends EntityThrowable
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
		if(color != -1)
			world.spawnParticle(TARParticleTypes.COLOR_DROP, posX, posY, posZ, 0, 0, 0, color);
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