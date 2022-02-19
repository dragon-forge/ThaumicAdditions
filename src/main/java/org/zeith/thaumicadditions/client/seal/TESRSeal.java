package org.zeith.thaumicadditions.client.seal;

import com.zeitheron.hammercore.annotations.AtTESR;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.hammercore.utils.EnumRotation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.api.seals.ISealRenderer;
import org.zeith.thaumicadditions.init.BlocksTAR;
import org.zeith.thaumicadditions.tiles.TileSeal;
import thaumcraft.api.aspects.Aspect;

@AtTESR(TileSeal.class)
public class TESRSeal
		extends TESR<TileSeal>
{
	@Override
	public void renderTileEntityAt(TileSeal te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		if(te.getLocation().getBlock() != BlocksTAR.SEAL)
			return;

		GL11.glPushMatrix();
		translateFromOrientation(x, y, z, te.getLocation().getState().getValue(EnumRotation.EFACING));
		GL11.glRotated(90, 1, 0, 0);
		GL11.glTranslated(0, -2 / 16D, 0);
		mc.getRenderItem().renderItem(te.stack.get(), TransformType.GROUND);

		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_DST_ALPHA);

		for(int i = 0; i < 3; ++i)
		{
			Aspect ss = te.getSymbol(i);
			if(ss != null)
				TARSealRenders.renderStandart(te, partialTicks, i);
		}

		bb:
		if(te.combination != null)
		{
			ISealRenderer render = te.combination.getRender(te, 0);
			if(render != null)
				try
				{
					render.render(te, partialTicks);
				} catch(Throwable err)
				{
					err.printStackTrace();
				}
		}
		GL11.glPopMatrix();
	}

	@Override
	public void translateFromOrientation(double x, double y, double z, EnumFacing facing)
	{
		int orientation = facing.ordinal();

		if(orientation == 0)
		{
			GL11.glTranslated(x + .5, y + 1, z + .5);
			GL11.glRotated(180, 1, 0, 0);
		} else if(orientation == 1)
			GL11.glTranslated(x + .5, y, z + .5);
		else if(orientation == 2)
		{
			GL11.glTranslated(x + .5, y + .5, z + 1);
			GL11.glRotatef(-90, 1, 0, 0);
		} else if(orientation == 3)
		{
			GL11.glTranslated(x + .5, y + .5, z);
			GL11.glRotatef(90, 1, 0, 0);
		} else if(orientation == 4)
		{
			GL11.glTranslated(x + 1, y + .5, z + .5);
			GL11.glRotatef(90, 0, 0, 1);
		} else if(orientation == 5)
		{
			GL11.glTranslated(x, y + .5, z + .5);
			GL11.glRotatef(-90, 0, 0, 1);
		}
	}
}