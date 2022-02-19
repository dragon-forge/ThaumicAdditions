package org.zeith.thaumicadditions.events;

import com.zeitheron.hammercore.client.render.shader.ShaderProgram;
import com.zeitheron.hammercore.client.render.shader.impl.ShaderEnderField;
import com.zeitheron.hammercore.client.render.world.VirtualWorld;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.net.HCNet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.AttributesTAR;
import org.zeith.thaumicadditions.api.EdibleAspect;
import org.zeith.thaumicadditions.client.HudHandlerHookTAR;
import org.zeith.thaumicadditions.client.ParticleHooksTAR;
import org.zeith.thaumicadditions.items.ItemSealSymbol;
import org.zeith.thaumicadditions.items.armor.ItemMithminiteDress;
import org.zeith.thaumicadditions.items.tools.ItemVoidThaumometer;
import org.zeith.thaumicadditions.net.PacketLeftClick;
import org.zeith.thaumicadditions.tiles.TileSeal;
import org.zeith.thaumicadditions.utils.Foods;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.Utils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;

public class ClientEventReactor
{
	public static final ClientEventReactor REACTOR = new ClientEventReactor();

	final List<Particle> sounding = new ArrayList<>();
	final List<BlockPos> excludesRAJ = new ArrayList<>();
	final VirtualWorld ores = new VirtualWorld();
	boolean voidThaumometer;
	WeakReference<TileSeal> seal_MH;
	int sealHoverTime_MH;
	private boolean keyBindAttack;
	private final Map<EntityEquipmentSlot, ItemStack> armor = new HashMap<>();

	public static void translatePlayerIrrelative(double x, double y, double z)
	{
		GlStateManager.translate(x - TileEntityRendererDispatcher.staticPlayerX, y - TileEntityRendererDispatcher.staticPlayerY, z - TileEntityRendererDispatcher.staticPlayerZ);
	}

	@SubscribeEvent
	public void clientTick(ClientTickEvent cte)
	{
		if(cte.phase == Phase.START)
			return;

		World world = Minecraft.getMinecraft().world;
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack head;
		boolean mithminiteHelm = false;
		if(world != null && player != null && !(head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)).isEmpty() && head.getItem() instanceof ItemMithminiteDress)
		{
			mithminiteHelm = true;

			Profiler prof = Minecraft.getMinecraft().profiler;
			prof.startSection("sounding_figure_ores");

			Field particles = ParticleEngine.class.getDeclaredFields()[2];
			particles.setAccessible(true);
			try
			{
				HashMap<Integer, ArrayList<Particle>>[] ps = (HashMap<Integer, ArrayList<Particle>>[]) particles.get(null);

				int pss = sounding.size();

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

				if(pss != sounding.size())
				{
					excludesRAJ.clear();

					prof.startSection("render");
					for(int i = 0; i < sounding.size(); ++i)
					{
						Particle s = sounding.get(i);
						renderAllAdjacent(world, new BlockPos(s.posX, s.posY, s.posZ), null, null);
					}
					prof.endStartSection("sort");
					excludesRAJ.sort((a, b) ->
					{
						double da = a.distanceSq(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ) * 100;
						double db = b.distanceSq(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ) * 100;
						return (int) (db - da);
					});
					prof.endSection();
				}
			} catch(IllegalArgumentException | IllegalAccessException | ConcurrentModificationException e1)
			{
			}

			prof.endSection();
		} else
		{
			sounding.clear();
			excludesRAJ.clear();
		}

		if(world != null)
		{
			KeyBinding kb = Minecraft.getMinecraft().gameSettings.keyBindAttack;
			if(kb.isKeyDown() != keyBindAttack)
			{
				keyBindAttack = kb.isKeyDown();
				HCNet.INSTANCE.sendToServer(new PacketLeftClick(keyBindAttack));
			}
		}

