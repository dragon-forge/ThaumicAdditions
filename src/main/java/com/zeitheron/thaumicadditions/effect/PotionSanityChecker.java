package com.zeitheron.thaumicadditions.effect;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.client.lib.UtilsFX;

public class PotionSanityChecker extends Potion
{
	public PotionSanityChecker()
	{
		super(false, 0xFFFFFFFF);
		setRegistryName("sanity_checker");
	}
	
	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
	{
		EntityPlayer player = mc.player;
		if(player == null)
			return;
		IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
		if(warp == null)
			return;
		
		x -= 3;
		
		com.zeitheron.hammercore.client.utils.UtilsFX.bindTexture("thaumcraft", "textures/gui/hud.png");
		GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glClear((int)256);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)sr.getScaledWidth_double(), (double)sr.getScaledHeight_double(), (double)0.0, (double)1000.0, (double)3000.0);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-2000.0f);
        int ww = sr.getScaledWidth();
        int hh = sr.getScaledHeight();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 100, y + 5, 0);
		
		GlStateManager.rotate(90, 0, 0, 1);
		
		GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		UtilsFX.drawTexturedQuad(1.0f, 1.0f, 152.0f, 0.0f, 20.0f, 76.0f, -90.0);
		int p = warp.get(IPlayerWarp.EnumWarpType.PERMANENT);
		int s = warp.get(IPlayerWarp.EnumWarpType.NORMAL);
		int t = warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);
		float tw = p + s + t;
		float mod = 1.0f;
		if(tw > 100.0f)
		{
			mod = 100.0f / tw;
			tw = 100.0f;
		}
		int gap = (int) ((100.0f - tw) / 100.0f * 48.0f);
		int wt = (int) ((float) t / 100.0f * 48.0f * mod);
		int ws = (int) ((float) s / 100.0f * 48.0f * mod);
		if(t > 0)
		{
			GL11.glPushMatrix();
			GL11.glColor4f((float) 1.0f, (float) 0.5f, (float) 1.0f, (float) 1.0f);
			UtilsFX.drawTexturedQuad(7.0f, 21 + gap, 200.0f, gap, 8.0f, wt + gap, -90.0);
			GL11.glPopMatrix();
		}
		if(s > 0)
		{
			GL11.glPushMatrix();
			GL11.glColor4f((float) 0.75f, (float) 0.0f, (float) 0.75f, (float) 1.0f);
			UtilsFX.drawTexturedQuad(7.0f, 21 + wt + gap, 200.0f, wt + gap, 8.0f, wt + ws + gap, -90.0);
			GL11.glPopMatrix();
		}
		if(p > 0)
		{
			GL11.glPushMatrix();
			GL11.glColor4f((float) 0.5f, (float) 0.0f, (float) 0.5f, (float) 1.0f);
			UtilsFX.drawTexturedQuad(7.0f, 21 + wt + ws + gap, 200.0f, wt + ws + gap, 8.0f, 48.0f, -90.0);
			GL11.glPopMatrix();
		}
		GL11.glPushMatrix();
		GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		UtilsFX.drawTexturedQuad(1.0f, 16.0f, 176.0f, 15.0f, 20.0f, 60.0f, -90.0);
		GL11.glPopMatrix();
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 82, y + 8, 0);
		GL11.glPushMatrix();
		GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		UtilsFX.drawTexturedQuad(1.0f, 1.0f, 176.0f, 0.0f, 20.0f, 15.0f, -90.0);
		GL11.glPopMatrix();
		if(tw >= 100.0f)
		{
			float rx = ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat();
			float ry = ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat();
			
			GL11.glPushMatrix();
			GlStateManager.translate(2.5f + rx * 1F, 2.5f + ry * 1F, 0);
			GlStateManager.scale(.8F, .8F, 1F);
			UtilsFX.drawTexturedQuad(0, 0, 216.0f, 0.0f, 20.0f, 16.0f, -90);
			GL11.glPopMatrix();
		}
		GlStateManager.popMatrix();
		
        GL11.glPopMatrix();
	}
	
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
	{
	}
	
	@Override
	public boolean shouldRender(PotionEffect effect)
	{
		return true;
	}
	
	@Override
	public boolean shouldRenderHUD(PotionEffect effect)
	{
		return false;
	}
	
	@Override
	public boolean shouldRenderInvText(PotionEffect effect)
	{
		return false;
	}
}