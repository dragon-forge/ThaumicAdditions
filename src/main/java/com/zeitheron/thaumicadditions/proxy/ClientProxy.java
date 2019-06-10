package com.zeitheron.thaumicadditions.proxy;

import javax.annotation.Nonnull;

import com.zeitheron.hammercore.client.render.item.ItemRenderingHandler;
import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import com.zeitheron.hammercore.internal.blocks.base.IBlockOrientable;
import com.zeitheron.hammercore.utils.NBTUtils;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.api.EdibleAspect;
import com.zeitheron.thaumicadditions.api.fx.TARParticleTypes;
import com.zeitheron.thaumicadditions.blocks.BlockAbstractEssentiaJar.BlockAbstractJarItem;
import com.zeitheron.thaumicadditions.client.ClientChainReactor;
import com.zeitheron.thaumicadditions.client.isr.ItemRenderJar;
import com.zeitheron.thaumicadditions.client.render.entity.RenderEntityChester;
import com.zeitheron.thaumicadditions.client.render.tile.TESRAspectCombiner;
import com.zeitheron.thaumicadditions.client.render.tile.TESRAuraCharger;
import com.zeitheron.thaumicadditions.client.render.tile.TESRAuraDisperser;
import com.zeitheron.thaumicadditions.client.render.tile.TESRCrystalBore;
import com.zeitheron.thaumicadditions.client.render.tile.TESRCrystalCrusher;
import com.zeitheron.thaumicadditions.client.texture.TextureThaumonomiconBG;
import com.zeitheron.thaumicadditions.compat.ITARC;
import com.zeitheron.thaumicadditions.entity.EntityChester;
import com.zeitheron.thaumicadditions.init.BlocksTAR;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.inventory.gui.GuiSealGlobe;
import com.zeitheron.thaumicadditions.items.ItemSealSymbol;
import com.zeitheron.thaumicadditions.items.ItemVisPod;
import com.zeitheron.thaumicadditions.items.ItemVisSeeds;
import com.zeitheron.thaumicadditions.proxy.fx.FXHandler;
import com.zeitheron.thaumicadditions.proxy.fx.FXHandlerClient;
import com.zeitheron.thaumicadditions.tiles.TileAspectCombiner;
import com.zeitheron.thaumicadditions.tiles.TileAuraCharger;
import com.zeitheron.thaumicadditions.tiles.TileAuraDisperser;
import com.zeitheron.thaumicadditions.tiles.TileCrystalBore;
import com.zeitheron.thaumicadditions.tiles.TileCrystalCrusher;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.blocks.essentia.BlockJarItem;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		ModelLoader.setCustomStateMapper(BlocksTAR.CRYSTAL_WATER, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());
		ModelLoader.setCustomStateMapper(BlocksTAR.ASPECT_COMBINER, new StateMap.Builder().ignore(IBlockHorizontal.FACING).build());
		ModelLoader.setCustomStateMapper(BlocksTAR.CRYSTAL_BORE, new StateMap.Builder().ignore(IBlockOrientable.FACING).build());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityChester.class, RenderEntityChester.FACTORY);
	}
	
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(ClientChainReactor.REACTOR);
		
		for(ITARC a : TAReconstructed.arcs)
			a.initClient();
		
		// Assign custom texture
		Minecraft.getMinecraft().getTextureManager().loadTickableTexture(TEXTURE_THAUMONOMICON_BG, new TextureThaumonomiconBG());
		
		// Adding custom color handlers
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemsTAR.SALT_ESSENCE::getItemColor, ItemsTAR.SALT_ESSENCE);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemVisPod::getColor, ItemsTAR.VIS_POD);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemVisSeeds::getColor, ItemsTAR.VIS_SEEDS);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, layer) ->
		{
			if(layer == 1)
			{
				AspectList al = EdibleAspect.getSalt(stack);
				return al.visSize() > 0 ? AspectUtil.getColor(al, true) : 0xFF0000;
			}
			return 0xFFFFFF;
		}, BlocksTAR.CAKE);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemsTAR.ENTITY_CELL::getColor, ItemsTAR.ENTITY_CELL);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(BlocksTAR.CRYSTAL_BLOCK::getColor, BlocksTAR.CRYSTAL_BLOCK.getItemBlock());
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, index) ->
		{
			Aspect a;
			return index == 0 && (a = ItemSealSymbol.getAspect(stack)) != null ? a.getColor() : 0xFFFFFF;
		}, ItemsTAR.SEAL_SYMBOL);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, layer) ->
		{
			if(layer == 1)
			{
				int color = 0xFF0000;
				
				if(stack.hasTagCompound())
				{
					int[] rgb = stack.getTagCompound().getIntArray("RGB");
					if(rgb != null && rgb.length >= 3)
						color = rgb[0] << 16 | rgb[1] << 8 | rgb[2];
				}
				
				return color;
			}
			
			return 0xFFFFFF;
		}, Item.getItemFromBlock(BlocksTAR.SEAL));
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(BlocksTAR.CRYSTAL_BLOCK::getColor, BlocksTAR.CRYSTAL_BLOCK);
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(BlocksTAR.VIS_CROPS::getColor, BlocksTAR.VIS_CROPS);
		
		// Add custom TESRs
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileAuraDisperser.class, new TESRAuraDisperser());
		
		ItemRenderingHandler.INSTANCE.applyItemRender(new ItemRenderJar(), i -> i instanceof BlockAbstractJarItem || i instanceof BlockJarItem);
		
		TESRAspectCombiner acom = new TESRAspectCombiner();
		ClientRegistry.bindTileEntitySpecialRenderer(TileAspectCombiner.class, acom);
		ItemRenderingHandler.INSTANCE.setItemRender(Item.getItemFromBlock(BlocksTAR.ASPECT_COMBINER), acom);
		Minecraft.getMinecraft().getRenderItem().registerItem(Item.getItemFromBlock(BlocksTAR.ASPECT_COMBINER), 0, "chest");
		
		TESRAuraCharger cha = new TESRAuraCharger();
		ClientRegistry.bindTileEntitySpecialRenderer(TileAuraCharger.class, cha);
		ItemRenderingHandler.INSTANCE.setItemRender(Item.getItemFromBlock(BlocksTAR.AURA_CHARGER), cha);
		Minecraft.getMinecraft().getRenderItem().registerItem(Item.getItemFromBlock(BlocksTAR.AURA_CHARGER), 0, "chest");
		
		TESRCrystalCrusher crycr = new TESRCrystalCrusher();
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalCrusher.class, crycr);
		ItemRenderingHandler.INSTANCE.setItemRender(Item.getItemFromBlock(BlocksTAR.CRYSTAL_CRUSHER), crycr);
		Minecraft.getMinecraft().getRenderItem().registerItem(Item.getItemFromBlock(BlocksTAR.CRYSTAL_CRUSHER), 0, "chest");
		
		TESRCrystalBore crybo = new TESRCrystalBore();
		ClientRegistry.bindTileEntitySpecialRenderer(TileCrystalBore.class, crybo);
		ItemRenderingHandler.INSTANCE.setItemRender(Item.getItemFromBlock(BlocksTAR.CRYSTAL_BORE), crybo);
		Minecraft.getMinecraft().getRenderItem().registerItem(Item.getItemFromBlock(BlocksTAR.CRYSTAL_BORE), 0, "chest");
		
		{
			ModelResourceLocation cryloc = new ModelResourceLocation(BlocksTAR.CRYSTAL_BLOCK.getRegistryName(), "normal");
			ModelLoader.setCustomStateMapper(BlocksTAR.CRYSTAL_BLOCK, new StateMapperBase()
			{
				@Override
				protected ModelResourceLocation getModelResourceLocation(IBlockState state)
				{
					return cryloc;
				}
			});
		}
		
		// Fluid state mapping.
		mapFluid(BlocksTAR.CRYSTAL_WATER);
	}
	
	@Override
	public void postInit()
	{
		Minecraft.getMinecraft().effectRenderer.registerParticle(TARParticleTypes.ITEMSTACK_CRACK.getParticleID(), (particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, args) -> new ParticleColoredBreaking(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, new ItemStack(NBTUtils.toNBT(args))));
		
		Minecraft.getMinecraft().effectRenderer.registerParticle(TARParticleTypes.POLLUTION.getParticleID(), (particleID, w, x, y, z, x2, y2, z2, args) ->
		{
			FXGeneric fb = new FXGeneric(w, x, y, z, (w.rand.nextFloat() - w.rand.nextFloat()) * 0.005, 0.02, (w.rand.nextFloat() - w.rand.nextFloat()) * 0.005);
			fb.setMaxAge(400 + w.rand.nextInt(100));
			fb.setRBGColorF(1F, .3F, .9F);
			fb.setAlphaF(.5F, 0);
			fb.setGridSize(16);
			fb.setParticles(56, 1, 1);
			fb.setScale(2, 5);
			fb.setLayer(1);
			fb.setSlowDown(1);
			fb.setWind(0.001);
			fb.setRotationSpeed(w.rand.nextFloat(), w.rand.nextBoolean() ? -1 : 1);
			ParticleEngine.addEffect(w, fb);
			return null;
		});
		
		Minecraft.getMinecraft().effectRenderer.registerParticle(TARParticleTypes.COLOR_CLOUD.getParticleID(), (particleID, worldIn, x, y, z, x2, y2, z2, args) ->
		{
			int red = args.length > 1 ? args[0] : 255;
			int green = args.length > 2 ? args[1] : 255;
			int blue = args.length > 3 ? args[2] : 255;
			int alpha = args.length > 4 ? args[3] : 0;
			int a = 200 + worldIn.rand.nextInt(100);
			FXGeneric fb = new FXGeneric(worldIn, x, y, z, (x2 - x) / (a * .9), (y2 - y) / (a * .9), (z2 - z) / (a * .9));
			fb.setMaxAge(a);
			fb.setRBGColorF(red / 255F, green / 255F, blue / 255F);
			fb.setAlphaF(alpha == 0 ? .3F : alpha / 255F, 0);
			fb.setGridSize(16);
			fb.setParticles(56, 1, 1);
			fb.setScale(2, 5);
			fb.setLayer(0);
			fb.setSlowDown(1);
			fb.setNoClip(args.length > 5 ? args[4] > 0 : false);
			fb.setRotationSpeed(worldIn.rand.nextFloat(), worldIn.rand.nextBoolean() ? -1 : 1);
			ParticleEngine.addEffect(worldIn, fb);
			return null;
		});
	}
	
	@Override
	public int getItemColor(ItemStack stack, int layer)
	{
		return Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, layer);
	}
	
	@Override
	protected FXHandler createFX()
	{
		return new FXHandlerClient();
	}
	
	private static void mapFluid(BlockFluidBase fluidBlock)
	{
		final Item item = Item.getItemFromBlock(fluidBlock);
		assert item != null;
		ModelBakery.registerItemVariants(item);
		ModelResourceLocation modelResourceLocation = new ModelResourceLocation(InfoTAR.MOD_ID + ":fluid", fluidBlock.getFluid().getName());
		ModelLoader.setCustomMeshDefinition(item, stack -> modelResourceLocation);
		ModelLoader.setCustomStateMapper(fluidBlock, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return modelResourceLocation;
			}
		});
	}
	
	public static class ParticleColoredBreaking extends ParticleBreaking
	{
		protected ParticleColoredBreaking(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, ItemStack stack)
		{
			super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn, stack.getItem(), stack.getItemDamage());
			int color = TAReconstructed.proxy.getItemColor(stack, 0);
			setRBGColorF(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color));
		}
	}
	
	@Nonnull
	public static TextureAtlasSprite getSprite(String path)
	{
		TextureMap m = Minecraft.getMinecraft().getTextureMapBlocks();
		TextureAtlasSprite s = m.getTextureExtry(path);
		if(s == null)
			s = m.getAtlasSprite(path);
		return s != null ? s : m.getMissingSprite();
	}
	
	@Override
	public void viewSeal(TileSeal tile)
	{
		Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiSealGlobe(tile)));
	}
}