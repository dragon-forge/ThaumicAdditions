package com.zeitheron.thaumicadditions.client.render.tile;

import com.zeitheron.hammercore.annotations.AtTESR;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.thaumicadditions.tiles.TileEssentiaSink;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@AtTESR(TileEssentiaSink.class)
public class TESREssentiaSink extends TESR<TileEssentiaSink>
{
	@Override
	public void renderTileEntityAt(TileEssentiaSink te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		ItemStack stack = te.inventory.getStackInSlot(0);
	}
}