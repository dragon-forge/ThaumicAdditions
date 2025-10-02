package org.zeith.thaumicadditions.init;

import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.infusion.InfusionResultType;

@EventBusSubscriber
public class RegistriesTAR
{
	private static IForgeRegistry<InfusionResultType> INFUSION_TYPES;
	
	@SubscribeEvent
	public static void newRegistries(NewRegistry e)
	{
		INFUSION_TYPES = new RegistryBuilder<InfusionResultType>()
				.setType(InfusionResultType.class)
				.setName(InfoTAR.id("infusion_types"))
				.disableSaving()
				.create();
	}
	
	public static IForgeRegistry<InfusionResultType> INFUSION_TYPES()
	{
		return INFUSION_TYPES;
	}
}