package org.zeith.thaumicadditions.client.models;

import com.zeitheron.hammercore.client.model.mc.ModelRendererWavefront;
import com.zeitheron.hammercore.client.utils.rendering.WavefrontLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.zeith.thaumicadditions.InfoTAR;

public class ModelBlueWolfHead
		extends ModelBiped
{
	private final double EAR_DEGREES_RAD = Math.toRadians(14);
	private final double TAIL_DEGREES_RAD = Math.toRadians(16);
	public ModelRendererWavefront headBase, headLeftEar, headRightEar;
	public ModelRendererWavefront bodyBase, bodyLeftPaw, bodyRightPaw;
	public ModelRendererWavefront legsTail, legsRight, legsLeft;
	public ModelRendererWavefront feetpawLeft, feetpawRight;
	public ModelBlueWolfHead(float modelSize, boolean isHelmet, boolean isChestPiece, boolean isLeggings, boolean isdBoots)
	{
		super(modelSize);

		this.headBase = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/head_base.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_head.png"));
		this.headLeftEar = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/head_left_ear.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_head.png"));
		this.headRightEar = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/head_right_ear.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_head.png"));

		this.bodyBase = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/body_base.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_body.png"));
		this.bodyLeftPaw = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/body_left_paw.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_body.png"));
		this.bodyRightPaw = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/body_right_paw.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_body.png"));

		this.legsTail = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/legs_tail.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_legs.png"));
		this.legsLeft = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/legs_left.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_legs.png"));
		this.legsRight = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/legs_right.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_legs.png"));

		this.feetpawLeft = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/feetpaw_left.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_feetpaws.png"));
		this.feetpawRight = new ModelRendererWavefront(this, WavefrontLoader.getMeshProvider(new ResourceLocation(InfoTAR.MOD_ID, "models/blue_wolf_suit/feetpaw_right.obj")), new ResourceLocation(InfoTAR.MOD_ID, "textures/armor/blue_wolf_feetpaws.png"));

		this.bipedHead.cubeList.clear();
		this.bipedHeadwear.cubeList.clear();
		this.bipedBody.cubeList.clear();
		this.bipedRightArm.cubeList.clear();
		this.bipedLeftArm.cubeList.clear();
		this.bipedLeftLeg.cubeList.clear();
		this.bipedRightLeg.cubeList.clear();

		headBase.offsetY = 0.97F;
		headLeftEar.offsetY = -0.012F;
		headRightEar.offsetY = -0.012F;
		headLeftEar.offsetX = -0.07F;
		headRightEar.offsetX = 0.07F;

		bodyBase.offsetZ = 0.005F;
		bodyBase.offsetY = 1F;
		bodyRightPaw.offsetY = 1F;
		bodyRightPaw.offsetX = 0.4F;
		bodyRightPaw.offsetZ = 0F;
		bodyLeftPaw.offsetY = 1F;
		bodyLeftPaw.offsetX = -0.4F;
		bodyLeftPaw.offsetZ = 0F;

		legsTail.offsetY = 0.756F;
		legsTail.offsetZ = 1 / 11F;
		legsRight.offsetY = 0.27F;
		legsRight.offsetX = 0.14F;
		legsLeft.offsetY = 0.27F;
		legsLeft.offsetX = -0.14F;

		feetpawRight.offsetX = 0.206F;
		feetpawRight.offsetY = 0.225F;
		feetpawRight.offsetZ = 0.067F;

		feetpawLeft.offsetX = -0.074F;
		feetpawLeft.offsetY = 0.225F;
		feetpawLeft.offsetZ = 0.067F;

		//

		legsRight.scale = 1F / 15F;
		legsLeft.scale = 1F / 15F;
		feetpawLeft.scale = 1F / 15F;
		feetpawRight.scale = 1F / 15F;

		bodyLeftPaw.scale = 1F / 13.7F;
		bodyRightPaw.scale = 1F / 13.7F;

		headBase.scale = 1 / 16F;

		if(isHelmet)
		{
			this.bipedHead.addChild(headBase);
			this.bipedHead.addChild(headLeftEar);
			this.bipedHead.addChild(headRightEar);
		}

		if(isChestPiece)
		{
			this.bipedBody.addChild(bodyBase);
			this.bipedLeftArm.addChild(bodyLeftPaw);
			this.bipedRightArm.addChild(bodyRightPaw);
		}

		if(isLeggings)
		{
			this.bipedBody.addChild(legsTail);
			this.bipedLeftLeg.addChild(legsLeft);
			this.bipedRightLeg.addChild(legsRight);
		}

		if(isdBoots)
		{
			this.bipedLeftLeg.addChild(feetpawLeft);
			this.bipedRightLeg.addChild(feetpawRight);
		}
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{

		if(entity == null || entity instanceof EntityArmorStand)
		{
			isSneak = false;
			isRiding = false;
			isChild = false;

			headRightEar.rotateAngleX = headLeftEar.rotateAngleX = 0;
			legsTail.rotateAngleX = (float) TAIL_DEGREES_RAD;

			legsTail.rotateAngleZ = 0;
			legsTail.offsetX = 0;

			this.bipedRightArm.rotateAngleX = 0F;
			this.bipedRightArm.rotateAngleY = 0F;
			this.bipedRightArm.rotateAngleZ = 0F;
			this.bipedLeftArm.rotateAngleX = 0F;
			this.bipedLeftArm.rotateAngleY = 0F;
			this.bipedLeftArm.rotateAngleZ = 0F;

			bipedBody.rotateAngleX = 0F;
			bipedBody.rotateAngleY = 0F;
			bipedBody.rotateAngleZ = 0F;

			bipedHead.rotateAngleX = 0F;
			bipedHead.rotateAngleY = 0F;
			bipedHead.rotateAngleZ = 0F;

			bipedLeftLeg.rotateAngleX = 0F;
			bipedLeftLeg.rotateAngleY = 0F;
			bipedLeftLeg.rotateAngleZ = 0F;

			bipedRightLeg.rotateAngleX = 0F;
			bipedRightLeg.rotateAngleY = 0F;
			bipedRightLeg.rotateAngleZ = 0F;

			setRotationAngles(0, 0, 0, 0, 0, 0, null);
		} else
		{
			super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			if(entity != null)
			{
				float pt = Minecraft.getMinecraft().getRenderPartialTicks();

				Vec3d move = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
				double speed = move.distanceTo(Vec3d.ZERO);
				if(speed > 0.05F)
				{
					double fwdRad = entity.getLook(pt).dotProduct(move);
					headRightEar.rotateAngleX = headLeftEar.rotateAngleX = (float) -Math.max(Math.min(fwdRad * speed * Math.PI, EAR_DEGREES_RAD), -EAR_DEGREES_RAD);
					legsTail.rotateAngleX = (float) TAIL_DEGREES_RAD - headLeftEar.rotateAngleX * 1.5F;
				} else
				{
					headRightEar.rotateAngleX = headLeftEar.rotateAngleX = 0F;
					legsTail.rotateAngleX = (float) TAIL_DEGREES_RAD;
				}

				float time = entity.ticksExisted + pt;

				float rotator = (float) Math.sin(time / 5F) / 4F;

				legsTail.rotateAngleZ = (float) (rotator * TAIL_DEGREES_RAD);
				legsTail.offsetX = -1 / 32F * rotator;
			}
		}
		GlStateManager.pushMatrix();
		if(entity.isSneaking())
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
		this.bipedHead.render(1F / 13F);
		this.bipedRightArm.render(1F / 16F);
		this.bipedLeftArm.render(1F / 16F);
		this.bipedBody.render(1F / 16F);
		this.bipedRightLeg.render(1F / 16F);
		this.bipedLeftLeg.render(1F / 16F);
		GlStateManager.popMatrix();
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightArm.rotationPointZ = 0.0F;
		this.bipedLeftArm.rotationPointZ = 0.0F;
		this.bipedRightLeg.rotateAngleY = 0.0F;
		this.bipedLeftLeg.rotateAngleY = 0.0F;
		this.bipedRightArm.rotateAngleY = 0.0F;
		this.bipedLeftArm.rotateAngleY = 0.0F;
		this.bipedBody.rotateAngleX = 0.0F;
		this.bipedRightLeg.rotationPointZ = 0.1F;
		this.bipedLeftLeg.rotationPointZ = 0.1F;
		this.bipedRightLeg.rotationPointY = 12.0F;
		this.bipedLeftLeg.rotationPointY = 12.0F;
		this.bipedHead.rotationPointY = 0.0F;
		this.bipedHeadwear.rotationPointY = 0.0F;
		this.legsLeft.rotationPointZ = 0F;
		this.legsRight.rotationPointZ = 0F;
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
	}
}