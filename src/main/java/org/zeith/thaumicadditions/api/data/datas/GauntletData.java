package org.zeith.thaumicadditions.api.data.datas;

import net.minecraft.item.ItemStack;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.DataType;

public class GauntletData
{
	public static final GauntletData INSTANCE = new GauntletData();
	public static final DataType<GauntletData> TYPE = new DataType<>(InfoTAR.id("gauntlet_data"), GauntletData.class);
	
	@Override
	public String toString()
	{
		return "GauntletData";
	}
	
	public static boolean isGauntlet(ItemStack stack)
	{
		return DataProviderRegistry.first(stack.getItem(), TYPE).isPresent();
	}
}