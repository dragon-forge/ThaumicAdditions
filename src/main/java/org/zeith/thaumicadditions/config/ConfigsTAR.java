package org.zeith.thaumicadditions.config;

import com.zeitheron.hammercore.cfg.HCModConfigurations;
import com.zeitheron.hammercore.cfg.IConfigReloadListener;
import com.zeitheron.hammercore.cfg.fields.ModConfigPropertyBool;
import com.zeitheron.hammercore.cfg.fields.ModConfigPropertyInt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import org.zeith.thaumicadditions.InfoTAR;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@HCModConfigurations(modid = InfoTAR.MOD_ID)
public class ConfigsTAR
		implements IConfigReloadListener
{
	@ModConfigPropertyBool(category = "Client", name = "Seethrough Portal", comment = "Should portal seal be see-throught?", defaultValue = true)
	public static boolean portalGfx;

	@ModConfigPropertyBool(category = "Knowledge Tome", name = "Reusable", comment = "Should the tome of sharing me reusable by multiple players?", defaultValue = true)
	public static boolean reusable;

	@ModConfigPropertyBool(category = "Knowledge Tome", name = "Rewritable", comment = "Should the tome of sharing me rewritable?", defaultValue = true)
	public static boolean rewritable;

	@ModConfigPropertyInt(category = "Blocks", name = "Cake Restoration Speed", comment = "How often the cake will restore? The higher the value is, lower the chance of growth.", defaultValue = 3, min = 1, max = Integer.MAX_VALUE)
	public static int cateRestoreRate;

	public static List<ResourceLocation> entityBlacklist;

	@Override
	public void reloadCustom(Configuration cfgs)
	{
		entityBlacklist = Arrays.stream(cfgs.getStringList("EntityCellBlacklist", "Entities", new String[0], "What entities should not be able to get picked up by Entity Cell?\nFormat: mod:entity\nExample: minecraft:creeper\n(Aww man)")).map(ResourceLocation::new).collect(Collectors.toList());
	}
}