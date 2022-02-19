package org.zeith.thaumicadditions.inventory.gui;

import com.zeitheron.hammercore.client.utils.RenderUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.entity.EntityChester;
import org.zeith.thaumicadditions.init.ItemsTAR;
import org.zeith.thaumicadditions.inventory.container.ContainerChester;

public class GuiChester
		extends GuiContainer
{
	public final ResourceLocation back = new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/gui_chester_base.png");
	public final ResourceLocation slots = new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/gui_chester_slots.png");

	public GuiChester(EntityChester chester, EntityPlayer player)
	{
		super(new ContainerChester(chester, player));
		xSize = 176;
		ySize = 192;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		EntityChester chester = ((ContainerChester) inventorySlots).chester;

		mc.getTextureManager().bindTexture(back);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
//		drawTexturedModalRect(guiLeft + 159, guiTop + 88, xSize + (chester.staying ? 10 : 0), 0, 10, 10);
		RenderUtil.drawTexturedModalRect(guiLeft + 130, guiTop + 6, 176, 16, 39 * chester.getHealth() / chester.getMaxHealth(), 6);

		mc.getTextureManager().bindTexture(slots);
		drawTexturedModalRect(guiLeft + 7, guiTop + 16, 7, 16, 161, 2 * chester.inventory.getSizeInventory());
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(ItemsTAR.CHESTER.getDefaultInstance().getDisplayName(), 8, 6, 1052688);
		fontRenderer.drawString(I18n.format("container.inventory"), 8, 96, 1052688);

		EntityChester trunk = ((ContainerChester) inventorySlots).chester;

		mouseX -= guiLeft;
		mouseY -= guiTop;

//		if(trunk.getUpgrades()[1] >= 0)
//		{
//			int x = 120;
//			int y = 91;
//			ItemStack stack = new ItemStack(ItemUpgrade.byId(trunk.getUpgrades()[1]));
//			itemRender.renderItemAndEffectIntoGUI(stack, x, y);
//			if(mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16)
//				drawHoveringText(stack.getTooltip(mc.player, TooltipFlags.NORMAL), mouseX, mouseY);
//		}

//		if(trunk.getUpgrades()[0] >= 0)
//		{
//			int x = 98;
//			int y = 91;
//			ItemStack stack = new ItemStack(ItemUpgrade.byId(trunk.getUpgrades()[0]));
//			itemRender.renderItemAndEffectIntoGUI(stack, x, y);
//			if(mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16)
//				drawHoveringText(stack.getTooltip(mc.player, TooltipFlags.NORMAL), mouseX, mouseY);
//		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
}