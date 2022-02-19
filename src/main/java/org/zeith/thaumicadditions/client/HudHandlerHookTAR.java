package org.zeith.thaumicadditions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.common.world.aura.AuraChunk;

import java.text.DecimalFormat;

public class HudHandlerHookTAR
{
	static DecimalFormat secondsFormatter = new DecimalFormat("#######.#");

	public static void renderThaumometer(Minecraft mc, float partialTicks, EntityPlayer player, long time, int ww, int hh, int x)
	{
		AuraChunk currentAura = HudHandler.currentAura;

		String msg;
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		float base = MathHelper.clamp((float) currentAura.getBase() / 525.0f, 0.0f, 1.0f);
		float vis = MathHelper.clamp(currentAura.getVis() / 525.0f, 0.0f, 1.0f);
		float flux = MathHelper.clamp(currentAura.getFlux() / 525.0f, 0.0f, 1.0f);
		float count = (float) Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks;
		float count2 = (float) Minecraft.getMinecraft().getRenderViewEntity().ticksExisted / 3.0f + partialTicks;
		if(flux + vis > 1.0f)
		{
			float m = 1.0f / (flux + vis);
			base *= m;
			vis *= m;
			flux *= m;
		}
		float start = 10.0f + (1.0f - vis) * 64.0f;
		if(vis > 0.0f)
		{
			GL11.glPushMatrix();
			GL11.glColor4f(0.7f, 0.4f, 0.9f, 1.0f);
			GL11.glTranslated(5.0, start, 0.0);
			GL11.glScaled(1.0, vis, 1.0);
			UtilsFX.drawTexturedQuad(0.0f, 0.0f, 88.0f, 56.0f, 8.0f, 64.0f, -90.0);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, 1);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
			GL11.glTranslated(5.0, start, 0.0);
			UtilsFX.drawTexturedQuad(0.0f, 0.0f, 96.0f, 56.0f + count % 64.0f, 8.0f, vis * 64.0f, -90.0);
			GL11.glBlendFunc(770, 771);
			GL11.glPopMatrix();
			if(player.isSneaking())
			{
				GL11.glPushMatrix();
				GL11.glTranslated(16.0, start, 0.0);
				GL11.glScaled(0.5, 0.5, 0.5);
				msg = secondsFormatter.format(currentAura.getVis());
				mc.ingameGUI.drawString(mc.fontRenderer, msg, 0, 0, 15641343);
				GL11.glPopMatrix();
				com.zeitheron.hammercore.client.utils.UtilsFX.bindTexture("thaumcraft", "textures/gui/hud.png");
			}
		}
		if(flux > 0.0f)
		{
			start = 10.0f + (1.0f - flux - vis) * 64.0f;
			GL11.glPushMatrix();
			GL11.glColor4f(0.25f, 0.1f, 0.3f, 1.0f);
			GL11.glTranslated(5.0, start, 0.0);
			GL11.glScaled(1.0, flux, 1.0);
			UtilsFX.drawTexturedQuad(0.0f, 0.0f, 88.0f, 56.0f, 8.0f, 64.0f, -90.0);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, 1);
			GL11.glColor4f(0.7f, 0.4f, 1.0f, 0.5f);
			GL11.glTranslated(5.0, start, 0.0);
			UtilsFX.drawTexturedQuad(0.0f, 0.0f, 104.0f, 120.0f - count2 % 64.0f, 8.0f, flux * 64.0f, -90.0);
			GL11.glBlendFunc(770, 771);
			GL11.glPopMatrix();
			if(player.isSneaking())
			{
				GL11.glPushMatrix();
				GL11.glTranslated(16.0, start - 4.0f, 0.0);
				GL11.glScaled(0.5, 0.5, 0.5);
				msg = secondsFormatter.format(currentAura.getFlux());
				mc.ingameGUI.drawString(mc.fontRenderer, msg, 0, 0, 11145659);
				GL11.glPopMatrix();
				com.zeitheron.hammercore.client.utils.UtilsFX.bindTexture("thaumcraft", "textures/gui/hud.png");
			}
		}
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		UtilsFX.drawTexturedQuad(1.0f, 1.0f, 72.0f, 48.0f, 16.0f, 80.0f, -90.0);
		GL11.glPopMatrix();
		start = 8.0f + (1.0f - base) * 64.0f;
		GL11.glPushMatrix();
		UtilsFX.drawTexturedQuad(2.0f, start, 117.0f, 61.0f, 14.0f, 5.0f, -90.0);
		GL11.glPopMatrix();
	}
}