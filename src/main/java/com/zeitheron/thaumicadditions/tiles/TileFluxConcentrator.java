package com.zeitheron.thaumicadditions.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.FrictionRotator;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.api.RecipesFluxConcentrator;
import com.zeitheron.thaumicadditions.utils.BlockSideHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.FXDispatcher;

public class TileFluxConcentrator extends TileSyncableTickable implements IAspectContainer, IEssentiaTransport
{
	public static final int CAPACITY = 10;
	public int vitium;
	
	public int fuel, cooldown;
	List<BlockPos> fluxed = new ArrayList<>();
	EnumFacing facing;
	
	Map<BlockPos, IBlockState> cache = new HashMap<>();
	
	public final FrictionRotator valve = new FrictionRotator();
	{
		valve.friction = .1F;
	}
	
	@Override
	public void tick()
	{
		valve.update();
		
		if(fuel <= 10 && vitium > 0)
		{
			--vitium;
			fuel += 10;
			sendChangesToNearby();
		}
		
		if(cooldown > 0)
			--cooldown;
		
		facing = WorldUtil.getFacing(world.getBlockState(pos));
		if(!world.isRemote)
		{
			EnumFacing rf = facing.getOpposite();
			
			IEssentiaTransport l = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(world, pos, rf.getOpposite());
			
			if(l != null && l.canOutputTo(rf))
			{
				Aspect lasp = l.getEssentiaType(rf);
				if(lasp == Aspect.FLUX)
				{
					int amt = CAPACITY - vitium;
					int taken;
					if(amt > 0 && (taken = l.takeEssentia(lasp, amt, rf)) > 0)
					{
						vitium += taken;
						sendChangesToNearby();
					}
				}
			}
		}
		
		if(world.isRemote && fuel > 0 && !fluxed.isEmpty())
		{
			valve.speedup(.25F);
			for(int i = 0; i < Math.min(fluxed.size() / 2, 5); ++i)
			{
				BlockPos sel = fluxed.get(getRNG().nextInt(fluxed.size()));
				FXDispatcher.INSTANCE.drawTaintParticles(sel.getX() + getRNG().nextFloat(), sel.getY() + getRNG().nextFloat(), sel.getZ() + getRNG().nextFloat(), (getRNG().nextFloat() - getRNG().nextFloat()) / 10F, (getRNG().nextFloat() - getRNG().nextFloat()) / 10F, (getRNG().nextFloat() - getRNG().nextFloat()) / 10F, 0.75F);
			}
		}
		
		if(getLocation().getRedstone() > 0)
		{
			if(!world.isRemote && !fluxed.isEmpty() && atTickRate(5))
			{
				BlockPos furthest = fluxed.get(0);
				for(BlockPos pos : fluxed)
					if(pos.distanceSq(getPos()) > furthest.distanceSq(getPos()))
						furthest = pos;
				fluxed.remove(furthest);
				sendChangesToNearby();
			}
			return;
		}
		
		if(!world.isRemote && !fluxed.isEmpty() && atTickRate(600) && getRNG().nextBoolean())
		{
			BlockPos sel = fluxed.get(getRNG().nextInt(fluxed.size()));
			AuraHelper.polluteAura(world, sel, .5F + getRNG().nextFloat() * .5F, true);
		}
		
		if(atTickRate(20) && fuel > 0 && !world.isRemote)
		{
			if(fluxed.isEmpty())
			{
				fluxed.add(this.pos.offset(facing.getOpposite()));
				cache.put(fluxed.get(0), world.getBlockState(fluxed.get(0)));
			}
			int fs = fluxed.size();
			
			int mx = Integer.MAX_VALUE;
			int my = Integer.MAX_VALUE;
			int mz = Integer.MAX_VALUE;
			int xx = Integer.MIN_VALUE;
			int xy = Integer.MIN_VALUE;
			int xz = Integer.MIN_VALUE;
			
			boolean changed = false;
			for(int i = 0; i < fs; ++i)
			{
				BlockPos pos = fluxed.get(i);
				
				mx = Math.min(pos.getX(), mx);
				my = Math.min(pos.getY(), my);
				mz = Math.min(pos.getZ(), mz);
				xx = Math.max(pos.getX(), xx);
				xy = Math.max(pos.getY(), xy);
				xz = Math.max(pos.getZ(), xz);
				
				for(int j = pos.getY(); j < world.getHeight(); ++j)
				{
					BlockPos ap = pos.up(j - pos.getY());
					if(!BlockSideHelper.isPassable(world, ap, EnumFacing.UP))
						break;
					if(j == world.getHeight() - 1)
					{
						fluxed.clear();
						return;
					}
				}
				
				if(pos.distanceSq(getPos()) < 256)
				{
					IBlockState cached = cache.get(pos);
					IBlockState state = world.getBlockState(pos);
					if(!Objects.equals(cached, state))
						changed = true;
					
					BlockPos targ;
					for(EnumFacing face : EnumFacing.VALUES)
						if(BlockSideHelper.isPassable(world, pos, face) && !fluxed.contains(targ = pos.offset(face)))
						{
							IBlockState rs = world.getBlockState(targ);
							if(BlockSideHelper.isPassable(world, targ, face.getOpposite()))
							{
								fluxed.add(targ);
								cache.put(targ, rs);
								--fuel;
								sendChangesToNearby();
							}
						}
				}
			}
			if(changed)
				redefine();
			
			for(EntityLivingBase elb : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(mx, my, mz, xx + 1, xy + 1, xz + 1)))
				if(fluxed.contains(elb.getPosition()))
				{
					elb.addPotionEffect(new PotionEffect(PotionVisExhaust.instance, 219));
					if(atTickRate(30))
						elb.attackEntityFrom(DamageSourceThaumcraft.dissolve, 1F);
					if(fuel >= 5)
						fuel -= 5;
				}
			
			if(cooldown <= 0 && fuel >= 10)
			{
				cooldown += 200;
				for(int i = 0; i < fluxed.size(); ++i)
				{
					BlockPos pos = fluxed.get(i);
					if(RecipesFluxConcentrator.handle(world, pos))
					{
						cooldown += 400;
						fuel -= 10;
						sendChangesToNearby();
						break;
					}
				}
			}
		}
	}
	
	public void redefine()
	{
		fluxed.clear();
		cache.clear();
		
		fluxed.add(this.pos.offset(facing.getOpposite()));
		cache.put(fluxed.get(0), world.getBlockState(fluxed.get(0)));
		
		for(int i = 0; i < fluxed.size(); ++i)
		{
			BlockPos pos = fluxed.get(i);
			
			for(int j = pos.getY(); j < world.getHeight(); ++j)
			{
				BlockPos ap = pos.up(j - pos.getY());
				if(!BlockSideHelper.isPassable(world, ap, EnumFacing.UP))
					break;
				if(j == world.getHeight() - 1)
				{
					fluxed.clear();
					return;
				}
			}
			
			if(pos.distanceSq(getPos()) < 256)
			{
				IBlockState state = world.getBlockState(pos);
				BlockPos targ;
				for(EnumFacing face : EnumFacing.VALUES)
					if(BlockSideHelper.isPassable(world, pos, face) && !fluxed.contains(targ = pos.offset(face)))
					{
						IBlockState rs = world.getBlockState(targ);
						if(BlockSideHelper.isPassable(world, targ, face.getOpposite()))
						{
							fluxed.add(targ);
							cache.put(targ, rs);
							sendChangesToNearby();
						}
					}
			}
		}
	}
	
	public List<BlockPos> getFluxed()
	{
		return fluxed;
	}
	
	public boolean isFluxed(BlockPos pos)
	{
		return fluxed.contains(pos);
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Flux", vitium);
		nbt.setInteger("Fuel", fuel);
		nbt.setInteger("Cooldown", cooldown);
		
		NBTTagList fps = new NBTTagList();
		for(int i = 0; i < fluxed.size(); ++i)
			fps.appendTag(new NBTTagLong(fluxed.get(i).toLong()));
		nbt.setTag("FPS", fps);
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		vitium = nbt.getInteger("Flux");
		fuel = nbt.getInteger("Fuel");
		cooldown = nbt.getInteger("Cooldown");
		
		NBTTagList fps = nbt.getTagList("FPS", NBT.TAG_LONG);
		fluxed.clear();
		for(int i = 0; i < fps.tagCount(); ++i)
			fluxed.add(BlockPos.fromLong(((NBTTagLong) fps.get(i)).getLong()));
	}
	
	@Override
	public AspectList getAspects()
	{
		return new AspectList().add(Aspect.FLUX, vitium);
	}
	
	@Override
	public void setAspects(AspectList list)
	{
		vitium = list.getAmount(Aspect.FLUX);
	}
	
	@Override
	public boolean doesContainerAccept(Aspect asp)
	{
		return asp == Aspect.FLUX && vitium < CAPACITY;
	}
	
	@Override
	public int addToContainer(Aspect asp, int amt)
	{
		if(asp == Aspect.FLUX)
		{
			int ma = Math.min(amt, CAPACITY - vitium);
			vitium += ma;
			sendChangesToNearby();
			return ma;
		}
		return 0;
	}
	
	@Override
	public boolean takeFromContainer(Aspect asp, int amt)
	{
		return false;
	}
	
	@Override
	public boolean takeFromContainer(AspectList list)
	{
		return false;
	}
	
	@Override
	public boolean doesContainerContainAmount(Aspect asp, int amt)
	{
		return asp == Aspect.FLUX && vitium >= amt;
	}
	
	@Override
	public boolean doesContainerContain(AspectList list)
	{
		return false;
	}
	
	@Override
	public int containerContains(Aspect asp)
	{
		return asp == Aspect.FLUX ? vitium : 0;
	}
	
	@Override
	public boolean isConnectable(EnumFacing face)
	{
		return facing == face;
	}
	
	@Override
	public boolean canInputFrom(EnumFacing face)
	{
		return isConnectable(face);
	}
	
	@Override
	public boolean canOutputTo(EnumFacing face)
	{
		return false;
	}
	
	@Override
	public void setSuction(Aspect var1, int var2)
	{
		
	}
	
	@Override
	public Aspect getSuctionType(EnumFacing face)
	{
		return isConnectable(face) ? Aspect.FLUX : null;
	}
	
	@Override
	public int getSuctionAmount(EnumFacing face)
	{
		return isConnectable(face) ? 128 : 0;
	}
	
	@Override
	public int takeEssentia(Aspect asp, int amt, EnumFacing face)
	{
		return 0;
	}
	
	@Override
	public int addEssentia(Aspect asp, int amt, EnumFacing face)
	{
		return addToContainer(asp, amt);
	}
	
	@Override
	public Aspect getEssentiaType(EnumFacing face)
	{
		return Aspect.FLUX;
	}
	
	@Override
	public int getEssentiaAmount(EnumFacing face)
	{
		return vitium;
	}
	
	@Override
	public int getMinimumSuction()
	{
		return 0;
	}
}