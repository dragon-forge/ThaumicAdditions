package org.zeith.thaumicadditions.compat.thaumicaugmentation;

import com.zeitheron.hammercore.mod.ModuleLoader;
import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.api.data.*;
import org.zeith.thaumicadditions.api.data.datas.MirrorData;
import org.zeith.thaumicadditions.compat.ITARC;
import thecodex6824.thaumicaugmentation.api.util.DimensionalBlockPos;
import thecodex6824.thaumicaugmentation.common.tile.TileImpetusMirror;

@ModuleLoader(requiredModid = "thaumicaugmentation")
public class TARCThaumicAugmentation
		implements ITARC
{
	@Override
	public void init()
	{
		DataProviderRegistry.register(MirrorData.TYPE, IDataProvider.simple(TileImpetusMirror.class,
				ISimpleDataProvider.simple(MirrorData.TYPE, m ->
				{
					DimensionalBlockPos link = m.getLink();
					if(link == null || link == DimensionalBlockPos.INVALID) return new MirrorData(
							false,
							BlockPos.ORIGIN,
							0
					);
					return new MirrorData(
							true,
							link.getPos(),
							link.getDimension()
					);
				})
		));
	}
}