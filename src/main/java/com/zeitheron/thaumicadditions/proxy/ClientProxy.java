package com.zeitheron.thaumicadditions.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicates;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.ColoredLightManager;
import com.zeitheron.hammercore.api.lighting.LightingBlacklist;
import com.zeitheron.hammercore.client.render.item.ItemRenderingHandler;
import com.zeitheron.hammercore.internal.blocks.base.IBlockHorizontal;
import com.zeitheron.hammercore.internal.blocks.base.IBlockOrientable;
import com.zeitheron.hammercore.proxy.RenderProxy_Client;
import com.zeitheron.hammercore.utils.NBTUtils;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.AspectUtil;
import com.zeitheron.thaumicadditions.api.EdibleAspect;
import com.zeitheron.thaumicadditions.api.fx.TARParticleTypes;
import com.zeitheron.thaumicadditions.blocks.BlockAbstractEssentiaJar.BlockAbstractJarItem;
import com.zeitheron.thaumicadditions.blocks.plants.BlockVisCrop;
import com.zeitheron.thaumicadditions.client.fx.FXColoredDrop;
import com.zeitheron.thaumicadditions.client.isr.ItemRenderJar;
import com.zeitheron.thaumicadditions.client.models.baked.BakedCropModel;
import com.zeitheron.thaumicadditions.client.render.entity.RenderEntityChester;
import com.zeitheron.thaumicadditions.client.render.entity.RenderEntityEssentiaShot;
import com.zeitheron.thaumicadditions.client.render.tile.TESRAspectCombiner;
import com.zeitheron.thaumicadditions.client.render.tile.TESRAuraCharger;
import com.zeitheron.thaumicadditions.client.render.tile.TESRAuraDisperser;
import com.zeitheron.thaumicadditions.client.render.tile.TESRCrystalBore;
import com.zeitheron.thaumicadditions.client.render.tile.TESRCrystalCrusher;
import com.zeitheron.thaumicadditions.client.render.tile.TESRFluxConcentrator;
import com.zeitheron.thaumicadditions.client.texture.TextureThaumonomiconBG;
import com.zeitheron.thaumicadditions.compat.ITARC;
import com.zeitheron.thaumicadditions.entity.EntityChester;
import com.zeitheron.thaumicadditions.entity.EntityEssentiaShot;
import com.zeitheron.thaumicadditions.events.ClientEventReactor;
import com.zeitheron.thaumicadditions.init.BlocksTAR;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.inventory.gui.GuiSealGlobe;
import com.zeitheron.thaumicadditions.items.ItemSealSymbol;
import com.zeitheron.thaumicadditions.items.ItemVisPod;
import com.zeitheron.thaumicadditions.items.seed.ItemVisSeeds;
import com.zeitheron.thaumicadditions.items.weapons.ItemEssentiaPistol;
import com.zeitheron.thaumicadditions.items.weapons.ItemShadowBeamStaff;
import com.zeitheron.thaumicadditions.items.weapons.ItemEssentiaPistol.ItemRendererEssentiaPistol;
import com.zeitheron.thaumicadditions.proxy.fx.FXHandler;
import com.zeitheron.thaumicadditions.proxy.fx.FXHandlerClient;
import com.zeitheron.thaumicadditions.tiles.TileAspectCombiner;
import com.zeitheron.thaumicadditions.tiles.TileAuraCharger;
import com.zeitheron.thaumicadditions.tiles.TileAuraDisperser;
import com.zeitheron.thaumicadditions.tiles.TileCrystalBore;
import com.zeitheron.thaumicadditions.tiles.TileCrystalCrusher;
import com.zeitheron.thaumicadditions.tiles.TileFluxConcentrator;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.misc.TileHole;

public class ClientProxy extends CommonProxy
{
	public static final NoiseGeneratorSimplex BASE_SIMPLEX = new NoiseGeneratorSimplex();
	
	@Override
	public void preInit()
	{
		ModelLoader.setCustomStateMapper(BlocksTAR.CRYSTAL_WATER, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());
		ModelLoader.setCustomStateMapper(BlocksTAR.ASPECT_COMBINER, new StateMap.Builder().ignore(IBlockHorizontal.FACING).build());
		ModelLoader.setCustomStateMapper(BlocksTAR.CRYSTAL_BORE, new StateMap.Builder().ignore(IBlockOrientable.FACING).build());
		
		RenderingRegistry.registerEntityRenderingHandler(EntityChester.class, RenderEntityChester::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEssentiaShot.class, RenderEntityEssentiaShot::new);
		
		OBJLoader.INSTANCE.addDomain(InfoTAR.MOD_ID);
	}
	
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(ClientEventReactor.REACTOR);
		
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
		
		for(BlockVisCrop blk : BlocksTAR.VIS_CROPS.values())
		{
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(blk::getColor, blk);
			blk.getBlockState().getValidStates().forEach(state -> RenderProxy_Client.bakedModelStore.putConstant(state, new BakedCropModel(state)));
		}
		
