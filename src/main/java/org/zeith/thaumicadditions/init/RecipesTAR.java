package org.zeith.thaumicadditions.init;

import com.zeitheron.hammercore.utils.ArrayHelper;
import com.zeitheron.hammercore.utils.OnetimeCaller;
import com.zeitheron.hammercore.utils.recipes.helper.RecipeRegistry;
import com.zeitheron.hammercore.utils.recipes.helper.RegisterRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.api.RecipesFluxConcentrator;
import org.zeith.thaumicadditions.api.blueprint.BlueprintBuilder;
import org.zeith.thaumicadditions.api.utils.ILogAxisObtainer;
import org.zeith.thaumicadditions.compat.ITARC;
import org.zeith.thaumicadditions.config.ConfigsTAR;
import org.zeith.thaumicadditions.items.ItemSealSymbol;
import org.zeith.thaumicadditions.items.seed.ItemVisSeeds;
import org.zeith.thaumicadditions.recipes.*;
import org.zeith.thaumicadditions.recipes.ingr.NBTRespectfulIngredient;
import org.zeith.thaumicadditions.tiles.TileAuraCharger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.*;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.world.taint.BlockTaintLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@RegisterRecipes(modid = InfoTAR.MOD_ID)
public class RecipesTAR
		extends RecipeRegistry
{
	public static final ResourceLocation crystalBlockRecipeIDFake = new ResourceLocation(InfoTAR.MOD_ID, "crystal_block_recipes_all");
	public static final List<ResourceLocation> crystalBlockRecipes = new ArrayList<>();
	public static final ResourceLocation visSeedsRecipeIDFake = new ResourceLocation(InfoTAR.MOD_ID, "vis_seeds_recipes_all");
	public static final List<ResourceLocation> visSeedsRecipes = new ArrayList<>();
	public static final ResourceLocation sealSymbolRecipeIDFake = new ResourceLocation(InfoTAR.MOD_ID, "seal_symbol_recipes_all");
	public static final List<ResourceLocation> sealSymbolRecipes = new ArrayList<>();
	public static final Map<Item, List<ResourceLocation>> FAKE_RECIPE_MAP = new HashMap<>();
	public static OnetimeCaller init, postInit;
	static ResourceLocation defaultGroup = new ResourceLocation("");
	private static RecipesTAR instance;
	
	{
		instance = this;
		init = new OnetimeCaller(this::init);
		postInit = new OnetimeCaller(this::postInit);
	}
	
	public static void addCrucibleRecipe(String path, String research, ItemStack output, Object catalyst, AspectList aspects)
	{
		ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(InfoTAR.MOD_ID, path), new CrucibleRecipe(research, output, catalyst, aspects));
	}
	
	public static void addInfusionRecipe(String path, Object output, String research, int instability, Object catalyst, AspectList aspects, Object... inputs)
	{
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(InfoTAR.MOD_ID, path), new InfusionRecipe(research, output, instability, aspects, catalyst, inputs));
	}
	
	public static void addShapedArcaneRecipe(String path, String res, int vis, AspectList crystals, ItemStack result, Object... recipe)
	{
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(InfoTAR.MOD_ID, path), new ShapedArcaneRecipe(defaultGroup, res, vis, crystals, result, recipe));
	}
	
	public static void addShapedArcaneRecipe(String path, String res, int vis, AspectList crystals, Item result, Object... recipe)
	{
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(InfoTAR.MOD_ID, path), new ShapedArcaneRecipe(defaultGroup, res, vis, crystals, result, recipe));
	}
	
	public static void addShapedArcaneRecipe(String path, String res, int vis, AspectList crystals, Block result, Object... recipe)
	{
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(InfoTAR.MOD_ID, path), new ShapedArcaneRecipe(defaultGroup, res, vis, crystals, result, recipe));
	}
	
	public static String[] getFakeRecipesFor(Item it)
	{
		List<ResourceLocation> locs = FAKE_RECIPE_MAP.get(it);
		if(locs == null)
			FAKE_RECIPE_MAP.put(it, locs = new ArrayList<>());
		return locs.stream().map(ResourceLocation::toString).collect(Collectors.toList()).toArray(new String[locs.size()]);
	}
	
	public static String[] getFakeRecipes(Item it, String... appends)
	{
		return ArrayHelper.merge(getFakeRecipesFor(it), appends);
	}
	
	public static String[] getFakeRecipesPreAll(Item[] items, String... prepends)
	{
		for(Item i : items)
			prepends = getFakeRecipesPre(i, prepends);
		return prepends;
	}
	
	public static String[] getFakeRecipesPre(Item it, String... prepends)
	{
		return ArrayHelper.merge(prepends, getFakeRecipesFor(it));
	}
	
	private static final Map<IProperty<?>, ILogAxisObtainer<?>> AXIS_OBTAINER = new ConcurrentHashMap<>();
	
	public static <T extends Comparable<T>> void registerAxisObtainer(IProperty<T> property, Function<T, EnumAxis> axis)
	{
		AXIS_OBTAINER.computeIfAbsent(property, k -> new ILogAxisObtainer<T>()
		{
			@Override
			public IProperty<T> property()
			{
				return property;
			}
			
			@Override
			public EnumAxis from(T t)
			{
				if(t == null) return null;
				return axis.apply(t);
			}
		});
	}
	
	private void init()
	{
		registerAxisObtainer(BlockLog.LOG_AXIS, UnaryOperator.identity());
		registerAxisObtainer(BlockPillar.FACING, f -> EnumAxis.values()[f.getAxis().ordinal()]);
		
		infusing();
		arcaneCrafting();
		crucible();
		multiblock();
		
		TAReconstructed.arcs.forEach(ITARC::addArcaneRecipes);
		
		RecipesFluxConcentrator.handle(Blocks.BROWN_MUSHROOM.getDefaultState(), RecipesFluxConcentrator.output(BlocksTC.vishroom.getDefaultState()));
		RecipesFluxConcentrator.handle(Blocks.RED_MUSHROOM.getDefaultState(), RecipesFluxConcentrator.output(BlocksTC.vishroom.getDefaultState()));
		RecipesFluxConcentrator.handle(Blocks.PUMPKIN.getDefaultState(), RecipesFluxConcentrator.output(BlocksTAR.TAINTKIN.getDefaultState()));
		RecipesFluxConcentrator.handle(Blocks.LIT_PUMPKIN.getDefaultState(), RecipesFluxConcentrator.output(BlocksTAR.TAINTKIN_LIT.getDefaultState()));
		
		ForgeRegistries.BLOCKS.getValuesCollection()
				.stream()
				.filter(b -> b instanceof BlockLog)
				.flatMap(log -> log.getBlockState().getValidStates().stream())
				.forEach(log ->
				{
					Set<IProperty<?>> keys = new HashSet<>(log.getPropertyKeys());
					keys.retainAll(AXIS_OBTAINER.keySet());
					
					EnumAxis axis = EnumAxis.NONE;
					
					if(!keys.isEmpty())
					{
						for(IProperty<?> key : keys)
						{
							ILogAxisObtainer<?> obtainer = AXIS_OBTAINER.get(key);
							if(obtainer == null) continue;
							Optional<EnumAxis> axisOpt = obtainer.tryObtain(log);
							if(!axisOpt.isPresent()) continue;
							axis = axisOpt.orElse(axis);
						}
					}
					
					if(axis == EnumAxis.NONE) axis = EnumAxis.Y;
					
					Axis value = Axis.values()[axis.ordinal()];
					
					RecipesFluxConcentrator.handle(log, RecipesFluxConcentrator.output(BlocksTC.taintLog.getDefaultState().withProperty(BlockTaintLog.AXIS, value)));
				});
	}
	
	private void postInit()
	{
		postAspects();
	}
	
	@Override
	public void crafting()
	{
		shaped(Items.QUARTZ, "sss", "sss", "sss", 's', new ItemStack(ItemsTC.nuggets, 1, 9));
		
		shapeless(ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET, ItemsTAR.MITHRILLIUM_NUGGET);
		shapeless(ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET, ItemsTAR.ADAMINITE_NUGGET);
		shapeless(ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET, ItemsTAR.MITHMINITE_NUGGET);
		
		shapeless(new ItemStack(ItemsTAR.MITHRILLIUM_NUGGET, 9), ItemsTAR.MITHRILLIUM_INGOT);
		shapeless(new ItemStack(ItemsTAR.ADAMINITE_NUGGET, 9), ItemsTAR.ADAMINITE_INGOT);
		shapeless(new ItemStack(ItemsTAR.MITHMINITE_NUGGET, 9), ItemsTAR.MITHMINITE_INGOT);
		
		shapeless(BlocksTAR.MITHRILLIUM_BLOCK, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT, ItemsTAR.MITHRILLIUM_INGOT);
		shapeless(BlocksTAR.ADAMINITE_BLOCK, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT, ItemsTAR.ADAMINITE_INGOT);
		shapeless(BlocksTAR.MITHMINITE_BLOCK, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT, ItemsTAR.MITHMINITE_INGOT);
		
		shapeless(new ItemStack(ItemsTAR.MITHRILLIUM_INGOT, 9), BlocksTAR.MITHRILLIUM_BLOCK);
		shapeless(new ItemStack(ItemsTAR.ADAMINITE_INGOT, 9), BlocksTAR.ADAMINITE_BLOCK);
		shapeless(new ItemStack(ItemsTAR.MITHMINITE_INGOT, 9), BlocksTAR.MITHMINITE_BLOCK);
		
		shaped(new ItemStack(BlocksTAR.CHISELED_AMBER_BLOCK, 4), " a ", "a a", " a ", 'a', BlocksTC.amberBlock);
		
		shapeless(new ItemStack(BlocksTAR.TAINTWOOD_PLANK, 4), new ItemStack(BlocksTC.taintLog));
		
		if(ConfigsTAR.rewritable)
			shapeless(new ItemStack(ItemsTAR.KNOWLEDGE_TOME), new ItemStack(ItemsTAR.KNOWLEDGE_TOME));
		shaped(new ItemStack(ItemsTAR.MITHRILLIUM_PLATE, 3), "ppp", 'p', ItemsTAR.MITHRILLIUM_INGOT);
		shaped(new ItemStack(ItemsTAR.ADAMINITE_PLATE, 3), "ppp", 'p', ItemsTAR.ADAMINITE_INGOT);
		shaped(new ItemStack(ItemsTAR.MITHMINITE_PLATE, 3), "ppp", 'p', ItemsTAR.MITHMINITE_INGOT);
		shaped(new ItemStack(BlocksTAR.THAUMIC_LECTERN), "mtm", "www", " w ", 'w', new ItemStack(BlocksTC.slabGreatwood), 'm', "ingotThaumium", 't', new ItemStack(ItemsTC.thaumonomicon));
		shaped(new ItemStack(BlocksTAR.ESSENTIA_SINK), "lvl", "igt", "isi", 'l', new ItemStack(ItemsTC.jarBrace), 'v', new ItemStack(BlocksTC.tubeValve), 'i', "plateIron", 'g', new ItemStack(BlocksTC.tableWood), 't', new ItemStack(BlocksTC.tube), 's', new ItemStack(BlocksTC.slabGreatwood));
		
		shaped(new ItemStack(BlocksTAR.IRON_FRAMED_GREATWOOD, 5), "npn", "ppp", "npn", 'n', "nuggetIron", 'p', new ItemStack(BlocksTC.plankGreatwood));
		shaped(new ItemStack(BlocksTAR.BRASS_PLATED_SILVERWOOD, 5), "npn", "ppp", "npn", 'n', "nuggetBrass", 'p', new ItemStack(BlocksTC.plankSilverwood));
		shaped(new ItemStack(BlocksTAR.AMBER_LAMP, 3), "bnb", "aaa", "bnb", 'b', new ItemStack(Blocks.IRON_BARS), 'n', "nitor", 'a', new ItemStack(BlocksTC.amberBlock));
		shaped(new ItemStack(BlocksTAR.CHISELED_GREATWOOD, 3), " s ", "sps", " s ", 's', new ItemStack(BlocksTC.slabGreatwood), 'p', new ItemStack(BlocksTC.plankGreatwood));
		
		recipe(new RecipeMixSalts().setRegistryName(new ResourceLocation(getMod(), "essence_salt.mix")));
		recipe(new RecipeApplySalt().setRegistryName(new ResourceLocation(getMod(), "essence_salt.apply")));
		recipe(new RecipeClearSalt().setRegistryName(new ResourceLocation(getMod(), "essence_salt.remove")));
		recipe(new RecipePaintSeal());
		recipe(new RecipeApplyPhantomInk().setRegistryName(new ResourceLocation(getMod(), "phantom_ink.apply")));
		recipe(new RecipeRemovePhantomInk().setRegistryName(new ResourceLocation(getMod(), "phantom_ink.remove")));
		recipe(new RecipeDisenchant().setRegistryName(new ResourceLocation(getMod(), "disenchant")));
	}
	
	private void infusing()
	{
		Ingredient primordialPearl = Ingredient.fromItem(ItemsTC.primordialPearl);
		
		addInfusionRecipe("mithrillium_ingot", new ItemStack(ItemsTAR.MITHRILLIUM_INGOT), "TAR_MITHRILLIUM", 5, new ItemStack(ItemsTC.ingots, 1, 1), new AspectList().add(Aspect.CRYSTAL, 30).add(Aspect.ENERGY, 15).add(Aspect.ELDRITCH, 10).add(Aspect.METAL, 30)
				.add(Aspect.MAGIC, 10), new ItemStack(ItemsTC.amber), new ItemStack(ItemsTC.alumentum), new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.amber), new ItemStack(ItemsTC.alumentum), new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus));
		addInfusionRecipe("adaminite_ingot", new ItemStack(ItemsTAR.ADAMINITE_INGOT), "TAR_ADAMINITE", 10, new ItemStack(ItemsTAR.MITHRILLIUM_INGOT), new AspectList().add(Aspect.LIFE, 100).add(Aspect.ALCHEMY, 30).add(Aspect.EXCHANGE, 40).add(Aspect.METAL, 40).add(Aspect.SOUL, 120).add(Aspect.MAGIC, 100).add(KnowledgeTAR.DRACO, 20).add(KnowledgeTAR.INFERNUM, 100)
				.add(KnowledgeTAR.VISUM, 20), new ItemStack(Items.NETHER_STAR), new ItemStack(ItemsTC.fabric), primordialPearl, new ItemStack(Items.NETHER_STAR));
		addInfusionRecipe("mithminite_ingot", new ItemStack(ItemsTAR.MITHMINITE_INGOT), "TAR_MITHMINITE", 8, new ItemStack(ItemsTAR.ADAMINITE_INGOT), new AspectList().add(KnowledgeTAR.CAELES, 10).add(Aspect.METAL, 60).add(Aspect.LIFE, 90).add(Aspect.MAGIC, 120), new ItemStack(ItemsTAR.MITHRILLIUM_INGOT), new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTAR.MITHRILLIUM_INGOT), new ItemStack(ItemsTC.quicksilver));
		addInfusionRecipe("mithminite_jar", new ItemStack(BlocksTAR.MITHMINITE_JAR), "TAR_MITHMINITE_JAR", 7, new ItemStack(BlocksTAR.ADAMINITE_JAR), new AspectList().add(KnowledgeTAR.CAELES, 16).add(Aspect.ALCHEMY, 32).add(Aspect.EXCHANGE, 10).add(Aspect.WATER, 40).add(Aspect.VOID, 30), new ItemStack(ItemsTAR.MITHMINITE_INGOT), new ItemStack(ItemsTC.amber), new ItemStack(ItemsTC.alumentum), new ItemStack(BlocksTC.jarVoid));
		addInfusionRecipe("aspect_combiner", new ItemStack(BlocksTAR.ASPECT_COMBINER), "TAR_ASPECT_COMBINER", 6, new ItemStack(BlocksTC.centrifuge), new AspectList().add(Aspect.EXCHANGE, 16).add(Aspect.ENTROPY, 30).add(Aspect.ALCHEMY, 20)
				.add(Aspect.MECHANISM, 15), new ItemStack(ItemsTAR.MITHRILLIUM_NUGGET), new ItemStack(ItemsTAR.MITHRILLIUM_NUGGET), new ItemStack(ItemsTAR.MITHRILLIUM_NUGGET), new ItemStack(ItemsTC.mechanismComplex), AspectUtil.crystalEssence(Aspect.EXCHANGE), new ItemStack(ItemsTC.filter), "plateBrass", new ItemStack(ItemsTC.alumentum));
		addInfusionRecipe("aura_charger", new ItemStack(BlocksTAR.AURA_CHARGER), "TAR_AURA_CHARGER", 8, new ItemStack(BlocksTAR.ASPECT_COMBINER), new AspectList().add(TileAuraCharger.AURA, 20).add(Aspect.MAGIC, 20).add(Aspect.ENERGY, 40), AspectUtil.crystalEssence(TileAuraCharger.AURA), new ItemStack(ItemsTAR.ADAMINITE_NUGGET), new ItemStack(ItemsTAR.ADAMINITE_NUGGET), new ItemStack(ItemsTC.mechanismComplex), new ItemStack(ItemsTAR.ADAMINITE_NUGGET), new ItemStack(ItemsTAR.ADAMINITE_NUGGET));
		addInfusionRecipe("crystal_crusher", new ItemStack(BlocksTAR.CRYSTAL_CRUSHER), "TAR_CRYSTAL_CRUSHER", 3, new ItemStack(ItemsTC.mechanismComplex), new AspectList().add(Aspect.CRAFT, 20).add(KnowledgeTAR.EXITIUM, 20)
				.add(Aspect.TOOL, 20), AspectUtil.crystalEssence(Aspect.AIR), AspectUtil.crystalEssence(Aspect.EARTH), AspectUtil.crystalEssence(Aspect.FIRE), AspectUtil.crystalEssence(Aspect.WATER), AspectUtil.crystalEssence(Aspect.ORDER), AspectUtil.crystalEssence(Aspect.ENTROPY), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(BlocksTC.slabArcaneStone), new ItemStack(BlocksTC.slabArcaneStone), new ItemStack(BlocksTC.slabArcaneStone), new ItemStack(ItemsTAR.SALT_ESSENCE), new ItemStack(ItemsTAR.SALT_ESSENCE));
		addInfusionRecipe("aura_disperser", new ItemStack(BlocksTAR.AURA_DISPERSER), "TAR_AURA_DISPERSER", 4, new ItemStack(Blocks.DISPENSER), new AspectList().add(KnowledgeTAR.FLUCTUS, 30).add(Aspect.AURA, 10).add(Aspect.ALCHEMY, 20)
				.add(KnowledgeTAR.VENTUS, 50), new ItemStack(ItemsTC.mechanismComplex), AspectUtil.salt(Aspect.AURA), AspectUtil.salt(Aspect.ALCHEMY), new ItemStack(BlocksTC.shimmerleaf), new ItemStack(ItemsTC.morphicResonator), AspectUtil.salt(KnowledgeTAR.FLUCTUS), AspectUtil.salt(KnowledgeTAR.DRACO), "nitor");
		addInfusionRecipe("crystal_bore", new ItemStack(BlocksTAR.CRYSTAL_BORE), "TAR_CRYSTAL_BORE", 5, new ItemStack(ItemsTC.morphicResonator), new AspectList().add(KnowledgeTAR.EXITIUM, 20).add(Aspect.EARTH, 10).add(Aspect.ENTROPY, 30), new ItemStack(BlocksTC.stoneArcane), new ItemStack(ItemsTC.plate), new ItemStack(BlocksTC.stoneArcane), new ItemStack(ItemsTC.plate), new ItemStack(BlocksTC.stoneArcane), new ItemStack(ItemsTC.mechanismComplex));
		addInfusionRecipe("enchanted_golden_apple", new ItemStack(Items.GOLDEN_APPLE, 1, 1), "TAR_ENCHANTED_GOLDEN_APPLE", 6, new ItemStack(Items.GOLDEN_APPLE, 1, 0), new AspectList().add(Aspect.DESIRE, 10).add(Aspect.LIFE, 5)
				.add(KnowledgeTAR.VISUM, 20), new ItemStack(Blocks.GOLD_BLOCK), AspectUtil.crystalEssence(Aspect.DESIRE), new ItemStack(Blocks.GOLD_BLOCK), AspectUtil.crystalEssence(Aspect.DESIRE), new ItemStack(Blocks.GOLD_BLOCK), AspectUtil.crystalEssence(Aspect.DESIRE), new ItemStack(Blocks.GOLD_BLOCK), AspectUtil.crystalEssence(Aspect.DESIRE));
		addInfusionRecipe("mob_summoner", new ItemStack(BlocksTAR.ENTITY_SUMMONER), "TAR_MOB_SUMMONING", 5, new ItemStack(BlocksTC.metalBlockThaumium), new AspectList().add(KnowledgeTAR.DRACO, 10).add(KnowledgeTAR.IMPERIUM, 50).add(KnowledgeTAR.EXITIUM, 10).add(Aspect.LIFE, 15).add(Aspect.SOUL, 20).add(Aspect.MAN, 30).add(Aspect.BEAST, 25)
				.add(KnowledgeTAR.INFERNUM, 100), new ItemStack(ItemsTAR.ENTITY_CELL), new ItemStack(ItemsTC.mechanismSimple), new ItemStack(ItemsTC.filter), new ItemStack(ItemsTC.alumentum), "nitor", new ItemStack(BlocksTAR.TWILIGHT_TOTEM));
		addInfusionRecipe("puriflower", new ItemStack(BlocksTAR.PURIFLOWER), "TAR_PURIFLOWER", 2, new ItemStack(BlocksTAR.DAWN_TOTEM), new AspectList().add(Aspect.AURA, 10).add(KnowledgeTAR.IMPERIUM, 15).add(Aspect.ORDER, 30).add(Aspect.PLANT, 60), BlocksTC.shimmerleaf, BlocksTC.vishroom, AspectUtil.crystalEssence(Aspect.PLANT), BlocksTC.vishroom);
		addInfusionRecipe("growth_chamber", new ItemStack(BlocksTAR.GROWTH_CHAMBER), "TAR_GROWTH_CHAMBER", 3, BlocksTAR.CRYSTAL_BLOCK, new AspectList().add(Aspect.ORDER, 20).add(Aspect.MECHANISM, 15).add(KnowledgeTAR.IMPERIUM, 10), ItemsTC.amber, new ItemStack(ItemsTC.plate, 1, 2), ItemsTC.morphicResonator, new ItemStack(ItemsTC.plate, 1, 2), ItemsTC.visResonator, new ItemStack(ItemsTC.plate, 1, 2), ItemsTC.mechanismComplex, new ItemStack(ItemsTC.plate, 1, 2));
		addInfusionRecipe("flux_concentrator", new ItemStack(BlocksTAR.FLUX_CONCENTRATOR), "TAR_FLUX_CONCENTRATOR", 2, new ItemStack(ItemsTC.mechanismComplex), new AspectList().add(Aspect.FLUX, 100).add(Aspect.EXCHANGE, 50), new ItemStack(ItemsTC.filter), new ItemStack(ItemsTC.quicksilver), new ItemStack(BlocksTC.bellows), new ItemStack(ItemsTC.quicksilver));
		addInfusionRecipe("arcane_cake", new ItemStack(BlocksTAR.CAKE), "TAR_CAKE", 5, new ItemStack(Items.CAKE), new AspectList().add(Aspect.LIFE, 20).add(Aspect.DESIRE, 30).add(Aspect.MAGIC, 15).add(KnowledgeTAR.VENTUS, 10), new ItemStack(ItemsTC.primordialPearl), new ItemStack(ItemsTC.quicksilver), new ItemStack(ItemsTC.salisMundus), AspectUtil.crystalEssence(KnowledgeTAR.IMPERIUM));
		addInfusionRecipe("chester", new ItemStack(ItemsTAR.CHESTER), "TAR_CHESTER", 2, new ItemStack(BlocksTC.hungryChest), new AspectList().add(KnowledgeTAR.IMPERIUM, 80).add(Aspect.LIFE, 40).add(Aspect.VOID, 20)
				.add(Aspect.MECHANISM, 10), new ItemStack(BlocksTC.plankGreatwood), "ingotThaumium", new ItemStack(BlocksTC.plankGreatwood), new ItemStack(ItemsTC.brain), new ItemStack(BlocksTC.plankGreatwood), new ItemStack(ItemsTC.morphicResonator), new ItemStack(BlocksTC.plankGreatwood), "ingotThaumium");
		addInfusionRecipe("mithrillium_resonator", new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), "TAR_MITHRILLIUM", 5, new ItemStack(ItemsTC.morphicResonator), AspectUtil.primals(10).add(KnowledgeTAR.IMPERIUM, 10), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), new ItemStack(ItemsTC.mechanismComplex), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), new ItemStack(ItemsTC.mechanismComplex));
		addInfusionRecipe("void_thaumometer", new ItemStack(ItemsTAR.VOID_THAUMOMETER), "TAR_VOID_THAUMOMETER", 4, new ItemStack(ItemsTC.thaumometer), AspectUtil.primals(10).add(Aspect.ELDRITCH, 40).add(Aspect.MIND, 20), new ItemStack(ItemsTC.ingots, 1, 1), new ItemStack(ItemsTC.ingots, 1, 1), new ItemStack(ItemsTC.ingots, 1, 1), new ItemStack(ItemsTC.ingots, 1, 1));
		addInfusionRecipe("wormhole_mirror", new ItemStack(ItemsTAR.WORMHOLE_MIRROR), "TAR_WORMHOLE_MIRROR", 6, new ItemStack(ItemsTC.handMirror), new AspectList().add(Aspect.ELDRITCH, 50).add(KnowledgeTAR.IMPERIUM, 100).add(KnowledgeTAR.VISUM, 80), new ItemStack(ItemsTAR.VOID_THAUMOMETER), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3));
		
		addInfusionRecipe("void_elemental_hoe", new ItemStack(ItemsTAR.VOID_ELEMENTAL_HOE), "TAR_VOID_ELEMENTAL_HOE", 6, new ItemStack(ItemsTC.elementalHoe), new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.PLANT, 80).add(Aspect.LIFE, 100), primordialPearl, new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3));
		addInfusionRecipe("void_elemental_shovel", ItemsTAR.VOID_ELEMENTAL_SHOVEL.defInst(), "TAR_VOID_ELEMENTAL_SHOVEL", 6, new ItemStack(ItemsTC.elementalShovel), new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.CRAFT, 100).add(Aspect.EARTH, 150), primordialPearl, new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3));
		addInfusionRecipe("void_elemental_axe", ItemsTAR.VOID_ELEMENTAL_AXE.defInst(), "TAR_VOID_ELEMENTAL_AXE", 6, new ItemStack(ItemsTC.elementalAxe), new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.WATER, 100).add(Aspect.PLANT, 150), primordialPearl, new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3));
		addInfusionRecipe("void_elemental_pickaxe", ItemsTAR.VOID_ELEMENTAL_PICKAXE.defInst(), "TAR_VOID_ELEMENTAL_PICKAXE", 6, new ItemStack(ItemsTC.elementalPick), new AspectList().add(Aspect.ELDRITCH, 40).add(Aspect.FIRE, 80).add(Aspect.METAL, 300).add(Aspect.SENSES, 50), primordialPearl, new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3));
		
		addInfusionRecipe("void_crop", new ItemStack(ItemsTAR.VOID_SEED), "TAR_VOID_CROP", 5, new ItemStack(Items.WHEAT_SEEDS), AspectUtil.primals(20).add(Aspect.DARKNESS, 100).add(KnowledgeTAR.CAELES, 10), primordialPearl, new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.voidSeed), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(ItemsTC.voidSeed));
		addInfusionRecipe("shadow_enchanter", new ItemStack(BlocksTAR.SHADOW_ENCHANTER), "TAR_SHADOW_ENCHANTER", 10, new ItemStack(Blocks.ENCHANTING_TABLE), AspectUtil.primals(120)
				.add(Aspect.MIND, 500), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), "ingotThaumium", new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), new ItemStack(ItemsTC.mechanismComplex), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), "ingotThaumium", new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), new ItemStack(ItemsTC.mechanismComplex), new ItemStack(ItemsTAR.DISENCHANT_FABRIC));
		addInfusionRecipe("mithminite_hood", new ItemStack(ItemsTAR.MITHMINITE_HOOD), "TAR_MITHMINITE_HOOD", 10, new ItemStack(ItemsTAR.ADAMINITE_HOOD), new AspectList().add(Aspect.PROTECT, 200).add(KnowledgeTAR.VISUM, 250).add(Aspect.FLUX, 150).add(Aspect.LIGHT, 200).add(Aspect.WATER, 250).add(Aspect.MIND, 250)
				.add(KnowledgeTAR.CAELES, 75), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(BlocksTC.jarBrain), new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), new ItemStack(Blocks.SEA_LANTERN), new ItemStack(ItemsTAR.FRAGNANT_PENDANT), new ItemStack(ItemsTAR.SEAL_GLOBE), new ItemStack(BlocksTC.lampArcane), new ItemStack(ItemsTC.sanityChecker));
		addInfusionRecipe("mithminite_robe", new ItemStack(ItemsTAR.MITHMINITE_ROBE), "TAR_MITHMINITE_ROBE", 10, new ItemStack(ItemsTAR.ADAMINITE_ROBE), new AspectList().add(Aspect.PROTECT, 200).add(Aspect.LIFE, 250).add(Aspect.FLUX, 150).add(KnowledgeTAR.VENTUS, 200).add(Aspect.FIRE, 250)
				.add(KnowledgeTAR.CAELES, 75), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), new ItemStack(ItemsTAR.LEVITATION_DEVICE), new ItemStack(Items.GOLDEN_APPLE, 1, 1), new ItemStack(ItemsTC.modules, 1, 1));
		addInfusionRecipe("mithminite_belt", new ItemStack(ItemsTAR.MITHMINITE_BELT), "TAR_MITHMINITE_BELT", 10, new ItemStack(ItemsTAR.ADAMINITE_BELT), new AspectList().add(Aspect.PROTECT, 200).add(KnowledgeTAR.FLUCTUS, 250).add(Aspect.FLUX, 150).add(KnowledgeTAR.VENTUS, 200)
				.add(KnowledgeTAR.CAELES, 75), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), new ItemStack(ItemsTAR.LEVITATION_DEVICE));
		addInfusionRecipe("mithminite_boots", new ItemStack(ItemsTAR.MITHMINITE_BOOTS), "TAR_MITHMINITE_BOOTS", 10, new ItemStack(ItemsTAR.ADAMINITE_BOOTS), new AspectList().add(Aspect.PROTECT, 200).add(Aspect.AIR, 250).add(Aspect.FLUX, 150).add(KnowledgeTAR.FLUCTUS, 200)
				.add(KnowledgeTAR.CAELES, 75), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHMINITE_FABRIC), new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), new ItemStack(ItemsTC.travellerBoots));
		addInfusionRecipe("shadow_beam_staff", new ItemStack(ItemsTAR.SHADOW_BEAM_STAFF), "TAR_SHADOW_BEAM_STAFF", 10, new ItemStack(ItemsTC.visResonator), AspectUtil.primals(50).add(Aspect.ELDRITCH, 500).add(KnowledgeTAR.DRACO, 50), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), "plateIron", new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), "plateIron", new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), "plateIron");
		addInfusionRecipe("void_anvil", new ItemStack(BlocksTAR.VOID_ANVIL), "TAR_VOID_ANVIL", 7, new ItemStack(Blocks.ANVIL), new AspectList().add(Aspect.ELDRITCH, 200)
				.add(KnowledgeTAR.CAELES, 10), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(BlocksTC.metalBlockVoid));
		addInfusionRecipe("crystal_bag", new ItemStack(ItemsTAR.CRYSTAL_BAG), "TAR_CRYSTAL_BAG", 6, new ItemStack(ItemsTC.focusPouch), AspectUtil.primals(10).add(Aspect.VOID, 50), new ItemStack(BlocksTC.crystalAir), new ItemStack(BlocksTC.crystalEarth), new ItemStack(BlocksTC.crystalEntropy), new ItemStack(BlocksTC.crystalFire), new ItemStack(BlocksTC.crystalOrder), new ItemStack(BlocksTC.crystalTaint), new ItemStack(BlocksTC.crystalWater));
		addInfusionRecipe("blue_bone", new ItemStack(ItemsTAR.BLUE_BONE), "TAR_MITHRILLIUM", 1, new ItemStack(Items.BONE), new AspectList().add(KnowledgeTAR.EXITIUM, 6).add(Aspect.EXCHANGE, 6).add(KnowledgeTAR.DRACO, 6), new ItemStack(ItemsTAR.MITHRILLIUM_NUGGET), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTAR.MITHRILLIUM_NUGGET), new ItemStack(ItemsTC.salisMundus));
		addInfusionRecipe("the_beheader", new ItemStack(ItemsTAR.THE_BEHEADER), "TAR_THE_BEHEADER", 8, new ItemStack(ItemsTC.thaumiumAxe), new AspectList().add(Aspect.UNDEAD, 100).add(Aspect.ENTROPY, 70).add(Aspect.MAGIC, 85).add(Aspect.AVERSION, 120)
				.add(Aspect.DEATH, 70), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), new ItemStack(Items.SKULL, 1, 1), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), new ItemStack(Items.SKULL, 1, 1));
		
		addInfusionRecipe("traveller_belt", new ItemStack(ItemsTAR.TRAVELLER_BELT), "TAR_TRAVELLER_BELT", 1, new ItemStack(ItemsTC.baubles, 1, 2), new AspectList().add(Aspect.MOTION, 100).add(Aspect.FLIGHT, 100), AspectUtil.crystalEssence(Aspect.AIR), AspectUtil.crystalEssence(Aspect.AIR), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(Items.FEATHER), new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE));
		addInfusionRecipe("striding_belt", new ItemStack(ItemsTAR.STRIDING_BELT), "TAR_STRIDING_BELT", 3, new ItemStack(ItemsTAR.TRAVELLER_BELT), new AspectList().add(Aspect.MOTION, 150).add(Aspect.FLIGHT, 150).add(Aspect.AIR, 200).add(KnowledgeTAR.FLUCTUS, 50), new ItemStack(ItemsTC.amber), new ItemStack(Items.FEATHER), new ItemStack(ItemsTC.amber), new ItemStack(Items.FEATHER));
		addInfusionRecipe("meteor_belt", new ItemStack(ItemsTAR.METEOR_BELT), "TAR_METEOR_BELT", 5, new ItemStack(ItemsTAR.STRIDING_BELT), new AspectList().add(Aspect.MOTION, 250).add(Aspect.FLIGHT, 250).add(Aspect.FIRE, 200).add(KnowledgeTAR.FLUCTUS, 50), AspectUtil.crystalEssence(Aspect.FIRE), new ItemStack(Items.NETHERBRICK), AspectUtil.crystalEssence(Aspect.FIRE), new ItemStack(Items.NETHERBRICK));
		
		addInfusionRecipe("adaminite_sword", new ItemStack(ItemsTAR.ADAMINITE_SWORD), "TAR_ADAMINITE_SWORD", 6, new ItemStack(ItemsTC.voidSword), new AspectList().add(KnowledgeTAR.DRACO, 40).add(Aspect.MAGIC, 70).add(Aspect.AVERSION, 150).add(KnowledgeTAR.INFERNUM, 90).add(Aspect.METAL, 60), "plateAdaminite", "plateAdaminite", primordialPearl, "plateAdaminite");
		
		addInfusionRecipe("mithminite_scythe", new ItemStack(ItemsTAR.MITHMINITE_SCYTHE), "TAR_MITHMINITE_SCYTHE", 1, primordialPearl, new AspectList().add(Aspect.AVERSION, 250).add(Aspect.TOOL, 150).add(Aspect.MAGIC, 100), new ItemStack(ItemsTAR.MITHMINITE_BLADE), new ItemStack(ItemsTAR.MITHMINITE_PLATE), new ItemStack(ItemsTAR.MITHMINITE_HANDLE), new ItemStack(ItemsTAR.MITHMINITE_PLATE));
		addInfusionRecipe("mithminite_blade", new ItemStack(ItemsTAR.MITHMINITE_BLADE), "TAR_MITHMINITE_SCYTHE", 1, new ItemStack(ItemsTAR.ADAMINITE_SWORD), new AspectList().add(Aspect.AVERSION, 250).add(KnowledgeTAR.INFERNUM, 150), new ItemStack(ItemsTAR.MITHMINITE_PLATE), new ItemStack(ItemsTAR.MITHMINITE_PLATE), new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR), new ItemStack(ItemsTAR.MITHMINITE_PLATE));
		addInfusionRecipe("mithminite_handle", new ItemStack(ItemsTAR.MITHMINITE_HANDLE), "TAR_MITHMINITE_SCYTHE", 1, new ItemStack(ItemsTAR.MITHMINITE_NUGGET), new AspectList().add(Aspect.TOOL, 100).add(Aspect.SOUL, 150), new ItemStack(ItemsTAR.MITHMINITE_PLATE), new ItemStack(ItemsTAR.MITHMINITE_INGOT), new ItemStack(ItemsTAR.MITHMINITE_INGOT));
		
		for(Aspect a : Aspect.aspects.values())
		{
			InfusionRecipe rec = new InfusionRecipe("TAR_SEAL_SYMBOLS", ItemSealSymbol.createItem(a, 1), 1, new AspectList().add(a, 10), AspectUtil.crystalEssence(a), "nuggetGold", "nuggetGold", "nuggetGold", "nuggetGold");
			ResourceLocation loc = new ResourceLocation(InfoTAR.MOD_ID, a.getTag() + "_seal_symbol");
			sealSymbolRecipes.add(loc);
			ThaumcraftApi.addInfusionCraftingRecipe(loc, rec);
		}
		
		ThaumcraftApi.addFakeCraftingRecipe(sealSymbolRecipeIDFake, sealSymbolRecipes);
	}
	
	private void arcaneCrafting()
	{
		addShapedArcaneRecipe("mithrillium_smelter", "TAR_MITHRILLIUM_SMELTER", 1000, new AspectList().add(Aspect.FIRE, 6).add(Aspect.WATER, 2), new ItemStack(BlocksTAR.MITHRILLIUM_SMELTER), "bsb", "mcm", "mmm", 'b', "plateBrass", 's', BlocksTC.smelterVoid, 'm', ItemsTAR.MITHRILLIUM_PLATE, 'c', BlocksTC.metalAlchemicalAdvanced);
		addShapedArcaneRecipe("adaminite_smelter", "TAR_ADAMINITE_SMELTER", 1200, new AspectList().add(Aspect.FIRE, 12).add(Aspect.WATER, 6), new ItemStack(BlocksTAR.ADAMINITE_SMELTER), "bsb", "mcm", "mmm", 'b', "plateBrass", 's', BlocksTAR.MITHRILLIUM_SMELTER, 'm', ItemsTAR.ADAMINITE_PLATE, 'c', BlocksTC.metalAlchemicalAdvanced);
		addShapedArcaneRecipe("mithminite_smelter", "TAR_MITHMINITE_SMELTER", 1500, new AspectList().add(Aspect.FIRE, 24).add(Aspect.WATER, 12), new ItemStack(BlocksTAR.MITHMINITE_SMELTER), "bsb", "mcm", "mmm", 'b', "plateBrass", 's', BlocksTAR.ADAMINITE_SMELTER, 'm', ItemsTAR.MITHMINITE_PLATE, 'c', BlocksTC.metalAlchemicalAdvanced);
		addShapedArcaneRecipe("dna_sample", "TAR_MOB_SUMMONING@2", 100, AspectUtil.primals(6), new ItemStack(ItemsTAR.ENTITY_CELL), "tmt", "rpr", "tmt", 'r', ItemsTC.mechanismSimple, 't', "ingotThaumium", 'm', ItemsTC.morphicResonator, 'p', "plateThaumium");
		addShapedArcaneRecipe("brass_jar", "TAR_BRASS_JAR", 7, new AspectList(), new ItemStack(BlocksTAR.BRASS_JAR), "gpg", "gjg", "ggg", 'g', "paneGlass", 'p', "plateBrass", 'j', BlocksTC.jarNormal);
		addShapedArcaneRecipe("thaumium_jar", "TAR_THAUMIUM_JAR", 15, new AspectList().add(Aspect.WATER, 2), new ItemStack(BlocksTAR.THAUMIUM_JAR), "gpg", "gjg", "ggg", 'g', "paneGlass", 'p', new ItemStack(ItemsTC.plate, 1, 2), 'j', BlocksTAR.BRASS_JAR);
		addShapedArcaneRecipe("eldritch_jar", "TAR_ELDRITCH_JAR", 150, new AspectList().add(Aspect.WATER, 6), new ItemStack(BlocksTAR.ELDRITCH_JAR), "gpg", "gjg", "ggg", 'g', "paneGlass", 'p', new ItemStack(ItemsTC.plate, 1, 3), 'j', BlocksTAR.THAUMIUM_JAR);
		addShapedArcaneRecipe("mithrillium_jar", "TAR_MITHRILLIUM_JAR", 750, new AspectList().add(Aspect.WATER, 12), new ItemStack(BlocksTAR.MITHRILLIUM_JAR), "gpg", "gjg", "ggg", 'g', "paneGlass", 'p', new ItemStack(ItemsTAR.MITHRILLIUM_PLATE), 'j', BlocksTAR.ELDRITCH_JAR);
		addShapedArcaneRecipe("adaminite_jar", "TAR_ADAMINITE_JAR@2", 1000, new AspectList().add(Aspect.WATER, 24), new ItemStack(BlocksTAR.ADAMINITE_JAR), "gpg", "gjg", "ggg", 'g', "paneGlass", 'p', new ItemStack(ItemsTAR.ADAMINITE_PLATE), 'j', BlocksTAR.MITHRILLIUM_JAR);
		addShapedArcaneRecipe("seal", "TAR_SEAL", 50, AspectUtil.primals(1), new ItemStack(BlocksTAR.SEAL, 2), " g ", "gwg", " g ", 'g', "nuggetGold", 'w', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));
		addShapedArcaneRecipe("twilight_totem", "TAR_TOTEMS@2", 50, new AspectList().add(Aspect.ENTROPY, 1).add(Aspect.AIR, 1), new ItemStack(BlocksTAR.TWILIGHT_TOTEM), "sws", "wcw", "sws", 's', BlocksTAR.TAINTWOOD_PLANK, 'w', ItemsTC.fabric, 'c', new NBTRespectfulIngredient(AspectUtil.crystalEssence(Aspect.FLUX)));
		addShapedArcaneRecipe("dawn_totem", "TAR_TOTEMS", 50, new AspectList().add(Aspect.ORDER, 1).add(Aspect.AIR, 1), new ItemStack(BlocksTAR.DAWN_TOTEM), "sws", "wcw", "sws", 's', BlocksTC.plankSilverwood, 'w', new NBTRespectfulIngredient(AspectUtil.crystalEssence(Aspect.AURA)), 'c', BlocksTC.shimmerleaf);
		addShapedArcaneRecipe("seal_globe", "TAR_SEAL_GLOBE", 100, AspectUtil.primals(1), new ItemStack(ItemsTAR.SEAL_GLOBE), "ggg", "grg", "lcl", 'g', "blockGlass", 'r', ItemsTC.visResonator, 'l', "ingotGold", 'c', new NBTRespectfulIngredient(AspectUtil.crystalEssence(KnowledgeTAR.IMPERIUM)));
		addShapedArcaneRecipe("knowledge_tome", "TAR_KNOWLEDGE_TOME", 100, AspectUtil.primals(1), new ItemStack(ItemsTAR.KNOWLEDGE_TOME), "s", "c", "t", 's', new ItemStack(ItemsTC.scribingTools, 1, OreDictionary.WILDCARD_VALUE), 'c', new ItemStack(ItemsTC.curio, 1, 1), 't', new ItemStack(ItemsTC.thaumonomicon));
		addShapedArcaneRecipe("fragnant_pendant", "TAR_FRAGNANT_PENDANT", 50, AspectUtil.primals(1), new ItemStack(ItemsTAR.FRAGNANT_PENDANT), " f ", "saf", "mp ", 'f', new ItemStack(ItemsTC.filter), 's', new ItemStack(ItemsTC.salisMundus), 'a', new ItemStack(ItemsTC.baubles, 1, 4), 'm', new ItemStack(ItemsTC.amber), 'p', new ItemStack(ItemsTAR.ODOUR_POWDER));
		addShapedArcaneRecipe("bone_eye", "TAR_CHESTER", 100, new AspectList().add(Aspect.ORDER, 1).add(Aspect.AIR, 1), new ItemStack(ItemsTAR.BONE_EYE), " b ", " eb", "t  ", 'b', Items.QUARTZ, 'e', Items.SPIDER_EYE, 't', "ingotThaumium");
		addShapedArcaneRecipe("vis_scribing_tools", "TAR_VIS_SCRIBING_TOOLS", 200, AspectUtil.primals(1), new ItemStack(ItemsTAR.VIS_SCRIBING_TOOLS), " v ", "vtg", " ga", 'v', AspectUtil.crystalEssence(Aspect.MAGIC), 't', new ItemStack(ItemsTC.scribingTools), 'g', "blockGlass", 'a', AspectUtil.phial(Aspect.AURA));
		addShapedArcaneRecipe("adaminite_fabric", "TAR_ADAMINITE_FABRIC", 200, AspectUtil.primals(4), new ItemStack(ItemsTAR.ADAMINITE_FABRIC, 2), "fff", "faf", "fff", 'f', new ItemStack(ItemsTC.fabric), 'a', new ItemStack(ItemsTAR.ADAMINITE_INGOT));
		addShapedArcaneRecipe("mithminite_fabric", "TAR_MITHMINITE_FABRIC", 400, AspectUtil.primals(8), new ItemStack(ItemsTAR.MITHMINITE_FABRIC, 2), " a ", "ama", " a ", 'm', new ItemStack(ItemsTAR.MITHMINITE_INGOT), 'a', new ItemStack(ItemsTAR.ADAMINITE_FABRIC));
		addShapedArcaneRecipe("levitation_device", "TAR_THAUMADDS", 20, new AspectList().add(Aspect.AIR, 5).add(Aspect.ORDER, 2), new ItemStack(ItemsTAR.LEVITATION_DEVICE), "ini", "cmc", "iai", 'i', "plateIron", 'n', "nitor", 'c', new ItemStack(ItemsTC.mechanismComplex), 'm', new ItemStack(BlocksTC.levitator), 'a', new ItemStack(ItemsTC.alumentum));
		addShapedArcaneRecipe("adaminite_hood", "TAR_ADAMINITE_FABRIC", 200, AspectUtil.primals(4), new ItemStack(ItemsTAR.ADAMINITE_HOOD), "fff", "fmf", 'f', new ItemStack(ItemsTAR.ADAMINITE_FABRIC), 'm', new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR));
		addShapedArcaneRecipe("adaminite_robe", "TAR_ADAMINITE_FABRIC", 200, AspectUtil.primals(4), new ItemStack(ItemsTAR.ADAMINITE_ROBE), "f f", "fmf", "fff", 'f', new ItemStack(ItemsTAR.ADAMINITE_FABRIC), 'm', new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR));
		addShapedArcaneRecipe("adaminite_belt", "TAR_ADAMINITE_FABRIC", 200, AspectUtil.primals(4), new ItemStack(ItemsTAR.ADAMINITE_BELT), " f ", "fmf", 'f', new ItemStack(ItemsTAR.ADAMINITE_FABRIC), 'm', new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR));
		addShapedArcaneRecipe("adaminite_boots", "TAR_ADAMINITE_FABRIC", 200, AspectUtil.primals(4), new ItemStack(ItemsTAR.ADAMINITE_BOOTS), "f f", "fmf", 'f', new ItemStack(ItemsTAR.ADAMINITE_FABRIC), 'm', new ItemStack(ItemsTAR.MITHRILLIUM_RESONATOR));
		addShapedArcaneRecipe("essentia_pistol", "TAR_ESSENTIA_PISTOL", 100, new AspectList().add(Aspect.AIR, 1), new ItemStack(ItemsTAR.ESSENTIA_PISTOL), "stt", "gbd", "g  ", 's', new ItemStack(ItemsTC.mechanismSimple), 't', "ingotThaumium", 'g', new ItemStack(BlocksTC.plankGreatwood), 'b', new ItemStack(Blocks.STONE_BUTTON), 'd', new ItemStack(BlocksTAR.AURA_DISPERSER));
		addShapedArcaneRecipe("disenchant_fabric", "TAR_DISENCHANT_FABRIC", 150, new AspectList().add(Aspect.EARTH, 1), new ItemStack(ItemsTAR.DISENCHANT_FABRIC), " f ", "fsf", " f ", 'f', new ItemStack(ItemsTC.fabric), 's', new ItemStack(ItemsTC.salisMundus));
		addShapedArcaneRecipe("crystal_lamp", "TAR_CRYSTAL_LAMP", 30, new AspectList(), new ItemStack(BlocksTAR.CRYSTAL_LAMP), "g g", " a ", "g g", 'g', new ItemStack(BlocksTC.plankGreatwood), 'a', "nitor");
		
		for(Aspect a : Aspect.aspects.values())
		{
			IArcaneRecipe rec = new ShapedArcaneRecipe(defaultGroup, "TAR_CRYSTAL_BLOCK", 10, new AspectList(), AspectUtil.crystalBlock(a), "ccc", "ccc", "ccc", 'c', new NBTRespectfulIngredient(AspectUtil.crystalEssence(a)));
			ResourceLocation loc = new ResourceLocation(InfoTAR.MOD_ID, a.getTag() + "_crystal_block");
			crystalBlockRecipes.add(loc);
			ThaumcraftApi.addArcaneCraftingRecipe(loc, rec);
		}
		
		ThaumcraftApi.addFakeCraftingRecipe(crystalBlockRecipeIDFake, crystalBlockRecipes);
	}
	
	private void postAspects()
	{
		IThaumcraftRecipe hedge_gunpowder = ThaumcraftApi.getCraftingRecipes().get(new ResourceLocation("thaumcraft:hedge_gunpowder"));
		if(hedge_gunpowder instanceof CrucibleRecipe)
		{
			CrucibleRecipe rec = (CrucibleRecipe) hedge_gunpowder;
			AspectList catal = rec.getAspects();
			if(catal.getAmount(KnowledgeTAR.EXITIUM) <= 0)
				catal.add(KnowledgeTAR.EXITIUM, 10);
		}
	}
	
	private void crucible()
	{
		addCrucibleRecipe("crystal_water", "TAR_CRYSTAL_WATER", FluidUtil.getFilledBucket(new FluidStack(FluidsTAR.CRYSTAL_WATER, Fluid.BUCKET_VOLUME)), new ItemStack(Items.WATER_BUCKET), new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.DESIRE, 4).add(Aspect.EXCHANGE, 6));
		addCrucibleRecipe("odour_powder", "TAR_FRAGNANT_PENDANT", new ItemStack(ItemsTAR.ODOUR_POWDER, 4), new ItemStack(ItemsTC.bathSalts), new AspectList().add(Aspect.ORDER, 10).add(KnowledgeTAR.EXITIUM, 5).add(KnowledgeTAR.VENTUS, 5));
		addCrucibleRecipe("phantom_ink_phial", "TAR_THAUMADDS", new ItemStack(ItemsTAR.PHANTOM_INK_PHIAL), new ItemStack(ItemsTC.phial), new AspectList().add(KnowledgeTAR.VISUM, 10).add(Aspect.ELDRITCH, 20));
		
		for(Aspect a : Aspect.aspects.values())
		{
			CrucibleRecipe cr = new CrucibleRecipe("TAR_VIS_SEEDS", ItemVisSeeds.create(a, 1), new ItemStack(Items.WHEAT_SEEDS), new AspectList().add(Aspect.PLANT, 10).add(a, 20));
			ResourceLocation loc = new ResourceLocation(InfoTAR.MOD_ID, a.getTag() + "_vis_seed");
			visSeedsRecipes.add(loc);
			ThaumcraftApi.addCrucibleRecipe(loc, cr);
		}
		
		ThaumcraftApi.addFakeCraftingRecipe(visSeedsRecipeIDFake, visSeedsRecipes);
	}
	
	private void multiblock()
	{
		{
			BlueprintBuilder b = new BlueprintBuilder(3, 3, 3).center(1, 0, 1);
			for(int x = -1; x < 2; x++)
				for(int z = -1; z < 2; z++)
					b.part(x, 0, z, BlocksTAR.CRYSTAL_WATER, null);
			b.part(0, 0, 0, new ItemStack(Blocks.STONE), null);
			b.part(0, 1, 0, new ItemStack(BlocksTC.crystalOrder), null);
			ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation(InfoTAR.MOD_ID, "mb.crystal_acceleration"), b.build("TAR_CRYSTAL_WATER", new ItemStack(ItemsTC.crystalEssence)));
		}
	}
	
	@Override
	public void smelting()
	{
	}
	
	@Override
	protected IRecipe recipe(IRecipe recipe)
	{
		super.recipe(recipe);
		Item it = recipe.getRecipeOutput().getItem();
		List<ResourceLocation> locs = FAKE_RECIPE_MAP.get(it);
		if(locs == null)
			FAKE_RECIPE_MAP.put(it, locs = new ArrayList<>());
		if(!locs.contains(recipe.getRegistryName()))
			locs.add(recipe.getRegistryName());
		ThaumcraftApi.addFakeCraftingRecipe(recipe.getRegistryName(), recipe);
		return recipe;
	}
}