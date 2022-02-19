package org.zeith.thaumicadditions.misc.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.TAReconstructed;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

import java.util.Random;

public class CardThaumicAdditions
		extends TheorycraftCard
{
	@Override
	public int getInspirationCost()
	{
		return 2;
	}

	@Override
	public String getLocalizedName()
	{
		return new TextComponentTranslation("card." + InfoTAR.MOD_ID + ":base.name").getFormattedText();
	}

	@Override
	public String getLocalizedText()
	{
		int min = 21 + new Random(getSeed()).nextInt(10);
		int max = min + new Random(getSeed()).nextInt(48) + 1;

		return I18n.translateToLocalFormatted("card." + InfoTAR.MOD_ID + ":base.text", min, max);
	}

	@Override
	public String getResearchCategory()
	{
		return TAReconstructed.RES_CAT.key;
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data)
	{
		int min = 21 + new Random(getSeed()).nextInt(10);
		int max = min + new Random(getSeed()).nextInt(48) + 1;

		data.addTotal(TAReconstructed.RES_CAT.key, min + player.getRNG().nextInt(max - min));
		data.bonusDraws++;

		if(player.getRNG().nextFloat() < .7F)
			data.addInspiration(1);

		return true;
	}
}