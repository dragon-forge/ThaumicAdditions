package com.zeitheron.thaumicadditions.blocks.decor;

import com.zeitheron.hammercore.utils.IRegisterListener;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.oredict.OreDictionary;

public class BlockTARStorage
		extends Block
		implements IRegisterListener
{
	private final String[] OD;

	public BlockTARStorage(String name, String... ods)
	{
		super(Material.IRON);
		this.OD = ods;
		setHardness(3F);
		setHarvestLevel("pickaxe", 3);
		setTranslationKey(name);
		setSoundType(SoundType.METAL);
	}

	@Override
	public void onRegistered()
	{
		if(OD != null)
			for(String o : OD)
				OreDictionary.registerOre(o, this);
	}
}
