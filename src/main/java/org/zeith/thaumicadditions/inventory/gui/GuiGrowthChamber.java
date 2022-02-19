package org.zeith.thaumicadditions.inventory.gui;

import com.zeitheron.hammercore.client.gui.GuiWTFMojang;
import com.zeitheron.hammercore.client.render.world.VirtualWorld;
import com.zeitheron.hammercore.client.render.world.VirtualWorldRenderer;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.init.BlocksTAR;
import org.zeith.thaumicadditions.inventory.container.ContainerGrowthChamber;
import org.zeith.thaumicadditions.tiles.TileCrystalBlock;
import org.zeith.thaumicadditions.tiles.TileGrowthChamber;
import thaumcraft.api.aspects.Aspect;

public class GuiGrowthChamber
		extends GuiWTFMojang<ContainerGrowthChamber>
{
	public TileGrowthChamber tile;
	public float prevAmount;
	VirtualWorld world = new VirtualWorld();

	public GuiGrowthChamber(EntityPlayer player, TileGrowthChamber tile)
	{
		super(new ContainerGrowthChamber(player, tile));
		this.tile = tile;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		world.setBlockState(BlockPos.ORIGIN, BlocksTAR.CRYSTAL_BLOCK.getStateFromMeta(EnumFacing.DOWN.ordinal()));
		TileCrystalBlock tcb = new TileCrystalBlock();
		world.setTileEntity(BlockPos.ORIGIN, tcb);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		if(tile.slotAspect != null)
		{
			float rotY = (System.currentTimeMillis() % 7200L) / 20F;

			GL11.glPushMatrix();
			GL11.glTranslated(63 + 18, 27, 250F);
			VirtualWorldRenderer.renderVirtualWorld(world, mc, new Rectangle(0, 0, 56, 56), 135, rotY, 2000F);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void updateScreen()
	{
		prevAmount = tile.toDrainAura;
		((TileCrystalBlock) world.getTileEntity(BlockPos.ORIGIN)).setAspect(tile.slotAspect);
		super.updateScreen();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/gui/growth_chamber.png");
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 45, guiTop + 7, 191, 0, 12, 62);

		Aspect a = tile.slotAspect;
		if(a != null)
		{
			ColorHelper.gl(255 << 24 | tile.slotAspect.getColor());

			float fill = (TileGrowthChamber.MAX_AURA_DRAIN - (prevAmount + (tile.toDrainAura - prevAmount) * partialTicks)) / TileGrowthChamber.MAX_AURA_DRAIN;

			RenderUtil.drawTexturedModalRect(guiLeft + 47, guiTop + 13 + 50 - fill * 50, 176, 0, 8, fill * 50);
			RenderUtil.drawTexturedModalRect(guiLeft + 47, guiTop + 13 + 50 - fill * 50, 248, (tile.ticksExisted + partialTicks) % 256, 8, fill * 50);

			ColorHelper.gl(0xFFFFFFFF);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GL11.glColor4f(1, 1, 1, 1);
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		GL11.glColor4f(1, 1, 1, 1);
	}
}