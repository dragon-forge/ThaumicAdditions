package com.zeitheron.thaumicadditions.init;

import com.zeitheron.thaumicadditions.items.ItemBoneEye;
import com.zeitheron.thaumicadditions.items.ItemChester;
import com.zeitheron.thaumicadditions.items.ItemDNASample;
import com.zeitheron.thaumicadditions.items.ItemKnowledgeTome;
import com.zeitheron.thaumicadditions.items.ItemMaterial;
import com.zeitheron.thaumicadditions.items.ItemSaltEssence;
import com.zeitheron.thaumicadditions.items.ItemSealSymbol;
import com.zeitheron.thaumicadditions.items.ItemVisPod;
import com.zeitheron.thaumicadditions.items.ItemVisScribingTools;
import com.zeitheron.thaumicadditions.items.ItemVisSeeds;
import com.zeitheron.thaumicadditions.items.ItemVoidThaumometer;
import com.zeitheron.thaumicadditions.items.ItemWormholeMirror;
import com.zeitheron.thaumicadditions.items.ItemZeithScale;
import com.zeitheron.thaumicadditions.items.armor.ItemAdaminiteDress;
import com.zeitheron.thaumicadditions.items.armor.ItemMithminiteDress;
import com.zeitheron.thaumicadditions.items.baubles.ItemFragnantPendant;
import com.zeitheron.thaumicadditions.items.baubles.ItemRechargeCharm;

import net.minecraft.inventory.EntityEquipmentSlot;

public class ItemsTAR
{
	public static final ItemMaterial MITHRILLIUM_NUGGET = new ItemMaterial("mithrillium_nugget", "nuggetMithrillium");
	public static final ItemMaterial ADAMINITE_NUGGET = new ItemMaterial("adaminite_nugget", "nuggetAdaminite");
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
	public static final ItemMaterial ZEITH_SCALES = new ItemZeithScale();
	public static final ItemRechargeCharm RECHARGE_CHARM = new ItemRechargeCharm();
	public static final ItemKnowledgeTome KNOWLEDGE_TOME = new ItemKnowledgeTome();
	public static final ItemFragnantPendant FRAGNANT_PENDANT = new ItemFragnantPendant();
	public static final ItemChester CHESTER = new ItemChester();
	public static final ItemBoneEye BONE_EYE = new ItemBoneEye();
	public static final ItemVisScribingTools VIS_SCRIBING_TOOLS = new ItemVisScribingTools();
	public static final ItemVoidThaumometer VOID_THAUMOMETER = new ItemVoidThaumometer();
	public static final ItemWormholeMirror WORMHOLE_MIRROR = new ItemWormholeMirror();
	
	// ARMOR
	
	public static final ItemAdaminiteDress ADAMINITE_HOOD = new ItemAdaminiteDress(EntityEquipmentSlot.HEAD).setTranslationKey("adaminite_hood");
	public static final ItemAdaminiteDress ADAMINITE_ROBE = new ItemAdaminiteDress(EntityEquipmentSlot.CHEST).setTranslationKey("adaminite_robe");
	public static final ItemAdaminiteDress ADAMINITE_BELT = new ItemAdaminiteDress(EntityEquipmentSlot.LEGS).setTranslationKey("adaminite_belt");
	public static final ItemAdaminiteDress ADAMINITE_BOOTS = new ItemAdaminiteDress(EntityEquipmentSlot.FEET).setTranslationKey("adaminite_boots");
	
	public static final ItemMithminiteDress MITHMINITE_HOOD = new ItemMithminiteDress(EntityEquipmentSlot.HEAD).setTranslationKey("mithminite_hood");
	public static final ItemMithminiteDress MITHMINITE_ROBE = new ItemMithminiteDress(EntityEquipmentSlot.CHEST).setTranslationKey("mithminite_robe");
	public static final ItemMithminiteDress MITHMINITE_BELT = new ItemMithminiteDress(EntityEquipmentSlot.LEGS).setTranslationKey("mithminite_belt");
	public static final ItemMithminiteDress MITHMINITE_BOOTS = new ItemMithminiteDress(EntityEquipmentSlot.FEET).setTranslationKey("mithminite_boots");
	
	//
	
	public static final ItemDNASample ENTITY_CELL = new ItemDNASample();
	
	// VIS-BOUND ITEMS
	
	public static final ItemSaltEssence SALT_ESSENCE = new ItemSaltEssence();
	public static final ItemSealSymbol SEAL_SYMBOL = new ItemSealSymbol();
	public static final ItemVisPod VIS_POD = new ItemVisPod();
	public static final ItemVisSeeds VIS_SEEDS = new ItemVisSeeds();
}