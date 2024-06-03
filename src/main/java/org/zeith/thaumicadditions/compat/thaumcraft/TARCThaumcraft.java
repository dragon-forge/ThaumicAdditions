package org.zeith.thaumicadditions.compat.thaumcraft;

import com.zeitheron.hammercore.mod.ModuleLoader;
import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.datas.GauntletData;
import org.zeith.thaumicadditions.api.data.datas.MirrorData;
import org.zeith.thaumicadditions.compat.ITARC;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;

@ModuleLoader(requiredModid = "thaumcraft")
public class TARCThaumcraft
		implements ITARC
{
	@Override
	public void init()
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
		
		DataProviderRegistry.registerStable(GauntletData.TYPE, ItemCaster.class, GauntletData.INSTANCE);
	}
}