package org.zeith.thaumicadditions.client.render.tile;

import com.zeitheron.hammercore.annotations.AtTESR;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.hammercore.client.render.vertex.SimpleBlockRendering;
import com.zeitheron.hammercore.client.utils.RenderBlocks;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.client.utils.texture.TextureAtlasSpriteFull;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.tiles.TileArcaneCake;

@AtTESR(TileArcaneCake.class)
public class TESRArcaneCake
		extends TESR<TileArcaneCake>
{
	ResourceLocation cakeTop = new ResourceLocation(InfoTAR.MOD_ID, "textures/blocks/cake_top_cherries.png");

	@Override
	public void renderTileEntityAt(TileArcaneCake te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		GlStateManager.blendFunc(771, 1);

		SimpleBlockRendering sbr = RenderBlocks.forMod(InfoTAR.MOD_ID).simpleRenderer;
		UtilsFX.bindTexture(cakeTop);
		sbr.begin();
		sbr.setBrightness(getBrightnessForRB(te, sbr.rb));
		sbr.setSprite(TextureAtlasSpriteFull.sprite);
		{
			sbr.disableFaces();
			sbr.enableFace(EnumFacing.UP);
			sbr.setColor(EnumFacing.UP, 255 << 24 | te.getRGB());
			AxisAlignedBB aabb = te.getWorld().getBlockState(te.getPos()).getCollisionBoundingBox(te.getWorld(), te.getPos());
			if(aabb != null) // Fix random crash with tessellation
				sbr.setRenderBounds(aabb.offset(0, .01 / 16D, 0));

			sbr.drawBlock(x, y, z);
		}
		Tessellator.getInstance().draw();

		GlStateManager.enableLighting();
	}
}