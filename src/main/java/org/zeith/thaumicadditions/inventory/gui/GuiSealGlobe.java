package org.zeith.thaumicadditions.inventory.gui;

import com.zeitheron.hammercore.client.gui.GuiCentered;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.items.ItemSealSymbol;
import org.zeith.thaumicadditions.tiles.TileSeal;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

public class GuiSealGlobe
		extends GuiCentered
{
	public final TileSeal seal;
	final List<String> tooltip = new ArrayList<>();

	public GuiSealGlobe(TileSeal seal)
	{
		this.seal = seal;
		xSize = 192;
		ySize = 192 + 36;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.enableBlend();
		UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/gui/seal_globe.png");
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop + 36, 0);
		GlStateManager.scale(192 / 256F, 192 / 256F, 192 / 256F);
		RenderUtil.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		GlStateManager.popMatrix();

		float size = 32F;
		int total = 3;

		double gap = xSize / total;

		for(int i = 0; i < total; ++i)
		{
			Aspect s = seal.getSymbol(i);
			UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/gui/widgets.png");
			GlStateManager.pushMatrix();
			float x = (float) (guiLeft + (xSize - size) / 2 + gap * (i - 1));
			float y = (float) guiTop;
			boolean hover = mouseX >= x && mouseY >= y && mouseX < x + size && mouseY < y + size;
			GlStateManager.translate(x, y, 0);
			GlStateManager.scale(size / 16F, size / 16F, size / 16F);
			RenderUtil.drawTexturedModalRect(0, 0, 0, 16, 16, 16);
			if(s == null)
			{
				if(hover)
					tooltip.add(I18n.format("gui." + InfoTAR.MOD_ID + ":null"));
				RenderUtil.drawTexturedModalRect(0, 0, 0, 32, 16, 16);
			} else
			{
				ItemStack is = ItemSealSymbol.createItem(s, 1);
				mc.getRenderItem().renderItemAndEffectIntoGUI(is, 0, 0);

				if(hover)
				{
					tooltip.addAll(is.getTooltip(mc.player, TooltipFlags.NORMAL));

					GlStateManager.pushMatrix();
					GlStateManager.translate(9.5, 9.5, 350);
					GlStateManager.scale(1 / 3., 1 / 3., 1 / 3.);
					thaumcraft.client.lib.UtilsFX.drawTag(0, 0, s, 0F, 0, 0., 771, 1F, false);
					GlStateManager.popMatrix();
				}
			}
			GlStateManager.popMatrix();
		}

		String text;

		if(seal.combination == null)
			text = I18n.format("seal." + InfoTAR.MOD_ID + ":none");
		else
		{
			String d = seal.combination.getDescription(seal);
			if(d != null)
				text = d;
			else
				text = I18n.format("seal." + InfoTAR.MOD_ID + ":unconfigured", seal.combination.getModName(), seal.combination.getAuthor());
		}

		int maxLength = 150;
		int height = fontRenderer.getWordWrappedHeight(text, maxLength);

		fontRenderer.drawSplitString(text, (int) (guiLeft + 26.25), (int) (guiTop + 36 + ((ySize - 36) - height) / 2), maxLength, 0xFFFFFFFF);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(!tooltip.isEmpty())
		{
			drawHoveringText(tooltip, mouseX, mouseY);
			tooltip.clear();
		}
	}
}