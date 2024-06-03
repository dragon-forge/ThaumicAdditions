package org.zeith.thaumicadditions.api.data.datas;

import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.DataType;

public class MirrorData
{
	public static final DataType<MirrorData> TYPE = new DataType<>(InfoTAR.id("mirror_data"), MirrorData.class);
	public final boolean linked;
	public final BlockPos linkPos;
	public final int linkDimension;
	
	public MirrorData(boolean linked, BlockPos linkPos, int linkDimension)
	{
		this.linked = linked;
		this.linkPos = linkPos;
		this.linkDimension = linkDimension;
	}
}