package org.zeith.thaumicadditions.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import org.zeith.thaumicadditions.api.AttributesTAR;

import java.util.UUID;

public class PotionSoundSensivity
		extends PotionBaseTAR
{
	public static final UUID SENS = new UUID(5794693234585715527L, -7627586313040753090L);

	public PotionSoundSensivity()
	{
		super(false, 0xFFAA00, "sonus", 0, 0);
	}

	public PotionSoundSensivity(boolean bad, int color, String name, int tx, int ty)
	{
		super(bad, color, name, tx, ty);
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier)
	{
		if(entityLivingBaseIn instanceof EntityPlayer)
		{
			IAttributeInstance attr = attributeMapIn.getAttributeInstance(AttributesTAR.SOUND_SENSIVITY);

			float amt = 1;

			if(amplifier > 20)
				amt = .01F;
			else
				amt += amplifier / 8F;

			attr.removeModifier(SENS);
			attr.applyModifier(new AttributeModifier(SENS, "Sonus Potion", amt - 1, 2));
		}
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier)
	{
		if(entityLivingBaseIn instanceof EntityPlayer)
		{
			IAttributeInstance attr = attributeMapIn.getAttributeInstance(AttributesTAR.SOUND_SENSIVITY);
			attr.removeModifier(SENS);
		}
	}
}