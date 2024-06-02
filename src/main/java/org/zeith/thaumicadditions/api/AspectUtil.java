package org.zeith.thaumicadditions.api;

import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import org.zeith.thaumicadditions.init.BlocksTAR;
import org.zeith.thaumicadditions.init.ItemsTAR;
import org.zeith.thaumicadditions.items.ItemSealSymbol;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApi.EntityTags;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AspectUtil
{
	public static boolean containsAll(AspectList list, AspectList aspects)
	{
		return getMissing(list, aspects).visSize() == 0;
	}

	public static AspectList getMissing(AspectList list, AspectList required)
	{
		AspectList al = new AspectList();
		for(Aspect a : required.getAspects())
			if(list.getAmount(a) < required.getAmount(a))
				al.add(a, required.getAmount(a) - list.getAmount(a));
		return al;
	}

	/**
	 * Gets the average color of the entire aspect list. The color also depends
	 * on the quantity of aspects, if desired.
	 */
	public static int getColor(AspectList al, boolean calcCount)
	{
		if(al != null && al.visSize() > 0)
		{
			Aspect[] all = al.getAspects();
			int total = calcCount ? al.visSize() : all.length;
			float r = 0, g = 0, b = 0;
			for(Aspect a : all)
			{
				float mult = calcCount ? al.getAmount(a) : 1;
				int rgb = a.getColor();
				r += ColorHelper.getRed(rgb) * mult;
				g += ColorHelper.getGreen(rgb) * mult;
				b += ColorHelper.getBlue(rgb) * mult;
			}
			r /= total;
			g /= total;
			b /= total;
			return ColorHelper.packRGB(r, g, b);
		}

		return 0xFFFFFF;
	}

	public static ItemStack salt(Aspect a)
	{
		return salt(new AspectList().add(a, 1));
	}

	public static ItemStack salt(AspectList list)
	{
		return salt(list, 1);
	}

	public static ItemStack crystalEssence(Aspect a)
	{
		return crystalEssence(a, 1);
	}

	public static ItemStack phial(Aspect a)
	{
		return phial(a, 1);
	}

	public static ItemStack phial(Aspect a, int count)
	{
		return phial(a, count, 10);
	}

	public static ItemStack salt(Aspect a, int count)
	{
		return salt(new AspectList().add(a, 1), count);
	}

	public static ItemStack salt(AspectList list, int count)
	{
		ItemStack i = new ItemStack(ItemsTAR.SALT_ESSENCE, count);
		ItemsTAR.SALT_ESSENCE.setAspects(i, list);
		return i;
	}

	public static ItemStack phial(Aspect a, int count, int aspectPerPhial)
	{
		if(a == null)
			return new ItemStack(ItemsTC.phial, count, 0);
		ItemStack i = new ItemStack(ItemsTC.phial, count, 1);
		((ItemPhial) ItemsTC.phial).setAspects(i, new AspectList().add(a, aspectPerPhial));
		return i;
	}

	public static ItemStack crystalEssence(Aspect a, int count)
	{
		ItemStack is = new ItemStack(ItemsTC.crystalEssence, count);
		((ItemCrystalEssence) ItemsTC.crystalEssence).setAspects(is, new AspectList().add(a, 1));
		return is;
	}

	public static ItemStack crystalBlock(Aspect a)
	{
		ItemStack is = new ItemStack(BlocksTAR.CRYSTAL_BLOCK);
		is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setString("Aspect", a.getTag());
		return is;
	}

	public static ItemStack sealSymbol(Aspect a, int count)
	{
		return ItemSealSymbol.createItem(a, count);
	}

	public static ItemStack sealSymbol(Aspect a)
	{
		return sealSymbol(a, 1);
	}

	public static Aspect getAspectFromCrystalBlockStack(ItemStack is)
	{
		if(is.hasTagCompound() && is.getTagCompound().hasKey("Aspect", NBT.TAG_STRING))
		{
			Aspect a = Aspect.getAspect(is.getTagCompound().getString("Aspect"));
			if(a != null)
				return a;
		}
		return cycleRandomAspect();
	}

	public static Aspect cycleRandomAspect()
	{
		return cycleRandomAspect(new ArrayList<>(Aspect.aspects.values()));
	}

	public static Aspect cycleRandomAspect(List<Aspect> al)
	{
		return al.get((int) (System.currentTimeMillis() % (al.size() * 1000L) / 1000));
	}

	public static NBTTagCompound writeALToNBT(AspectList list, NBTTagCompound nbt)
	{
		if(list != null)
			list.writeToNBT(nbt);
		return nbt;
	}

	public static AspectList primals(int i)
	{
		return new AspectList().add(Aspect.AIR, i).add(Aspect.WATER, i).add(Aspect.ORDER, i).add(Aspect.ENTROPY, i).add(Aspect.FIRE, i).add(Aspect.EARTH, i);
	}

	public static Optional<EntityTags> getOrCreateEntityTags(Class<? extends Entity> type)
	{
		ResourceLocation key = EntityList.getKey(type);
		if(key != null)
		{
			String oldName = EntityList.getTranslationName(key);
			for(EntityTags et : CommonInternals.scanEntities)
				if(et.entityName.equals(oldName))
					return Optional.of(et);

			EntityTags tags;
			CommonInternals.scanEntities.add(tags = new EntityTags(oldName, new AspectList()));
			return Optional.of(tags);
		}

		return Optional.empty();
	}

	public static AspectList getAspects(Class<? extends Entity> type)
	{
		AspectList tags = new AspectList();

		ResourceLocation key = EntityList.getKey(type);
		if(key != null)
		{
			String oldName = EntityList.getTranslationName(key);
			for(EntityTags et : CommonInternals.scanEntities)
				if(et.entityName.equals(oldName))
					tags.add(et.aspects);
		}

		return tags;
	}
}