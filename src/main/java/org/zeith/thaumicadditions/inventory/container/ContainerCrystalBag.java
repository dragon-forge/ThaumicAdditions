package org.zeith.thaumicadditions.inventory.container;

import com.zeitheron.hammercore.client.gui.impl.container.ItemTransferHelper.TransferableContainer;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.api.items.IAspectChargableItem;
import org.zeith.thaumicadditions.api.items.IAspectChargableItem.AspectChargableItemHelper;
import org.zeith.thaumicadditions.items.ItemCrystalBag;
import org.zeith.thaumicadditions.net.PacketCrystalBagAspects;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

public class ContainerCrystalBag
		extends TransferableContainer<Object>
{
	public AspectList aspects;
	public EntityPlayer player;
	public EnumHand hand;

	public ContainerCrystalBag(EntityPlayer player, EnumHand hand)
	{
		super(player, player, 8, 104);
		this.player = player;
		this.hand = hand;
		this.aspects = AspectChargableItemHelper.getAspects(player.getHeldItem(hand));
	}

	@Override
	protected void addInventorySlots(EntityPlayer player, int x, int y)
	{
		int start = inventorySlots.size();

		for(int i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(player.inventory, i, x + i * 18, 58 + y)
			{
				@Override
				public boolean canTakeStack(EntityPlayer playerIn)
				{
					return super.canTakeStack(playerIn) && !(getStack().getItem() instanceof ItemCrystalBag);
				}
			});

		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(player.inventory, 9 + j + i * 9, x + 18 * j, y + i * 18)
				{
					@Override
					public boolean canTakeStack(EntityPlayer playerIn)
					{
						return super.canTakeStack(playerIn) && !(getStack().getItem() instanceof ItemCrystalBag);
					}
				});

		transfer.setInventorySlots(start, inventorySlots.size());
	}

	public void onAspectClick(EntityPlayer player, Aspect aspect, boolean shift, int btn)
	{
		ItemStack mouse = player.inventory.getItemStack();

		if(aspect != null)
		{
			if(mouse.isEmpty())
			{
				ItemCrystalEssence ice = (ItemCrystalEssence) ItemsTC.crystalEssence;
				int reduce = Math.min(shift && btn == 1 ? 1 : 64, aspects.getAmount(aspect));
				if(btn == 1)
					reduce = MathHelper.ceil(reduce / 2F);
				aspects.remove(aspect, reduce);
				ItemStack stack = AspectUtil.crystalEssence(aspect, reduce);

				player.inventory.setItemStack(stack);
				HCNet.setMouseStack(player, stack);
				setBag();
			} else if(!mouse.isEmpty() && mouse.getItem() == ItemsTC.crystalEssence)
			{
				ItemCrystalEssence ice = (ItemCrystalEssence) ItemsTC.crystalEssence;
				AspectList aspects = ice.getAspects(mouse);
				if(aspects.size() == 1)
				{
					Aspect a = aspects.getAspects()[0];

					if(btn == 0)
					{
						int accept = Math.min(shift ? 1 : mouse.getCount(), 32_768 - this.aspects.getAmount(a));
						mouse.shrink(accept);
						this.aspects.add(a, accept);
					} else if(btn == 1 && aspect == a)
					{
						int accept = Math.min(this.aspects.getAmount(a), 64 - Math.max(mouse.getCount(), shift ? 63 : 0));
						mouse.grow(accept);
						this.aspects.remove(a, accept);
					}

					player.inventory.setItemStack(mouse);
					HCNet.setMouseStack(player, mouse);
					setBag();
				}
			}
		} else
		{
			if(!mouse.isEmpty() && mouse.getItem() == ItemsTC.crystalEssence)
			{
				ItemCrystalEssence ice = (ItemCrystalEssence) ItemsTC.crystalEssence;
				AspectList aspects = ice.getAspects(mouse);
				if(aspects.size() == 1)
				{
					Aspect a = aspects.getAspects()[0];

					if(btn == 0)
					{
						int accept = Math.min(shift ? 1 : mouse.getCount(), 32_768 - this.aspects.getAmount(a));
						mouse.shrink(accept);
						this.aspects.add(a, accept);
					}

					player.inventory.setItemStack(mouse);
					HCNet.setMouseStack(player, mouse);
					setBag();
				}
			}
		}
	}

	@Override
	public boolean enchantItem(EntityPlayer playerIn, int id)
	{
		if(id == 0x01)
		{
			ItemStack mouse = playerIn.inventory.getItemStack();

			if(!mouse.isEmpty() && mouse.getItem() == ItemsTC.crystalEssence)
			{
				ItemCrystalEssence ice = (ItemCrystalEssence) ItemsTC.crystalEssence;
				AspectList aspects = ice.getAspects(mouse);
				if(aspects.size() == 1)
				{
					Aspect a = aspects.getAspects()[0];

					int accept = Math.min(mouse.getCount(), 32_768 - this.aspects.getAmount(a));

					mouse.shrink(accept);
					this.aspects.add(a, accept);

					player.inventory.setItemStack(mouse);
					HCNet.setMouseStack(player, mouse);
					setBag();
				}
			}

			return true;
		}

		return true;
	}

	private void setBag()
	{
		AspectChargableItemHelper.setAspects(player.getHeldItem(hand), aspects);
		if(player instanceof EntityPlayerMP)
			HCNet.INSTANCE.sendTo(PacketCrystalBagAspects.create(aspects), Cast.cast(player, EntityPlayerMP.class));
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		setBag();
		super.onCraftMatrixChanged(inventoryIn);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		setBag();
		super.onContainerClosed(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		Slot slot = inventorySlots.get(index);
		if(slot.getHasStack() && slot.getStack().getItem() == ItemsTC.crystalEssence)
		{
			ItemCrystalEssence ice = (ItemCrystalEssence) ItemsTC.crystalEssence;
			AspectList al = ice.getAspects(slot.getStack());
			if(al.size() == 1)
			{
				Aspect a = al.getAspects()[0];
				int accept = Math.min(32_768 - aspects.getAmount(a), slot.getStack().getCount());
				aspects.add(a, accept);
				slot.getStack().shrink(accept);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		ItemStack s = player.getHeldItem(hand);
		return !s.isEmpty() && s.getItem() instanceof ItemCrystalBag;
	}
}