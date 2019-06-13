package com.zeitheron.thaumicadditions.seals.woid;

import java.util.List;
import java.util.Random;

import com.zeitheron.hammercore.utils.ItemStackUtil;
import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.tiles.TileSeal;
import com.zeitheron.thaumicadditions.utils.ThaumicMath;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SealPickup extends SealInstance
{
	public SealPickup(TileSeal seal)
	{
		super(seal);
	}
	
	@Override
	public void tick()
	{
		if(!seal.atTickRate(3))
			return;
		
		WorldLocation loc = seal.getLocation();
		Random rand = loc.getWorld().rand;
		
		if(loc.getRedstone() > 0)
			return;
		
		EnumFacing to = seal.orientation;
		EnumFacing from = to.getOpposite();
		
		float mult = .7F;
		
		float modX = (rand.nextFloat() - rand.nextFloat()) * .5F;
		float modY = (rand.nextFloat() - rand.nextFloat()) * .5F;
		float modZ = (rand.nextFloat() - rand.nextFloat()) * .5F;
		
		Vec3d pos = new Vec3d(loc.getPos()).add(.5, .5, .5);
		
		BlockPos center0 = loc.getPos().offset(to, 4);
		if(to.getAxis() == Axis.Y)
			center0 = loc.getPos().offset(to, to == EnumFacing.UP ? 2 : 4);
		AxisAlignedBB suck = new AxisAlignedBB(center0).grow(5, 3, 5);
		List<EntityItem> drops = loc.getWorld().getEntitiesWithinAABB(EntityItem.class, suck);
		
		for(EntityItem ei : drops)
		{
			double moveX = ThaumicMath.cap((pos.x - ei.posX) / 30D, .0025);
			double moveY = ThaumicMath.cap((pos.y - ei.posY) / 30D, .0025);
			double moveZ = ThaumicMath.cap((pos.z - ei.posZ) / 30D, .0025);
			
			ei.setNoGravity(true);
			
			ei.motionX = moveX;
			ei.motionY = moveY;
			ei.motionZ = moveZ;
		}
	}
	
	@Override
	public void onEntityCollisionWithSeal(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if(entityIn instanceof EntityItem)
		{
			BlockPos rock = seal.getPos().offset(seal.orientation.getOpposite());
			
			for(EnumFacing face : EnumFacing.VALUES)
			{
				ItemStack it = ((EntityItem) entityIn).getItem();
				if(it.isEmpty())
				{
					entityIn.setDead();
					return;
				}
				TileEntity tile = worldIn.getTileEntity(rock.offset(face));
				((EntityItem) entityIn).setItem(it = ItemStackUtil.inject(it, tile, face.getOpposite()));
				if(it.isEmpty())
				{
					entityIn.setDead();
					return;
				}
			}
		}
	}
}