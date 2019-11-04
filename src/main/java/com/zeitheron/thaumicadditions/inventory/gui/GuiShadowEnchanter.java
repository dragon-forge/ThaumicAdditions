package com.zeitheron.thaumicadditions.inventory.gui;

import java.awt.Rectangle;
import java.io.IOException;

import org.lwjgl.input.Mouse;

import com.zeitheron.hammercore.api.lighting.ColoredLightManager;
import com.zeitheron.hammercore.client.gui.GuiWTFMojang;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.api.ShadowEnchantment;
import com.zeitheron.thaumicadditions.inventory.container.ContainerShadowEnchanter;
import com.zeitheron.thaumicadditions.tiles.TileShadowEnchanter;

import net.minecraft.util.ResourceLocation;

public class GuiShadowEnchanter extends GuiWTFMojang<ContainerShadowEnchanter>
{
	public static final ResourceLocation tex = new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/shadow_enchanter.png");
	
	static final int RECIPE_Y = 16, SELECT_Y = 53;
	
	int enchantmentScroll, essentiaScroll;
	float lift = SELECT_Y - RECIPE_Y;
	final Rectangle enchantmentArea = new Rectangle(), essentiaArea = new Rectangle();
	
	public GuiShadowEnchanter(ContainerShadowEnchanter inventorySlotsIn)
	{
		super(inventorySlotsIn);
		xSize = 176;
		ySize = 186;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		UtilsFX.bindTexture(tex);
		RenderUtil.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		TileShadowEnchanter t = getContainer().t;
		
		RenderUtil.drawTexturedModalRect(guiLeft + 27, guiTop + RECIPE_Y + lift, 0, ySize, 122, 35);
		int xOff = 0;
		for(ShadowEnchantment se : ShadowEnchantment.getRegistry())
			if(t.isAplicableBy(se.enchantment, ColoredLightManager.getClientPlayer()))
			{
				UtilsFX.bindTexture(se.getIcon());
				RenderUtil.drawFullTexturedModalRect(guiLeft + 28 + xOff, guiTop + RECIPE_Y + lift + 1, 16, 16);
				xOff += 16;
			}
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		int dwheel = Mouse.getEventDWheel();
		if(dwheel != 0)
		{
			
		}
		super.handleMouseInput();
	}
}