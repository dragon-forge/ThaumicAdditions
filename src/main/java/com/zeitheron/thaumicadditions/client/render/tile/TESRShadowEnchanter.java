package com.zeitheron.thaumicadditions.client.render.tile;

import com.zeitheron.hammercore.annotations.AtTESR;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.thaumicadditions.tiles.TileShadowEnchanter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@AtTESR(TileShadowEnchanter.class)
public class TESRShadowEnchanter extends TESR<TileShadowEnchanter>
{
	@Override
	public void renderTileEntityAt(TileShadowEnchanter te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		ItemStack stack = te.items.getStackInSlot(0);
		if(stack.isEmpty())
			stack = te.items.getStackInSlot(1);
		if(stack.isEmpty())
			return;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + .5, y + 1 + MathHelper.sin((float) Math.PI * ((te.ticksExisted + partialTicks) / 60F % 60F)) * 0.05F, z + .5);
		GlStateManager.rotate((te.ticksExisted + partialTicks) % 360F, 0, 1, 0);
		GlStateManager.translate(.08, 0, 0);
		GlStateManager.rotate(45, 0, 0, 1);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.GROUND);
		GlStateManager.popMatrix();
	}
}