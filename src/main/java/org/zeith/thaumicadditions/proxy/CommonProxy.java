package org.zeith.thaumicadditions.proxy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.proxy.fx.FXHandler;
import org.zeith.thaumicadditions.tiles.TileSeal;

public class CommonProxy
{
	public static final ResourceLocation TEXTURE_THAUMONOMICON_BG = new ResourceLocation(InfoTAR.MOD_ID, "textures/builting/texture_thaumonomicon_bg");
	public static final String BUILD = "DEV_BUILD";

	protected FXHandler fx;

	public void construct()
	{
		if(BUILD.contains("TRUSTED"))
		{
			TAReconstructed.LOG.warn("You are running trusted build " + InfoTAR.MOD_VERSION + "@" + SProt.BUILD_COPY + ". We must check that everything is OK before continuing!");
			String err = SProt.playErr();
			if(err != null)
			{
				TAReconstructed.LOG.error("Uh oh! Zeitheron detected that you can't play with this test build! Sorry :c");
				throw new RuntimeException(err);
			}
			TAReconstructed.LOG.info("Zeitheron checked everything, you are allowed ;)");
		} else if(BUILD.contains("DEV"))
			TAReconstructed.LOG.info("Welcome to Minecraft, developer!");
		else
			TAReconstructed.LOG.info("You are running general public build " + InfoTAR.MOD_VERSION + "@" + SProt.BUILD_COPY + ". You're fine! ;)");
	}

	public void preInit()
	{
	}

	public void init()
	{
	}

	public void postInit()
	{
	}

	protected FXHandler createFX()
	{
		return new FXHandler();
	}

	public FXHandler getFX()
	{
		if(fx == null)
			fx = createFX();
		return fx;
	}

	public int getItemColor(ItemStack stack, int layer)
	{
		return 0xFFFFFF;
	}

	public void viewSeal(TileSeal tile)
	{
	}
}