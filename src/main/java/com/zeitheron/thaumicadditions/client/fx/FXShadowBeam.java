package com.zeitheron.thaumicadditions.client.fx;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.particle.api.SimpleParticle;
import com.zeitheron.hammercore.utils.color.ColorHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FXShadowBeam extends SimpleParticle
{
	public final List<Vec3d> positions;
	
	public FXShadowBeam(World worldIn, double posXIn, double posYIn, double posZIn, List<Vec3d> positions)
	{
		super(worldIn, posXIn, posYIn, posZIn);
		this.positions = positions;
		particleMaxAge = 20;
	}
	
	@Override
	public void doRenderParticle(double x, double y, double z, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		float age = Math.min(particleAge + partialTicks, particleMaxAge);
		if(age <= 5F)
			particleScale = age / 5F;
		if(particleMaxAge - age <= 5F)
			particleScale = (particleMaxAge - age) / 5F;
		
		GlStateManager.pushMatrix();
		float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(8F * Math.max(0.001F, particleScale));
		GlStateManager.enableBlend();
		ColorHelper.glColor1ia(0x7712DFFF);
		GlStateManager.translate(x, y, z);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		for(int i = 0; i < positions.size() - 1; ++i)
		{
			Vec3d pos = positions.get(i);
			Vec3d pos2 = positions.get(i + 1);
			
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(pos.x - posX, pos.y - posY, pos.z - posZ);
			GL11.glVertex3d(pos2.x - posX, pos2.y - posY, pos2.z - posZ);
			GL11.glEnd();
		}
		GL11.glLineWidth(w);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}
}