		RayTraceResult over;
		if(mithminiteHelm && (over = Minecraft.getMinecraft().objectMouseOver) != null && over.typeOfHit == Type.BLOCK && world.getTileEntity(over.getBlockPos()) instanceof TileSeal)
		{
			if(sealHoverTime_MH < 20)
				++sealHoverTime_MH;
			else
				seal_MH = new WeakReference<>((TileSeal) world.getTileEntity(over.getBlockPos()));
		} else
		{
			seal_MH = null;
			sealHoverTime_MH = 0;
		}
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
			e.getToolTip().add(I18n.format("tooltip." + InfoTAR.MOD_ID + ":vis", salt.visSize(), EdibleAspect.MAX_ESSENTIA));
			for(Aspect a : salt.getAspectsSortedByName())
				e.getToolTip().add(a.getName() + " x" + salt.getAmount(a));
		}

		if(stack.getItem() instanceof ItemArmor && stack.hasTagCompound() && stack.getTagCompound().getBoolean("TAR_PHANTOM"))
			e.getToolTip().add(TextFormatting.DARK_AQUA + I18n.format("tooltip." + InfoTAR.MOD_ID + ":phantom"));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void renderWorldLast(RenderWorldLastEvent e)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player == null)
			return;
		Profiler prof = Minecraft.getMinecraft().profiler;
		prof.startSection("sounding_render_ores");
		BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		World world = Minecraft.getMinecraft().world;
		BlockPos origin = excludesRAJ.isEmpty() ? BlockPos.ORIGIN : excludesRAJ.get(0);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		translatePlayerIrrelative(0, 0, 0);
		UtilsFX.bindTexture("minecraft", "textures/entity/end_portal.png");
		if(ShaderEnderField.endShader == null)
			ShaderEnderField.reloadShader();
		ShaderEnderField.endShader.freeBindShader();
		ARBShaderObjects.glUniform4fARB(ShaderEnderField.endShader.getUniformLoc("color"), 0.044F, 0.036F, 0.063F, .2F);
		GlStateManager.disableDepth();

		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		int c = excludesRAJ.size();
		for(int i = 0; i < c; ++i)
		{
			BlockPos pos = excludesRAJ.get(i);
			IBlockState state = world.getBlockState(pos).getActualState(world, pos);
			IBakedModel model = brd.getModelForState(state);
			state = state.getBlock().getExtendedState(state, world, pos);
			brd.getBlockModelRenderer().renderModel(ores, model, state, pos, bb, true);
		}
		Tessellator.getInstance().draw();

		ShaderProgram.unbindShader();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
		prof.endSection();

		if(voidThaumometer)
		{
			EntityFluxRift rift = ItemVoidThaumometer.getSelectedRift();
			if(rift != null)
			{
				double x = 0, y = 0, z = 0;

				for(Vec3d p : rift.points)
				{
					x += p.x;
					y += p.y;
					z += p.z;
				}

				int np = rift.points.size();

				x /= np;
				y /= np;
				z /= np;

				x += rift.posX;
				y += rift.posY + 1;
				z += rift.posZ;

				if(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer)
				{
					String text = I18n.format("stability." + rift.getStability()) + (rift.getCollapse() ? ", Collapsing..." : "");
					float scale = 1.5F;

					float partialTicks = e.getPartialTicks();
					player = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();
					double iPX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
					double iPY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
					double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;
					GL11.glPushMatrix();
					GL11.glTranslated(-iPX + x + 0.5, -iPY + y + 0.5, -iPZ + z + 0.5);
					float xd = (float) (iPX - (x + 0.5));
					float zd = (float) (iPZ - (z + 0.5));
					float rotYaw = (float) (Math.atan2(xd, zd) * 180.0 / 3.141592653589793);
					GL11.glRotatef(rotYaw + 180.0f, 0.0f, 1.0f, 0.0f);
					GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
					GL11.glScalef(0.02f * scale, 0.02f * scale, 0.02f * scale);
					int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
					GL11.glEnable(3042);
					GL11.glBlendFunc(770, 771);
					Minecraft.getMinecraft().fontRenderer.drawString(text, 1 - sw / 2, 1, 1118481);
					GL11.glTranslated(0.0, 0.0, -0.1);
					Minecraft.getMinecraft().fontRenderer.drawString(text, (-sw) / 2, 0, 16777215);
					GL11.glPopMatrix();
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void preRenderPlayer(Pre e)
	{
		EntityPlayer player = e.getEntityPlayer();
		for(EntityEquipmentSlot s : EntityEquipmentSlot.values())
			if(s.getSlotIndex() != 0 && s.getSlotIndex() != 5)
			{
				ItemStack stack = player.getItemStackFromSlot(s);
				if(!stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().getBoolean("TAR_PHANTOM"))
				{
					player.inventory.armorInventory.set(s.getIndex(), ItemStack.EMPTY);
					armor.put(s, stack);
				}
			}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void postRenderPlayer(Post e)
	{
		EntityPlayer player = e.getEntityPlayer();
		for(EntityEquipmentSlot s : EntityEquipmentSlot.values())
			if(s.getSlotIndex() != 0 && s.getSlotIndex() != 5 && armor.containsKey(s))
			{
				player.inventory.armorInventory.set(s.getIndex(), armor.remove(s));
			}
	}

	@SubscribeEvent
	public void renderTick(RenderTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			Minecraft mc = FMLClientHandler.instance().getClient();
			if(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();
				long time = System.currentTimeMillis();
				if(player != null)
					renderHuds(mc, event.renderTickTime, player, time);
			}
		}
	}

	@SideOnly(value = Side.CLIENT)
	void renderHuds(Minecraft mc, float renderTickTime, EntityPlayer player, long time)
	{
		GL11.glPushMatrix();
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		GL11.glClear(256);
		GL11.glMatrixMode(5889);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
		int ww = sr.getScaledWidth();
		int hh = sr.getScaledHeight();
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		if(mc.inGameHasFocus && Minecraft.isGuiEnabled())
		{
			UtilsFX.bindTexture("thaumcraft", "textures/gui/hud.png");
			ItemStack handStack = player.getHeldItemMainhand();
			boolean rT = false;
			for(int a = 0; a < 2; ++a)
			{
				if(handStack != null && !handStack.isEmpty())
				{
					if(!rT && handStack.getItem() instanceof ItemVoidThaumometer)
					{
						HudHandlerHookTAR.renderThaumometer(mc, renderTickTime, player, time, ww, hh, 0);
						rT = true;
					}
				}
				handStack = player.getHeldItemOffhand();
			}
			voidThaumometer = rT;

			TileSeal seal;
			if(seal_MH != null && (seal = seal_MH.get()) != null)
			{
				int xSize = 192;
				int ySize = 96;
				int guiLeft = (sr.getScaledWidth() - xSize) / 2;
				int guiTop = (sr.getScaledHeight() - ySize) / 2;

				float size = 32F;
				int total = 3;

				double gap = xSize / total;

				for(int i = 0; i < total; ++i)
				{
					Aspect s = seal.getSymbol(i);
					UtilsFX.bindTexture(InfoTAR.MOD_ID, "textures/gui/widgets.png");
					GlStateManager.pushMatrix();
					float x = (float) (guiLeft + (xSize - size) / 2 + gap * (i - 1));
					float y = (float) guiTop;
					GlStateManager.translate(x, y, 0);
					GlStateManager.scale(size / 16F, size / 16F, size / 16F);
					RenderUtil.drawTexturedModalRect(0, 0, 0, 16, 16, 16);
					if(s == null)
						RenderUtil.drawTexturedModalRect(0, 0, 0, 32, 16, 16);
					else
					{
						ItemStack is = ItemSealSymbol.createItem(s, 1);
						mc.getRenderItem().renderItemAndEffectIntoGUI(is, 0, 0);
						GlStateManager.pushMatrix();
						GlStateManager.translate(9.5, 9.5, 350);
						GlStateManager.scale(1 / 3., 1 / 3., 1 / 3.);
						thaumcraft.client.lib.UtilsFX.drawTag(0, 0, s, 0F, 0, 0., 771, 1F, false);
						GlStateManager.popMatrix();
					}
					GlStateManager.popMatrix();
				}

				String text;

				if(seal.combination == null)
					text = I18n.format("seal." + InfoTAR.MOD_ID + ":none");
				else
				{
					String d = seal.combination.getDescription(seal);
					if(d != null)
						text = d;
					else
						text = I18n.format("seal." + InfoTAR.MOD_ID + ":unconfigured", seal.combination.getModName(), seal.combination.getAuthor());
				}

				FontRenderer fontRenderer = mc.fontRenderer;

				int width = Math.min(fontRenderer.getStringWidth(text), sr.getScaledWidth() / 2);

				fontRenderer.drawSplitString(text, (sr.getScaledWidth() - width) / 2 + 1, guiTop + 37 + (ySize - 36) / 2, width, 0xFF444444);
				fontRenderer.drawSplitString(text, (sr.getScaledWidth() - width) / 2, guiTop + 36 + (ySize - 36) / 2, width, 0xFFFFFFFF);
			}
		}
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}

	private void renderAllAdjacent(World world, BlockPos pos, BufferBuilder bb, BlockRendererDispatcher brd)
	{
		if(excludesRAJ.size() >= 8192 || excludesRAJ.contains(pos) || !Utils.isOreBlock(world, pos))
			return;
		excludesRAJ.add(pos);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		ores.setBlockState(pos, state);
		ores.setTileEntity(pos, world.getTileEntity(pos));
		for(EnumFacing face : EnumFacing.VALUES)
		{
			BlockPos rem = pos.offset(face);
			if(block == world.getBlockState(rem).getBlock() && !excludesRAJ.contains(rem))
				renderAllAdjacent(world, rem, bb, brd);
		}
	}

	@SubscribeEvent
	public void fixFOV(FOVUpdateEvent e)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null && player.getEntityData().getInteger("TAR_LockFOV") > 0)
			e.setNewfov(1F);
	}
}