package com.zeitheron.thaumicadditions.init;

import java.util.HashMap;
import java.util.Map;

import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.api.events.ProvideThaumicAspectsEvent;
import com.zeitheron.thaumicadditions.api.utils.IOcasionalPositionedEvent;
import com.zeitheron.thaumicadditions.blocks.BlockAbstractEssentiaJar;
import com.zeitheron.thaumicadditions.blocks.BlockAbstractSmelter;
import com.zeitheron.thaumicadditions.blocks.BlockArcaneCake;
import com.zeitheron.thaumicadditions.blocks.BlockAspectCombiner;
import com.zeitheron.thaumicadditions.blocks.BlockAuraCharger;
import com.zeitheron.thaumicadditions.blocks.BlockAuraDisperser;
import com.zeitheron.thaumicadditions.blocks.BlockCrystal;
import com.zeitheron.thaumicadditions.blocks.BlockCrystalBore;
import com.zeitheron.thaumicadditions.blocks.BlockCrystalCrusher;
import com.zeitheron.thaumicadditions.blocks.BlockCrystalWater;
import com.zeitheron.thaumicadditions.blocks.BlockEntitySummoner;
import com.zeitheron.thaumicadditions.blocks.BlockFluxConcentrator;
import com.zeitheron.thaumicadditions.blocks.BlockGrowthChamber;
import com.zeitheron.thaumicadditions.blocks.BlockSeal;
import com.zeitheron.thaumicadditions.blocks.BlockShadowEnchanter;
import com.zeitheron.thaumicadditions.blocks.BlockThaumicLectern;
import com.zeitheron.thaumicadditions.blocks.BlockTotem;
import com.zeitheron.thaumicadditions.blocks.plants.BlockPuriflower;
import com.zeitheron.thaumicadditions.blocks.plants.BlockVisCrop;
import com.zeitheron.thaumicadditions.blocks.plants.BlockVoidCrop;
import com.zeitheron.thaumicadditions.tiles.jars.TileAdaminiteJar;
import com.zeitheron.thaumicadditions.tiles.jars.TileBrassJar;
import com.zeitheron.thaumicadditions.tiles.jars.TileEldritchJar;
import com.zeitheron.thaumicadditions.tiles.jars.TileMithminiteJar;
import com.zeitheron.thaumicadditions.tiles.jars.TileMithrilliumJar;
import com.zeitheron.thaumicadditions.tiles.jars.TileThaumiumJar;

import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.aspects.Aspect;

public class BlocksTAR
{
	public static final BlockAbstractSmelter MITHRILLIUM_SMELTER = new BlockAbstractSmelter("mithrillium_smelter", 1F, 20, 1000);
	public static final BlockAbstractSmelter ADAMINITE_SMELTER = new BlockAbstractSmelter("adaminite_smelter", 1.2F, 15, 2000);
	public static final BlockAbstractSmelter MITHMINITE_SMELTER = new BlockAbstractSmelter("mithminite_smelter", 1.5F, 10, 4000);
	
	public static final BlockCrystalWater CRYSTAL_WATER = new BlockCrystalWater();
	public static final BlockAuraDisperser AURA_DISPERSER = new BlockAuraDisperser();
	public static final BlockAspectCombiner ASPECT_COMBINER = new BlockAspectCombiner();
	public static final BlockAuraCharger AURA_CHARGER = new BlockAuraCharger();
	public static final BlockCrystalCrusher CRYSTAL_CRUSHER = new BlockCrystalCrusher();
	public static final BlockCrystalBore CRYSTAL_BORE = new BlockCrystalBore();
	public static final BlockEntitySummoner ENTITY_SUMMONER = new BlockEntitySummoner();
	public static final BlockPuriflower PURIFLOWER = new BlockPuriflower();
	public static final BlockFluxConcentrator FLUX_CONCENTRATOR = new BlockFluxConcentrator();
	public static final BlockArcaneCake CAKE = new BlockArcaneCake();
	public static final BlockTotem DAWN_TOTEM = new BlockTotem("dawn", IOcasionalPositionedEvent.DAWN);
	public static final BlockTotem TWILIGHT_TOTEM = new BlockTotem("twilight", IOcasionalPositionedEvent.TWILIGHT);
	public static final BlockGrowthChamber GROWTH_CHAMBER = new BlockGrowthChamber();
	public static final BlockSeal SEAL = new BlockSeal();
	public static final Map<Aspect, BlockVisCrop> VIS_CROPS = new HashMap<>();
	public static final BlockVoidCrop VOID_CROP = BlockVoidCrop.CROP;
	public static final BlockShadowEnchanter SHADOW_ENCHANTER = new BlockShadowEnchanter();
	public static final BlockThaumicLectern THAUMIC_LECTERN = new BlockThaumicLectern();
	
	// public static final BlockAuraTotem AURA_TOTEM = new BlockAuraTotem();
	
	public static final BlockAbstractEssentiaJar<TileBrassJar> BRASS_JAR = new BlockAbstractEssentiaJar<>(TileBrassJar.class, 275, "jar_brass");
	public static final BlockAbstractEssentiaJar<TileThaumiumJar> THAUMIUM_JAR = new BlockAbstractEssentiaJar<>(TileThaumiumJar.class, 350, "jar_thaumium");
	public static final BlockAbstractEssentiaJar<TileEldritchJar> ELDRITCH_JAR = new BlockAbstractEssentiaJar<>(TileEldritchJar.class, 500, "jar_eldritch");
	public static final BlockAbstractEssentiaJar<TileMithrilliumJar> MITHRILLIUM_JAR = new BlockAbstractEssentiaJar<>(TileMithrilliumJar.class, 1000, "jar_mithrillium");
	public static final BlockAbstractEssentiaJar<TileAdaminiteJar> ADAMINITE_JAR = new BlockAbstractEssentiaJar<>(TileAdaminiteJar.class, 2000, "jar_adaminite");
	public static final BlockAbstractEssentiaJar<TileMithminiteJar> MITHMINITE_JAR = new BlockAbstractEssentiaJar<>(TileMithminiteJar.class, 4000, "jar_mithminite");
	
	public static final BlockCrystal CRYSTAL_BLOCK = new BlockCrystal();
	
	public static void loadAspectBlocks()
	{
		ProvideThaumicAspectsEvent evt = new ProvideThaumicAspectsEvent();
		MinecraftForge.EVENT_BUS.post(evt);
		for(Aspect a : evt.getAspects())
			if(!VIS_CROPS.containsKey(a))
			{
				BlockVisCrop block;
				VIS_CROPS.put(a, block = new BlockVisCrop(a));
				SimpleRegistration.registerBlock(block, InfoTAR.MOD_ID, null);
			}
	}
}