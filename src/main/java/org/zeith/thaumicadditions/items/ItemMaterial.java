package org.zeith.thaumicadditions.items;

import com.zeitheron.hammercore.utils.IRegisterListener;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class ItemMaterial
		extends Item
		implements IRegisterListener
{
	private final String[] OD;

	private boolean hide;

	public ItemMaterial(String name)
	{
		this(name, new String[0]);
	}

	public ItemMaterial(String name, boolean hide)
	{
		this(name, new String[0]);
		this.hide = hide;
	}

	public ItemMaterial(String name, String... ods)
	{
		setTranslationKey(name);
		this.OD = ods;
	}

	@Override
	public void onRegistered()
	{
		if(OD != null)
			for(String o : OD)
				OreDictionary.registerOre(o, this);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(!hide)
			super.getSubItems(tab, items);
	}
}