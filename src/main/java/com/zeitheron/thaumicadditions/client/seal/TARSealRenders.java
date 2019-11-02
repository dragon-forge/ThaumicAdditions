package com.zeitheron.thaumicadditions.client.seal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.render.vertex.SimpleBlockRendering;
import com.zeitheron.hammercore.client.utils.RenderBlocks;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.client.utils.texture.TextureAtlasSpriteFull;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.client.util.GLDownloader;
import com.zeitheron.thaumicadditions.config.ConfigsTAR;
import com.zeitheron.thaumicadditions.entity.EntitySealViewer;
import com.zeitheron.thaumicadditions.items.ItemSealSymbol;
import com.zeitheron.thaumicadditions.seals.magic.SealPortal;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

@SideOnly(Side.CLIENT)
public class TARSealRenders
{
	static
	{
		MinecraftForge.EVENT_BUS.register(new TARSealRenders());
	}
	
	public static List<SealPortal> rendered = new ArrayList<>();
	
	public static SealPortal saveRender;
	
	public static void renderNone(TileSeal seal, double x, double y, double z, float partialTicks)
	{
		
	}
	
	public static void renderPortal(TileSeal seal, double x, double y, double z, float partialTicks)
	{
		SealPortal portal = Cast.cast(seal.instance, SealPortal.class);
		if(portal == null)
			return;
		
		float size = (portal.holeSize + Math.min(Math.max(-partialTicks, portal.targetHoleSize - portal.holeSize), partialTicks)) / 5F;
		
		if(portal.holeSize <= 0)
			return;
		
		GlStateManager.disableLighting();
		
		if(portal.txRender == null)
			portal.txRender = new PortalRenderer();
		PortalRenderer pr = (PortalRenderer) portal.txRender;
		
		if(!rendered.contains(portal))
			rendered.add(portal);
		
		SimpleBlockRendering sbr = RenderBlocks.forMod(InfoTAR.MOD_ID).simpleRenderer;
		
		if(ConfigsTAR.portalGfx && portal.txRender != null && PortalRenderer.renderRecursion < 2 && portal.viewer != null)
		{
			GL11.glPushMatrix();
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GlStateManager.bindTexture(pr.portalTexture);
			GlStateManager.color(1, 1, 1);
			GL11.glDepthMask(false);
			GL11.glEnable(3042);
			GL11.glPushMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(0, .125, 0);
			GL11.glScaled(size / 1.2, size / 1.2, size / 1.2);
			GL11.glTranslated(-.5, -.5, 0);
			GL11.glTranslated(.5, .5, -.65);
			
			GL11.glRotated(-90, 0, 0, 1);
			GL11.glRotated(seal.orientation == EnumFacing.WEST ? 270 : seal.orientation == EnumFacing.EAST ? 90 : seal.orientation == EnumFacing.NORTH ? 0 : 180, 0, 0, 1);
			GL11.glRotated(180, 0, 1, 0);
			
			GL11.glTranslated(-.5, -.5, -.6);
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glTexCoord2d(0, 0);
				GL11.glVertex3d(0, 0, 0);
				
				GL11.glTexCoord2d(0, 1);
				GL11.glVertex3d(1, 0, 0);
				
				GL11.glTexCoord2d(-1, 1);
				GL11.glVertex3d(1, 1, 0);
				
				GL11.glTexCoord2d(-1, 0);
				GL11.glVertex3d(0, 1, 0);
			}
			GL11.glEnd();
			
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
		
		UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/misc/portal" + (portal.viewer != null && pr != null && ConfigsTAR.portalGfx ? "2" : "") + ".png");
		GL11.glDepthMask(false);
		// GL11.glEnable(3042);
		GlStateManager.enableTexture2D();
		
		int count = 6;
		for(int i = 0; i < count; ++i)
		{
			if(i == 3)
			{
				Aspect a = seal.getSymbol(2);
				if(a != null)
				{
					ColorHelper.gl(255 << 24 | a.getColor());
					UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/misc/portal2_nc.png");
				}
			} else if(i == 4)
			{
				UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/misc/portal2.png");
				GlStateManager.color(1, 1, 1);
			}
			
			double ascale = i / 5D;
			float scale = (float) (size + ascale);
			GL11.glPushMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(0, .13, -.04 - i / 100D);
			GL11.glScaled(size, size, size);
			
			GL11.glBlendFunc(770, i % 2 == 0 ? 1 : 771);
			
			float sx = scale, sy = scale;
			double rotation = (seal.getWorld().getTotalWorldTime() + partialTicks + i * scale * 14) % 360D;
			
			if(i % 2 == 0)
				rotation = 360 - rotation;
			double rot = Math.toRadians(rotation);
			double rot2 = Math.toRadians(rotation + 90);
			double rot3 = Math.toRadians(rotation + 180);
			double rot4 = Math.toRadians(rotation + 270);
			float x1 = (float) Math.cos(rot) * sx;
			float y1 = (float) Math.sin(rot) * sy;
			float x2 = (float) Math.cos(rot2) * sx;
			float y2 = (float) Math.sin(rot2) * sy;
			float x3 = (float) Math.cos(rot3) * sx;
			float y3 = (float) Math.sin(rot3) * sy;
			float x4 = (float) Math.cos(rot4) * sx;
			float y4 = (float) Math.sin(rot4) * sy;
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex3f(-x1, y1, 0);
				GL11.glTexCoord2f(1, 0);
				GL11.glVertex3f(-x2, y2, 0);
				GL11.glTexCoord2f(1, 1);
				GL11.glVertex3f(-x3, y3, 0);
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex3f(-x4, y4, 0);
			}
			GL11.glEnd();
			
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
		
		GL11.glDepthMask(true);
		GL11.glBlendFunc(770, 771);
		GlStateManager.enableLighting();
	}
	
