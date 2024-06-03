package org.zeith.thaumicadditions.client.isr;

import com.zeitheron.hammercore.client.render.item.IItemRender;
import com.zeitheron.hammercore.client.utils.RenderBlocks;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.datas.JarData;
import org.zeith.thaumicadditions.api.data.datas.JarData.JarStorage;
import org.zeith.thaumicadditions.client.render.tile.TESRAbstractJar;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ModConfig.CONFIG_GRAPHICS;

public class ItemRenderJar
		implements IItemRender
{
	public static void renderJarEssentia(int amount, int cap, Aspect aspect)
	{
		if(amount > 0 && cap > 0 && aspect != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5F, 0.01F, 0.5F);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			renderLiquidInItem(amount, aspect, cap, 0, 0, 0);
			GlStateManager.popMatrix();
		}
	}
	
	public static void renderLiquidInItem(int amount, Aspect aspect, int cap, double x, double y, double z)
	{
		if(aspect == null)
			return;
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		RenderBlocks renderBlocks = RenderBlocks.forMod(InfoTAR.MOD_ID);
		GL11.glDisable(2896);
		float level = amount / ((float) cap) * 0.625F;
		
		Minecraft mc = Minecraft.getMinecraft();
		Tessellator t = Tessellator.getInstance();
		renderBlocks.setRenderBounds(0.25, 0.0625, 0.25, 0.75, 0.0625 + level, 0.75);
		t.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		int rgb = aspect.getColor();
		TextureAtlasSprite icon = mc.getTextureMapBlocks().getAtlasSprite("thaumcraft:blocks/animatedglow");
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		float r = ColorHelper.getRed(rgb);
		float g = ColorHelper.getGreen(rgb);
		float b = ColorHelper.getBlue(rgb);
		renderBlocks.renderFaceYNeg(-0.5, 0.0, -0.5, icon, r, g, b, 200);
		renderBlocks.renderFaceYPos(-0.5, 0.0, -0.5, icon, r, g, b, 200);
		renderBlocks.renderFaceZNeg(-0.5, 0.0, -0.5, icon, r, g, b, 200);
		renderBlocks.renderFaceZPos(-0.5, 0.0, -0.5, icon, r, g, b, 200);
		renderBlocks.renderFaceXNeg(-0.5, 0.0, -0.5, icon, r, g, b, 200);
		renderBlocks.renderFaceXPos(-0.5, 0.0, -0.5, icon, r, g, b, 200);
		t.draw();
		GL11.glEnable(2896);
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void renderItem(ItemStack stack)
	{
		JarData jd = JarData.fromStack(stack);
		if(jd == null) return;
		JarStorage storage = jd.getStorage(stack);
		if(storage == null) return;
		
		int amount = storage.amount;
		int cap = jd.capacity;
		Aspect aspect = storage.aspect;
		Aspect aspectFilter = storage.aspectFilter;
		
		renderJarEssentia(amount, cap, aspect);
		
		if(aspectFilter == null) return;
		
		GlStateManager.pushMatrix();
		{
			GlStateManager.disableBlend();
			GlStateManager.translate(0.5F, 0.4F, -0.135F);
			GlStateManager.blendFunc(770, 771);
			float rot = aspectFilter.getTag().hashCode() % 4 - 2;
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 0, 0.315F);
				if(CONFIG_GRAPHICS.crooked)
					GlStateManager.rotate(rot, 0.0F, 0.0F, 1.0F);
				UtilsFX.renderQuadCentered(TESRAbstractJar.TEX_LABEL, 0.5F, 1.0F, 1.0F, 1.0F, -99, 771, 1F);
				GlStateManager.popMatrix();
			}
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 0, 0.316F);
				if(CONFIG_GRAPHICS.crooked)
					GlStateManager.rotate(rot, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(-0.021F, -0.021F, -0.021F);
				UtilsFX.drawTag(-8, -8, aspectFilter);
				GlStateManager.popMatrix();
			}
			GlStateManager.enableBlend();
		}
		GlStateManager.popMatrix();
		
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}
}