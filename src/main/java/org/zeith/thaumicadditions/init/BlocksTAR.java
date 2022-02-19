package org.zeith.thaumicadditions.init;

import com.zeitheron.hammercore.internal.SimpleRegistration;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.MinecraftForge;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.events.ProvideThaumicAspectsEvent;
import org.zeith.thaumicadditions.api.utils.IOcasionalPositionedEvent;
import org.zeith.thaumicadditions.blocks.*;
import org.zeith.thaumicadditions.blocks.decor.*;
import org.zeith.thaumicadditions.blocks.plants.BlockPuriflower;
import org.zeith.thaumicadditions.blocks.plants.BlockVisCrop;
import org.zeith.thaumicadditions.blocks.plants.BlockVoidCrop;
import org.zeith.thaumicadditions.blocks.sink.BlockEssentiaSink;
import org.zeith.thaumicadditions.blocks.sink.BlockEssentiaSinkAux;
import org.zeith.thaumicadditions.tiles.jars.*;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static final BlockVoidCrop VOID_CROP = BlockVoidCrop.CROP;
	public static final BlockShadowEnchanter SHADOW_ENCHANTER = new BlockShadowEnchanter();
	public static final BlockThaumicLectern THAUMIC_LECTERN = new BlockThaumicLectern();

	public static final BlockVoidAnvil VOID_ANVIL = new BlockVoidAnvil();

	public static final BlockEssentiaSink ESSENTIA_SINK = new BlockEssentiaSink();
	public static final BlockEssentiaSinkAux ESSENTIA_SINK_AUX = new BlockEssentiaSinkAux();

	// public static final BlockAuraTotem AURA_TOTEM = new BlockAuraTotem();

	public static final BlockAbstractEssentiaJar<TileBrassJar> BRASS_JAR = new BlockAbstractEssentiaJar<>(TileBrassJar.class, 275, "jar_brass");
	public static final BlockAbstractEssentiaJar<TileThaumiumJar> THAUMIUM_JAR = new BlockAbstractEssentiaJar<>(TileThaumiumJar.class, 350, "jar_thaumium");
	public static final BlockAbstractEssentiaJar<TileEldritchJar> ELDRITCH_JAR = new BlockAbstractEssentiaJar<>(TileEldritchJar.class, 500, "jar_eldritch");
	public static final BlockAbstractEssentiaJar<TileMithrilliumJar> MITHRILLIUM_JAR = new BlockAbstractEssentiaJar<>(TileMithrilliumJar.class, 1000, "jar_mithrillium");
	public static final BlockAbstractEssentiaJar<TileAdaminiteJar> ADAMINITE_JAR = new BlockAbstractEssentiaJar<>(TileAdaminiteJar.class, 2000, "jar_adaminite");
	public static final BlockAbstractEssentiaJar<TileMithminiteJar> MITHMINITE_JAR = new BlockAbstractEssentiaJar<>(TileMithminiteJar.class, 4000, "jar_mithminite");

	public static final BlockCrystal CRYSTAL_BLOCK = new BlockCrystal();

	public static final BlockTaintkin TAINTKIN = new BlockTaintkin();
	public static final BlockTaintkinLit TAINTKIN_LIT = new BlockTaintkinLit();
	public static final BlockTARStorage MITHRILLIUM_BLOCK = new BlockTARStorage("mithrillium_block", "blockMithrillium");
	public static final BlockTARStorage ADAMINITE_BLOCK = new BlockTARStorage("adaminite_block", "blockAdaminite");
	public static final BlockTARStorage MITHMINITE_BLOCK = new BlockTARStorage("mithminite_block", "blockMithminite");
	public static final BlockTARStorage TAINTWOOD_PLANK = new BlockTARStorage(Material.WOOD, SoundType.WOOD, 1F, "axe", 0, "taintwood_planks", "plankWood");
	public static final BlockTARStorage CHISELED_AMBER_BLOCK = new BlockTARStorage(Material.ROCK, SoundType.STONE, 1F, "pickaxe", 0, "chiseled_amber_block", "blockAmber");
	public static final BlockIronFramedGreatwood IRON_FRAMED_GREATWOOD = new BlockIronFramedGreatwood("iron_framed_greatwood");
	public static final BlockIronFramedGreatwood BRASS_PLATED_SILVERWOOD = new BlockIronFramedGreatwood("brass_plated_silverwood");
	public static final BlockIronFramedGreatwood CHISELED_GREATWOOD = new BlockIronFramedGreatwood("chiseled_greatwood");
	public static final BlockAmberLamp AMBER_LAMP = new BlockAmberLamp();
	public static final BlockCrystalLamp CRYSTAL_LAMP = new BlockCrystalLamp();

	public static final Map<Aspect, BlockVisCrop> VIS_CROPS = new HashMap<>();

	public static List<Aspect> INDEXED_ASPECTS = new ArrayList<>();

	public static void loadAspectBlocks()
	{
		ProvideThaumicAspectsEvent evt = new ProvideThaumicAspectsEvent();
		MinecraftForge.EVENT_BUS.post(evt);
		INDEXED_ASPECTS.addAll(evt.getAspects());
		for(Aspect a : evt.getAspects())
			if(!VIS_CROPS.containsKey(a))
			{
				BlockVisCrop block;
				VIS_CROPS.put(a, block = new BlockVisCrop(a));
				SimpleRegistration.registerBlock(block, InfoTAR.MOD_ID, null);
			}
	}
}