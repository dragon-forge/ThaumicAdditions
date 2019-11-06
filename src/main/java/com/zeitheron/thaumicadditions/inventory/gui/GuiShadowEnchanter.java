package com.zeitheron.thaumicadditions.inventory.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.lwjgl.input.Mouse;

import com.zeitheron.hammercore.client.gui.GuiWTFMojang;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.compat.jei.IJeiHelper;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.api.ShadowEnchantment;
import com.zeitheron.thaumicadditions.inventory.container.ContainerShadowEnchanter;
import com.zeitheron.thaumicadditions.net.PacketHandleShadowLvl;
import com.zeitheron.thaumicadditions.tiles.TileShadowEnchanter;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.SoundsTC;

public class GuiShadowEnchanter extends GuiWTFMojang<ContainerShadowEnchanter>
{
	public static final ResourceLocation tex = new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/shadow_enchanter.png");
	
	static final int RECIPE_Y = 16, SELECT_Y = 53, Y_DELTA = SELECT_Y - RECIPE_Y;
	
	int enchantmentScroll, essentiaScroll;
	float lift = Y_DELTA;
	final Rectangle enchantmentArea = new Rectangle();
	
	ShadowEnchantment hoveredEnch, selectedEnch;
	AspectList aspects;
	Aspect[] aspectArray;
	Aspect hoveredAspect;
	
	public GuiShadowEnchanter(ContainerShadowEnchanter inventorySlotsIn)
	{
		super(inventorySlotsIn);
		xSize = 176;
		ySize = 186;
	}
	
