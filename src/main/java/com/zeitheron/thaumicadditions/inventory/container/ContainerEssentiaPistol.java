package com.zeitheron.thaumicadditions.inventory.container;

import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import com.zeitheron.thaumicadditions.api.items.EssentiaJarManager;
import com.zeitheron.thaumicadditions.items.weapons.ItemEssentiaPistol;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class ContainerEssentiaPistol extends Container
{
	EntityPlayer player;
	EnumHand hand;
	
	final InventoryDummy inventory = new InventoryDummy(1);
	
	public ContainerEssentiaPistol(EntityPlayer player, EnumHand hand)
	{
		this.player = player;
		this.hand = hand;
		
		inventory.inventoryStackLimit = 1;
		
		ItemStack held = player.getHeldItem(hand);
		
		inventory.setInventorySlotContents(0, ItemEssentiaPistol.getJar(held));
		
		addSlotToContainer(new Slot(inventory, 0, 80, 47)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return EssentiaJarManager.fromStack(stack) != null;
			}
			
			@Override
			public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
			{
				setJar();
				super.onSlotChange(p_75220_1_, p_75220_2_);
			}
		});
		
		for(int l = 0; l < 9; ++l)
			addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 168)
			{
				@Override
				public boolean canTakeStack(EntityPlayer playerIn)
				{
					return super.canTakeStack(playerIn) && !(getStack().getItem() instanceof ItemEssentiaPistol);
				}
			});
		
		for(int k = 0; k < 3; ++k)
			for(int j1 = 0; j1 < 9; ++j1)
				addSlotToContainer(new Slot(player.inventory, j1 + k * 9 + 9, 8 + j1 * 18, 110 + k * 18)
				{
					@Override
					public boolean canTakeStack(EntityPlayer playerIn)
					{
						return super.canTakeStack(playerIn) && !(getStack().getItem() instanceof ItemEssentiaPistol);
					}
				});
	}
	
	public void setJar()
	{
		ItemStack held = player.getHeldItem(hand);
		ItemEssentiaPistol.setJar(held, inventory.getStackInSlot(0));
		player.setHeldItem(hand, held);
	}
	
	public int getAmount()
	{
		ItemStack held = player.getHeldItem(hand);
		if(!held.hasTagCompound())
			return 1;
		return Math.max(1, held.getTagCompound().getInteger("Count"));
	}
	
	@Override
	public boolean enchantItem(EntityPlayer playerIn, int id)
	{
		if(id >= 1 && id <= 32)
		{
			ItemStack held = player.getHeldItem(hand);
			if(!held.hasTagCompound())
				held.setTagCompound(new NBTTagCompound());
			held.getTagCompound().setInteger("Count", id);
		}
		return super.enchantItem(playerIn, id);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		ItemStack current = ItemEssentiaPistol.getJar(player.getHeldItem(hand));
		if(!current.isItemEqual(inventory.getStackInSlot(0)))
			setJar();
		super.detectAndSendChanges();
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		setJar();
		super.onCraftMatrixChanged(inventoryIn);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		setJar();
		super.onContainerClosed(playerIn);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		ItemStack s = player.getHeldItem(hand);
		return !s.isEmpty() && s.getItem() instanceof ItemEssentiaPistol;
	}
}