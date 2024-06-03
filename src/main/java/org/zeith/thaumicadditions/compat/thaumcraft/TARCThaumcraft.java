package org.zeith.thaumicadditions.compat.thaumcraft;

import com.zeitheron.hammercore.mod.ModuleLoader;
import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.api.data.*;
import org.zeith.thaumicadditions.api.data.datas.MirrorData;
import org.zeith.thaumicadditions.compat.ITARC;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;

@ModuleLoader(requiredModid = "thaumcraft")
public class TARCThaumcraft
		implements ITARC
{
	@Override
	public void init()
	{
		DataProviderRegistry.register(MirrorData.TYPE, IDataProvider.simple(TileMirror.class,
				ISimpleDataProvider.simple(MirrorData.TYPE, m -> new MirrorData(
						m.linked,
						new BlockPos(m.linkX, m.linkY, m.linkZ),
						m.linkDim
				))
		));
		
		DataProviderRegistry.register(MirrorData.TYPE, IDataProvider.simple(TileMirrorEssentia.class,
				ISimpleDataProvider.simple(MirrorData.TYPE, m -> new MirrorData(
						m.linked,
						new BlockPos(m.linkX, m.linkY, m.linkZ),
						m.linkDim
				))
		));
	}
}