	GuiButton uplvl, downlvl;
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		addButton(uplvl = new GuiButton(0, guiLeft + 151, guiTop + 41, 18, 20, "+"));
		addButton(downlvl = new GuiButton(1, guiLeft + 151, guiTop + 81, 18, 20, "-"));
	}
	
	@Override
	public void updateScreen()
	{
		aspects = getContainer().t.calculateAspects();
		aspectArray = aspects != null ? aspects.getAspectsSortedByAmount() : null;
		super.updateScreen();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		UtilsFX.bindTexture(tex);
		RenderUtil.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		AspectList left = getContainer().t.pending;
		if(left == null || left.size() == 0)
			left = null;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft + 120, guiTop + 18, 0);
		GlStateManager.rotate(90, 0, 0, 1);
		if(aspects != null && aspects.size() > 0)
		{
			AspectList all = aspects.copy();
			int tot = all.visSize();
			if(left != null)
			{
				float filledPart = 0F;
				for(Aspect a : all.getAspectsSortedByName())
				{
					int amt = all.getAmount(a) - left.getAmount(a);
					if(amt <= 0)
						continue;
					float part = amt / (float) tot;
					{
						float u = part * 50;
						float i = 50 - u;
						ColorHelper.glColor1ia(255 << 24 | a.getColor());
						RenderUtil.drawTexturedModalRect(3, 6 + i - filledPart * 50, 176, i, 8, u);
						RenderUtil.drawTexturedModalRect(3, 6 + i - filledPart * 50, 248, i + (System.currentTimeMillis() % 256000L / 100F % 256F), 8, u);
					}
					filledPart += part;
				}
			}
		}
		GlStateManager.color(1F, 1F, 1F, 1F);
		RenderUtil.drawTexturedModalRect(0, 0, 190, 0, 14, 62);
		GlStateManager.popMatrix();
		
		TileShadowEnchanter t = getContainer().t;
		Map<Enchantment, Integer> lvlvs = t.enchants.stream().collect(Collectors.toMap(d -> d.enchantment, d -> d.enchantmentLevel));
		
		hoveredEnch = null;
		
		enchantmentArea.setBounds(guiLeft + 20, guiTop + RECIPE_Y + (int) lift, 130, 18);
		RenderUtil.drawTexturedModalRect(guiLeft + 20, guiTop + RECIPE_Y + lift, 0, ySize, 130, 35);
		boolean clear = true;
		int xOff = 0, sc = enchantmentScroll, count = 0;
		for(ShadowEnchantment se : ShadowEnchantment.getRegistry())
			if(t.isAplicableBy(se.enchantment, mc.player))
			{
				if(selectedEnch == se)
					clear = false;
				int lvl = lvlvs.getOrDefault(se.enchantment, 0);
				if(sc > 0)
				{
					--sc;
					continue;
				}
				
				UtilsFX.bindTexture(se.getIcon());
				RenderUtil.drawFullTexturedModalRect(guiLeft + 21 + xOff, guiTop + RECIPE_Y + lift + 1, 16, 16);
				
				cacheRect.setBounds(guiLeft + 21 + xOff, guiTop + RECIPE_Y + (int) lift + 1, 18, 18);
				if(cacheRect.contains(mouseX, mouseY))
					hoveredEnch = se;
				
				if(lvl > 0)
				{
					String str = Integer.toString(lvl);
					
					float scale = 0.5f;
					
					float x = cacheRect.x + (cacheRect.width - (fontRenderer.getStringWidth(str) + 4) * scale);
					float y = cacheRect.y + cacheRect.height - (fontRenderer.FONT_HEIGHT + 3) * scale;
					
					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y, 0);
					GlStateManager.scale(scale, scale, scale);
					fontRenderer.drawString(str, -1, 0, 0xFF000000);
					fontRenderer.drawString(str, 0, -1, 0xFF000000);
					fontRenderer.drawString(str, 1, 0, 0xFF000000);
					fontRenderer.drawString(str, 0, 1, 0xFF000000);
					fontRenderer.drawString(str, 0, 0, 0xFFFFFFFF);
					GlStateManager.popMatrix();
				}
				
				xOff += 16;
				if(++count >= 8)
					break;
			}
		if(clear)
			selectedEnch = null;
		hoveredAspect = null;
		xOff = 0;
		int yOff = 0;
		int ai = 0;
		AspectList render = left != null ? left : aspects;
		if(render != null)
			for(Aspect a : render.getAspectsSortedByName())
			{
				int amt = render.getAmount(a);
				
				cacheRect.setBounds(guiLeft + 21 + xOff, guiTop + RECIPE_Y + (int) lift + 18 + yOff, 8, 8);
				GlStateManager.pushMatrix();
				GlStateManager.translate(guiLeft + 21 + xOff, guiTop + RECIPE_Y + lift + 18 + yOff, 0);
				GlStateManager.scale(0.5, 0.5, 0.5);
				thaumcraft.client.lib.UtilsFX.drawTag(0, 0, a, amt, 0, 0);
				GlStateManager.popMatrix();
				xOff += 8;
				if(xOff >= 128)
				{
					yOff += 8;
					xOff = 0;
				}
				++ai;
				if(ai >= 32)
				{
					break;
				}
				if(cacheRect.contains(mouseX, mouseY))
					hoveredAspect = a;
			}
		
		if(selectedEnch != null)
		{
			UtilsFX.bindTexture(selectedEnch.getIcon());
			RenderUtil.drawFullTexturedModalRect(guiLeft + 152, guiTop + 63, 16, 16);
			cacheRect.setBounds(guiLeft + 152, guiTop + 63, 18, 18);
			
			int lvl = lvlvs.getOrDefault(selectedEnch.enchantment, 0);
			String str = Integer.toString(lvl);
			
			int x = cacheRect.x + (cacheRect.width - fontRenderer.getStringWidth(str)) / 2;
			int y = cacheRect.y + cacheRect.height - fontRenderer.FONT_HEIGHT;
			
			fontRenderer.drawString(str, x - 1, y, 0xFF000000);
			fontRenderer.drawString(str, x, y - 1, 0xFF000000);
			fontRenderer.drawString(str, x + 1, y, 0xFF000000);
			fontRenderer.drawString(str, x, y + 1, 0xFF000000);
			fontRenderer.drawString(str, x, y, 0xFFFFFFFF);
			
			cacheRect.setBounds(guiLeft + 152, guiTop + 63, 18, 18);
			if(cacheRect.contains(mouseX, mouseY))
				hoveredEnch = selectedEnch;
		}
		
		if(t.infusing)
			selectedEnch = null;
		uplvl.enabled = downlvl.enabled = selectedEnch != null;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(selectedEnch != null)
		{
			if(button == uplvl)
			{
				HCNet.INSTANCE.sendToServer(PacketHandleShadowLvl.create(1, selectedEnch.enchantment));
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsTC.hhon, 1F));
			} else if(button == downlvl)
			{
				HCNet.INSTANCE.sendToServer(PacketHandleShadowLvl.create(2, selectedEnch.enchantment));
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsTC.hhoff, 1F));
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		if(hoveredEnch != null)
		{
			Map<Enchantment, Integer> lvlvs = getContainer().t.enchants.stream().collect(Collectors.toMap(d -> d.enchantment, d -> d.enchantmentLevel));
			drawHoveringText(hoveredEnch.enchantment.getTranslatedName(lvlvs.getOrDefault(hoveredEnch.enchantment, 1)), mouseX - guiLeft, mouseY - guiTop);
		}
		if(hoveredAspect != null)
		{
			AspectList left = getContainer().t.pending;
			if(left == null || left.size() == 0)
				left = null;
			AspectList render = left != null ? left : aspects;
			String count = render != null ? " (x" + render.getAmount(hoveredAspect) + ")" : "";
			drawHoveringText(Arrays.asList(hoveredAspect.getName() + count, TextFormatting.GRAY + hoveredAspect.getLocalizedDescription()), mouseX - guiLeft, mouseY - guiTop);
		}
	}
	
	final Rectangle cacheRect = new Rectangle();
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(hoveredEnch != null && selectedEnch != hoveredEnch && !getContainer().t.infusing)
		{
			selectedEnch = hoveredEnch;
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsTC.clack, 1F));
		}
		
		if(selectedEnch != null)
		{
			
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(hoveredAspect != null)
		{
			IJeiHelper jei = IJeiHelper.instance();
			
			KeyBinding r = Cast.cast(jei.getKeybind_showRecipes(), KeyBinding.class);
			KeyBinding u = Cast.cast(jei.getKeybind_showUses(), KeyBinding.class);
			
			try
			{
				if(r != null && r.getKeyCode() == keyCode)
					jei.showRecipes(new AspectList().add(hoveredAspect, 1));
				if(u != null && u.getKeyCode() == keyCode)
					jei.showUses(new AspectList().add(hoveredAspect, 1));
			} catch(Throwable err)
			{
			}
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		int dwheel = Mouse.getEventDWheel();
		if(dwheel != 0)
		{
			int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
			int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			
			if(enchantmentArea.contains(mouseX, mouseY))
			{
				int xOff = 0, sc = enchantmentScroll, count = 0;
				boolean canGoNext = false;
				for(ShadowEnchantment se : ShadowEnchantment.getRegistry())
					if(getContainer().t.isAplicableBy(se.enchantment, mc.player))
					{
						if(sc > 0)
						{
							--sc;
							continue;
						}
						xOff += 16;
						if(++count >= 7)
						{
							canGoNext = true;
							break;
						}
					}
				if(dwheel < 0 && canGoNext)
					++enchantmentScroll;
				if(dwheel > 0 && enchantmentScroll > 0)
					--enchantmentScroll;
			}
		}
		super.handleMouseInput();
	}
}