		LightingBlacklist.registerShadedTile(TileHole.class);
		LightingBlacklist.registerShadedTile(TileMirror.class);
		LightingBlacklist.registerShadedTile(TileMirrorEssentia.class);
		ColoredLightManager.addGenerator(partialTicks ->
		{
			EntityPlayer player = ColoredLightManager.getClientPlayer();
			if(player != null)
			{
				return player.world.tickableTileEntities.stream().filter(Predicates.instanceOf(TileInfusionMatrix.class)).map(te ->
				{
					ColoredLight light = null;
					TileInfusionMatrix im = (TileInfusionMatrix) te;
					if(im.active)
					{
						float mod = im.crafting ? 1F : 0.5F;
						float rad = (float) (BASE_SIMPLEX.getValue(im.count / 128F, 0) + 1.5F) * 5F;
						light = ColoredLight.builder().pos(te.getPos()).color(mod * 1F, mod * 0.5F, mod * 1F).radius(rad).build();
					}
					return light;
				});
			}
			return Stream.empty();
		});
		
		// Add custom TESRs
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileAuraDisperser.class, new TESRAuraDisperser());
		
		ItemRenderingHandler.INSTANCE.applyItemRender(new ItemRenderJar(), i -> i instanceof BlockAbstractJarItem || i instanceof BlockJarItem);
		ItemRenderingHandler.INSTANCE.applyItemRender(new ItemRendererEssentiaPistol(), i -> i instanceof ItemEssentiaPistol);
		
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
		
		TESRFluxConcentrator fc = new TESRFluxConcentrator();
		ClientRegistry.bindTileEntitySpecialRenderer(TileFluxConcentrator.class, fc);
		ItemRenderingHandler.INSTANCE.setItemRender(Item.getItemFromBlock(BlocksTAR.FLUX_CONCENTRATOR), fc);
		Minecraft.getMinecraft().getRenderItem().registerItem(Item.getItemFromBlock(BlocksTAR.FLUX_CONCENTRATOR), 0, "chest");
		
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
		
		Minecraft.getMinecraft().effectRenderer.registerParticle(TARParticleTypes.COLOR_DROP.getParticleID(), (particleID, worldIn, x, y, z, x2, y2, z2, args) ->
		{
			if(args.length < 1)
				return null;
			return new FXColoredDrop(worldIn, x, y, z, args[0]);
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
	
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre e)
	{
		TextureMap txMap = e.getMap();
		
		for(String tx0 : BakedCropModel.textures0)
			txMap.registerSprite(new ResourceLocation(tx0));
		for(String tx1 : BakedCropModel.textures1)
			txMap.registerSprite(new ResourceLocation(tx1));
	}
	
	private final List<Vec3d> shadowPositions = new ArrayList<>();
	
	@SubscribeEvent
	public void renderLast(RenderWorldLastEvent e)
	{
		EntityPlayer player = ColoredLightManager.getClientPlayer();
		ItemStack mainhand;
		if(player != null && !(mainhand = player.getHeldItemMainhand()).isEmpty() && mainhand.getItem() instanceof ItemShadowBeamStaff)
		{
			shadowPositions.clear();
			double cx = 0, cy = 0, cz = 0;
			ItemShadowBeamStaff.recursiveLoop(player, e.getPartialTicks(), shadowPositions, 80);
			Vec3d v = shadowPositions.get(0);
			shadowPositions.set(0, new Vec3d(v.x, v.y - 0.5, v.z));
			
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				float w = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
				short st = (short) GL11.glGetInteger(GL11.GL_LINE_STIPPLE);
				GL11.glLineWidth(4F);
				GL11.glLineStipple(1, (short) 0x2020);
				GL11.glEnable(GL11.GL_LINE_STIPPLE);
				GlStateManager.enableBlend();
				ColorHelper.glColor1ia(0x33FFFFFF);
				GlStateManager.disableTexture2D();
				GlStateManager.disableLighting();
				for(int i = 0; i < shadowPositions.size() - 1; ++i)
				{
					Vec3d pos = shadowPositions.get(i);
					Vec3d pos2 = shadowPositions.get(i + 1);
					
					GL11.glBegin(GL11.GL_LINES);
					GL11.glVertex3d(pos.x - cx, pos.y - cy, pos.z - cz);
					GL11.glVertex3d(pos2.x - cx, pos2.y - cy, pos2.z - cz);
					GL11.glEnd();
				}
				GL11.glLineWidth(w);
				GL11.glLineStipple(1, st);
				GlStateManager.enableLighting();
				GlStateManager.enableTexture2D();
				GlStateManager.popMatrix();
				GL11.glPopAttrib();
			}
		}
	}
	
	@Override
	public void viewSeal(TileSeal tile)
	{
		Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiSealGlobe(tile)));
	}
}