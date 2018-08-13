package com.zeitheron.thaumicadditions.tiles;

import com.zeitheron.hammercore.tile.ITileDroppable;
import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.InterItemStack;
import com.zeitheron.hammercore.utils.ItemStackUtil;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.blocks.BlockCrystal;
import com.zeitheron.thaumicadditions.init.BlocksTAR;
import com.zeitheron.thaumicadditions.inventory.container.ContainerGrowthChamber;
import com.zeitheron.thaumicadditions.inventory.gui.GuiGrowthChamber;
import com.zeitheron.thaumicadditions.net.PacketBlockEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXVisSparkle;

public class TileGrowthChamber extends TileSyncableTickable implements IEssentiaTransport, IAspectContainer, ITileDroppable
{
	public static final float MAX_AURA_DRAIN = 20F;
	
	public final InventoryDummy growthInventory = new InventoryDummy(2);
	{
		growthInventory.validSlots = (index, stack) -> index == 0 && stack.getItem() == BlocksTAR.CRYSTAL_BLOCK.getItemBlock();
	}
	
	public Aspect slotAspect;
	public int amount;
	public int capacity = 6;
	
	public float toDrainAura = MAX_AURA_DRAIN;
	
	@Override
	public void tick()
	{
		if(!growthInventory.getStackInSlot(1).isEmpty() && growthInventory.getStackInSlot(1).getCount() >= growthInventory.getStackInSlot(1).getMaxStackSize())
			return;
		
		if(!growthInventory.getStackInSlot(0).isEmpty() && growthInventory.getStackInSlot(0).getItem() == BlocksTAR.CRYSTAL_BLOCK.getItemBlock())
			slotAspect = AspectUtil.getAspectFromCrystalBlockStack(growthInventory.getStackInSlot(0));
		else
			slotAspect = null;
		
		if(slotAspect == null)
		{
			if(amount > 0)
			{
				AuraHelper.polluteAura(world, pos, amount, true);
				amount = 0;
				sendChangesToNearby();
			}
			
			if(MAX_AURA_DRAIN - toDrainAura > 0)
			{
				AuraHelper.addVis(world, pos, MAX_AURA_DRAIN - toDrainAura);
				toDrainAura = MAX_AURA_DRAIN;
			}
			
			return;
		}
		
		if(!world.isRemote)
			for(EnumFacing rf : EnumFacing.VALUES)
			{
				IEssentiaTransport l = (IEssentiaTransport) ThaumcraftApiHelper.getConnectableTile(world, pos, rf.getOpposite());
				
				if(l != null && l.canOutputTo(rf))
				{
					Aspect lasp = l.getEssentiaType(rf);
					if(lasp == slotAspect)
					{
						int amt = capacity - amount;
						int taken;
						if(amt > 0 && (taken = l.takeEssentia(lasp, amt, rf)) > 0)
						{
							amount += taken;
							sendChangesToNearby();
						}
					}
				}
			}
		
		if(canCraft())
		{
			toDrainAura -= AuraHelper.drainVis(world, pos, MAX_AURA_DRAIN / (20F * 60), false);
			if(atTickRate(5) && !world.isRemote)
				sendChangesToNearby();
			if(atTickRate(10) && !world.isRemote)
				PacketBlockEvent.performBlockEvent(world, pos, 1, 0);
			if(toDrainAura <= 0F)
			{
				craft();
				--amount;
				toDrainAura += MAX_AURA_DRAIN;
			}
		}
	}
	
	public boolean canCraft()
	{
		if(!growthInventory.getStackInSlot(1).isEmpty() && growthInventory.getStackInSlot(1).getCount() >= growthInventory.getStackInSlot(1).getMaxStackSize())
			return false;
		if(!growthInventory.getStackInSlot(0).isEmpty() && growthInventory.getStackInSlot(0).getItem() == BlocksTAR.CRYSTAL_BLOCK.getItemBlock() && slotAspect != null)
			return amount > 0;
		return false;
	}
	
	public void craft()
	{
		if(growthInventory.getStackInSlot(1).isEmpty())
		{
			ItemStack is = AspectUtil.crystalEssence(AspectUtil.getAspectFromCrystalBlockStack(growthInventory.getStackInSlot(0)));
			is.setCount(1);
			growthInventory.setInventorySlotContents(1, is);
		} else if(!growthInventory.getStackInSlot(1).isEmpty() && growthInventory.getStackInSlot(1).getItem() == ItemsTC.crystalEssence)
		{
			ItemStack is = growthInventory.getStackInSlot(1);
			if(is.getCount() + 1 <= is.getMaxStackSize())
				is.grow(1);
		}
	}
	
	public static boolean itemsEqual(ItemStack a, ItemStack b)
	{
		if(InterItemStack.isStackNull(a) || InterItemStack.isStackNull(b))
			return false;
		return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage() && ItemStackUtil.tagsEqual(a.getTagCompound(), b.getTagCompound());
	}
	
