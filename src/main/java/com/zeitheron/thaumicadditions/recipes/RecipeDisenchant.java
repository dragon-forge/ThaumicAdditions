package com.zeitheron.thaumicadditions.recipes;

import com.zeitheron.hammercore.utils.ReflectionUtil;
import com.zeitheron.thaumicadditions.items.ItemDisenchantingFabric;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.Field;
import java.util.Map;

public class RecipeDisenchant
		extends IForgeRegistryEntry.Impl<IRecipe>
		implements IRecipe
{
	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width * height >= 2;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		ItemStack enchantedItem = ItemStack.EMPTY;
		boolean fabric = false;

		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof ItemDisenchantingFabric)
				{
					if(!fabric)
						fabric = true;
					else return false;
				} else if(stack.isItemEnchanted())
				{
					if(enchantedItem.isEmpty())
						enchantedItem = stack;
					else
						return false;
				}
			}
		}

		return !enchantedItem.isEmpty() && fabric;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack enchantedItem = ItemStack.EMPTY;
		boolean fabric = false;

		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof ItemDisenchantingFabric)
				{
					if(!fabric)
						fabric = true;
					else
						return ItemStack.EMPTY;
				} else if(stack.isItemEnchanted())
				{
					if(enchantedItem.isEmpty())
						enchantedItem = stack;
					else
						return ItemStack.EMPTY;
				}
			}
		}

		if(fabric && !enchantedItem.isEmpty())
		{
			ItemStack di = enchantedItem.copy().splitStack(1);
			Map<Enchantment, Integer> ench = EnchantmentHelper.getEnchantments(di);
			if(di.hasTagCompound())
				di.getTagCompound().removeTag("ench");
			return di;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		ItemStack enchantedItem = ItemStack.EMPTY;
		boolean fabric = false;

		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof ItemDisenchantingFabric)
				{
					if(!fabric)
						fabric = true;
					else
						return IRecipe.super.getRemainingItems(inv);
				} else if(stack.isItemEnchanted())
				{
					if(enchantedItem.isEmpty())
						enchantedItem = stack;
					else
						return IRecipe.super.getRemainingItems(inv);
				}
			}
		}

		if(fabric && !enchantedItem.isEmpty())
		{
			ItemStack di = enchantedItem.copy().splitStack(1);
			Map<Enchantment, Integer> ench = EnchantmentHelper.getEnchantments(di);
			if(di.hasTagCompound())
				di.getTagCompound().removeTag("ench");

			EntityPlayer crafter = null;
			Field f = ReflectionUtil.getField(InventoryCrafting.class, Container.class);
			if(f != null)
			{
				f.setAccessible(true);
				try
				{
					Container ctr = Container.class.cast(f.get(inv));
					for(Slot s : ctr.inventorySlots)
					{
						if(s.inventory instanceof InventoryPlayer)
						{
							crafter = ((InventoryPlayer) s.inventory).player;
							break;
						}
					}
				} catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}

			if(crafter != null && !crafter.world.isRemote)
			{
				int xp = 0;
				for(Enchantment e : ench.keySet())
				{
					int lvl = ench.get(e);
					float prog = 50F * (lvl - e.getMinLevel()) / Math.max(1, (e.getMaxLevel() - e.getMinLevel()));  // we do not want infinite experience, right?
					prog /= e.getRarity().getWeight();
					xp += prog;
				}
				while(xp > 0)
				{
					int expValue = Math.min(xp, 5);
					crafter.world.spawnEntity(new EntityXPOrb(crafter.world, crafter.posX + crafter.width / 2F, crafter.posY, crafter.posZ + crafter.width / 2F, expValue));
					xp -= expValue;
				}
			}
		}

		return IRecipe.super.getRemainingItems(inv);
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}
}