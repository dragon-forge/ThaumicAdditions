package com.zeitheron.thaumicadditions.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelChester extends ModelBase
{
	public ModelRenderer chestLid;
	public ModelRenderer chestBelow;
	public ModelRenderer chestKnob;
	
	public ModelChester()
	{
		chestLid = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
		chestLid.addBox(0.0f, -5.0f, -14.0f, 14, 5, 14, 0.0f);
		chestLid.setRotationPoint(1, 7, 15);
		chestKnob = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
		chestKnob.addBox(-1.0f, -2.0f, -15.0f, 2, 4, 1, 0.0f);
		chestKnob.setRotationPoint(8, 7, 15);
		chestBelow = new ModelRenderer(this, 0, 19).setTextureSize(64, 64);
		chestBelow.addBox(0.0f, 0.0f, 0.0f, 14, 10, 14, 0.0f);
		chestBelow.setRotationPoint(1, 6, 1);
	}
	
	public void a(float f, float f1, float f2, float f3, float f4, float f5)
	{
	}
	
	public void render()
	{
		chestKnob.rotateAngleX = chestLid.rotateAngleX;
		chestLid.render(0.0625f);
		chestKnob.render(0.0625f);
		chestBelow.render(0.0625f);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		chestKnob.rotateAngleX = chestLid.rotateAngleX;
		chestLid.render(0.0625f);
		chestKnob.render(0.0625f);
		chestBelow.render(0.0625f);
	}
}