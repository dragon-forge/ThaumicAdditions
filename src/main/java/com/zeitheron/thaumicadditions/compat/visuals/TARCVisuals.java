package com.zeitheron.thaumicadditions.compat.visuals;

import java.util.Iterator;
import java.util.Random;

import com.zeitheron.hammercore.client.render.shader.ShaderProgram;
import com.zeitheron.hammercore.client.render.shader.impl.ShaderEnderField;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.mod.ModuleLoader;
import com.zeitheron.hammercore.utils.ItemStackUtil;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.client.render.entity.RenderEntityChester;
import com.zeitheron.thaumicadditions.compat.ITARC;
import com.zeitheron.visuals.Visuals;
import com.zeitheron.visuals.client.tesr.TESRChestModified;
import com.zeitheron.visuals.client.tex.TextureTransformer;
import com.zeitheron.visuals.proxy.ClientProxy;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ModuleLoader(requiredModid = "visuals")
public class TARCVisuals implements ITARC
{
	@Override
	public void init()
	{
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		TAReconstructed.LOG.info("Client compat with Visuals enabled!");
		
		TextureTransformer.transform(RenderEntityChester.CHESTER_TEXTURE, TextureTransformer.CHEST_SINGLE_SAW);
		RenderEntityChester.visualsEnabled = true;
		RenderEntityChester.visualsRenderer = (chester, transform) ->
		{
			float pt = transform.get2().y;
			float f = chester.getCurrentLidRotation(pt);
			double x = transform.get1().x;
			double y = transform.get1().y;
			double z = transform.get1().z;
			
			if(f > 0F)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(x - 14 / 16F, y, z + 14 / 16F);
				
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(0, -1, 0);
				
				GlStateManager.translate(14 / 16F, .5, 14 / 16F);
				GlStateManager.rotate(chester.rotationYaw, 0, 1, 0);
				GlStateManager.translate(-7 / 16F, -.5005, -7 / 16F);
				
				UtilsFX.bindTexture(RenderEntityChester.CHESTER_TEXTURE);
				TESRChestModified.CHEST_INSIDE.render(null, 0, 0, 0, 0, 0, 0.0625F);
				
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				
				GlStateManager.popMatrix();
				
				NonNullList<ItemStack> list = ItemStackUtil.getTopItems(chester.inventory, 8);
				
				if(list != null)
				{
					GlStateManager.pushMatrix();
					{
						GlStateManager.translate(x - 14 / 16F, y, z + 14 / 16F);
						
						GlStateManager.rotate(180, 1, 0, 0);
						
						GlStateManager.translate(14 / 16F, .5F, 14 / 16F);
						GlStateManager.rotate(chester.rotationYaw, 0, 1, 0);
						GlStateManager.translate(-6.5F / 16F, -.46F, 8F / 16F);
						
						GlStateManager.rotate(180, 1, 0, 0);
					}
					GlStateManager.enableCull();
					Random random = new Random();
					
					random.setSeed(254L);
					int shift = 0;
					float blockScale = .6F;
					float distMult = .9F;
					float timeD = (float) (360.0D * (double) (System.currentTimeMillis() & 16383L) / 16383.0D) - pt;
					if(list.get(1).isEmpty())
					{
						shift = 8;
						blockScale = 0.85F;
					}
					
					TESRChestModified.customitem.setWorld(chester.world);
					TESRChestModified.customitem.hoverStart = 0;
					Iterator<ItemStack> var19 = list.iterator();
					
					while(var19.hasNext())
					{
						ItemStack item = var19.next();
						if(shift > TESRChestModified.shifts.length || shift > 8)
							break;
						
						if(item.isEmpty())
							++shift;
						else
						{
							float shiftX = TESRChestModified.shifts[shift][0] * distMult;
							float shiftY = TESRChestModified.shifts[shift][1] * distMult;
							float shiftZ = TESRChestModified.shifts[shift][2] * distMult;
							++shift;
							GlStateManager.pushMatrix();
							GlStateManager.translate(shiftX, shiftY, shiftZ);
							GlStateManager.rotate(timeD, 0.0F, 1.0F, 0.0F);
							GlStateManager.scale(blockScale, blockScale, blockScale);
							TESRChestModified.customitem.setItem(item);
							
							TESRChestModified.getRenderEntityItem().doRender(TESRChestModified.customitem, 0.0D, 0.0D, 0.0D, 0.0F, 0);
							GlStateManager.popMatrix();
						}
					}
					
					GlStateManager.popMatrix();
				}
			}
		};
	}
}