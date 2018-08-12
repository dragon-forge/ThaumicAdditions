package com.zeitheron.thaumicadditions.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySealViewer extends Entity
{
	private Minecraft mc = Minecraft.getMinecraft();
	
	private int facing;
	public boolean rendering;
	
	public EntitySealViewer(World worldIn)
	{
		super(worldIn);
	}
	
	public EntitySealViewer(World worldIn, double x, double y, double z, EnumFacing facing)
	{
		super(worldIn);
		facing = facing.getOpposite();
		this.facing = facing.ordinal();
		noClip = true;
		height = 0.001F;
		width = 0.001F;
		setPositionConsideringRotation(x, y, z, facing.ordinal());
	}
	
	@Override
	protected void entityInit()
	{
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onUpdate()
	{
		// super.onUpdate();
		setPositionConsideringRotation(posX - .5, posY - .5, posZ - .5, facing);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund)
	{
		
	}
	
	public void setPositionConsideringRotation(double x, double y, double z, int rotation)
	{
		float yaw = 0.0f;
		float pitch = 0.0f;
		switch(rotation)
		{
		case 0:
		{
			pitch = -90.0f;
			break;
		}
		case 1:
		{
			pitch = 90.0f;
			break;
		}
		case 2:
		{
			yaw = 0.0f;
			break;
		}
		case 3:
		{
			yaw = 180.0f;
			break;
		}
		case 4:
		{
			yaw = 90.0f;
			break;
		}
		case 5:
		{
			yaw = 270.0f;
			break;
		}
		}
		setPosition(x + 0.5D, y + 0.5, z + 0.5D);
		rotationYaw = 360 - yaw;
		rotationPitch = pitch;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderInPass(int pass)
	{
		return false;
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		
	}
}