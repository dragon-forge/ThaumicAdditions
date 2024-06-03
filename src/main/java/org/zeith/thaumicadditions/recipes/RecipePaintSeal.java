package org.zeith.thaumicadditions.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.init.BlocksTAR;

import java.util.List;
import java.util.Map;

public class RecipePaintSeal
		extends Impl<IRecipe>
		implements IRecipe
{
	/**
	 * Map from EnumDyeColor to RGB values for passage to GlStateManager.color()
	 */
	private static final Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);

	static
	{
		for(EnumDyeColor enumdyecolor : EnumDyeColor.values())
			DYE_TO_RGB.put(enumdyecolor, createSheepColor(enumdyecolor));

		DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[]{
				0.9019608F,
				0.9019608F,
				0.9019608F
		});
	}

	{
		setRegistryName(InfoTAR.id("seal_paint"));
	}

	private static float[] createSheepColor(EnumDyeColor p_192020_0_)
	{
		float[] afloat = p_192020_0_.getColorComponentValues();
		float f = 0.75F;
		return new float[]{
				afloat[0] * 0.75F,
				afloat[1] * 0.75F,
				afloat[2] * 0.75F
		};
	}

	/**
	 * Allows to actually get color by dye color - not client only.
	 */
	public static float[] getDyeRgb(EnumDyeColor dyeColor)
	{
		return DYE_TO_RGB.get(dyeColor);
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width * height > 1;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		int[] aint = new int[3];
		int i = 0;
		int j = 0;
		Item itemseal = null;

		for(int k = 0; k < inv.getSizeInventory(); ++k)
		{
			ItemStack itemstack1 = inv.getStackInSlot(k);

			if(!itemstack1.isEmpty())
				if(itemstack1.getItem() == Item.getItemFromBlock(BlocksTAR.SEAL))
				{
					itemseal = Item.getItemFromBlock(BlocksTAR.SEAL);
					itemstack = itemstack1.copy();
					itemstack.setCount(1);

					if(itemstack1.hasTagCompound() && itemstack1.getTagCompound().hasKey("RGB", NBT.TAG_INT_ARRAY))
					{
						int[] rgb = itemstack1.getTagCompound().getIntArray("RGB");
						int l = rgb[0] << 16 | rgb[1] << 8 | rgb[2] << 0;
						float f = (l >> 16 & 255) / 255.0F;
						float f1 = (l >> 8 & 255) / 255.0F;
						float f2 = (l & 255) / 255.0F;
						i = (int) (i + Math.max(f, Math.max(f1, f2)) * 255.0F);
						aint[0] = (int) (aint[0] + f * 255.0F);
						aint[1] = (int) (aint[1] + f1 * 255.0F);
						aint[2] = (int) (aint[2] + f2 * 255.0F);
						++j;
					}
				} else
				{
					if(itemstack1.getItem() != Items.DYE)
						return ItemStack.EMPTY;

					float[] afloat = getDyeRgb(EnumDyeColor.byDyeDamage(itemstack1.getMetadata()));
					int l1 = (int) (afloat[0] * 255.0F);
					int i2 = (int) (afloat[1] * 255.0F);
					int j2 = (int) (afloat[2] * 255.0F);
					i += Math.max(l1, Math.max(i2, j2));
					aint[0] += l1;
					aint[1] += i2;
					aint[2] += j2;
					++j;
				}
		}

		if(itemseal == null)
			return ItemStack.EMPTY;
		else
		{
			int i1 = aint[0] / j;
			int j1 = aint[1] / j;
			int k1 = aint[2] / j;
			float f3 = (float) i / (float) j;
			float f4 = Math.max(i1, Math.max(j1, k1));
			i1 = (int) (i1 * f3 / f4);
			j1 = (int) (j1 * f3 / f4);
			k1 = (int) (k1 * f3 / f4);

			int col = (i1 << 8) + j1;
			col = (col << 8) + k1;

			NBTTagCompound nbt = itemstack.getTagCompound();
			if(nbt == null)
				nbt = new NBTTagCompound();
			nbt.setIntArray("RGB", new int[]{
					col >> 16 & 0xFF,
					col >> 8 & 0xFF,
					col >> 0 & 0xFF
			});
			itemstack.setTagCompound(nbt);

			return itemstack;
		}
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for(int i = 0; i < nonnulllist.size(); ++i)
		{
			ItemStack itemstack = inv.getStackInSlot(i);
			nonnulllist.set(i, ForgeHooks.getContainerItem(itemstack));
		}

		return nonnulllist;
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.newArrayList();

		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack itemstack1 = inv.getStackInSlot(i);

			if(!itemstack1.isEmpty())
				if(itemstack1.getItem() == Item.getItemFromBlock(BlocksTAR.SEAL))
				{
					if(!itemstack.isEmpty())
						return false;
					itemstack = itemstack1;
				} else
				{
					if(itemstack1.getItem() != Items.DYE)
						return false;
					list.add(itemstack1);
				}
		}

		return !itemstack.isEmpty() && !list.isEmpty();
	}
}