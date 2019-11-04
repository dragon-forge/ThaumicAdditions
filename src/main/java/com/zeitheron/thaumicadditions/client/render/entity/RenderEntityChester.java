package com.zeitheron.thaumicadditions.client.render.entity;

import java.util.function.BiConsumer;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.client.models.ModelChester;
import com.zeitheron.thaumicadditions.entity.EntityChester;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderEntityChester extends RenderLiving<EntityChester>
{
	private final ModelChester trunkModel;
	
	public RenderEntityChester(RenderManager renderManager)
	{
		this(renderManager, new ModelChester());
	}
	
	protected RenderEntityChester(RenderManager renderManager, ModelChester model)
	{
		super(renderManager, model, .01F);
		trunkModel = model;
	}
	
	protected void adjustTrunk(EntityChester entity, float f)
	{
		int i = 2;
		float f1 = (entity.field_767_b + (entity.field_768_a - entity.field_767_b) * f) / (i * .5F + 1);
		float f2 = 1 / (f1 + 1);
		float f3 = i;
		
		f1 /= 1.5F;
		f2 /= 1.4F;
		f3 /= 1.5F;
		
		GL11.glScalef(f2 * f3, .5F / f2 * f3, f2 * f3);
		GL11.glTranslatef(-.45F, .45F, -.45F);
		f1 = 1 - entity.getCurrentLidRotation(f);
		f1 = 0;
		f1 = 1 - f1 * f1 * f1;
		trunkModel.chestLid.rotateAngleX = -f1 * (float) Math.PI / 2F;
	}
	
	public static final ResourceLocation CHESTER_TEXTURE = new ResourceLocation(InfoTAR.MOD_ID, "textures/models/chester.png");
	public static boolean visualsEnabled = false;
	public static BiConsumer<EntityChester, TwoTuple<Vec3d, Vec2f>> visualsRenderer = null;
	
	@Override
	protected ResourceLocation getEntityTexture(EntityChester entity)
	{
		return CHESTER_TEXTURE;
	}
	
	@Override
	protected void preRenderCallback(EntityChester entity, float partialTickTime)
	{
		int i = 2;
		float f1 = (entity.field_767_b + (entity.field_768_a - entity.field_767_b) * partialTickTime) / (i * .5F + 1);
		float f2 = 1 / (f1 + 1);
		float f3 = i;
		
		f1 /= 1.5F;
		f2 /= 1.4F;
		f3 /= 1.5F;
		
		float lid = entity.getCurrentLidRotation(partialTickTime);
		
		GL11.glScalef(f2 * f3, .5F / f2 * f3, f2 * f3);
		GL11.glTranslatef(-.45F, .45F, -.45F);
		f1 = 1 - lid;
		f1 = 1 - f1 * f1 * f1;
		trunkModel.chestLid.rotateAngleX = -f1 * (float) Math.PI / 2F;
	}
	
	@Override
	public void doRender(EntityChester entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		if(visualsRenderer != null)
			visualsRenderer.accept(entity, new TwoTuple<>(new Vec3d(x, y , z), new Vec2f(entityYaw, partialTicks)));
	}
}