package org.zeith.thaumicadditions.api.seals;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.tiles.TileSeal;

@FunctionalInterface
public interface ISealRenderer
{
	@SideOnly(Side.CLIENT)
	void render(TileSeal seal, float partialTicks);
}