package com.zeitheron.thaumicadditions.items;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public class ItemSealSymbol extends Item
{
	{
		setMaxStackSize(16);
		setTranslationKey("seal_symbol");
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
	
	public static final ResourceLocation[] TEXTURES = new ResourceLocation[3];
	{
		TEXTURES[0] = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/seal_0.png");
		TEXTURES[1] = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/seal_1.png");
		TEXTURES[2] = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/seal_2.png");
	}
	
	public static ResourceLocation getTexture(Aspect a, TileSeal seal, int index)
	{
		return TEXTURES[index];
	}
	
	/**
	 * Used to invoke a static method via reflection. <br>
	 * Format: com.package.RenderClass.methodName
	 */
	public static String getRender(Aspect a, TileSeal seal, int index)
	{
		return "com.zeitheron.thaumicadditions.client.seal.TARSealRenders.renderStandart";
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean renderSymbol(Aspect a, TileSeal seal, double x, double y, double z, float partialTicks, int index)
	{
		Method render = null;
		
		try
		{
			String patz = getRender(a, seal, index);
			int i = patz.lastIndexOf(".");
			String claz = patz.substring(0, i);
			String meth = patz.substring(i + 1, patz.length());
			render = Class.forName(claz).getDeclaredMethod(meth, TileSeal.class, double.class, double.class, double.class, float.class, int.class);
			render.setAccessible(true);
			if(!Modifier.isStatic(render.getModifiers()))
				return false;
		} catch(Throwable err)
		{
			err.printStackTrace();
			return false;
		}
		
		try
		{
			render.invoke(null, seal, x, y, z, partialTicks, index);
			return true;
		} catch(Throwable err)
		{
		}
		
		return false;
	}
}