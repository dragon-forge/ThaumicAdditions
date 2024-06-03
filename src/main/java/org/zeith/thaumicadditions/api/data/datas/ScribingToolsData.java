package org.zeith.thaumicadditions.api.data.datas;

import net.minecraft.item.ItemStack;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.DataType;
import org.zeith.thaumicadditions.compat.thaumcraft.TARCThaumcraft;

/**
 * Register for custom {@link net.minecraft.item.Item} to let ThaumicAdditions know your {@link net.minecraft.item.Item} is a set of scribing tools.
 * <p>
 * This data is used for patching {@link thaumcraft.common.lib.research.ScanSky}.
 * Patch is located in {@link org.zeith.thaumicadditions.asm.mixins.ScanSkyMixin}.
 *
 * @see TARCThaumcraft#initScribingTools()
 */
public class ScribingToolsData
{
	public static final ScribingToolsData INSTANCE = new ScribingToolsData();
	public static final DataType<ScribingToolsData> TYPE = new DataType<>(InfoTAR.id("scribing_tools_data"), ScribingToolsData.class);
	
	@Override
	public String toString()
	{
		return "ScribingToolsData";
	}
	
	public static boolean isScribingTools(ItemStack stack)
	{
		return DataProviderRegistry.first(stack.getItem(), TYPE).isPresent();
	}
}