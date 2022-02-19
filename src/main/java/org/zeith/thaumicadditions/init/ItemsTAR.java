package org.zeith.thaumicadditions.init;

import net.minecraft.inventory.EntityEquipmentSlot;
import org.zeith.thaumicadditions.items.*;
import org.zeith.thaumicadditions.items.armor.ItemAdaminiteDress;
import org.zeith.thaumicadditions.items.armor.ItemMithminiteDress;
import org.zeith.thaumicadditions.items.baubles.*;
import org.zeith.thaumicadditions.items.seed.ItemVisSeeds;
import org.zeith.thaumicadditions.items.seed.ItemVoidSeed;
import org.zeith.thaumicadditions.items.tools.*;
import org.zeith.thaumicadditions.items.weapons.*;

public class ItemsTAR
{
	// MATERIALS

	public static final ItemMaterial MITHRILLIUM_NUGGET = new ItemMaterial("mithrillium_nugget", "nuggetMithrillium");
	public static final ItemMaterial ADAMINITE_NUGGET = new ItemMaterial("adaminite_nugget", "nuggetAdaminite");
	public static final ItemMaterial MITHMINITE_NUGGET = new ItemMaterial("mithminite_nugget", "nuggetMithminite");
	public static final ItemMaterial MITHRILLIUM_INGOT = new ItemMaterial("mithrillium_ingot", "ingotMithrillium");
	public static final ItemMaterial ADAMINITE_INGOT = new ItemMaterial("adaminite_ingot", "ingotAdaminite");
	public static final ItemMaterial MITHMINITE_INGOT = new ItemMaterial("mithminite_ingot", "ingotMithminite");
	public static final ItemMaterial MITHRILLIUM_PLATE = new ItemMaterial("mithrillium_plate", "plateMithrillium");
	public static final ItemMaterial ADAMINITE_PLATE = new ItemMaterial("adaminite_plate", "plateAdaminite");
	public static final ItemMaterial MITHMINITE_PLATE = new ItemMaterial("mithminite_plate", "plateMithminite");
	public static final ItemMaterial ADAMINITE_FABRIC = new ItemMaterial("adaminite_fabric");
	public static final ItemMaterial MITHMINITE_FABRIC = new ItemMaterial("mithminite_fabric");
	public static final ItemMaterial MITHRILLIUM_RESONATOR = new ItemMaterial("mithrillium_resonator");
	public static final ItemMaterial LEVITATION_DEVICE = new ItemMaterial("levitation_device");
	public static final ItemMaterial SEAL_GLOBE = new ItemMaterial("seal_globe");
	public static final ItemMaterial ODOUR_POWDER = new ItemMaterial("odour_powder");
	public static final ItemMaterial PHANTOM_INK_PHIAL = new ItemMaterial("phantom_ink_phial");
	public static final ItemMaterial MITHMINITE_BLADE = new ItemMaterial("mithminite_blade");
	public static final ItemMaterial MITHMINITE_HANDLE = new ItemMaterial("mithminite_handle");
	public static final ItemMaterial ZEITH_FUR = new ItemZeithFur();
	public static final ItemBlueBone BLUE_BONE = new ItemBlueBone();

	// BAUBLES

	public static final ItemFragnantPendant FRAGNANT_PENDANT = new ItemFragnantPendant();
	public static final ItemRechargeCharm RECHARGE_CHARM = new ItemRechargeCharm();
	public static final ItemBeltTraveller TRAVELLER_BELT = new ItemBeltTraveller();
	public static final ItemBeltStriding STRIDING_BELT = new ItemBeltStriding();
	public static final ItemBeltMeteor METEOR_BELT = new ItemBeltMeteor();

	// TOOLS

	public static final ItemBoneEye BONE_EYE = new ItemBoneEye();
	public static final ItemVisScribingTools VIS_SCRIBING_TOOLS = new ItemVisScribingTools();
	public static final ItemVoidThaumometer VOID_THAUMOMETER = new ItemVoidThaumometer();
	public static final ItemWormholeMirror WORMHOLE_MIRROR = new ItemWormholeMirror();

