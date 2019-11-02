package com.zeitheron.thaumicadditions.tiles;

import java.util.Random;

import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.FrictionRotator;
import com.zeitheron.hammercore.utils.ItemStackUtil;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.api.AspectUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.world.aura.AuraHandler;

public class TileCrystalBore extends TileSyncableTickable
{
	public int minCrystals = 1;
	public final FrictionRotator rotator = new FrictionRotator();
	{
		rotator.degree = new Random().nextFloat() * 360;
	}
	
	public EnumFacing face;
	public EnumFacing oldFace;
	
	// VERY, VERY BORING!
	public int boring = 0;
	
	@Override
	public void tick()
	{
		rotator.friction = .25F;
		rotator.update();
		
		oldFace = face;
		face = WorldUtil.getFacing(loc.getState());
		
		if(canBore())
		{
			boolean isDraneable = AuraHandler.drainVis(world, pos, 1F, true) >= .02F;
			if(isDraneable)
			{
				++boring;
				rotator.speedup(.5F);
				AuraHandler.drainVis(world, pos, .02F, false);
				if(world.isRemote && atTickRate(3))
				{
					int i = 4;
					double j = .8;
					
					for(int a = 0; a < i; ++a)
					{
						double x = getPos().getX() - face.getXOffset() / 1.6 + .5 + world.rand.nextGaussian() * .02500000037252903;
						double y = getPos().getY() - face.getYOffset() / 1.6 + .5 + world.rand.nextGaussian() * .02500000037252903;
						double z = getPos().getZ() - face.getZOffset() / 1.6 + .5 + world.rand.nextGaussian() * .02500000037252903;
						FXDispatcher.INSTANCE.drawCurlyWisp(x + face.getXOffset() / 2., y + face.getYOffset() / 2., z + face.getZOffset() / 2., face.getXOffset() / 25. * j + world.rand.nextGaussian() * .0032999999821186066, face.getYOffset() / 25. * j + world.rand.nextGaussian() * .0032999999821186066, face.getZOffset() / 25. * j + world.rand.nextGaussian() * .0032999999821186066, .5F, .25F, .75F, 1F, .25F, null, 1, 0, a % 3 * 2);
						
						x = getPos().getX() - face.getXOffset() / 1.6 + .5 + world.rand.nextGaussian() * .02500000037252903;
						y = getPos().getY() - face.getYOffset() / 1.6 + .5 + world.rand.nextGaussian() * .02500000037252903;
						z = getPos().getZ() - face.getZOffset() / 1.6 + .5 + world.rand.nextGaussian() * .02500000037252903;
						FXDispatcher.INSTANCE.drawCurlyWisp(x, y, z, (double) ((float) face.getXOffset() / 25.0f * (float) j) + this.world.rand.nextGaussian() * 0.0020000000949949026, (double) ((float) face.getYOffset() / 25.0f * (float) j) + this.world.rand.nextGaussian() * 0.0020000000949949026, (double) ((float) face.getZOffset() / 25.0f * (float) j) + this.world.rand.nextGaussian() * 0.0020000000949949026, 0.25f, this.world.rand.nextFloat(), this.world.rand.nextFloat(), this.world.rand.nextFloat(), 0.5f, null, 1, 0, 1 + a % 3 * 2);
					}
					
					if(atTickRate(3))
						SoundUtil.playSoundEffect(loc, "thaumcraft:grind", .5F, 1F, SoundCategory.BLOCKS);
				}
			}
			
			if(boring >= 100)
			{
				boring = 0;
				ItemStack bored = bore();
				if(!bored.isEmpty())
				{
					EnumFacing of = face.getOpposite();
					BlockPos opos = pos.offset(of);
					if(!(bored = ItemStackUtil.inject(bored, world.getTileEntity(opos), face)).isEmpty())
					{
						Random rand = world.rand;
						if(!world.isRemote)
						{
							EntityItem ei = new EntityItem(world, pos.getX() + .5 + of.getXOffset() * .6, pos.getY() + .5 + of.getYOffset() * .6, pos.getZ() + .5 + of.getZOffset() * .6, bored.copy());
							ei.motionX = of.getXOffset() * rand.nextDouble() * 0.35;
							ei.motionY = of.getYOffset() * rand.nextDouble() * 0.35;
							ei.motionZ = of.getZOffset() * rand.nextDouble() * 0.35;
							world.spawnEntity(ei);
						}
					}
				}
				sendChangesToNearby();
			}
		}
	}
	
	public boolean canBore()
	{
		IBlockState state = world.getBlockState(pos.offset(face));
		return getLocation().getRedstone() == 0 && state.getBlock() instanceof BlockCrystal && state.getValue(BlockCrystal.SIZE) >= minCrystals;
	}
	
	public ItemStack bore()
	{
		IBlockState state = world.getBlockState(pos.offset(face));
		int co;
		if(state.getBlock() instanceof BlockCrystal && (co = state.getValue(BlockCrystal.SIZE)) >= minCrystals - 1)
		{
			state = state.withProperty(BlockCrystal.SIZE, co - 1);
			world.setBlockState(pos.offset(face), state);
			if(!world.isRemote)
				SoundUtil.playSoundEffect(world, SoundsTC.crystal.getRegistryName().toString(), pos, .3F, .8F, SoundCategory.BLOCKS);
			return AspectUtil.crystalEssence(((BlockCrystal) state.getBlock()).aspect);
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("MinCrystals", minCrystals);
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		minCrystals = nbt.getInteger("MinCrystals");
	}
}