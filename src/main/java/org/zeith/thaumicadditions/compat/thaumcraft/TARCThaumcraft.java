package org.zeith.thaumicadditions.compat.thaumcraft;

import com.zeitheron.hammercore.mod.ModuleLoader;
import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.datas.*;
import org.zeith.thaumicadditions.blocks.BlockAbstractSmelter;
import org.zeith.thaumicadditions.compat.ITARC;
import org.zeith.thaumicadditions.items.tools.ItemVisScribingTools;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.tools.ItemScribingTools;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;

@ModuleLoader(requiredModid = "thaumcraft")
public class TARCThaumcraft
		implements ITARC
{
	@Override
	public void init()
	{
		initSmelters();
		initMirrors();
		initGauntlets();
		initScribingTools();
	}
	
	public void initScribingTools()
	{
		DataProviderRegistry.registerStable(ScribingToolsData.TYPE, ItemScribingTools.class, ScribingToolsData.INSTANCE);
		DataProviderRegistry.registerStable(ScribingToolsData.TYPE, ItemVisScribingTools.class, ScribingToolsData.INSTANCE);
	}
	
	public void initSmelters()
	{
		DataProviderRegistry.registerStable(SmelterData.TYPE, BlockAbstractSmelter.class, SmelterData.INSTANCE);
	}
	
	public void initMirrors()
	{
		DataProviderRegistry.registerSimple(MirrorData.TYPE, TileMirror.class, m -> new MirrorData(
				m.linked,
				new BlockPos(m.linkX, m.linkY, m.linkZ),
				m.linkDim
		));
		
		DataProviderRegistry.registerSimple(MirrorData.TYPE, TileMirrorEssentia.class, m -> new MirrorData(
				m.linked,
				new BlockPos(m.linkX, m.linkY, m.linkZ),
				m.linkDim
		));
	}
	
	public void initGauntlets()
	{
		DataProviderRegistry.registerStable(GauntletData.TYPE, ItemCaster.class, GauntletData.INSTANCE);
	}
}