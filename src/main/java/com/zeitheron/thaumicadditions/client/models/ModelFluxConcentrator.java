package com.zeitheron.thaumicadditions.client.models;

import com.zeitheron.hammercore.client.model.ModelSimple;
import com.zeitheron.hammercore.utils.FrictionRotator;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.tiles.TileFluxConcentrator;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelFluxConcentrator extends ModelSimple<TileFluxConcentrator>
{
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape1_1;
	public ModelRenderer shape4;
	public ModelRenderer shape1_2;
	public ModelRenderer shape6;
	public ModelRenderer shape6_1;
	public ModelRenderer shape6_2;
	public ModelRenderer shape6_3;
	public ModelRenderer shape10;
	public ModelRenderer shape11;
	public ModelRenderer shape11_1;
	public ModelRenderer shape14;
	public ModelRenderer shape14_1;
	public ModelRenderer shape16;
	public ModelRenderer shape17;
	public ModelRenderer shape18;
	public ModelRenderer shape19;
	public ModelRenderer shape21;
	public ModelRenderer shape22;
	public ModelRenderer shape22_1;
	public ModelRenderer shape24;
	public ModelRenderer shape24_1;
	
	public ModelFluxConcentrator()
	{
		super(128, 64, new ResourceLocation(InfoTAR.MOD_ID, "textures/models/flux_concentrator.png"));
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.shape6_3 = new ModelRenderer(this, 100, 0);
		this.shape6_3.setRotationPoint(6.5F, 10.0F, 6.5F);
		this.shape6_3.addBox(0.0F, 0.0F, 0.0F, 1, 14, 1, 0.0F);
		this.shape14_1 = new ModelRenderer(this, 48, 0);
		this.shape14_1.setRotationPoint(1.0F, 21.0F, 3.0F);
		this.shape14_1.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.shape24 = new ModelRenderer(this, 74, 8);
		this.shape24.setRotationPoint(7.0F, 23.0F, -7.0F);
		this.shape24.addBox(0.0F, 0.0F, 0.0F, 1, 1, 14, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(-1.0F, 13.0F, -1.0F);
		this.shape1.addBox(0.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
		this.shape6_1 = new ModelRenderer(this, 12, 0);
		this.shape6_1.setRotationPoint(-7.5F, 10.0F, -7.5F);
		this.shape6_1.addBox(0.0F, 0.0F, 0.0F, 1, 14, 1, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 0);
		this.shape2.setRotationPoint(-8.0F, 8.0F, -8.0F);
		this.shape2.addBox(0.0F, 0.0F, 0.0F, 16, 2, 16, 0.0F);
		this.shape6 = new ModelRenderer(this, 8, 0);
		this.shape6.setRotationPoint(-7.5F, 10.0F, 6.5F);
		this.shape6.addBox(0.0F, 0.0F, 0.0F, 1, 14, 1, 0.0F);
		this.shape6_2 = new ModelRenderer(this, 96, 0);
		this.shape6_2.setRotationPoint(6.5F, 10.0F, -7.5F);
		this.shape6_2.addBox(0.0F, 0.0F, 0.0F, 1, 14, 1, 0.0F);
		this.shape10 = new ModelRenderer(this, 104, 0);
		this.shape10.setRotationPoint(0.0F, 15.0F, 2.0F);
		this.shape10.addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, 0.0F);
		this.shape1_2 = new ModelRenderer(this, 80, 0);
		this.shape1_2.setRotationPoint(-2.0F, 23.0F, -2.0F);
		this.shape1_2.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4, 0.0F);
		this.shape22 = new ModelRenderer(this, 48, 9);
		this.shape22.setRotationPoint(-8.0F, 23.0F, -8.0F);
		this.shape22.addBox(0.0F, 0.0F, 0.0F, 16, 1, 1, 0.0F);
		this.shape14 = new ModelRenderer(this, 116, 3);
		this.shape14.setRotationPoint(1.0F, 11.0F, 3.0F);
		this.shape14.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, 0.0F);
		this.shape16 = new ModelRenderer(this, 120, 4);
		this.shape16.setRotationPoint(1.0F, 23.0F, 2.0F);
		this.shape16.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.shape17 = new ModelRenderer(this, 80, 5);
		this.shape17.setRotationPoint(-0.5F, 17.0F, -2.7F);
		this.shape17.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.shape11 = new ModelRenderer(this, 113, 0);
		this.shape11.setRotationPoint(0.5F, 14.3F, 2.5F);
		this.shape11.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
		this.shape11_1 = new ModelRenderer(this, 119, 1);
		this.shape11_1.setRotationPoint(0.5F, 20.7F, 2.5F);
		this.shape11_1.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
		this.shape22_1 = new ModelRenderer(this, 48, 11);
		this.shape22_1.setRotationPoint(-8.0F, 23.0F, 7.0F);
		this.shape22_1.addBox(0.0F, 0.0F, 0.0F, 16, 1, 1, 0.0F);
		this.shape24_1 = new ModelRenderer(this, 90, 9);
		this.shape24_1.setRotationPoint(-8.0F, 23.0F, -7.0F);
		this.shape24_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 14, 0.0F);
		this.shape18 = new ModelRenderer(this, 86, 5);
		this.shape18.setRotationPoint(-1.0F, 16.5F, -2.2F);
		this.shape18.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		this.shape19 = new ModelRenderer(this, 60, 0);
		this.shape19.setRotationPoint(-2.0F, 20.0F, 0.0F);
		this.shape19.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.shape21 = new ModelRenderer(this, 92, 5);
		this.shape21.setRotationPoint(-3.0F, 12.0F, 0.0F);
		this.shape21.addBox(0.0F, 0.0F, 0.0F, 1, 9, 1, 0.0F);
		this.shape1_1 = new ModelRenderer(this, 48, 0);
		this.shape1_1.setRotationPoint(-2.0F, 11.0F, -2.0F);
		this.shape1_1.addBox(0.0F, 0.0F, 0.0F, 4, 2, 4, 0.0F);
		this.shape4 = new ModelRenderer(this, 56, 0);
		this.shape4.setRotationPoint(-4.0F, 10.0F, -4.0F);
		this.shape4.addBox(0.0F, 0.0F, 0.0F, 8, 1, 8, 0.0F);
	}
	
	FrictionRotator valve;
	
	@Override
	public void bindTexture(TileFluxConcentrator o)
	{
		super.bindTexture(o);
		if(o != null)
			valve = o.valve;
		else
			valve = null;
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.shape6_3.render(f5);
		this.shape14_1.render(f5);
		this.shape24.render(f5);
		this.shape1.render(f5);
		this.shape6_1.render(f5);
		this.shape2.render(f5);
		this.shape6.render(f5);
		this.shape6_2.render(f5);
		this.shape1_2.render(f5);
		this.shape22.render(f5);
		this.shape14.render(f5);
		this.shape16.render(f5);
		this.shape17.render(f5);
		this.shape11.render(f5);
		this.shape11_1.render(f5);
		this.shape22_1.render(f5);
		this.shape24_1.render(f5);
		this.shape19.render(f5);
		this.shape21.render(f5);
		this.shape1_1.render(f5);
		this.shape4.render(f5);
		
		if(valve != null)
		{
			float r = valve.getActualRotation(f);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.shape18.rotationPointX * f5, this.shape18.rotationPointY * f5, this.shape18.rotationPointZ * f5);
			this.shape18.rotationPointX = 0F;
			this.shape18.rotationPointY = 0F;
			this.shape18.rotationPointZ = 0F;
			GlStateManager.translate(1 / 16F, 1 / 16F, 0);
			GlStateManager.rotate(r, 0, 0, 1);
			GlStateManager.translate(-1 / 16F, -1 / 16F, 0);
			this.shape18.render(f5);
			GlStateManager.popMatrix();
			
			this.shape18.rotationPointX = -1F;
			this.shape18.rotationPointY = 16.5F;
			this.shape18.rotationPointZ = -2.2F;
		} else
			this.shape18.render(f5);
		
		this.shape10.render(f5);
	}
}