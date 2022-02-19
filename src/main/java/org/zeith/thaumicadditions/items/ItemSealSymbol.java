package org.zeith.thaumicadditions.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.init.ItemsTAR;
import org.zeith.thaumicadditions.tiles.TileSeal;
import thaumcraft.api.aspects.Aspect;

public class ItemSealSymbol
		extends Item
{
	public static final ResourceLocation[] TEXTURES = new ResourceLocation[3];

	{
		setMaxStackSize(16);
		setTranslationKey("seal_symbol");
	}

	{
		TEXTURES[0] = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/seal_0.png");
		TEXTURES[1] = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/seal_1.png");
		TEXTURES[2] = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/seal_2.png");
	}

	public static Aspect getAspect(ItemStack stack)
	{
		return AspectUtil.getAspectFromCrystalBlockStack(stack);
	}

	public static ItemStack createItem(Aspect aspect, int quantity)
	{
		ItemStack s = new ItemStack(ItemsTAR.SEAL_SYMBOL, quantity);
		s.setTagCompound(new NBTTagCompound());
		s.getTagCompound().setString("Aspect", aspect.getTag());
		return s;
	}

	public static boolean doesRotate(Aspect a, TileSeal seal, int index)
	{
		return index == 2;
	}

	public static int getColorMultiplier(Aspect a, TileSeal seal, int index)
	{
		return seal.getSymbol(index).getColor();
	}

	public static ResourceLocation getTexture(Aspect a, TileSeal seal, int index)
	{
		return TEXTURES[index];
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(isInCreativeTab(tab))
			for(Aspect a : Aspect.aspects.values())
				items.add(AspectUtil.sealSymbol(a));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		return super.getItemStackDisplayName(stack).replace("@ASPECT", AspectUtil.getAspectFromCrystalBlockStack(stack).getName());
	}
}