	public static final ItemVoidElementalPickaxe VOID_ELEMENTAL_PICKAXE = new ItemVoidElementalPickaxe();
	public static final ItemVoidElementalShovel VOID_ELEMENTAL_SHOVEL = new ItemVoidElementalShovel();
	public static final ItemVoidElementalAxe VOID_ELEMENTAL_AXE = new ItemVoidElementalAxe();
	public static final ItemVoidElementalHoe VOID_ELEMENTAL_HOE = new ItemVoidElementalHoe();

	// WEAPONS

	public static final ItemShadowBeamStaff SHADOW_BEAM_STAFF = new ItemShadowBeamStaff();
	public static final ItemEssentiaPistol ESSENTIA_PISTOL = new ItemEssentiaPistol();
	public static final ItemTheBeheader THE_BEHEADER = new ItemTheBeheader();
	public static final ItemAdaminiteSword ADAMINITE_SWORD = new ItemAdaminiteSword();
	public static final ItemMithminiteScythe MITHMINITE_SCYTHE = new ItemMithminiteScythe();

	// ARMOR

	public static final ItemAdaminiteDress ADAMINITE_HOOD = new ItemAdaminiteDress(EntityEquipmentSlot.HEAD).setTranslationKey("adaminite_hood");
	public static final ItemAdaminiteDress ADAMINITE_ROBE = new ItemAdaminiteDress(EntityEquipmentSlot.CHEST).setTranslationKey("adaminite_robe");
	public static final ItemAdaminiteDress ADAMINITE_BELT = new ItemAdaminiteDress(EntityEquipmentSlot.LEGS).setTranslationKey("adaminite_belt");
	public static final ItemAdaminiteDress ADAMINITE_BOOTS = new ItemAdaminiteDress(EntityEquipmentSlot.FEET).setTranslationKey("adaminite_boots");

	public static final ItemMithminiteDress MITHMINITE_HOOD = new ItemMithminiteDress(EntityEquipmentSlot.HEAD).setTranslationKey("mithminite_hood");
	public static final ItemMithminiteDress MITHMINITE_ROBE = new ItemMithminiteDress(EntityEquipmentSlot.CHEST).setTranslationKey("mithminite_robe");
	public static final ItemMithminiteDress MITHMINITE_BELT = new ItemMithminiteDress(EntityEquipmentSlot.LEGS).setTranslationKey("mithminite_belt");
	public static final ItemMithminiteDress MITHMINITE_BOOTS = new ItemMithminiteDress(EntityEquipmentSlot.FEET).setTranslationKey("mithminite_boots");

//	public static final ItemBlueWolfSuit BLUE_WOLF_MASK = new ItemBlueWolfSuit(EntityEquipmentSlot.HEAD).setTranslationKey("blue_wolf_mask");
//	public static final ItemBlueWolfSuit BLUE_WOLF_BODY = new ItemBlueWolfSuit(EntityEquipmentSlot.CHEST).setTranslationKey("blue_wolf_body");
//	public static final ItemBlueWolfSuit BLUE_WOLF_LEGS = new ItemBlueWolfSuit(EntityEquipmentSlot.LEGS).setTranslationKey("blue_wolf_legs");
//	public static final ItemBlueWolfSuit BLUE_WOLF_FEETPAWS = new ItemBlueWolfSuit(EntityEquipmentSlot.FEET).setTranslationKey("blue_wolf_feetpaws");

	// ENTITY-BOUND ITEMS

	public static final ItemEntityCell ENTITY_CELL = new ItemEntityCell();

	// VIS-BOUND ITEMS

	public static final ItemSaltEssence SALT_ESSENCE = new ItemSaltEssence();
	public static final ItemSealSymbol SEAL_SYMBOL = new ItemSealSymbol();
	public static final ItemVisPod VIS_POD = new ItemVisPod();
	public static final ItemVisSeeds VIS_SEEDS = new ItemVisSeeds();

	// MISC ITEMS

	public static final ItemVoidSeed VOID_SEED = new ItemVoidSeed();
	public static final ItemChester CHESTER = new ItemChester();
	public static final ItemKnowledgeTome KNOWLEDGE_TOME = new ItemKnowledgeTome();
	public static final ItemDisenchantingFabric DISENCHANT_FABRIC = new ItemDisenchantingFabric();
	public static final ItemCrystalBag CRYSTAL_BAG = new ItemCrystalBag();
	public static final ItemVoidFruit VOID_FRUIT = new ItemVoidFruit();
}