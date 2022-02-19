package org.zeith.thaumicadditions.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.entity.EntityEssentiaShot;

@SideOnly(Side.CLIENT)
public class RenderEssentiaShot
		extends Render<EntityEssentiaShot>
{
	public static final ResourceLocation ESSENTIA_DROP = new ResourceLocation(InfoTAR.MOD_ID, "textures/misc/essentia_drop.png");

	public RenderEssentiaShot(RenderManager renderManager)
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