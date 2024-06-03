package org.zeith.thaumicadditions.api.data.datas;

import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.DataType;
import org.zeith.thaumicadditions.compat.thaumcraft.TARCThaumcraft;

/**
 * Register for custom {@link net.minecraft.tileentity.TileEntity} classes to let ThaumicAdditions know your tile is a mirror (optionally) linked to another mirror.
 *
 * @see TARCThaumcraft#initMirrors()
 */
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
	
	@Override
	public String toString()
	{
		return "MirrorData{" +
			   "linked=" + linked +
			   ", linkPos=" + linkPos +
			   ", linkDimension=" + linkDimension +
			   '}';
	}
}