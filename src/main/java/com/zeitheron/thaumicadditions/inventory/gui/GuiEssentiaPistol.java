package com.zeitheron.thaumicadditions.inventory.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zeitheron.hammercore.client.gui.GuiWTFMojang;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.inventory.container.ContainerEssentiaPistol;
import com.zeitheron.thaumicadditions.tiles.TileGrowthChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.lib.SoundsTC;

public class GuiEssentiaPistol extends GuiWTFMojang<ContainerEssentiaPistol>
{
	public GuiEssentiaPistol(ContainerEssentiaPistol inventorySlotsIn)
	{
		super(inventorySlotsIn);
		xSize = 176;
		ySize = 192;
	}
	
	List<String> tooltip = new ArrayList<>();
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		partialTicks = mc.getRenderPartialTicks();
		
		UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/gui/essentia_pistol.png");
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		float fill = getContainer().getAmount() / 32F;
		
		float hov = mouseX >= guiLeft + 45 && mouseX < guiLeft + 57 && mouseY >= guiTop + 30 && mouseY < guiTop + 80 ? 1F - (mouseY - guiTop - 30) / 50F : -1;
		
		drawTexturedModalRect(guiLeft + 45, guiTop + 24, 191, 0, 12, 62);
		ColorHelper.gl(255 << 24 | MathHelper.hsvToRGB(fill / 3F, 1F, 1F));
		RenderUtil.drawTexturedModalRect(guiLeft + 47, guiTop + 30 + 50 - fill * 50, 176, 0, 8, fill * 50);
		RenderUtil.drawTexturedModalRect(guiLeft + 47, guiTop + 30 + 50 - fill * 50, 248, (Minecraft.getMinecraft().player.ticksExisted + partialTicks) % 256, 8, fill * 50);
		if(hov > 0F)
		{
			fill = hov;
			ColorHelper.gl(30 << 24 | MathHelper.hsvToRGB(fill / 3F, 1F, 1F));
			RenderUtil.drawTexturedModalRect(guiLeft + 47, guiTop + 30 + 50 - fill * 50, 176, 0, 8, fill * 50);
			RenderUtil.drawTexturedModalRect(guiLeft + 47, guiTop + 30 + 50 - fill * 50, 248, (Minecraft.getMinecraft().player.ticksExisted + partialTicks) % 256, 8, fill * 50);
		}
		ColorHelper.gl(0xFFFFFFFF);
		
		if(!inventorySlots.inventorySlots.get(0).getHasStack())
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(guiLeft + 80, guiTop + 47, 0);
			GL11.glScalef(0.5F, 0.5F, 1F);
			drawTexturedModalRect(0, 0, 0, ySize, 32, 32);
			GL11.glPopMatrix();
		}
		
		if(hov >= 0F)
		{
			int amt = Math.round(32 * hov);
			tooltip.add(I18n.format("tooltip.thaumadditions:essentia_pistol.shoot", amt));
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		drawHoveringText(tooltip, mouseX - guiLeft, mouseY - guiTop);
		tooltip.clear();
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		float hov = mouseX >= guiLeft + 45 && mouseX < guiLeft + 57 && mouseY >= guiTop + 30 && mouseY < guiTop + 80 ? 1F - (mouseY - guiTop - 30) / 50F : -1;
		
		if(hov >= 0F)
		{
			int amt = Math.round(32 * hov);
			this.mc.playerController.sendEnchantPacket(getContainer().windowId, amt);
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsTC.tool, 1F));
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}