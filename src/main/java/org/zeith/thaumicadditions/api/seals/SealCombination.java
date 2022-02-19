package org.zeith.thaumicadditions.api.seals;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.client.seal.TARSealRenders;
import org.zeith.thaumicadditions.tiles.TileSeal;
import thaumcraft.api.aspects.Aspect;

public class SealCombination
{
	public final Aspect[] slots = new Aspect[3];

	private final String modid;
	private String desc;

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
	@SideOnly(Side.CLIENT)
	public ISealRenderer getRender(TileSeal seal, int index)
	{
		return TARSealRenders::renderNone;
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