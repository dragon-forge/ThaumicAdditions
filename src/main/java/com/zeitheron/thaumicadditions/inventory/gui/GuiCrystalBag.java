package com.zeitheron.thaumicadditions.inventory.gui;

import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.Rectangle;

import com.zeitheron.hammercore.client.gui.GuiWTFMojang;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.inventory.container.ContainerCrystalBag;
import com.zeitheron.thaumicadditions.net.PacketCrystalBagAspectClick;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class GuiCrystalBag extends GuiWTFMojang<ContainerCrystalBag>
{
	final Rectangle aspectArea = new Rectangle();
	final Rectangle scrollArea = new Rectangle();
	
	final Rectangle hoverArea = new Rectangle();
	Aspect selectedAspect;
	
	int sortRule;
	
	int skipRows;
	
	public GuiCrystalBag(ContainerCrystalBag c)
	{
		super(c);
		xSize = 176;
		ySize = 186;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		aspectArea.setBounds(guiLeft + 10, guiTop + 15, 144, 72);
		scrollArea.setBounds(guiLeft + 155, guiTop + 15, 11, 72);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
        this.fontRenderer.drawString(this.getContainer().player.inventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
        
		if(selectedAspect != null)
			drawHoveringText(Arrays.asList(selectedAspect.getName() + " (x" + getContainer().aspects.getAmount(selectedAspect) + ")", TextFormatting.DARK_GRAY + selectedAspect.getLocalizedDescription()), mouseX - guiLeft, mouseY - guiTop);
		
		hoverArea.setBounds(guiLeft + 14, guiTop + 4, 10, 10);
		if(hoverArea.contains(mouseX, mouseY))
		{
			drawHoveringText(I18n.format("tooltip." + InfoTAR.MOD_ID + ":cb_sort_rule." + sortRule), mouseX - guiLeft, mouseY - guiTop);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.selectedAspect = null;
		
		AspectList aspects = getContainer().aspects;
		
		UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/gui/crystal_bag.png");
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 14, guiTop + 4, sortRule / 2 * 10, 186 + (sortRule % 2) * 10, 10, 10);
		
		GlStateManager.disableDepth();
		
		Aspect[] as = sortRule / 2 == 1 ? aspects.getAspectsSortedByAmount() : aspects.getAspectsSortedByName();
		
		int e = 0, j = 0;
		boolean ifc = sortRule % 2 == 1;
		for(int i = ifc ? aspects.size() - 1 : 0; ifc ? i >= 0 : i < aspects.size(); i += ifc ? -1 : 1)
		{
			if(skipRows * 8 > j)
			{
				++j;
				continue;
			}
			
			if(e / 8 > 3)
				break;
			
			hoverArea.setBounds(guiLeft + 10 + (e % 8) * 18, guiTop + 15 + (e / 8) * 18, 18, 18);
			
			Aspect a = as[i];
			thaumcraft.client.lib.UtilsFX.drawTag(hoverArea.getX() + 1, hoverArea.getY() + 1, a, aspects.getAmount(a), 0, 1);
			++e;
			
			if(hoverArea.contains(mouseX, mouseY))
				selectedAspect = a;
			++j;
		}
		
		UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/gui/crystal_bag.png");
		
		if(aspectArea.contains(mouseX, mouseY))
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(aspectArea.getX(), aspectArea.getY() + aspectArea.getHeight(), 0);
			GlStateManager.rotate(-90, 0, 0, 1);
			drawTexturedModalRect(0, 0, 176, 12, 72, 144);
			GlStateManager.popMatrix();
		}
		
		if(scrollArea.contains(mouseX, mouseY))
		{
			drawTexturedModalRect(scrollArea.getX(), scrollArea.getY(), 176, 156, 11, 72);
		}
	}
	
	int mouseX, mouseY;
	int anchorX = -1, anchorY = -1;
	
	@Override
	public void handleMouseInput() throws IOException
	{
		int dw = Mouse.getDWheel();
		if(dw != 0)
		{
			if(aspectArea.contains(mouseX, mouseY))
			{
				AspectList aspects = getContainer().aspects;
				int rows = MathHelper.ceil(aspects.size() / 8F);
				if(dw < 0)
					skipRows = Math.min(skipRows - dw / 120, rows - 4);
				else
					skipRows = Math.max(0, skipRows - dw / 120);
			}
		}
		super.handleMouseInput();
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
	{
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		anchorX = anchorY = -1;
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		anchorX = mouseX;
		anchorY = mouseY;
		
		hoverArea.setBounds(guiLeft + 14, guiTop + 4, 10, 10);
		if(hoverArea.contains(mouseX, mouseY))
		{
			sortRule = (sortRule + 1) % 4;
		}
		
		if(aspectArea.contains(mouseX, mouseY))
			HCNet.INSTANCE.sendToServer(PacketCrystalBagAspectClick.create(selectedAspect, isShiftKeyDown(), mouseButton));
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}