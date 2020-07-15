package com.zeitheron.thaumicadditions.compat.thaumicwands;

import com.zeitheron.thaumicadditions.InfoTAR;
import de.zpenguin.thaumicwands.api.item.wand.IWandCap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;

public class TARWandCap
		implements IWandCap
{
	int craftCost;
	float discount;

	String tag;
	String research;
	AspectList aspectDiscount;
	ResourceLocation texture;
	ItemStack item;

	public TARWandCap(String tag, float discount, ItemStack item, int craftCost, String research)
	{
		this(tag, discount, new AspectList(), item, craftCost, research);
	}

	public TARWandCap(String tag, float discount, ItemStack item, int craftCost)
	{
		this(tag, discount, new AspectList(), item, craftCost, "TAR_CAP_" + tag.toUpperCase());
	}

	public TARWandCap(String tag, float discount, AspectList aspectDiscount, ItemStack item, int craftCost)
	{
		this(tag, discount, aspectDiscount, item, craftCost, "TAR_CAP_" + tag.toUpperCase());
	}

	public TARWandCap(String tag, float discount, AspectList aspectDiscount, ItemStack item, int craftCost, String research)
	{
		this.tag = tag;
		this.discount = discount;
		this.item = item;
		this.craftCost = craftCost;
		this.aspectDiscount = aspectDiscount;
		this.research = research;

	}

	@Override
	public ResourceLocation getTexture()
	{
		return new ResourceLocation(InfoTAR.MOD_ID, "textures/models/wand_cap_" + tag + ".png");
	}

	public int getCraftCost()
	{
		return craftCost;
	}

	@Override
	public String getRequiredResearch()
	{
		return research;
	}

	@Override
	public ItemStack getItemStack()
	{
		return item;
	}

	@Override
	public String getTag()
	{
		return tag;
	}

	@Override
	public float getDiscount()
	{
		return discount;
	}

	@Override
	public AspectList getAspectDiscount()
	{
		return aspectDiscount;
	}
}