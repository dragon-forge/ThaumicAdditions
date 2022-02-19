package org.zeith.thaumicadditions.client.render.tile;

import com.zeitheron.hammercore.annotations.AtTESR;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.zeith.thaumicadditions.tiles.TileEssentiaSink;

@AtTESR(TileEssentiaSink.class)
public class TESREssentiaSink
		extends TESR<TileEssentiaSink>
{
	@Override
	public void renderTileEntityAt(TileEssentiaSink te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		ItemStack stack = te.inventory.getStackInSlot(0);
	}
}