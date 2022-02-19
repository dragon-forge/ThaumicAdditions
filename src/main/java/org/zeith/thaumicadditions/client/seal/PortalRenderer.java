package org.zeith.thaumicadditions.client.seal;

import com.zeitheron.hammercore.client.utils.texture.TexLocUploader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.config.ConfigsTAR;
import org.zeith.thaumicadditions.tiles.TileSeal;

import java.nio.ByteBuffer;

@SideOnly(Side.CLIENT)
public class PortalRenderer
{
	public static int renderRecursion;
	public int frameBuffer;
	public int portalTexture;
	public boolean doRender = false;
	public ByteBuffer texture;

	public PortalRenderer()
	{
		if(!ConfigsTAR.portalGfx)
			return;
		try
		{
			int newTextureId = GL11.glGenTextures();
			TexLocUploader.cleanCallbacks.add(() -> GL11.glDeleteTextures(newTextureId));
			GlStateManager.bindTexture(newTextureId);
			int quality = 512;
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, quality, quality, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(3 * quality * quality));
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			portalTexture = newTextureId;
		} catch(IllegalStateException e)
		{
			System.out.println("[TAR] Error found with see-through portals. Disabling them this session.");
			ConfigsTAR.portalGfx = false;
		}
	}

	public static void createPortalView(final PortalRenderer portalRenderer, final TileSeal current, final TileSeal target)
	{
		if(!ConfigsTAR.portalGfx)
			return;
		++PortalRenderer.renderRecursion;
		Minecraft mc = Minecraft.getMinecraft();
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		EXTFramebufferObject.glBindFramebufferEXT(36160, portalRenderer.frameBuffer);

		GL11.glEnable(2960);
		GL11.glStencilFunc(519, 1, 1);
		GL11.glStencilOp(7680, 7680, 7681);
		GL11.glViewport(0, 0, 512, 512);
		GL11.glMatrixMode(5889);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glDisable(3553);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(6);
		GL11.glVertex2f(0.0f, 0.0f);
		for(int oh = 0; oh <= 10; ++oh)
		{
			final double aa = 6.283185307179586 * oh / 10.0;
			GL11.glVertex2f((float) Math.cos(aa), (float) Math.sin(aa));
		}
		GL11.glEnd();
		GL11.glStencilFunc(514, 1, 1);
		GL11.glStencilOp(7680, 7680, 7680);
		GL11.glEnable(3553);
		final EntityPlayerSP player = mc.player;
		Entity rve = mc.getRenderViewEntity();

		mc.setRenderViewEntity(new EntityPlayer(mc.world, mc.getSession().getProfile())
		{
			@Override
			public boolean isSpectator()
			{
				return true;
			}

			@Override
			public boolean isCreative()
			{
				return false;
			}
		});

		float yaw = 0.0f;
		float pitch = 0.0f;
		switch(target.orientation.ordinal())
		{
			case 0:
			{
				pitch = 90.0f;
				break;
			}
			case 1:
			{
				pitch = -90.0f;
				break;
			}
			case 2:
			{
				yaw = 180.0f;
				break;
			}
			case 3:
			{
				yaw = 0.0f;
				break;
			}
			case 4:
			{
				yaw = 90.0f;
				break;
			}
			case 5:
			{
				yaw = 270.0f;
				break;
			}
		}
		int xm = 0;
		int zm = 0;
		int ym = 0;
		switch(target.orientation.ordinal())
		{
			case 0:
			{
				ym = -1;
				break;
			}
			case 1:
			{
				ym = 1;
				break;
			}
			case 2:
			{
				zm = -1;
				break;
			}
			case 3:
			{
				zm = 1;
				break;
			}
			case 4:
			{
				xm = -1;
				break;
			}
			case 5:
			{
				xm = 1;
				break;
			}
		}
		mc.getRenderViewEntity().setPositionAndRotation(target.getPos().getX() + 0.5 + xm, target.getPos().getY() - 0.5f + ym, target.getPos().getZ() + 0.5 + zm, yaw, pitch);
		final boolean di = mc.gameSettings.showDebugInfo;
		mc.gameSettings.showDebugInfo = false;
		final float fov = mc.gameSettings.fovSetting;
		final int width = mc.displayWidth;
		final int height = mc.displayHeight;
		int tpv = mc.gameSettings.thirdPersonView;
		mc.displayWidth = 512;
		mc.displayHeight = 512;
		mc.gameSettings.thirdPersonView = 0;
		mc.gameSettings.fovSetting = 1.0f;
		mc.getRenderViewEntity().rotationYaw = yaw;
		mc.getRenderViewEntity().rotationPitch = pitch;
		final boolean hg = mc.gameSettings.hideGUI;
		mc.gameSettings.hideGUI = true;
		mc.entityRenderer.renderWorld(1F, 0L);
		mc.setRenderViewEntity(rve);
		mc.displayWidth = width;
		mc.displayHeight = height;
		mc.gameSettings.showDebugInfo = di;
		mc.gameSettings.hideGUI = hg;
		mc.gameSettings.fovSetting = fov;
		mc.gameSettings.thirdPersonView = tpv;
		GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
		EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
		GL11.glPopMatrix();
		GL11.glDisable(2960);
		--PortalRenderer.renderRecursion;
	}
}