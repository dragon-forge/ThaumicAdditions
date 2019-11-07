package com.zeitheron.thaumicadditions.tiles;

import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import com.zeitheron.hammercore.tile.ITileDroppable;
import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import com.zeitheron.thaumicadditions.api.items.IAspectChargableItem;
import com.zeitheron.thaumicadditions.init.BlocksTAR;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileEssentiaSink extends TileSyncableTickable implements ITileDroppable, IEssentiaTransport, IAspectContainer
{
	public final InventoryDummy inventory = new InventoryDummy(1);
	{
		inventory.inventoryStackLimit = 1;
	}
	
	IAspectChargableItem aci;
	EnumFacing essentiaFace = null;
	
	@Override
	public void tick()
	{
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == BlocksTAR.ESSENTIA_SINK)
			essentiaFace = state.getValue(IBlockHorizontal.FACING).rotateYCCW();
		else
		{
			world.destroyBlock(pos, true);
			return;
		}
		
		if(!world.isRemote && essentiaFace != null && aci != null)
		{
			EnumFacing rf = essentiaFace.getOpposite();
			IEssentiaTransport l = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(world, pos, essentiaFace);
			
			if(l != null && l.canOutputTo(rf))
			{
				Aspect lasp = l.getEssentiaType(rf);
				if(lasp == Aspect.ELDRITCH && l.getEssentiaAmount(rf) > 0 && aci.canAcceptAspect(inventory.getStackInSlot(0), lasp))
				{
					int amt = aci.acceptAspect(inventory.getStackInSlot(0), lasp, 1);
					if(amt > 0 && l.takeEssentia(lasp, amt, rf) > 0)
						sendChangesToNearby();
				}
			}
		}
		
		ItemStack stack = inventory.getStackInSlot(0);
		if(!stack.isEmpty() && stack.getItem() instanceof IAspectChargableItem)
			aci = (IAspectChargableItem) stack.getItem();
		else
			aci = null;
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setTag("Items", inventory.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		inventory.readFromNBT(nbt.getCompoundTag("Items"));
	}
	
	@Override
	public void createDrop(EntityPlayer player, World world, BlockPos pos)
	{
		inventory.drop(world, pos);
	}
	
	@Override
	public boolean isConnectable(EnumFacing face)
	{
		return essentiaFace == face;
	}
	
	@Override
	public boolean canInputFrom(EnumFacing face)
	{
		return essentiaFace == face;
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
		return aci != null ? aci.getCurrentRequest(inventory.getStackInSlot(0)) : null;
	}
	
	@Override
	public int getSuctionAmount(EnumFacing face)
	{
		return getSuctionType(face) != null ? 120 : 0;
	}
	
	@Override
	public int takeEssentia(Aspect var1, int var2, EnumFacing var3)
	{
		return 0;
	}
	
	@Override
	public int addEssentia(Aspect var1, int var2, EnumFacing var3)
	{
		return 0;
	}
	
	@Override
	public Aspect getEssentiaType(EnumFacing face)
	{
		return aci != null ? aci.getCurrentRequest(inventory.getStackInSlot(0)) : null;
	}
	
	@Override
	public int getEssentiaAmount(EnumFacing face)
	{
		return 0;
	}
	
	@Override
	public int getMinimumSuction()
	{
		return 0;
	}
	
	@Override
	public AspectList getAspects()
	{
		return aci != null ? aci.getHeldAspects(inventory.getStackInSlot(0)) : new AspectList();
	}
	
	@Override
	public void setAspects(AspectList var1)
	{
	}
	
	@Override
	public boolean doesContainerAccept(Aspect var1)
	{
		return false;
	}
	
	@Override
	public int addToContainer(Aspect var1, int var2)
	{
		return 0;
	}
	
	@Override
	public boolean takeFromContainer(Aspect var1, int var2)
	{
		return false;
	}
	
	@Override
	public boolean takeFromContainer(AspectList var1)
	{
		return false;
	}
	
	@Override
	public boolean doesContainerContainAmount(Aspect var1, int var2)
	{
		return getAspects().getAmount(var1) >= var2;
	}
	
	@Override
	public boolean doesContainerContain(AspectList var1)
	{
		return false;
	}
	
	@Override
	public int containerContains(Aspect var1)
	{
		return getAspects().getAmount(var1);
	}
}