	public static void renderStandart(TileSeal seal, double x, double y, double z, float partialTicks, int index)
	{
		if(seal.getLocation().getRedstone() > 0)
			partialTicks = 0;
		
		Aspect symb = seal.getSymbol(index);
		int color = ItemSealSymbol.getColorMultiplier(symb, seal, index);
		boolean rotates = ItemSealSymbol.doesRotate(symb, seal, index);
		ResourceLocation tex = ItemSealSymbol.getTexture(symb, seal, index);
		GlStateManager.enableTexture2D();
		SimpleBlockRendering sbr = RenderBlocks.forMod(InfoTAR.MOD_ID).simpleRenderer;
		sbr.rb.renderFromInside = false;
		sbr.rb.renderAlpha = 1;
		sbr.begin();
		sbr.setRenderBounds(0, 0, 0, 1, 1, 1);
		sbr.setBrightness(sbr.rb.setLighting(seal.getWorld(), seal.getPos()));
		Arrays.fill(sbr.rgb, color);
		sbr.disableFaces();
		sbr.enableFace(EnumFacing.NORTH);
		sbr.setSprite(TextureAtlasSpriteFull.sprite);
		sbr.drawBlock(0, 0, 0);
		
		int mult = index == 2 ? -1 : 1;
		float speed = .25F;
		
		GL11.glPushMatrix();
		GL11.glTranslated(x - 8 / 16D, y - 6 / 16D, z - .01 * (index + 1));
		GL11.glTranslated(.5, .5, .5);
		GL11.glRotated(index * 120 * mult, 0, 0, 1);
		if(rotates)
		{
			float f = (float) ((seal.ticksExisted + partialTicks) * (speed * 20) % 360D);
			f = (float) ((Math.sin(Math.toRadians(f)) + 1) / 2F);
			GlStateManager.translate(0, 0, f * -.01F);
			GL11.glRotated((seal.ticksExisted + partialTicks) * speed % 360D * mult, 0, 0, 1);
		}
		GL11.glTranslated(-.5, -.5, -.5);
		UtilsFX.bindTexture(tex);
		Tessellator.getInstance().draw();
		GL11.glPopMatrix();
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.RenderTickEvent event)
	{
		if(event.phase.equals(TickEvent.Phase.END))
			return;
		
		if(!ConfigsTAR.portalGfx)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		PortalRenderer.renderRecursion++;
		
		if(saveRender != null)
			rendered.add(saveRender);
		
		if(PortalRenderer.renderRecursion < 2)
			for(int i = 0; i < rendered.size(); ++i)
			{
				SealPortal portal = rendered.get(i);
				TileSeal rr = portal.seal;
				PortalRenderer pr = Cast.cast(portal.txRender, PortalRenderer.class);
				if(pr == null)
					continue;
				if(portal.holeSize <= 0)
					continue;
				
				boolean save = saveRender == portal;
				
				GameSettings settings = mc.gameSettings;
				RenderGlobal renderBackup = mc.renderGlobal;
				Entity entityBackup = mc.getRenderViewEntity();
				int thirdPersonBackup = settings.thirdPersonView;
				boolean hideGuiBackup = settings.hideGUI;
				int mipmapBackup = settings.mipmapLevels;
				float fovBackup = settings.fovSetting;
				int widthBackup = mc.displayWidth;
				int heightBackup = mc.displayHeight;
				int clouds = settings.clouds;
				
				int size = 512;
				
				EntitySealViewer esv = portal.viewer;
				mc.setRenderViewEntity(esv);
				settings.thirdPersonView = 0;
				settings.hideGUI = true;
				settings.mipmapLevels = 3;
				settings.clouds = 0;
				mc.displayWidth = size;
				mc.displayHeight = size;
				
				if(mc.world != null)
					try
					{
						mc.entityRenderer.renderWorld(event.renderTickTime, 0L);
					} catch(Throwable err)
					{
						err.printStackTrace();
					}
				
				GlStateManager.bindTexture(pr.portalTexture);
				GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, size, size, 0);
				if(save)
				{
					pr.texture = GLDownloader.toByteBuffer(pr.portalTexture);
				}
				
				mc.setRenderViewEntity(entityBackup);
				settings.fovSetting = fovBackup;
				settings.thirdPersonView = thirdPersonBackup;
				settings.hideGUI = hideGuiBackup;
				settings.clouds = clouds;
				settings.mipmapLevels = mipmapBackup;
				mc.displayWidth = widthBackup;
				mc.displayHeight = heightBackup;
			}
		
		--PortalRenderer.renderRecursion;
		rendered.clear();
		saveRender = null;
	}
}