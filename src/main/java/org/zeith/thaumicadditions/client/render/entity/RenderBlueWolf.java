package org.zeith.thaumicadditions.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.client.models.ModelBlueWolf;
import org.zeith.thaumicadditions.entity.EntityBlueWolf;

@SideOnly(Side.CLIENT)
public class RenderBlueWolf
		extends RenderLiving<EntityBlueWolf>
{
	private static final ResourceLocation WOLF_TEXTURES = new ResourceLocation(InfoTAR.MOD_ID, "textures/entity/blue_wolf.png");
	private static final ResourceLocation ANRGY_WOLF_TEXTURES = new ResourceLocation(InfoTAR.MOD_ID, "textures/entity/blue_wolf_angry.png");

	public RenderBlueWolf(RenderManager rendermanagerIn)
	{
		super(rendermanagerIn, new ModelBlueWolf(), 0.5F);
	}

	@Override
	protected float handleRotationFloat(EntityBlueWolf livingBase, float partialTicks)
	{
		return livingBase.getTailRotation();
	}

	@Override
	public void doRender(EntityBlueWolf entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		if(entity.isWolfWet())
		{
			float f = entity.getBrightness() * entity.getShadingWhileWet(partialTicks);
			GlStateManager.color(f, f, f);
		}

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBlueWolf entity)
	{
		return entity.isAngry() ? ANRGY_WOLF_TEXTURES : WOLF_TEXTURES;
	}
}