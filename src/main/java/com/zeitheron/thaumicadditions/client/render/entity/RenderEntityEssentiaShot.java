package com.zeitheron.thaumicadditions.client.render.entity;

import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.entity.EntityEssentiaShot;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderEntityEssentiaShot extends Render<EntityEssentiaShot>
{
	public static final ResourceLocation ESSENTIA_DROP = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/essentia_drop.png");
	
	public RenderEntityEssentiaShot(RenderManager renderManager)
	{
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityEssentiaShot entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityEssentiaShot entity)
	{
		return ESSENTIA_DROP;
	}
}