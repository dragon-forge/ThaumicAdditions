package com.zeitheron.thaumicadditions.inventory.container;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zeitheron.thaumicadditions.blocks.BlockVoidAnvil;
import com.zeitheron.thaumicadditions.init.BlocksTAR;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;

public class ContainerRepairVoid extends Container
{
	private static final Logger LOGGER = LogManager.getLogger();
	public final IInventory outputSlot;
	public final IInventory inputSlots;
	public final World world;
	public final BlockPos pos;
	public int maximumCost;
	public int materialCost;
	public String repairedItemName;
	public final EntityPlayer player;
	
	public ContainerRepairVoid(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player)
	{
		this.outputSlot = new InventoryCraftResult();
		this.inputSlots = new InventoryBasic("Repair", true, 2)
		{
			@Override
			public void markDirty()
			{
				super.markDirty();
				ContainerRepairVoid.this.onCraftMatrixChanged(this);
			}
		};
		this.pos = blockPosIn;
		this.world = worldIn;
		this.player = player;
		this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
		this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
		this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return false;
			}
			
			@Override
			public boolean canTakeStack(EntityPlayer playerIn)
			{
				return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerRepairVoid.this.maximumCost) && ContainerRepairVoid.this.maximumCost > 0 && this.getHasStack();
			}
			
			@Override
			public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
			{
				if(!thePlayer.capabilities.isCreativeMode)
				{
					thePlayer.addExperienceLevel(-ContainerRepairVoid.this.maximumCost);
				}
				
				float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(thePlayer, stack, ContainerRepairVoid.this.inputSlots.getStackInSlot(0), ContainerRepairVoid.this.inputSlots.getStackInSlot(1));
				
				ContainerRepairVoid.this.inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);
				
				if(ContainerRepairVoid.this.materialCost > 0)
				{
					ItemStack itemstack = ContainerRepairVoid.this.inputSlots.getStackInSlot(1);
					
					if(!itemstack.isEmpty() && itemstack.getCount() > ContainerRepairVoid.this.materialCost)
					{
						itemstack.shrink(ContainerRepairVoid.this.materialCost);
						ContainerRepairVoid.this.inputSlots.setInventorySlotContents(1, itemstack);
					} else
					{
						ContainerRepairVoid.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
					}
				} else
				{
					ContainerRepairVoid.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
				}
				
				ContainerRepairVoid.this.maximumCost = 0;
				IBlockState iblockstate = worldIn.getBlockState(blockPosIn);
				
				if(!thePlayer.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() instanceof BlockVoidAnvil && thePlayer.getRNG().nextFloat() < breakChance * 1.5F)
					AuraHelper.polluteAura(world, pos, 1F, true);
				
				if(!worldIn.isRemote)
					worldIn.playEvent(1030, blockPosIn, 0);
				
				return stack;
			}
		});
		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		for(int k = 0; k < 9; ++k)
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		super.onCraftMatrixChanged(inventoryIn);
		
		if(inventoryIn == this.inputSlots)
			updateRepairOutput();
	}
	
	public void updateRepairOutput()
	{
		ItemStack itemstack = this.inputSlots.getStackInSlot(0);
		this.maximumCost = 1;
		int i = 0;
		int j = 0;
		int k = 0;
		
		if(itemstack.isEmpty())
		{
			this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
			this.maximumCost = 0;
		} else
		{
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
			j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
			this.materialCost = 0;
			boolean flag = false;
			
			if(!itemstack2.isEmpty())
			{
				boolean flag4 = false;
				AnvilUpdateEvent e = new AnvilUpdateEvent(itemstack, itemstack2, repairedItemName, j);
				if(MinecraftForge.EVENT_BUS.post(e))
					flag4 = false;
				if(e.getOutput().isEmpty())
					flag4 = true;
				if(!flag4)
					return;
				
				flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).isEmpty();
				
				if(itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2))
				{
					int l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
					
					if(l2 <= 0)
					{
						this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
						this.maximumCost = 0;
						return;
					}
					
					int i3;
					
					for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3)
					{
						int j3 = itemstack1.getItemDamage() - l2;
						itemstack1.setItemDamage(j3);
						++i;
						l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
					}
					
					this.materialCost = i3;
				} else
				{
					if(!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable()))
					{
						this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
						this.maximumCost = 0;
						return;
					}
					
					if(itemstack1.isItemStackDamageable() && !flag)
					{
						int l = itemstack.getMaxDamage() - itemstack.getItemDamage();
						int i1 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
						int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
						int k1 = l + j1;
						int l1 = itemstack1.getMaxDamage() - k1;
						
						if(l1 < 0)
						{
							l1 = 0;
						}
						
						if(l1 < itemstack1.getItemDamage()) // vanilla uses
						                                    // metadata here
						                                    // instead of
						                                    // damage.
						{
							itemstack1.setItemDamage(l1);
							i += 2;
						}
					}
					
					Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
					boolean flag2 = false;
					boolean flag3 = false;
					
					for(Enchantment enchantment1 : map1.keySet())
					{
						if(enchantment1 != null)
						{
							int i2 = map.containsKey(enchantment1) ? ((Integer) map.get(enchantment1)).intValue() : 0;
							int j2 = ((Integer) map1.get(enchantment1)).intValue();
							j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
							boolean flag1 = enchantment1.canApply(itemstack);
							
							if(this.player.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK)
							{
								flag1 = true;
							}
							
							for(Enchantment enchantment : map.keySet())
							{
								if(enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment))
								{
									flag1 = false;
									++i;
								}
							}
							
							if(!flag1)
							{
								flag3 = true;
							} else
							{
								flag2 = true;
								
								if(j2 > enchantment1.getMaxLevel())
								{
									j2 = enchantment1.getMaxLevel();
								}
								
								map.put(enchantment1, Integer.valueOf(j2));
								int k3 = 0;
								
								switch(enchantment1.getRarity())
								{
								case COMMON:
									k3 = 1;
								break;
								case UNCOMMON:
									k3 = 2;
								break;
								case RARE:
									k3 = 4;
								break;
								case VERY_RARE:
									k3 = 8;
								}
								
								if(flag)
								{
									k3 = Math.max(1, k3 / 2);
								}
								
								i += k3 * j2;
								
								if(itemstack.getCount() > 1)
								{
									i = 40;
								}
							}
						}
					}
					
					if(flag3 && !flag2)
					{
						this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
						this.maximumCost = 0;
						return;
					}
				}
			}
			
			if(StringUtils.isBlank(this.repairedItemName))
			{
				if(itemstack.hasDisplayName())
				{
					k = 1;
					i += k;
					itemstack1.clearCustomName();
				}
			} else if(!this.repairedItemName.equals(itemstack.getDisplayName()))
			{
				k = 1;
				i += k;
				itemstack1.setStackDisplayName(this.repairedItemName);
			}
			if(flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2))
				itemstack1 = ItemStack.EMPTY;
			
			this.maximumCost = j + i;
			
			if(i <= 0)
			{
				itemstack1 = ItemStack.EMPTY;
			}
			
			if(k == i && k > 0 && this.maximumCost >= 40)
			{
				this.maximumCost = 39;
			}
			
			if(this.maximumCost >= 40 && !this.player.capabilities.isCreativeMode)
			{
				itemstack1 = ItemStack.EMPTY;
			}
			
			if(!itemstack1.isEmpty())
			{
				int k2 = itemstack1.getRepairCost();
				
				if(!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost())
				{
					k2 = itemstack2.getRepairCost();
				}
				
				if(k != i || k == 0)
				{
					k2 = k2 * 2 + 1;
				}
				
				itemstack1.setRepairCost(k2);
				EnchantmentHelper.setEnchantments(map, itemstack1);
			}
			
			this.outputSlot.setInventorySlotContents(0, itemstack1);
			this.detectAndSendChanges();
		}
	}
	
	@Override
	public void addListener(IContainerListener listener)
	{
		super.addListener(listener);
		listener.sendWindowProperty(this, 0, this.maximumCost);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		if(id == 0)
		{
			this.maximumCost = data;
		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		
		if(!this.world.isRemote)
		{
			this.clearContainer(playerIn, this.world, this.inputSlots);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		if(this.world.getBlockState(this.pos).getBlock() != BlocksTAR.VOID_ANVIL)
		{
			return false;
		} else
		{
			return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		
		if(slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if(index == 2)
			{
				if(!this.mergeItemStack(itemstack1, 3, 39, true))
				{
					return ItemStack.EMPTY;
				}
				
				slot.onSlotChange(itemstack1, itemstack);
			} else if(index != 0 && index != 1)
			{
				if(index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false))
				{
					return ItemStack.EMPTY;
				}
			} else if(!this.mergeItemStack(itemstack1, 3, 39, false))
			{
				return ItemStack.EMPTY;
			}
			
			if(itemstack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			} else
			{
				slot.onSlotChanged();
			}
			
			if(itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}
			
			slot.onTake(playerIn, itemstack1);
		}
		
		return itemstack;
	}
	
	public void updateItemName(String newName)
	{
		this.repairedItemName = newName;
		
		if(this.getSlot(2).getHasStack())
		{
			ItemStack itemstack = this.getSlot(2).getStack();
			
			if(StringUtils.isBlank(newName))
			{
				itemstack.clearCustomName();
			} else
			{
				itemstack.setStackDisplayName(this.repairedItemName);
			}
		}
		
		this.updateRepairOutput();
	}
}