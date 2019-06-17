package thaumcraft.client.lib.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class HudHandlerHookTAR
{
	public static void renderThaumometer(Minecraft mc, float partialTicks, EntityPlayer player, long time, int ww, int hh, int x)
	{
		RenderEventHandler.hudHandler.renderThaumometerHud(mc, partialTicks, player, time, ww, hh, x);
	}
}