package com.zeitheron.thaumicadditions.client.render.tile;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.thaumicadditions.client.models.ModelAuraCharger;
import com.zeitheron.thaumicadditions.tiles.TileFluxConcentrator;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TESRFluxConcentrator extends TESR<TileFluxConcentrator>
{
	@Override
	public void renderTileEntityAt(TileFluxConcentrator te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		
	}
	
	@Override
	public void renderItem(ItemStack item)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(.5, 2.25, .5);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glScaled(1.5, 1.5, 1.5);
//		bindTexture(texture);
//		new ModelAuraCharger().render(null, 0, 0, 0, 0, 0, 1 / 16F);
		GL11.glPopMatrix();
	}
}