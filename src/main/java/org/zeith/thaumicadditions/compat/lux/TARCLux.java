package org.zeith.thaumicadditions.compat.lux;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.mod.ModuleLoader;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.lux.api.LuxManager;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.compat.ITARC;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.world.ore.BlockCrystal;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@ModuleLoader(requiredModid = "lux")
public class TARCLux
		implements ITARC
{
	public static final NoiseGeneratorSimplex SIMPLEX = new NoiseGeneratorSimplex();

	@Override
	public void init()
	{
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		for(EnumDyeColor dye : EnumDyeColor.values())
		{
			Block blk = BlocksTC.nitor.get(dye);
			final int color = dye.getColorValue();

			LuxManager.registerBlockLight(blk, (world, pos, state, e) ->
			{
				float flicker = (float) SIMPLEX.getValue(pos.getX() * 80 + ManagementFactory.getRuntimeMXBean().getUptime() / 500D, 160 * pos.getZ() * pos.getY()) + 1F;
				e.add(ColoredLight.builder().pos(pos).color(color, false).radius(15 + flicker / 2F).build());
			});
		}

		Map<Block, Integer> crystals = new HashMap<>();
		crystals.put(BlocksTC.crystalAir, Aspect.AIR.getColor());
		crystals.put(BlocksTC.crystalEarth, Aspect.EARTH.getColor());
		crystals.put(BlocksTC.crystalEntropy, Aspect.ENTROPY.getColor());
		crystals.put(BlocksTC.crystalFire, Aspect.FIRE.getColor());
		crystals.put(BlocksTC.crystalOrder, Aspect.ORDER.getColor());
		crystals.put(BlocksTC.crystalTaint, Aspect.FLUX.getColor());
		crystals.put(BlocksTC.crystalWater, Aspect.WATER.getColor());
		for(Block crystal : crystals.keySet())
		{
			BlockCrystal c = Cast.cast(crystal, BlockCrystal.class);
			if(c != null)
			{
				int color = crystals.get(crystal);
				LuxManager.registerBlockLight(c, (world, pos, state, e) ->
				{
					boolean r = false;
					for(EnumFacing f : EnumFacing.VALUES)
						if(!world.getBlockState(pos.offset(f)).isSideSolid(world, pos.offset(f), f.getOpposite()))
						{
							r = true;
							break;
						}
					if(!r)
						return;
					int size = state.getValue(BlockCrystal.SIZE);
					float flicker = (float) SIMPLEX.getValue(pos.getX() * 80 + ManagementFactory.getRuntimeMXBean().getUptime() / 2000D, 160 * pos.getZ() * pos.getY()) * 2F;
					e.add(ColoredLight.builder().pos(pos).color(color, false).radius(flicker / 2F * size + (size + 1) * 1.5F).build());
				});
			}
		}

		LuxManager.registerBlockLight(BlocksTC.shimmerleaf, (world, pos, state, e) ->
		{
			float flicker = (float) SIMPLEX.getValue(80 * pos.getX() + ManagementFactory.getRuntimeMXBean().getUptime() / 2000D, 160 * pos.getZ() * pos.getY()) + 1F;
			e.add(ColoredLight.builder().pos(pos).color(0.1F, 0.3F, 0.3F, 1F).radius(flicker * 2F + 3).build());
		});

		LuxManager.registerBlockLight(BlocksTC.vishroom, (world, pos, state, e) ->
		{
			float flicker = (float) SIMPLEX.getValue(80 * pos.getX() + ManagementFactory.getRuntimeMXBean().getUptime() / 2000D, 160 * pos.getZ() * pos.getY()) + 1F;
			e.add(ColoredLight.builder().pos(pos).color(0.4F, 0.0F, 0.4F, 1F).radius(flicker * 2F + 3).build());
		});

		LuxManager.registerBlockLight(BlocksTC.cinderpearl, (world, pos, state, e) ->
		{
			float flicker = (float) SIMPLEX.getValue(80 * pos.getX() + ManagementFactory.getRuntimeMXBean().getUptime() / 2000D, 160 * pos.getZ() * pos.getY()) + 1F;
			e.add(ColoredLight.builder().pos(pos).color(0.5F, 0.3F, 0.0F, 1F).radius(flicker * 2F + 5).build());
		});

		TAReconstructed.LOG.info("ColoredLux compat initialized.");
	}
}