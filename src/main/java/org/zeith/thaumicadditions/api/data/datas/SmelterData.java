package org.zeith.thaumicadditions.api.data.datas;

import net.minecraft.block.Block;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.DataType;
import org.zeith.thaumicadditions.compat.thaumcraft.TARCThaumcraft;

/**
 * Register for custom {@link Block} classes to let ThaumicAdditions know your block is a smelter to let Thaumcraft connext auxiliary pumps and vents.
 * <p>
 * This data is used for patching {@link thaumcraft.common.blocks.essentia.BlockSmelterAux} and {@link thaumcraft.common.blocks.essentia.BlockSmelterVent}.
 * Patch is located in {@link org.zeith.thaumicadditions.asm.mixins.BlockSmelterAuxMixin}.
 *
 * @see TARCThaumcraft#initSmelters()
 */
public class SmelterData
{
	public static final SmelterData INSTANCE = new SmelterData();
	public static final DataType<SmelterData> TYPE = new DataType<>(InfoTAR.id("smelter_data"), SmelterData.class);
	
	@Override
	public String toString()
	{
		return "SmelterData";
	}
	
	public static boolean isSmelter(Block block)
	{
		return DataProviderRegistry.first(block, TYPE).isPresent();
	}
}