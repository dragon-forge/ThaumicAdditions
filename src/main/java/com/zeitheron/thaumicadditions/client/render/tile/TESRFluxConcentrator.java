package com.zeitheron.thaumicadditions.client.render.tile;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.client.models.ModelFluxConcentrator;
import com.zeitheron.thaumicadditions.tiles.TileFluxConcentrator;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;

public class TESRFluxConcentrator extends TESR<TileFluxConcentrator>
{
	public static final ModelFluxConcentrator MODEL = new ModelFluxConcentrator();
	
	@Override
	public void renderTileEntityAt(TileFluxConcentrator te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		for(int i = 0; i < (destroyStage != null ? 2 : 1); ++i)
		{
			GL11.glPushMatrix();
			translateFromOrientation(x, y, z, WorldUtil.getFacing(te.getWorld().getBlockState(te.getPos())).getOpposite());
			GlStateManager.translate(0, 1.5, 0);
			
			GL11.glRotated(180, 1, 0, 0);
			if(i == 1)
				bindTexture(destroyStage);
			else
			{
				MODEL.bindTexture(te);
				GlStateManager.enableBlend();
			}
			MODEL.render(null, partialTicks, 0, 0, 0, 0, 1 / 16F);
			if(i != 1)
				GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
	}
	
	@Override
	public void renderItem(ItemStack item)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(.5, 1.5, .5);
		GL11.glRotated(180, 1, 0, 0);
		MODEL.bindTexture(null);
		MODEL.render(null, 0, 0, 0, 0, 0, 1 / 16F);
		GL11.glPopMatrix();
	}
}