package com.zeitheron.thaumicadditions.api.seals;

import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import thaumcraft.api.aspects.Aspect;

public class SealCombination
{
	public final Aspect[] slots = new Aspect[3];
	
	private final String modid;
	
	public SealCombination(Aspect i, Aspect j, Aspect k)
	{
		slots[0] = i;
		slots[1] = j;
		slots[2] = k;
		
		ModContainer amc = Loader.instance().activeModContainer();
		if(amc != null)
			modid = amc.getModId();
		else
			modid = null;
	}
	
	public String getModid()
	{
		return modid;
	}
	
	public String getModName()
	{
		return modid != null ? Loader.instance().getIndexedModList().get(modid).getName() : "Unknown";
	}
	
	public String getAuthor()
	{
		String al;
		return modid != null && !(al = Loader.instance().getIndexedModList().get(modid).getMetadata().getAuthorList()).isEmpty() ? al : "Unknown";
	}
	
	/**
	 * Used to invoke a static method via reflection. <br>
	 * Format: com.package.RenderClass.methodName
	 */
	public String getRender(TileSeal seal, int index)
	{
		return "com.pengu.lostthaumaturgy.client.render.seal.LTSealRenders.renderNone";
	}
	
	public boolean isValid(TileSeal seal)
	{
		for(int i = 0; i < 3; ++i)
			if(slots[i] != seal.getSymbol(i))
				return false;
		return true;
	}
	
	private String nameAspect(int i)
	{
		return slots[i] != null ? slots[i].getName() : "None";
	}
	
	private String desc;
	
	public SealCombination withDescriptionKey(String key)
	{
		this.desc = key;
		return this;
	}
	
	public String getDescription(TileSeal seal)
	{
		return desc != null ? I18n.format(desc) : null;
	}
	
	@Override
	public String toString()
	{
		return nameAspect(0) + ", " + nameAspect(1) + ", " + nameAspect(2);
	}
}