package com.zeitheron.thaumicadditions;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.hammercore.utils.HammerCoreUtils;
import com.zeitheron.thaumicadditions.api.AttributesTAR;
import com.zeitheron.thaumicadditions.init.BlocksTAR;
import com.zeitheron.thaumicadditions.init.FluidsTAR;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.init.KnowledgeTAR;
import com.zeitheron.thaumicadditions.init.PotionsTAR;
import com.zeitheron.thaumicadditions.init.RecipesTAR;
import com.zeitheron.thaumicadditions.init.SealsTAR;
import com.zeitheron.thaumicadditions.proxy.CommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.entities.monster.EntityPech;

@Mod(modid = InfoTAR.MOD_ID, name = InfoTAR.MOD_NAME, version = InfoTAR.MOD_VERSION, certificateFingerprint = "4d7b29cd19124e986da685107d16ce4b49bc0a97", dependencies = "required-after:hammercore;required-after:thaumcraft@[6.1.BETA26,);before:iceandfire", updateJSON = "https://pastebin.com/raw/G6DJNXqg")
public class TAReconstructed
{
	public static final Logger LOG = LogManager.getLogger(InfoTAR.MOD_ID);
	
	@Instance
	public static TAReconstructed instance;
	
	public static CreativeTabs tab;
	
	@SidedProxy(serverSide = "com.zeitheron.thaumicadditions.proxy.CommonProxy", clientSide = "com.zeitheron.thaumicadditions.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	public static ResearchCategory RES_CAT;
	
	@EventHandler
	public void certificateViolation(FMLFingerprintViolationEvent e)
	{
		LOG.warn("*****************************");
		LOG.warn("WARNING: Somebody has been tampering with Thaumic Additions (Reconstructed) jar!");
		LOG.warn("It is highly recommended that you redownload mod from https://minecraft.curseforge.com/projects/247401 !");
		LOG.warn("*****************************");
		HammerCore.invalidCertificates.put(InfoTAR.MOD_ID, "https://minecraft.curseforge.com/projects/232564");
	}
	
	@EventHandler
	public void construct(FMLConstructionEvent e)
	{
		MinecraftForge.EVENT_BUS.register(proxy);
		proxy.construct();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		tab = HammerCoreUtils.createStaticIconCreativeTab(InfoTAR.MOD_ID, new ItemStack(ItemsTAR.MITHMINITE_INGOT));
		
		FluidsTAR.init.call();
		MinecraftForge.EVENT_BUS.register(this);
		
		ModMetadata meta = e.getModMetadata();
		meta.autogenerated = false;
		meta.version = InfoTAR.MOD_VERSION;
		meta.modId = InfoTAR.MOD_ID;
		meta.name = InfoTAR.MOD_NAME;
		meta.authorList = HammerCore.AUTHORS;
		
		SimpleRegistration.registerFieldItemsFrom(ItemsTAR.class, InfoTAR.MOD_ID, tab);
		SimpleRegistration.registerFieldBlocksFrom(BlocksTAR.class, InfoTAR.MOD_ID, tab);
		
		KnowledgeTAR.clInit.call();
		
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		proxy.init();
		RES_CAT = ResearchCategories.registerCategory("THAUMADDITIONS", "UNLOCKINFUSION", new AspectList().add(Aspect.ALCHEMY, 30).add(Aspect.FLUX, 10).add(Aspect.MAGIC, 10).add(Aspect.LIFE, 5).add(Aspect.AVERSION, 5).add(Aspect.DESIRE, 5).add(Aspect.WATER, 5), new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/thaumonomicon_icon.png"), CommonProxy.TEXTURE_THAUMONOMICON_BG, new ResourceLocation(InfoTAR.MOD_ID, "textures/gui/gui_research_back_over.png"));
		RecipesTAR.init.call();
		PotionsTAR.register.call();
		SealsTAR.init();
		
		WeightedRandomLoot.lootBagRare.add(new WeightedRandomLoot(new ItemStack(ItemsTAR.ZEITH_SCALES), 1));
		EntityPech.tradeInventory.get(2).add(Arrays.asList(5, new ItemStack(ItemsTAR.ZEITH_SCALES)));
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
		KnowledgeTAR.init.call();
		KnowledgeTAR.insertAspects.call();
		RecipesTAR.postInit.call();
	}
	
	@SubscribeEvent
	public void entityInit(EntityEvent.EntityConstructing e)
	{
		if(e.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntity();
			p.getAttributeMap().registerAttribute(AttributesTAR.SOUND_SENSIVITY);
		}
	}
}