	public boolean doDrainAura(float vis)
	{
		float drained = AuraHelper.drainVis(world, pos, vis, true);
		if(drained >= vis)
		{
			AuraHelper.drainVis(world, pos, vis, false);
			return true;
		}
		return false;
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setTag("Items", growthInventory.writeToNBT(new NBTTagCompound()));
		nbt.setFloat("Aura", toDrainAura);
		nbt.setInteger("Amount", amount);
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		growthInventory.readFromNBT(nbt.getCompoundTag("Items"));
		toDrainAura = nbt.getFloat("Aura");
		amount = nbt.getInteger("Amount");
	}
	
	@Override
	public boolean hasGui()
	{
		return true;
	}
	
	@Override
	public Object getClientGuiElement(EntityPlayer player)
	{
		return new GuiGrowthChamber(player, this);
	}
	
	@Override
	public Object getServerGuiElement(EntityPlayer player)
	{
		return new ContainerGrowthChamber(player, this);
	}
	
	@Override
	public void createDrop(EntityPlayer player, World world, BlockPos pos)
	{
		growthInventory.drop(world, pos);
		if(!world.isRemote)
			AuraHelper.polluteAura(world, pos, amount, true);
	}
	
	@Override
	public AspectList getAspects()
	{
		AspectList al = new AspectList();
		if(slotAspect != null)
			al.add(slotAspect, amount);
		return al;
	}
	
	@Override
	public void setAspects(AspectList var1)
	{
		if(slotAspect != null)
		{
			amount = var1.getAmount(slotAspect);
			sendChangesToNearby();
		}
	}
	
	@Override
	public boolean doesContainerAccept(Aspect var1)
	{
		return slotAspect != null && slotAspect == var1;
	}
	
	@Override
	public int addToContainer(Aspect var1, int var2)
	{
		if(slotAspect != null && slotAspect == var1)
		{
			int ma = Math.min(var2, capacity - amount);
			amount += ma;
			sendChangesToNearby();
			return ma;
		}
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
		return slotAspect != null && slotAspect == var1 && amount >= var2;
	}
	
	@Override
	public boolean doesContainerContain(AspectList var1)
	{
		return slotAspect != null && amount >= var1.getAmount(slotAspect);
	}
	
	@Override
	public int containerContains(Aspect var1)
	{
		return slotAspect != null && slotAspect == var1 ? amount : 0;
	}
	
	@Override
	public boolean isConnectable(EnumFacing var1)
	{
		return canInputFrom(var1);
	}
	
	@Override
	public boolean canInputFrom(EnumFacing var1)
	{
		return true;
	}
	
	@Override
	public boolean canOutputTo(EnumFacing var1)
	{
		return false;
	}
	
	@Override
	public void setSuction(Aspect var1, int var2)
	{
	}
	
	@Override
	public Aspect getSuctionType(EnumFacing var1)
	{
		return canInputFrom(var1) && slotAspect != null ? slotAspect : null;
	}
	
	@Override
	public int getSuctionAmount(EnumFacing f)
	{
		return canInputFrom(f) ? 128 : 0;
	}
	
	@Override
	public int takeEssentia(Aspect var1, int var2, EnumFacing var3)
	{
		return 0;
	}
	
	@Override
	public int addEssentia(Aspect var1, int var2, EnumFacing var3)
	{
		return slotAspect != null ? addToContainer(var1, var2) : 0;
	}
	
	@Override
	public Aspect getEssentiaType(EnumFacing var1)
	{
		return canInputFrom(var1) ? slotAspect : null;
	}
	
	@Override
	public int getEssentiaAmount(EnumFacing var1)
	{
		return canInputFrom(var1) ? amount : 0;
	}
	
	@Override
	public int getMinimumSuction()
	{
		return 0;
	}
	
	@Override
	public boolean receiveClientEvent(int id, int type)
	{
		int rays = 2;
		if(id == 1)
			if(world.isRemote)
				for(int i = 0; i < rays; ++i)
				{
					float deg = ticksExisted * 12 + i * (360F / rays);
					float rad = (float) Math.toRadians(deg);
					float sin = MathHelper.sin(rad);
					float cos = MathHelper.cos(rad);
					visSparkle(pos.getX() + .5F + cos * getRNG().nextFloat() * 6, pos.getY() + 1.5F + getRNG().nextFloat() * 4, pos.getZ() + .5F + sin * getRNG().nextFloat() * 6, pos.getX() + cos * .3F + .5F, pos.getY() + .5F, pos.getZ() + sin * .3F + .5F, Aspect.AURA.getColor());
				}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void visSparkle(float x, float y, float z, float x2, float y2, float z2, int color)
	{
		FXVisSparkle fb = new FXVisSparkle(FXDispatcher.INSTANCE.getWorld(), x, y, z, x2, y2, z2);
		fb.setRBGColorF(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color));
		ParticleEngine.addEffect(FXDispatcher.INSTANCE.getWorld(), fb);
	}
}