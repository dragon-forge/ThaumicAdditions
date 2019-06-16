package com.zeitheron.thaumicadditions.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.render.shader.ShaderProgram;
import com.zeitheron.hammercore.client.render.shader.impl.ShaderEnderField;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.thaumicadditions.api.AttributesTAR;
import com.zeitheron.thaumicadditions.api.EdibleAspect;
import com.zeitheron.thaumicadditions.items.armor.ItemMithminiteDress;
import com.zeitheron.thaumicadditions.utils.Foods;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.client.fx.particles.ParticleHooksTAR;

public class ClientChainReactor
{
	private final List<LinkedList<ChainReaction>> CHAINS = new ArrayList<>();
	public static final ClientChainReactor REACTOR = new ClientChainReactor();
	
	public void addChain(ChainReaction... sequence)
	{
		CHAINS.add(new LinkedList<>(Arrays.asList(sequence)));
	}
	
	public void addChain(Collection<ChainReaction> sequence)
	{
		CHAINS.add(new LinkedList<>(sequence));
	}
	
	public void updateChains()
	{
		if(Minecraft.getMinecraft().world == null)
			CHAINS.clear();
		
		for(int i = 0; i < CHAINS.size(); ++i)
		{
			LinkedList<ChainReaction> ll = CHAINS.get(i);
			if(ll.isEmpty())
				CHAINS.remove(i);
			else
				while(!ll.isEmpty() && !ll.getFirst().update())
					ll.removeFirst();
		}
	}
	
	final List<Particle> sounding = new ArrayList<>();
	final List<BlockPos> excludesRAJ = new ArrayList<>();
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent cte)
	{
		if(!Minecraft.getMinecraft().isGamePaused() && cte.phase == Phase.START)
			updateChains();
		
		World world = Minecraft.getMinecraft().world;
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack head;
		if(world != null && player != null && !(head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).isEmpty() && head.getItem() instanceof ItemMithminiteDress)
		{
			Field particles = ParticleEngine.class.getDeclaredFields()[2];
			particles.setAccessible(true);
			try
			{
				HashMap<Integer, ArrayList<Particle>>[] ps = (HashMap<Integer, ArrayList<Particle>>[]) particles.get(null);
				
				sounding.clear();
				for(HashMap<Integer, ArrayList<Particle>> effects : ps)
				{
					ArrayList<Particle> listParticles = effects.get(world.provider.getDimension());
					if(listParticles != null)
						for(Particle fx : listParticles)
						{
							if(fx instanceof FXGeneric)
							{
								FXGeneric fxg = (FXGeneric) fx;
								if(ParticleHooksTAR.isSoundingFX(fxg))
									sounding.add(fxg);
							}
						}
				}
			} catch(IllegalArgumentException | IllegalAccessException e1)
			{
				e1.printStackTrace();
			}
		} else
			sounding.clear();
	}
	
	/**
	 * SoundEvent handling, used to make sounds louder with a special potion
	 * effect.
	 */
	@SubscribeEvent
	public void soundEvent(PlaySoundEvent e)
	{
		if(e.getSound() instanceof PositionedSound)
		{
			PositionedSound ps = (PositionedSound) e.getSound();
			Field volume = PositionedSound.class.getDeclaredFields()[4];
			volume.setAccessible(true);
			if(volume.getType() == float.class)
				try
				{
					float f = volume.getFloat(ps);
					
					// Adjust volume
					if(Minecraft.getMinecraft().player != null)
					{
						IAttributeInstance ss = Minecraft.getMinecraft().player.getEntityAttribute(AttributesTAR.SOUND_SENSIVITY);
						f *= (float) ss.getAttributeValue();
					}
					
					volume.setFloat(ps, f);
				} catch(Throwable err)
				{
					err.printStackTrace();
				}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void tooltipEvent(ItemTooltipEvent e)
	{
		ItemStack stack = e.getItemStack();
		
		AspectList salt;
		if(!stack.isEmpty() && Foods.isFood(stack.getItem()) && (salt = EdibleAspect.getSalt(stack)).visSize() > 0)
		{
			e.getToolTip().add("Vis: " + salt.visSize() + "/" + EdibleAspect.MAX_ESSENTIA);
			for(Aspect a : salt.getAspectsSortedByName())
				e.getToolTip().add(a.getName() + " x" + salt.getAmount(a));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void renderWorldLast(RenderWorldLastEvent e)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player == null)
			return;
		BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.translate(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
		
		UtilsFX.bindTexture("minecraft", "textures/entity/end_portal.png");
		if(ShaderEnderField.endShader == null)
			ShaderEnderField.reloadShader();
		ShaderEnderField.endShader.freeBindShader();
		ARBShaderObjects.glUniform4fARB(ShaderEnderField.endShader.getUniformLoc("color"), 0.044F, 0.036F, 0.063F, .2F);
		
		GlStateManager.disableDepth();
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		World world = Minecraft.getMinecraft().world;
		excludesRAJ.clear();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		for(int i = 0; i < sounding.size(); ++i)
		{
			Particle s = sounding.get(i);
			renderAllAdjacent(world, new BlockPos(s.posX, s.posY, s.posZ), bb, brd);
		}
		excludesRAJ.sort((a, b) ->
		{
			double da = a.distanceSq(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ) * 100;
			double db = b.distanceSq(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ) * 100;
			return (int) (db - da);
		});
		for(BlockPos pos : excludesRAJ)
			brd.renderBlock(world.getBlockState(pos), pos, world, bb);
		Tessellator.getInstance().draw();
		ShaderProgram.unbindShader();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}
	
	private void renderAllAdjacent(World world, BlockPos pos, BufferBuilder bb, BlockRendererDispatcher brd)
	{
		if(excludesRAJ.contains(pos) || excludesRAJ.size() >= 1024)
			return;
		excludesRAJ.add(pos);
		Block block = world.getBlockState(pos).getBlock();
		for(EnumFacing face : EnumFacing.VALUES)
		{
			BlockPos rem = pos.offset(face);
			if(block == world.getBlockState(rem).getBlock())
				renderAllAdjacent(world, rem, bb, brd);
		}
	}
}