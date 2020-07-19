package com.zeitheron.thaumicadditions.compat.thaumicwands;

import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.hammercore.mod.ModuleLoader;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.compat.ITARC;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.init.KnowledgeTAR;
import com.zeitheron.thaumicadditions.init.KnowledgeTAR.REB;
import com.zeitheron.thaumicadditions.init.KnowledgeTAR.RSB;
import com.zeitheron.thaumicadditions.init.RecipesTAR;
import com.zeitheron.thaumicadditions.items.ItemMaterial;
import de.zpenguin.thaumicwands.api.ThaumicWandsAPI;
import de.zpenguin.thaumicwands.item.TW_Items;
import de.zpenguin.thaumicwands.main.ThaumicWands;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta;
import thaumcraft.api.research.ResearchStage.Knowledge;

/**
 * @author Zoko061602
 */
@ModuleLoader(requiredModid = "thaumicwands")
public class TARCThaumicWands
		implements ITARC
{
	public static final ItemMaterial MITHRILLIUM_CAP_INERT = new ItemMaterial("mithrillium_cap_inert");
	public static final ItemMaterial ADAMINITE_CAP_INERT = new ItemMaterial("adaminite_cap_inert");
	public static final ItemMaterial MITHMINITE_CAP_INERT = new ItemMaterial("mithminite_cap_inert");
	public static final ItemMaterial MITHRILLIUM_CAP = new ItemMaterial("mithrillium_cap");
	public static final ItemMaterial ADAMINITE_CAP = new ItemMaterial("adaminite_cap");
	public static final ItemMaterial MITHMINITE_CAP = new ItemMaterial("mithminite_cap");
	public static final ItemMaterial ADAMINITEWOOD_ROD = new ItemMaterial("adaminitewood_rod");

	@Override
	public void preInit()
	{
		SimpleRegistration.registerFieldItemsFrom(TARCThaumicWands.class, InfoTAR.MOD_ID, TAReconstructed.tab);
	}

	@Override
	public void init()
	{
		ThaumicWandsAPI.registerWandCap("mithrillium", new TARWandCap("mithrillium", 0.75F, AspectUtil.primals(1), new ItemStack(MITHRILLIUM_CAP), 30));
		ThaumicWandsAPI.registerWandCap("adaminite", new TARWandCap("adaminite", 0.7F, AspectUtil.primals(2), new ItemStack(ADAMINITE_CAP), 35));
		ThaumicWandsAPI.registerWandCap("mithminite", new TARWandCap("mithminite", 0.6F, AspectUtil.primals(3), new ItemStack(MITHMINITE_CAP), 40));

		ThaumicWandsAPI.registerWandRod("adaminitewood", new TARWandRod("adaminitewood", 3200, new ItemStack(ADAMINITEWOOD_ROD), 40));
	}

	@Override
	public void addArcaneRecipes()
	{
		Ingredient primordialPearl = Ingredient.fromItem(ItemsTC.primordialPearl);

		RecipesTAR.addInfusionRecipe("mithrillium_cap", new ItemStack(MITHRILLIUM_CAP), "TAR_CAP_MITHRILLIUM", 5, new ItemStack(MITHRILLIUM_CAP_INERT), AspectUtil.primals(50).add(KnowledgeTAR.DRACO, 50).add(Aspect.MAGIC, 50), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
		RecipesTAR.addInfusionRecipe("adaminite_cap", new ItemStack(ADAMINITE_CAP), "TAR_CAP_ADAMINITE", 8, new ItemStack(ADAMINITE_CAP_INERT), AspectUtil.primals(50).add(KnowledgeTAR.INFERNUM, 50).add(Aspect.MAGIC, 50), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
		RecipesTAR.addInfusionRecipe("mithminite_cap", new ItemStack(MITHMINITE_CAP), "TAR_CAP_MITHMINITE", 6, new ItemStack(MITHMINITE_CAP_INERT), AspectUtil.primals(50).add(KnowledgeTAR.CAELES, 10).add(Aspect.MAGIC, 50), primordialPearl, new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));
		RecipesTAR.addInfusionRecipe("adaminitewood_rod", new ItemStack(ADAMINITEWOOD_ROD), "TAR_ROD_ADAMINITEWOOD", 8, new ItemStack(TW_Items.itemWandRod, 1, 7), AspectUtil.primals(100).add(KnowledgeTAR.INFERNUM, 100).add(KnowledgeTAR.DRACO, 100), new ItemStack(ItemsTAR.ADAMINITE_INGOT), new ItemStack(ItemsTAR.ADAMINITE_INGOT), new ItemStack(ItemsTAR.ADAMINITE_FABRIC), new ItemStack(ItemsTAR.ADAMINITE_FABRIC), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.salisMundus));

		RecipesTAR.addShapedArcaneRecipe("mithrillium_cap_inert", "TAR_CAP_MITHRILLIUM", 100, AspectUtil.primals(10), new ItemStack(MITHRILLIUM_CAP_INERT), "nnn","n n", 'n', "nuggetMithrillium");
		RecipesTAR.addShapedArcaneRecipe("adaminite_cap_inert", "TAR_CAP_ADAMINITE", 150, AspectUtil.primals(15), new ItemStack(ADAMINITE_CAP_INERT), "nnn","n n", 'n', "nuggetAdaminite");
		RecipesTAR.addShapedArcaneRecipe("mithminite_cap_inert", "TAR_CAP_MITHMINITE", 200, AspectUtil.primals(20), new ItemStack(MITHMINITE_CAP_INERT), "nnn","n n", 'n', "nuggetMithminite");
	}

	@Override
	public void addResearches()
	{
		ResearchCategory R_THAUMADDS = TAReconstructed.RES_CAT;
		ResearchCategory R_THAUMICWANDS = ResearchCategories.getResearchCategory("THAUMATURGY");

		new REB().setBaseInfo("TAR_CAP_MITHRILLIUM", "cap_mithrillium", -2, -7, new ItemStack(MITHRILLIUM_CAP)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":cap_mithrillium.1").setRequiredCraft(new ItemStack(ItemsTAR.MITHRILLIUM_NUGGET)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2), new Knowledge(EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("THAUMATURGY"), 2)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":cap_mithrillium.2").setRecipes(InfoTAR.MOD_ID + ":mithrillium_cap_inert", InfoTAR.MOD_ID + ":mithrillium_cap").build()).setParents("CAP_VOID", "TAR_MITHRILLIUM").setCategory(R_THAUMICWANDS.key).buildAndRegister();

		new REB().setBaseInfo("TAR_CAP_ADAMINITE", "cap_adaminite", 0, -7, new ItemStack(ADAMINITE_CAP)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":cap_adaminite.1").setRequiredCraft(new ItemStack(ItemsTAR.ADAMINITE_NUGGET)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2), new Knowledge(EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("THAUMATURGY"), 2)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":cap_adaminite.2").setRecipes(InfoTAR.MOD_ID + ":adaminite_cap_inert", InfoTAR.MOD_ID + ":adaminite_cap").build()).setParents("TAR_CAP_MITHRILLIUM", "TAR_ADAMINITE").setCategory(R_THAUMICWANDS.key).buildAndRegister();

		new REB().setBaseInfo("TAR_CAP_MITHMINITE", "cap_mithminite", 2, -7, new ItemStack(MITHMINITE_CAP)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":cap_mithminite.1").setRequiredCraft(new ItemStack(ItemsTAR.MITHMINITE_NUGGET)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2), new Knowledge(EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("THAUMATURGY"), 2)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":cap_mithminite.2").setRecipes(InfoTAR.MOD_ID + ":mithminite_cap_inert", InfoTAR.MOD_ID + ":mithminite_cap").build()).setParents("TAR_CAP_ADAMINITE", "TAR_MITHMINITE").setCategory(R_THAUMICWANDS.key).buildAndRegister();

		new REB().setBaseInfo("TAR_ROD_ADAMINITEWOOD", "rod_adaminitewood", 10, 3, new ItemStack(ADAMINITEWOOD_ROD)).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":rod_adaminitewood.1").setRequiredCraft(new ItemStack(ItemsTAR.ADAMINITE_INGOT), new ItemStack(TW_Items.itemWandRod, 1, 7)).setKnow(new Knowledge(EnumKnowledgeType.THEORY, R_THAUMADDS, 2), new Knowledge(EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory("THAUMATURGY"), 2)).build(), new RSB().setText("research_stage." + InfoTAR.MOD_ID + ":rod_adaminitewood.2").setRecipes(InfoTAR.MOD_ID + ":adaminitewood_rod").build()).setParents("ROD_SILVERWOOD", "TAR_ADAMINITE_FABRIC").setCategory(R_THAUMICWANDS.key).buildAndRegister();
	}
}