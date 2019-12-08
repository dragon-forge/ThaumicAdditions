package com.zeitheron.thaumicadditions.client.models;

import com.zeitheron.thaumicadditions.entity.EntityBlueWolf;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBlueWolf extends ModelBase
{
	public ModelRenderer wolfHeadMain;
	public ModelRenderer wolfMane;
	public ModelRenderer wolfBody;
	public ModelRenderer wolfLeg4;
	public ModelRenderer wolfLeg1;
	public ModelRenderer wolfLeg2;
	public ModelRenderer wolfLeg3;
	public ModelRenderer wolfTail;
	
	public ModelBlueWolf()
	{
		this.textureWidth = 64;
		this.textureHeight = 64;
		
		this.wolfHeadMain = new ModelRenderer(this, 0, 0);
		this.wolfHeadMain.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
		this.wolfHeadMain.setRotationPoint(-1.0F, 13.5F, -7.0F);
		this.wolfBody = new ModelRenderer(this, 18, 14);
		this.wolfBody.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
		this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
		this.wolfMane = new ModelRenderer(this, 21, 0);
		this.wolfMane.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
		this.wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
		this.wolfLeg1 = new ModelRenderer(this, 0, 51);
		this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
		this.wolfLeg1.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.wolfLeg2 = new ModelRenderer(this, 0, 40);
		this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
		this.wolfLeg2.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.wolfLeg3 = new ModelRenderer(this, 0, 29);
		this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
		this.wolfLeg3.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.wolfLeg4 = new ModelRenderer(this, 0, 18);
		this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
		this.wolfLeg4.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.wolfTail = new ModelRenderer(this, 9, 18);
		this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
		this.wolfTail.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.wolfHeadMain.setTextureOffset(16, 16).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.wolfHeadMain.setTextureOffset(16, 13).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.wolfHeadMain.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		
		this.wolfHeadMain.renderWithRotation(scale);
		this.wolfBody.render(scale);
		this.wolfLeg1.render(scale);
		this.wolfLeg2.render(scale);
		this.wolfLeg3.render(scale);
		this.wolfLeg4.render(scale);
		this.wolfTail.renderWithRotation(scale);
		this.wolfMane.render(scale);
	}
	
	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		EntityBlueWolf entitywolf = (EntityBlueWolf) entitylivingbaseIn;
		
		if (entitywolf.isAngry())
		{
			this.wolfTail.rotateAngleY = 0.0F;
		}
		else
		{
			this.wolfTail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}
		
		{
			this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
			this.wolfBody.rotateAngleX = ((float)Math.PI / 2F);
			this.wolfMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
			this.wolfMane.rotateAngleX = this.wolfBody.rotateAngleX;
			this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
			this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
			this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
			this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
			this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
			this.wolfLeg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
			this.wolfLeg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
			this.wolfLeg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
			this.wolfLeg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}
		
		this.wolfHeadMain.rotateAngleZ = entitywolf.getInterestedAngle(partialTickTime) + entitywolf.getShakeAngle(partialTickTime, 0.0F);
		this.wolfMane.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.08F);
		this.wolfBody.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.16F);
		this.wolfTail.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.2F);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		this.wolfHeadMain.rotateAngleX = headPitch * 0.017453292F;
		this.wolfHeadMain.rotateAngleY = netHeadYaw * 0.017453292F;
		this.wolfTail.rotateAngleX = ageInTicks;
	}
}