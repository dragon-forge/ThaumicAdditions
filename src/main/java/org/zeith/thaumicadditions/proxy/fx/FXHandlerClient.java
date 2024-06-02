package org.zeith.thaumicadditions.proxy.fx;

import com.zeitheron.hammercore.proxy.ParticleProxy_Client;
import com.zeitheron.hammercore.utils.FrictionRotator;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.client.fx.FXColoredDrop;
import org.zeith.thaumicadditions.tiles.TileAuraDisperser;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;

import java.awt.*;
import java.util.Random;

public class FXHandlerClient
		extends FXHandler
{
	@Override
	public void spawnAuraDisperserFX(TileAuraDisperser tile)
	{
		if(tile == null)
			return;
		
		if(!tile.getWorld().isRemote)
		{
			// Invoke server-side code.
			super.spawnAuraDisperserFX(tile);
			return;
		}
		
		Random random = tile.getRNG();
		EnumFacing face = WorldUtil.getFacing(tile.getLocation().getState());
		BlockPos blockPosIn = tile.getPos();
		
		int data = AspectUtil.getColor(tile.aspects, true);
		
		float f5 = (float) (data >> 16 & 255) / 255.0F;
		float f = (float) (data >> 8 & 255) / 255.0F;
		float f1 = (float) (data >> 0 & 255) / 255.0F;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		AxisAlignedBB aabb = new AxisAlignedBB(blockPosIn).offset(face.getXOffset() * 5, face.getYOffset() * 4, face.getZOffset() * 5).grow(4, 3, 4);
		
		for(int j3 = 0; j3 < 100; ++j3)
		{
			double d18 = random.nextDouble() * 8D;
			double d21 = Math.toRadians((j3 / 100D * 360D) % 360D);
			
			double sin = Math.sin(d21) / 2.25;
			double cos = Math.cos(d21) / 2.25;
			
			double x = aabb.minX + (aabb.maxX - aabb.minX) * random.nextDouble();
			double y = aabb.minY + (aabb.maxY - aabb.minY) * random.nextDouble();
			double z = aabb.minZ + (aabb.maxZ - aabb.minZ) * random.nextDouble();
			
			Particle particle1 = mc.effectRenderer.spawnEffectParticle((random.nextBoolean() ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL).getParticleID(), x, y, z, 0, 0, 0);
			
			if(particle1 != null)
			{
				double d3 = particle1.posX - mc.player.posX;
				double d4 = particle1.posY - mc.player.posY;
				double d5 = particle1.posZ - mc.player.posZ;
				if(d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D)
					particle1.setExpired();
				
				float f4 = 0.75F + random.nextFloat() * 0.25F;
				particle1.setRBGColorF(f5 * f4, f * f4, f1 * f4);
				particle1.multiplyVelocity((float) d18);
			}
		}
	}
	
	@Override
	public void spawnItemCrack(World world, Vec3d pos, Vec3d motion, ItemStack stack)
	{
		if(!world.isRemote)
		{
			super.spawnItemCrack(world, pos, motion, stack);
			return;
		}
		
		class ParticleColoredBreaking
				extends ParticleBreaking
		{
			protected ParticleColoredBreaking(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, ItemStack stack)
			{
				super(worldIn, posXIn, posYIn, posZIn, xSpeedIn, ySpeedIn, zSpeedIn, stack.getItem(), stack.getItemDamage());
				int color = TAReconstructed.proxy.getItemColor(stack, 0);
				setRBGColorF(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color));
			}
		}
		
		ParticleProxy_Client.queueParticleSpawn(new ParticleColoredBreaking(world, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z, stack));
	}
	
	@Override
	public void spawnColorCloud(World world, Vec3d pos, Vec3d targetPos, Color color, boolean noclip)
	{
		if(!world.isRemote)
		{
			super.spawnColorCloud(world, pos, targetPos, color, noclip);
			return;
		}
		
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int alpha = color.getAlpha();
		int a = 100 + world.rand.nextInt(50);
		FXGeneric fb = new FXGeneric(world,
				pos.x, pos.y, pos.z,
				(targetPos.x - pos.x) / (a * .9),
				(targetPos.y - pos.y) / (a * .9),
				(targetPos.z - pos.z) / (a * .9)
		);
		fb.setMaxAge(a);
		fb.setRBGColorF(red / 255F, green / 255F, blue / 255F);
		fb.setAlphaF(alpha == 0 ? .3F : alpha / 255F, 0);
		fb.setGridSize(16);
		fb.setParticles(56, 1, 1);
		fb.setScale(2, 5);
		fb.setLayer(0);
		fb.setSlowDown(1);
		fb.setNoClip(noclip);
		fb.setRotationSpeed(world.rand.nextFloat(), world.rand.nextBoolean() ? -1 : 1);
		ParticleEngine.addEffect(world, fb);
	}
	
	@Override
	public void spawnColorDrop(World world, Vec3d pos, Vec3d targetPos, Color color)
	{
		if(!world.isRemote)
		{
			super.spawnColorDrop(world, pos, targetPos, color);
			return;
		}
		
		ParticleProxy_Client.queueParticleSpawn(new FXColoredDrop(world, pos.x, pos.y, pos.z, color.getRGB()));
	}
	
	@Override
	public void spawnPollution(World world, Vec3d pos, Vec3d targetPos)
	{
		if(!world.isRemote)
		{
			super.spawnPollution(world, pos, targetPos);
			return;
		}
		
		FXGeneric fb = new FXGeneric(world, pos.x, pos.y, pos.z, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.005, 0.02, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.005);
		fb.setMaxAge(400 + world.rand.nextInt(100));
		fb.setRBGColorF(1F, .3F, .9F);
		fb.setAlphaF(.5F, 0);
		fb.setGridSize(16);
		fb.setParticles(56, 1, 1);
		fb.setScale(2, 5);
		fb.setLayer(1);
		fb.setSlowDown(1);
		fb.setWind(0.001);
		fb.setRotationSpeed(world.rand.nextFloat(), world.rand.nextBoolean() ? -1 : 1);
		ParticleEngine.addEffect(world, fb);
	}
	
	@Override
	public void renderMob(Entity entity, FrictionRotator rotator, double posX, double posY, double posZ, float partialTicks)
	{
		if(entity == null) return;
		
		GlStateManager.pushMatrix();
		float f = 0.53125F / Math.max(entity.width, entity.height);
		GlStateManager.translate(0.0F, 0.4F, 0.0F);
		GlStateManager.rotate(rotator.getActualRotation(partialTicks), 0F, 1F, 0F);
		GlStateManager.translate(0F, -.2F, 0F);
		GlStateManager.rotate(-30F, 1F, 0F, 0F);
		GlStateManager.scale(f, f, f);
		entity.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
		Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
		GlStateManager.popMatrix();
	}
}