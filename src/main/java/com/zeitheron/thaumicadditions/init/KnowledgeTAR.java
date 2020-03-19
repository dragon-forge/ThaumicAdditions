package com.zeitheron.thaumicadditions.init;

import com.zeitheron.hammercore.annotations.MCFBus;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.hammercore.lib.zlib.utils.Joiner;
import com.zeitheron.hammercore.utils.OnetimeCaller;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.*;
import com.zeitheron.thaumicadditions.config.ConfigsTAR;
import com.zeitheron.thaumicadditions.entity.EntityBlueWolf;
import com.zeitheron.thaumicadditions.items.seed.ItemVisSeeds;
import com.zeitheron.thaumicadditions.tiles.TileAuraCharger;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.*;
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta;
import thaumcraft.api.research.ResearchStage.Knowledge;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ResearchManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@MCFBus
public class KnowledgeTAR
{
	public static final OnetimeCaller init = new OnetimeCaller(KnowledgeTAR::$init);
	public static final OnetimeCaller insertAspects = new OnetimeCaller(KnowledgeTAR::$insertAspects);
	public static final OnetimeCaller clInit = new OnetimeCaller(KnowledgeTAR::$);

	public static final Aspect FLUCTUS = new Aspect("fluctus", 0xA8A8A8, new Aspect[]{
			Aspect.MOTION,
			Aspect.WATER
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/fluctus.png"), 1);
	public static final Aspect SONUS = new Aspect("sonus", 0xFFAA00, new Aspect[]{
			FLUCTUS,
			Aspect.AIR
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/sonus.png"), 1);
	public static final Aspect EXITIUM = new Aspect("exitium", 0x777777, new Aspect[]{
			Aspect.ENTROPY,
			Aspect.TOOL
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/exitium.png"), 1);
	public static final Aspect CAELES = new CustomColoredAspect("caeles", 0xFF0000, new Aspect[]{
			Aspect.MAN,
			Aspect.DESIRE
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/caeles.png"), 1, CustomColoredAspect.RAINBOW);
	public static final Aspect DRACO = new Aspect("draco", 0x00BCFF, new Aspect[]{
			CAELES,
			Aspect.LIFE
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/draco.png"), 1);
	public static final Aspect INFERNUM = new Aspect("infernum", 0xFF2314, new Aspect[]{
			Aspect.FIRE,
			Aspect.DEATH
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/infernum.png"), 1);
	public static final Aspect VENTUS = new Aspect("ventus", 0xFCFCCF, new Aspect[]{
			Aspect.AIR,
			Aspect.FLIGHT
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/ventus.png"), 1);
	public static final Aspect VISUM = new Aspect("visum", 0x45CF35, new Aspect[]{
			Aspect.SENSES,
			Aspect.CRYSTAL
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/visum.png"), 1);
	public static final Aspect IMPERIUM = new Aspect("imperium", 0xD6A255, new Aspect[]{
			Aspect.MIND,
			Aspect.MECHANISM
	}, new ResourceLocation(InfoTAR.MOD_ID, "textures/aspects/imperium.png"), 1);

	@SubscribeEvent
	public void commandEvent(CommandEvent ce)
	{
		if(ce.getCommand() instanceof CommandThaumcraft && ce.getParameters().length > 0 && ce.getParameters()[0].equalsIgnoreCase("reload"))
		{
			new Thread(() ->
			{
				while(TAReconstructed.RES_CAT.research.containsKey("TAR_THAUMADDS"))
					try
					{
						Thread.sleep(10L);
					} catch(InterruptedException e)
					{
						e.printStackTrace();
					}

				$init();
			}).start();
		}
	}

	private static void registerScans()
	{
		ScanningManager.addScannableThing(new ScanBlock("!TARShimmerleaf", BlocksTC.shimmerleaf));
		ScanningManager.addScannableThing(new ScanEntity("!TARBlueWolf", EntityBlueWolf.class, false));
	}

	private static void $init()
	{
		registerScans();

		ResearchCategory R_BASICS = ResearchCategories.getResearchCategory("BASICS");
		ResearchCategory R_AUROMANCY = ResearchCategories.getResearchCategory("AUROMANCY");
		ResearchCategory R_ALCHEMY = ResearchCategories.getResearchCategory("ALCHEMY");
		ResearchCategory R_ARTIFICE = ResearchCategories.getResearchCategory("ARTIFICE");
		ResearchCategory R_INFUSION = ResearchCategories.getResearchCategory("INFUSION");
		ResearchCategory R_GOLEMANCY = ResearchCategories.getResearchCategory("GOLEMANCY");
		ResearchCategory R_ELDRITCH = ResearchCategories.getResearchCategory("ELDRITCH");
		ResearchCategory R_THAUMADDS = TAReconstructed.RES_CAT;

		new REB().setBaseInfo("TAR_THAUMADDS", "thaumadds", 0, 0, new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/thaumonomicon_icon.png")).setMeta(EnumResearchMeta.HIDDEN, EnumResearchMeta.SPIKY).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":thaumadds.1").setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":thaumadds.2").setRecipes(RecipesTAR.getFakeRecipesPreAll(new Item[]{
				Item.getItemFromBlock(BlocksTAR.THAUMIC_LECTERN),
				Items.QUARTZ,
				Item.getItemFromBlock(BlocksTAR.ESSENTIA_SINK)
		}, InfoTAR.MOD_ID + ":levitation_device", InfoTAR.MOD_ID + ":phantom_ink_phial")).build()).setParents("FIRSTSTEPS").buildAndRegister();
		{
			new REB().setBaseInfo("TAR_ESSENCE_SALT", "essence_salt", -2, -2, AspectUtil.salt(Aspect.AURA)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":essence_salt.1").setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).setConsumedItems(new ItemStack(ItemsTC.crystalEssence)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":essence_salt.2").setConsumedItems(new ItemStack(ItemsTAR.SALT_ESSENCE)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":essence_salt.3").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			{
				new REB().setBaseInfo("TAR_CRYSTAL_CRUSHER", "crystal_crusher", -2, -4, new ItemStack(BlocksTAR.CRYSTAL_CRUSHER)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_crusher.1").setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).setConsumedItems(new ItemStack(ItemsTC.mechanismSimple)).setRequiredCraft(new ItemStack(ItemsTC.mechanismComplex)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_crusher.2").setRecipes(InfoTAR.MOD_ID + ":crystal_crusher").build()).setParents("TAR_ESSENCE_SALT", "METALLURGY@2", "BASEARTIFICE").setRewardItems(new ItemStack(ItemsTC.plate, 3, 0), new ItemStack(ItemsTC.plate, 2, 1)).buildAndRegister();
				new REB().setBaseInfo("TAR_AURA_DISPERSER", "aura_disperser", 0, -2, new ItemStack(BlocksTAR.AURA_DISPERSER)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":aura_disperser.1").setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1), new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1)).setConsumedItems(new ItemStack(ItemsTAR.SALT_ESSENCE)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":aura_disperser.2").setRecipes(InfoTAR.MOD_ID + ":aura_disperser").build()).setParents("BASEARTIFICE", "METALLURGY@2", "TAR_ESSENCE_SALT").buildAndRegister();
				new REB().setBaseInfo("TAR_ESSENTIA_PISTOL", "essentia_pistol", 0, -4, new ItemStack(ItemsTAR.ESSENTIA_PISTOL)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":essentia_pistol.1").setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1), new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1)).setConsumedItems(new ItemStack(Items.BOW)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":essentia_pistol.2").setRecipes(InfoTAR.MOD_ID + ":essentia_pistol").build()).setParents("TAR_AURA_DISPERSER").buildAndRegister();
			}
			new REB().setBaseInfo("TAR_SEAL", "seal", -4, -1, new ItemStack(BlocksTAR.SEAL)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":seal.1").setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).setConsumedItems(new ItemStack(Items.GOLD_INGOT), new ItemStack(Blocks.WOOL)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":seal.2").setRecipes(new ResourceLocation(InfoTAR.MOD_ID, "seal")).build()).setParents("TAR_THAUMADDS").buildAndRegister();
			{
				new REB().setBaseInfo("TAR_SEAL_SYMBOLS", "seal_symbols", -4, -3, new ItemStack(ItemsTAR.SEAL_SYMBOL)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":seal_symbols.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ARTIFICE, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":seal_symbols.2").setRecipes(RecipesTAR.sealSymbolRecipeIDFake).build()).setParents("TAR_SEAL").setMeta(EnumResearchMeta.HIDDEN).buildAndRegister();
				new REB().setBaseInfo("TAR_SEAL_GLOBE", "seal_globe", -4, -10, new ItemStack(ItemsTAR.SEAL_GLOBE)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":seal_globe.1").setRequiredCraft(new ItemStack(ItemsTC.visResonator)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":seal_globe.2").setRecipes(InfoTAR.MOD_ID + ":seal_globe").build()).setParents("TAR_SEAL_SYMBOLS").setMeta(EnumResearchMeta.HIDDEN, EnumResearchMeta.HEX).buildAndRegister();
			}
			new REB().setBaseInfo("TAR_KNOWLEDGE_TOME", "knowledge_tome", -6, -1, new ItemStack(ItemsTAR.KNOWLEDGE_TOME)).setMeta(EnumResearchMeta.ROUND, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":knowledge_tome.1").setConsumedItems(new ItemStack(ItemsTC.thaumonomicon)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":knowledge_tome.2" + (!ConfigsTAR.reusable ? "_noreuse" : "")).setRecipes(InfoTAR.MOD_ID + ":knowledge_tome").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_CHESTER", "chester", -8, -1, new ItemStack(ItemsTAR.CHESTER)).setMeta(EnumResearchMeta.SPIKY).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":chester.1").setConsumedItems(new ItemStack(BlocksTC.logGreatwood)).setRequiredCraft(new ItemStack(ItemsTC.golemBell)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_GOLEMANCY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":chester.2").setRecipes(InfoTAR.MOD_ID + ":chester", InfoTAR.MOD_ID + ":bone_eye").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_VIS_SCRIBING_TOOLS", "vis_scribing_tools", -10, -1, new ItemStack(ItemsTAR.VIS_SCRIBING_TOOLS)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":vis_scribing_tools.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_BASICS, 1), new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setConsumedItems(new ItemStack(ItemsTC.scribingTools)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":vis_scribing_tools.2").setRecipes(InfoTAR.MOD_ID + ":vis_scribing_tools").build()).setParents("TAR_THAUMADDS", "RECHARGEPEDESTAL").buildAndRegister();

			//

			new REB().setBaseInfo("TAR_CRYSTAL_WATER", "crystal_water", -2, 2, FluidUtil.getFilledBucket(new FluidStack(FluidsTAR.CRYSTAL_WATER, Fluid.BUCKET_VOLUME))).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_water.1").setConsumedItems(new ItemStack(ItemsTC.crystalEssence), new ItemStack(Items.WATER_BUCKET)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_water.2").setRecipes(InfoTAR.MOD_ID + ":crystal_water", InfoTAR.MOD_ID + ":mb.crystal_acceleration").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_ENCHANTED_GOLDEN_APPLE", "enchanted_golden_apple", -4, 1, new ItemStack(Items.GOLDEN_APPLE, 1, 1)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":enchanted_golden_apple.1").setConsumedItems(new ItemStack(Items.GOLDEN_APPLE), AspectUtil.crystalEssence(Aspect.DESIRE)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":enchanted_golden_apple.2").setRecipes(InfoTAR.MOD_ID + ":enchanted_golden_apple").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_CRYSTAL_BLOCK", "crystal_block", -6, 1, new ItemStack(BlocksTAR.CRYSTAL_BLOCK)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_block.1").setConsumedItems(AspectUtil.crystalEssence(Aspect.AIR), AspectUtil.crystalEssence(Aspect.WATER), AspectUtil.crystalEssence(Aspect.FIRE), AspectUtil.crystalEssence(Aspect.EARTH), AspectUtil.crystalEssence(Aspect.ORDER), AspectUtil.crystalEssence(Aspect.ENTROPY)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ARTIFICE, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_block.2").setRecipes(RecipesTAR.crystalBlockRecipeIDFake).build()).setParents("TAR_THAUMADDS").buildAndRegister();
			{
				new REB().setBaseInfo("TAR_GROWTH_CHAMBER", "growth_chamber", -6, 3, new ItemStack(BlocksTAR.GROWTH_CHAMBER)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":growth_chamber.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setConsumedItems(AspectUtil.crystalEssence(Aspect.PLANT)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":growth_chamber.2").setRecipes(InfoTAR.MOD_ID + ":growth_chamber").build()).setParents("TAR_CRYSTAL_BLOCK", "CRYSTALFARMER").setMeta(EnumResearchMeta.HIDDEN).buildAndRegister();
			}
			new REB().setBaseInfo("TAR_FRAGNANT_PENDANT", "fragnant_pendant", -8, 1, new ItemStack(ItemsTAR.FRAGNANT_PENDANT)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":fragnant_pendant.1").setRequiredCraft(new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.baubles, 1, 4)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_ELDRITCH, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":fragnant_pendant.2").setRecipes(InfoTAR.MOD_ID + ":odour_powder", InfoTAR.MOD_ID + ":fragnant_pendant").build()).setParents("TAR_THAUMADDS", "BATHSALTS").buildAndRegister();
			new REB().setBaseInfo("TAR_VIS_SEEDS", "vis_seeds", -10, 1, ItemVisSeeds.create(DRACO, 1)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":vis_seeds.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1), new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setConsumedItems(new ItemStack(Items.WHEAT_SEEDS)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":vis_seeds.2").setRecipes(RecipesTAR.visSeedsRecipeIDFake).build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_CRYSTAL_BAG", "crystal_bag", -12, -1, new ItemStack(ItemsTAR.CRYSTAL_BAG)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_bag.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1), new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setConsumedItems(new ItemStack(ItemsTC.focusPouch)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_bag.2").setRecipes(InfoTAR.MOD_ID + ":crystal_bag").build()).setParents("TAR_THAUMADDS", "FOCUSPOUCH", "CRYSTALFARMER").setMeta(EnumResearchMeta.HIDDEN).buildAndRegister();
			new REB().setBaseInfo("TAR_TRAVELLER_BELT", "traveller_belt", -12, 1, new ItemStack(ItemsTAR.TRAVELLER_BELT)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":traveller_belt.1").setRequiredCraft(new ItemStack(ItemsTC.travellerBoots)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":traveller_belt.2").setRecipes(InfoTAR.MOD_ID + ":traveller_belt").build()).setParents("TAR_THAUMADDS", "BOOTSTRAVELLER").setMeta(EnumResearchMeta.HIDDEN).buildAndRegister();

			//

			new REB().setBaseInfo("TAR_BRASS_JAR", "brass_jar", 8, 5, new ItemStack(BlocksTAR.BRASS_JAR)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":brass_jar.1").setRequiredCraft(new ItemStack(BlocksTC.jarNormal)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":brass_jar.2").setRecipes(InfoTAR.MOD_ID + ":brass_jar").build()).setParents("WARDEDJARS").buildAndRegister();
			new REB().setBaseInfo("TAR_THAUMIUM_JAR", "thaumium_jar", 8, 3, new ItemStack(BlocksTAR.THAUMIUM_JAR)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":thaumium_jar.1").setRequiredCraft(new ItemStack(BlocksTAR.BRASS_JAR)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":thaumium_jar.2").setRecipes(InfoTAR.MOD_ID + ":thaumium_jar").build()).setParents("TAR_BRASS_JAR").buildAndRegister();
			new REB().setBaseInfo("TAR_ELDRITCH_JAR", "eldritch_jar", 8, 1, new ItemStack(BlocksTAR.ELDRITCH_JAR)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":eldritch_jar.1").setRequiredCraft(new ItemStack(BlocksTAR.THAUMIUM_JAR)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":eldritch_jar.2").setRecipes(InfoTAR.MOD_ID + ":eldritch_jar").build()).setParents("TAR_THAUMIUM_JAR").buildAndRegister();

			//

			new REB().setBaseInfo("TAR_MITHRILLIUM", "mithrillium", 2, -2, new ItemStack(ItemsTAR.MITHRILLIUM_INGOT)).setMeta(EnumResearchMeta.ROUND, EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithrillium.1").setConsumedItems(new ItemStack(ItemsTC.ingots, 1, 1)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_INFUSION, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithrillium.2").setRecipes(ArrayUtils.addAll(RecipesTAR.getFakeRecipesPreAll(new Item[]{
					Item.getItemFromBlock(BlocksTAR.MITHRILLIUM_BLOCK),
					ItemsTAR.MITHRILLIUM_PLATE,
					ItemsTAR.MITHRILLIUM_NUGGET
			}, InfoTAR.MOD_ID + ":mithrillium_ingot"), InfoTAR.MOD_ID + ":mithrillium_resonator", InfoTAR.MOD_ID + ":blue_bone")).build()).setParents("TAR_THAUMADDS", "INFUSION", "BASEELDRITCH").buildAndRegister();
			{
				new REB().setBaseInfo("TAR_MOB_SUMMONING", "mob_summoning", 4, -1, new ItemStack(BlocksTAR.ENTITY_SUMMONER)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mob_summoning.1").setConsumedItems(AspectUtil.phial(Aspect.LIFE), AspectUtil.phial(Aspect.DEATH)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_INFUSION, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mob_summoning.2").setRequiredCraft(new ItemStack(ItemsTAR.ENTITY_CELL)).setRecipes(InfoTAR.MOD_ID + ":dna_sample").build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mob_summoning.3").setRecipes(InfoTAR.MOD_ID + ":dna_sample", InfoTAR.MOD_ID + ":mob_summoner").build()).setParents("TAR_MITHRILLIUM", "TAR_TOTEMS@2").buildAndRegister();
				new REB().setBaseInfo("TAR_ASPECT_COMBINER", "aspect_combiner", 6, -1, new ItemStack(BlocksTAR.ASPECT_COMBINER)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":aspect_combiner.1").setRequiredCraft(new ItemStack(BlocksTC.centrifuge), new ItemStack(ItemsTAR.MITHRILLIUM_INGOT)).setConsumedItems(AspectUtil.phial(Aspect.EXCHANGE)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":aspect_combiner.2").setRecipes(InfoTAR.MOD_ID + ":aspect_combiner").build()).setParents("CENTRIFUGE", "TAR_MITHRILLIUM").buildAndRegister();
				new REB().setBaseInfo("TAR_MITHRILLIUM_JAR", "mithrillium_jar", 8, -1, new ItemStack(BlocksTAR.MITHRILLIUM_JAR)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithrillium_jar.1").setRequiredCraft(new ItemStack(BlocksTAR.ELDRITCH_JAR), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithrillium_jar.2").setRecipes(InfoTAR.MOD_ID + ":mithrillium_jar").build()).setParents("TAR_ELDRITCH_JAR", "TAR_MITHRILLIUM").buildAndRegister();
				new REB().setBaseInfo("TAR_MITHRILLIUM_SMELTER", "mithrillium_smelter", 10, -1, new ItemStack(BlocksTAR.MITHRILLIUM_SMELTER)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithrillium_smelter.1").setRequiredCraft(new ItemStack(ItemsTAR.MITHRILLIUM_PLATE)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithrillium_smelter.2").setRecipes(InfoTAR.MOD_ID + ":mithrillium_smelter").build()).setParents("TAR_MITHRILLIUM", "ESSENTIASMELTER").buildAndRegister();
				new REB().setBaseInfo("TAR_SHADOW_ENCHANTER", "shadow_enchanter", 12, -1, new ItemStack(BlocksTAR.SHADOW_ENCHANTER)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":shadow_enchanter.1").setConsumedItems(new ItemStack(Blocks.ENCHANTING_TABLE), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":shadow_enchanter.2").setRecipes(InfoTAR.MOD_ID + ":shadow_enchanter").build()).setParents("TAR_MITHRILLIUM", "TAR_DISENCHANT_FABRIC").buildAndRegister();
				new REB().setBaseInfo("TAR_SHADOW_BEAM_STAFF", "shadow_beam_staff", 14, -1, new ItemStack(ItemsTAR.SHADOW_BEAM_STAFF)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":shadow_beam_staff.1").setConsumedItems(new ItemStack(ItemsTC.visResonator)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":shadow_beam_staff.2").setRecipes(InfoTAR.MOD_ID + ":shadow_beam_staff").build()).setParents("TAR_MITHRILLIUM").buildAndRegister();
				new REB().setBaseInfo("TAR_THE_BEHEADER", "the_beheader", 16, -1, new ItemStack(ItemsTAR.THE_BEHEADER)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.ROUND, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":the_beheader.1").addConsumedItem("head").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":the_beheader.2").setRecipes(InfoTAR.MOD_ID + ":the_beheader").build()).setParents("TAR_MITHRILLIUM").buildAndRegister();

				//

				new REB().setBaseInfo("TAR_ADAMINITE", "adaminite", 4, -4, new ItemStack(ItemsTAR.ADAMINITE_INGOT)).setMeta(EnumResearchMeta.ROUND, EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite.1").setRequiredCraft(new ItemStack(ItemsTAR.MITHRILLIUM_INGOT)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setWarp(1).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite.2").setRecipes(RecipesTAR.getFakeRecipesPreAll(new Item[]{
						Item.getItemFromBlock(BlocksTAR.ADAMINITE_BLOCK),
						ItemsTAR.ADAMINITE_PLATE,
						ItemsTAR.ADAMINITE_NUGGET
				}, InfoTAR.MOD_ID + ":adaminite_ingot")).build()).setParents("TAR_MITHRILLIUM").buildAndRegister();
				{
					new REB().setBaseInfo("TAR_AURA_CHARGER", "aura_charger", 6, -3, new ItemStack(BlocksTAR.AURA_CHARGER)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":aura_charger.1").setRequiredCraft(new ItemStack(BlocksTAR.ASPECT_COMBINER), new ItemStack(ItemsTAR.ADAMINITE_NUGGET)).setConsumedItems(AspectUtil.phial(TileAuraCharger.AURA)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_AUROMANCY, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1), new Knowledge(EnumKnowledgeType.THEORY, R_INFUSION, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":aura_charger.2").setRecipes(InfoTAR.MOD_ID + ":aura_charger").build()).setParents("TAR_ASPECT_COMBINER", "TAR_ADAMINITE").buildAndRegister();
					new REB().setBaseInfo("TAR_ADAMINITE_JAR", "adaminite_jar", 8, -3, new ItemStack(BlocksTAR.ADAMINITE_JAR)).setMeta(EnumResearchMeta.HEX, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_jar.1").setRequiredCraft(new ItemStack(BlocksTAR.MITHRILLIUM_JAR), new ItemStack(ItemsTAR.ADAMINITE_PLATE)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_jar.2").setRequiredCraft(new ItemStack(BlocksTAR.ADAMINITE_JAR)).setRecipes(InfoTAR.MOD_ID + ":adaminite_jar").build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_jar.3").setRecipes(InfoTAR.MOD_ID + ":adaminite_jar").build()).setParents("TAR_MITHRILLIUM_JAR", "TAR_ADAMINITE").buildAndRegister();
					new REB().setBaseInfo("TAR_ADAMINITE_SMELTER", "adaminite_smelter", 10, -3, new ItemStack(BlocksTAR.ADAMINITE_SMELTER)).setMeta(EnumResearchMeta.HEX, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_smelter.1").setRequiredCraft(new ItemStack(ItemsTAR.ADAMINITE_PLATE)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_smelter.2").setRecipes(InfoTAR.MOD_ID + ":adaminite_smelter").build()).setParents("TAR_ADAMINITE", "TAR_MITHRILLIUM_SMELTER").buildAndRegister();
					new REB().setBaseInfo("TAR_ADAMINITE_SWORD", "adaminite_sword", 12, -3, new ItemStack(ItemsTAR.ADAMINITE_SWORD)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.ROUND, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_sword.1").setRequiredCraft(new ItemStack(ItemsTC.voidSword)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ELDRITCH, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_sword.2").setRecipes(InfoTAR.MOD_ID + ":adaminite_sword").build()).setParents("TAR_ADAMINITE", "BASEELDRITCH").buildAndRegister();

					//

					new REB().setBaseInfo("TAR_ADAMINITE_FABRIC", "adaminite_fabric", 3, -6, new ItemStack(ItemsTAR.ADAMINITE_FABRIC)).setMeta(EnumResearchMeta.ROUND, EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_fabric.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setConsumedItems(new ItemStack(ItemsTC.fabric)).setWarp(2).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":adaminite_fabric.2").setRecipes(InfoTAR.MOD_ID + ":adaminite_fabric", InfoTAR.MOD_ID + ":adaminite_hood", InfoTAR.MOD_ID + ":adaminite_robe", InfoTAR.MOD_ID + ":adaminite_belt", InfoTAR.MOD_ID + ":adaminite_boots").build()).setParents("TAR_ADAMINITE").buildAndRegister();
					new REB().setBaseInfo("TAR_MITHMINITE", "mithminite", 5, -6, new ItemStack(ItemsTAR.MITHMINITE_INGOT)).setMeta(EnumResearchMeta.ROUND, EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setRequiredCraft(new ItemStack(ItemsTAR.ADAMINITE_INGOT)).setWarp(2).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite.2").setRecipes(RecipesTAR.getFakeRecipesPreAll(new Item[]{
							Item.getItemFromBlock(BlocksTAR.MITHMINITE_BLOCK),
							ItemsTAR.MITHMINITE_PLATE,
							ItemsTAR.MITHMINITE_NUGGET
					}, InfoTAR.MOD_ID + ":mithminite_ingot")).build()).setParents("TAR_ADAMINITE").buildAndRegister();
					{
						new REB().setBaseInfo("TAR_MITHMINITE_JAR", "mithminite_jar", 8, -5, new ItemStack(BlocksTAR.MITHMINITE_JAR)).setMeta(EnumResearchMeta.HEX, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_jar.1").setRequiredCraft(new ItemStack(BlocksTAR.ADAMINITE_JAR), new ItemStack(ItemsTAR.MITHMINITE_PLATE)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_jar.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_jar").build()).setParents("TAR_ADAMINITE_JAR", "TAR_MITHMINITE").buildAndRegister();
						new REB().setBaseInfo("TAR_MITHMINITE_SMELTER", "mithminite_smelter", 10, -5, new ItemStack(BlocksTAR.MITHMINITE_SMELTER)).setMeta(EnumResearchMeta.HEX, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_smelter.1").setRequiredCraft(new ItemStack(BlocksTAR.ADAMINITE_SMELTER), new ItemStack(ItemsTAR.MITHMINITE_PLATE)).setConsumedItems(new ItemStack(ItemsTC.alumentum), new ItemStack(ItemsTC.crystalEssence)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_smelter.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_smelter").build()).setParents("TAR_MITHMINITE", "TAR_ADAMINITE_SMELTER").buildAndRegister();
						new REB().setBaseInfo("TAR_MITHMINITE_SCYTHE", "mithminite_scythe", 12, -5, new ItemStack(ItemsTAR.MITHMINITE_SCYTHE)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.ROUND, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_scythe.1").setRequiredCraft(new ItemStack(ItemsTAR.MITHMINITE_PLATE)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_scythe.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_scythe", InfoTAR.MOD_ID + ":mithminite_blade", InfoTAR.MOD_ID + ":mithminite_handle").build()).setParents("TAR_MITHMINITE").buildAndRegister();

						//

						new REB().setBaseInfo("TAR_MITHMINITE_FABRIC", "mithminite_fabric", 4, -8, new ItemStack(ItemsTAR.MITHMINITE_FABRIC)).setMeta(EnumResearchMeta.ROUND, EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_fabric.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setRequiredCraft(new ItemStack(ItemsTAR.ADAMINITE_FABRIC)).setWarp(2).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_fabric.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_fabric").build()).setParents("TAR_ADAMINITE_FABRIC", "TAR_MITHMINITE").buildAndRegister();
						{
							new REB().setBaseInfo("TAR_MITHMINITE_HOOD", "mithminite_hood", 1, -10, new ItemStack(ItemsTAR.MITHMINITE_HOOD)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_hood.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2)).setConsumedItems(new ItemStack(ItemsTAR.MITHMINITE_FABRIC)).setWarp(5).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_hood.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_hood").build()).setParents("TAR_MITHMINITE_FABRIC", "TAR_SEAL_GLOBE", "ARCANELAMP", "WARP", "JARBRAIN").buildAndRegister();
							new REB().setBaseInfo("TAR_MITHMINITE_ROBE", "mithminite_robe", 3, -10, new ItemStack(ItemsTAR.MITHMINITE_ROBE)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_robe.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2)).setConsumedItems(new ItemStack(ItemsTAR.MITHMINITE_FABRIC)).setWarp(5).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_robe.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_robe").build()).setParents("TAR_MITHMINITE_FABRIC", "SEALGUARD").buildAndRegister();
							new REB().setBaseInfo("TAR_MITHMINITE_BELT", "mithminite_belt", 5, -10, new ItemStack(ItemsTAR.MITHMINITE_BELT)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_belt.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2)).setConsumedItems(new ItemStack(ItemsTAR.MITHMINITE_FABRIC)).setWarp(5).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_belt.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_belt").build()).setParents("TAR_MITHMINITE_FABRIC").buildAndRegister();
							new REB().setBaseInfo("TAR_MITHMINITE_BOOTS", "mithminite_boots", 7, -10, new ItemStack(ItemsTAR.MITHMINITE_BOOTS)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_boots.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2)).setConsumedItems(new ItemStack(ItemsTAR.MITHMINITE_FABRIC)).setWarp(5).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":mithminite_boots.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_boots").build()).setParents("TAR_MITHMINITE_FABRIC", "BOOTSTRAVELLER").buildAndRegister();
						}
					}
				}
			}

			//

			new REB().setBaseInfo("TAR_CAKE", "arcane_cake", 2, 2, new ItemStack(BlocksTAR.CAKE)).setMeta(EnumResearchMeta.HEX, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":arcane_cake.1").setRequiredCraft(new ItemStack(Items.CAKE)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":arcane_cake.2").setRecipes(InfoTAR.MOD_ID + ":arcane_cake").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_TOTEMS", "totems", 4, 1, new ItemStack(BlocksTAR.DAWN_TOTEM), new ItemStack(BlocksTAR.TWILIGHT_TOTEM)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":totems.1").setResearch("!FluxRift").setResearchIcons("thaumcraft:textures/research/r_fluxrift.png").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_AUROMANCY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":totems.2").setResearch("!TARShimmerleaf").setResearchIcons(InfoTAR.MOD_ID + ":textures/research/shimmerleaf.png").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_AUROMANCY, 1)).setRecipes(InfoTAR.MOD_ID + ":twilight_totem").build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":totems.3").setRecipes(InfoTAR.MOD_ID + ":twilight_totem", InfoTAR.MOD_ID + ":dawn_totem").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			{
				new REB().setBaseInfo("TAR_PURIFLOWER", "puriflower", 4, 3, new ItemStack(BlocksTAR.PURIFLOWER)).setMeta(EnumResearchMeta.SPIKY, EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":puriflower.1").setConsumedItems(new ItemStack(BlocksTC.vishroom)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":puriflower.2").setRecipes(InfoTAR.MOD_ID + ":puriflower").build()).setParents("TAR_TOTEMS").buildAndRegister();
			}
			new REB().setBaseInfo("TAR_CRYSTAL_BORE", "crystal_bore", 6, 1, new ItemStack(BlocksTAR.CRYSTAL_BORE)).setMeta(EnumResearchMeta.SPIKY).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_bore.1").setConsumedItems(new ItemStack(ItemsTC.crystalEssence)).setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, R_ALCHEMY, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_GOLEMANCY, 1)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":crystal_bore.2").setRecipes(InfoTAR.MOD_ID + ":crystal_bore").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_FLUX_CONCENTRATOR", "flux_concentrator", 10, 1, new ItemStack(BlocksTAR.FLUX_CONCENTRATOR)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":flux_concentrator.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ARTIFICE, 1), new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1)).setConsumedItems(AspectUtil.crystalEssence(Aspect.FLUX)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":flux_concentrator.2").setRecipes(InfoTAR.MOD_ID + ":flux_concentrator").build()).setParents("TAR_THAUMADDS").buildAndRegister();
			new REB().setBaseInfo("TAR_DISENCHANT_FABRIC", "disenchant_fabric", 12, 1, new ItemStack(ItemsTAR.DISENCHANT_FABRIC)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":disenchant_fabric.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_BASICS, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).setConsumedItems(AspectUtil.crystalEssence(Aspect.EXCHANGE)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":disenchant_fabric.2").setRecipes(InfoTAR.MOD_ID + ":disenchant_fabric").build()).setParents("TAR_THAUMADDS").setMeta(EnumResearchMeta.HIDDEN).buildAndRegister();

			//

			new REB().setBaseInfo("TAR_ELDRITCH", "thaumcraft:BASEELDRITCH", 0, 8, new ItemStack(ItemsTC.voidSeed)).setMeta(EnumResearchMeta.HIDDEN, EnumResearchMeta.ROUND).setStages(new RSB().setText("research.BASEELDRITCH.stage.1").build(), new RSB().setText("research.BASEELDRITCH.stage.2").setRecipes("thaumcraft:voidingot", "thaumcraft:void_stuff").build()).setParents("TAR_THAUMADDS", "BASEELDRITCH").buildAndRegister();
			{
				new REB().setBaseInfo("TAR_VOID_THAUMOMETER", "void_thaumometer", 2, 7, new ItemStack(ItemsTAR.VOID_THAUMOMETER)).setMeta(EnumResearchMeta.HIDDEN, EnumResearchMeta.HEX).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_thaumometer.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1), new Knowledge(EnumKnowledgeType.THEORY, R_ELDRITCH, 1)).setWarp(2).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_thaumometer.2").setRecipes(InfoTAR.MOD_ID + ":void_thaumometer").build()).setParents("TAR_ELDRITCH").buildAndRegister();
				{
					new REB().setBaseInfo("TAR_WORMHOLE_MIRROR", "wormhole_mirror", 2, 5, new ItemStack(ItemsTAR.WORMHOLE_MIRROR)).setMeta(EnumResearchMeta.HIDDEN, EnumResearchMeta.SPIKY).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":wormhole_mirror.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2), new Knowledge(EnumKnowledgeType.THEORY, R_ELDRITCH, 2)).setWarp(3).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":wormhole_mirror.2").setRecipes(InfoTAR.MOD_ID + ":wormhole_mirror").build()).setParents("TAR_VOID_THAUMOMETER", "MIRRORHAND").buildAndRegister();
				}
				new REB().setBaseInfo("TAR_VOID_CROP", "void_crop", 2, 9, new ItemStack(ItemsTAR.VOID_SEED)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_crop.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1), new Knowledge(EnumKnowledgeType.THEORY, R_ELDRITCH, 1)).setConsumedItems(new ItemStack(ItemsTC.voidSeed, 2)).setWarp(2).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_crop.2").setRecipes(InfoTAR.MOD_ID + ":void_crop").build()).setParents("TAR_ELDRITCH").buildAndRegister();
				new REB().setBaseInfo("TAR_VOID_ANVIL", "void_anvil", 4, 7, new ItemStack(BlocksTAR.VOID_ANVIL)).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_anvil.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_ELDRITCH, 1), new Knowledge(EnumKnowledgeType.OBSERVATION, R_THAUMADDS, 1)).setConsumedItems(AspectUtil.crystalEssence(Aspect.ELDRITCH)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_anvil.2").setRecipes(InfoTAR.MOD_ID + ":void_anvil").build()).setParents("TAR_ELDRITCH").setMeta(EnumResearchMeta.HIDDEN).buildAndRegister();

				//

				new REB().setBaseInfo("TAR_VOID_ELEMENTAL_HOE", "void_elemental_hoe", -2, 8, new ItemStack(ItemsTAR.VOID_ELEMENTAL_HOE)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_elemental_hoe.1").setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 1), new Knowledge(EnumKnowledgeType.THEORY, R_ELDRITCH, 1)).setWarp(2).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":void_elemental_hoe.2").setRecipes(InfoTAR.MOD_ID + ":void_elemental_hoe").build()).setParents("TAR_ELDRITCH", "ELEMENTALTOOLS").buildAndRegister();
			}
		}

		new REB().setBaseInfo("!TARBlueWolf", "blue_wolf", -6, -10, new ResourceLocation(InfoTAR.MOD_ID, "textures/research/blue_wolf.png")).setMeta(EnumResearchMeta.HIDDEN, EnumResearchMeta.HEX).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":blue_wolf").build()).buildAndRegister();
	}

	private static void $insertAspects()
	{
		appendAspects("ingotAdaminite", new AspectList().add(CAELES, 16));
		appendAspects(new ItemStack(BlocksTC.jarNormal), new AspectList().add(Aspect.VOID, 4).add(Aspect.ALCHEMY, 8));

		removeAspects(new ItemStack(ItemsTAR.MITHRILLIUM_INGOT), Aspect.TRAP);
		appendAspects("ingotMithrillium", new AspectList().add(CAELES, 6));
		appendAspects(new ItemStack(Items.EGG), new AspectList().add(Aspect.EXCHANGE, 6));

		appendAspects(new ItemStack(Blocks.LEVER), new AspectList().add(IMPERIUM, 5));
		appendAspects(new ItemStack(Blocks.STONE_BUTTON), new AspectList().add(IMPERIUM, 5));
		appendAspects(new ItemStack(Blocks.WOODEN_BUTTON), new AspectList().add(IMPERIUM, 5));
		appendAspects(new ItemStack(BlocksTC.vishroom), new AspectList().add(Aspect.FLUX, 8));
		appendAspects(new ItemStack(Blocks.NOTEBLOCK), new AspectList().add(SONUS, 8));
		appendAspects(new ItemStack(Blocks.JUKEBOX), new AspectList().add(SONUS, 12));
		appendAspects(new ItemStack(BlocksTC.arcaneEar), new AspectList().add(SONUS, 12));
		appendAspects(new ItemStack(BlocksTC.levitator), new AspectList().add(FLUCTUS, 12).add(VENTUS, 20));
		appendAspects(new ItemStack(Items.WATER_BUCKET), new AspectList().add(FLUCTUS, 4));
		appendAspects(new ItemStack(Blocks.CHEST), new AspectList().add(Aspect.VOID, 6));
		appendAspects(new ItemStack(Blocks.TNT), new AspectList().add(EXITIUM, 50));
		appendAspects(new ItemStack(Items.GUNPOWDER), new AspectList().add(EXITIUM, 10));
		appendAspects(new ItemStack(Blocks.COBBLESTONE), new AspectList().add(EXITIUM, 1));
		appendAspects(new ItemStack(Blocks.NETHERRACK), new AspectList().add(EXITIUM, 1));
		appendAspects(new ItemStack(Blocks.DRAGON_EGG), new AspectList().add(DRACO, 100));
		appendAspects(new ItemStack(Items.DRAGON_BREATH), new AspectList().add(DRACO, 25));
		appendAspects(new ItemStack(ItemsTC.thaumometer), new AspectList().add(VISUM, 20));
		appendAspects(new ItemStack(Items.CARROT), new AspectList().add(VISUM, 10));
		appendAspects(new ItemStack(Blocks.NETHER_BRICK), new AspectList().add(INFERNUM, 15));
		appendAspects(new ItemStack(Blocks.NETHER_BRICK_STAIRS), new AspectList().add(INFERNUM, 10));
		appendAspects(new ItemStack(Blocks.NETHER_BRICK_FENCE), new AspectList().add(INFERNUM, 5));
		appendAspects(new ItemStack(Blocks.PISTON), new AspectList().add(FLUCTUS, 10).add(VENTUS, 10));
		appendAspects(new ItemStack(BlocksTC.logGreatwood), new AspectList().add(Aspect.MAGIC, 10));
		appendAspects(new ItemStack(ItemsTAR.ZEITH_FUR), new AspectList().add(Aspect.MIND, 15).add(DRACO, 15).add(Aspect.LIFE, 15).add(CAELES, 2).add(Aspect.AURA, 20));
		appendAspects(new ItemStack(Items.SKULL, 1, 5), new AspectList().add(DRACO, 30));
		appendAspects(new ItemStack(Items.FEATHER), new AspectList().add(VENTUS, 5));
		appendAspects(new ItemStack(Items.ROTTEN_FLESH), new AspectList().add(Aspect.DEATH, 2));

		for(Aspect a : Aspect.aspects.values())
			CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(AspectUtil.crystalBlock(a)), new AspectList().add(a, 6).add(Aspect.MAGIC, 2));

		// Add sonus to music discs
		ForgeRegistries.ITEMS.getValuesCollection().forEach(i ->
		{
			if(i instanceof ItemRecord)
				appendAspects(new ItemStack(i), new AspectList().add(SONUS, 20));
		});

		String prefix = addIfPresent("thaumictinkerer:ichor", new AspectList().add(CAELES, 5), "");
		prefix = addIfPresent("thaumictinkerer:ichorium", new AspectList().add(CAELES, 8), prefix);
		prefix = addIfPresent("draconicevolution:draconic_ingot", new AspectList().add(CAELES, 6).add(DRACO, 18), prefix);
		prefix = addIfPresent("draconicevolution:dragon_heart", new AspectList().add(CAELES, 20).add(DRACO, 100), prefix);
		prefix = addIfPresent("draconicevolution:draconic_block", new AspectList().add(CAELES, 54).add(DRACO, 18 * 9), prefix);
		prefix = addIfPresent("draconicevolution:chaos_shard", new AspectList().add(CAELES, 16).add(EXITIUM, 96), prefix);

		if(Loader.isModLoaded("thaumicbases"))
		{
			Aspect iter = Aspect.getAspect("iter");
			List<TwoTuple<ItemStack, Integer>> iters = new ArrayList<>();

			ForgeRegistries.ITEMS.getValuesCollection().forEach(i ->
			{
				if(i.getRegistryName().toString().contains("boat"))
					iters.add(new TwoTuple<ItemStack, Integer>(new ItemStack(i), 5));
			});

			for(TwoTuple<ItemStack, Integer> t : iters)
			{
				AspectList al = ThaumcraftCraftingManager.getObjectTags(t.get1());
				if(al == null || al.getAmount(iter) <= 0)
					appendAspects(t.get1(), new AspectList().add(iter, t.get2()));
			}
		}
	}

	private static void appendAspects(String oreDict, AspectList toAdd)
	{
		List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDict);
		if(toAdd == null)
			toAdd = new AspectList();
		if(ores != null && ores.size() > 0)
			for(ItemStack ore : ores)
				try
				{
					ItemStack oc = ore.copy();
					oc.setCount(1);
					appendAspects(oc, toAdd);
				} catch(Exception oc)
				{
				}
	}

	private static void appendAspects(ItemStack stack, AspectList toAdd)
	{
		toAdd = toAdd.copy();

		// Finds item's aspects, and if there are any, adds them to appended
		// aspects
		{
			AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
			if(al != null)
				toAdd = toAdd.add(al);
		}

		CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(stack), toAdd);
	}

	private static void removeAspects(ItemStack stack, Aspect... aspects)
	{
		AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
		if(al != null)
		{
			for(Aspect a : aspects)
				al.remove(a);
			CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(stack), al);
		}
	}

	private static String addIfPresent(String item, AspectList al, String prefix)
	{
		Item it = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(item));
		if(it != null)
		{
			List<String> fullAspectList = Arrays.stream(al.getAspectsSortedByAmount()).map(a -> al.getAmount(a) + "x " + a.getName()).collect(Collectors.toList());
			TAReconstructed.LOG.info("I " + prefix + "found " + item + " and I added some aspects to it! " + Joiner.on(", ").join(fullAspectList));
			if(prefix.isEmpty())
				prefix = "also ";
			appendAspects(new ItemStack(it), al);
		}
		return prefix;
	}

	private static class RAB
			extends ResearchAddendumBuilder
	{
	}

	private static class RSB
			extends ResearchStageBuilder
	{
	}

	private static class REB
			extends ResearchEntryBuilder
	{
		public ResearchEntryBuilder setBaseInfo(String key, String name, int x, int y, Object... icons)
		{
			if(name.contains("thaumcraft:"))
				return super.setBaseInfo(key, "THAUMADDITIONS", "research." + name.replace("thaumcraft:", "") + ".title", x, y, icons);
			return super.setBaseInfo(key, "THAUMADDITIONS", "research_name." + InfoTAR.MOD_ID + ":" + name, x, y, icons);
		}
	}

	private static Method addResearchToCategory = null;

	public static void addResearchToCategory(ResearchEntry ri)
	{
		if(addResearchToCategory == null)
			try
			{
				addResearchToCategory = ResearchManager.class.getDeclaredMethod("addResearchToCategory", ResearchEntry.class);
				addResearchToCategory.setAccessible(true);
			} catch(NoSuchMethodException | SecurityException e)
			{
				TAReconstructed.LOG.error(e);
			}

		try
		{
			addResearchToCategory.invoke(null, ri);
		} catch(Throwable e)
		{
			TAReconstructed.LOG.error(e);
		}
	}

	private static void $()
	{
	}
}