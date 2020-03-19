package com.zeitheron.thaumicadditions.client.fx;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import com.zeitheron.hammercore.client.particle.api.SimpleParticle;
import com.zeitheron.thaumicadditions.InfoTAR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleFX
		extends SimpleParticle
		implements IGlowingEntity
{
	public final ResourceLocation FX;
	public float lightStrength = 1F;
	protected int frame;

	public Vec3d lightColor = new Vec3d(1F, 1F, 1F);

	public ParticleFX(World worldIn, Vec3d pos, int idx)
	{
		super(worldIn, pos.x, pos.y, pos.z);
		FX = new ResourceLocation(InfoTAR.MOD_ID, "textures/particles/fx/" + idx + ".png");
		particleAge = 0;
		particleMaxAge = 15;
		frame = 0;
		canCollide = false;
		particleGravity = 0F;
		motionX = (rand.nextDouble() - rand.nextDouble()) * 0.15D;
		motionY = (rand.nextDouble() - rand.nextDouble()) * 0.15D;
		motionZ = (rand.nextDouble() - rand.nextDouble()) * 0.15D;
	}

	public ParticleFX setScale(float particleScale)
	{
		this.particleScale = particleScale;
		return this;
	}

	public ParticleFX setLightStrength(float lightStrength)
	{
		this.lightStrength = lightStrength;
		return this;
	}

	public ParticleFX setAgeMax(int max)
	{
		particleMaxAge = (int) Math.ceil(max + Math.random() * (max / 5F));
		return this;
	}

	public void setVel(double x, double y, double z)
	{
		motionX = x;
		motionY = y;
		motionZ = z;
	}

	public void spawnAt(double x, double y, double z)
	{
		posX = x;
		posY = y;
		posZ = z;
		spawn();
	}

	@Override
	public void onUpdate()
	{
		try
		{
			super.onUpdate();
			frame = (int) Math.round((double) particleAge / (double) particleMaxAge * 5D);
			particleAlpha = 1F - ((float) particleAge / (float) particleMaxAge);
		} catch(Throwable err)
		{
			setExpired();
		}
	}

	@Override
	public int getBrightnessForRender(float partialTick)
	{
		if(world == null)
			return 0;
		return super.getBrightnessForRender(partialTick);
	}

	@Override
	public void doRenderParticle(double x, double y, double z, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		try
		{
			float c = (System.currentTimeMillis() + hashCode()) % (particleMaxAge * 50L + 100L) / 1000F;
			prevParticleAngle = c;
			particleAngle = c;

			frame = (int) Math.round(((double) particleAge) / (double) particleMaxAge * 2D);

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder vb = tess.getBuffer();

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			Minecraft.getMinecraft().getTextureManager().bindTexture(FX);

			// X
			float minU = 0;
			float maxU = 5 / 16F;

			// Y
			float minV = frame * 5 / 16F;
			float maxV = minV + 5 / 16F;

			float f4 = 0.1F * this.particleScale;

			float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
			float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
			float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
			int i = this.getBrightnessForRender(partialTicks);
			int j = i >> 16 & 65535;
			int k = i & 65535;
			Vec3d[] avec3d = new Vec3d[]{
					new Vec3d(-rotationX * f4 - rotationXY * f4, -rotationZ * f4, -rotationYZ * f4 - rotationXZ * f4),
					new Vec3d(-rotationX * f4 + rotationXY * f4, rotationZ * f4, -rotationYZ * f4 + rotationXZ * f4),
					new Vec3d(rotationX * f4 + rotationXY * f4, rotationZ * f4, rotationYZ * f4 + rotationXZ * f4),
					new Vec3d(rotationX * f4 - rotationXY * f4, -rotationZ * f4, rotationYZ * f4 - rotationXZ * f4)
			};

			if(this.particleAngle != 0.0F)
			{
				float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
				float f9 = MathHelper.cos(f8 * 0.5F);
				float f10 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.x;
				float f11 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.y;
				float f12 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.z;
				Vec3d vec3d = new Vec3d(f10, f11, f12);

				for(int l = 0; l < 4; ++l)
				{
					avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double) (f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale(2.0F * f9));
				}
			}

			BufferBuilder buffer = tess.getBuffer();
			buffer.pos((double) f5 + avec3d[0].x, (double) f6 + avec3d[0].y, (double) f7 + avec3d[0].z).tex(maxU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
			buffer.pos((double) f5 + avec3d[1].x, (double) f6 + avec3d[1].y, (double) f7 + avec3d[1].z).tex(maxU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
			buffer.pos((double) f5 + avec3d[2].x, (double) f6 + avec3d[2].y, (double) f7 + avec3d[2].z).tex(minU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
			buffer.pos((double) f5 + avec3d[3].x, (double) f6 + avec3d[3].y, (double) f7 + avec3d[3].z).tex(minU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();

			tess.draw();
		} catch(Throwable err)
		{
			err.printStackTrace();
			setExpired();
		}
	}

	@Override
	public ColoredLight produceColoredLight(float partialTicks)
	{
		float x = (float) this.posX + .05F;
		float y = (float) this.posY + .05F;
		float z = (float) this.posZ + .05F;

		return ColoredLight.builder().pos(x, y, z).color(lightColor, 1F).radius(lightStrength * this.particleScale).build();
	}
}