package org.zeith.thaumicadditions.proxy.fx;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.FrictionRotator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.net.fxh.*;
import org.zeith.thaumicadditions.tiles.TileAuraDisperser;

import java.awt.*;

public class FXHandler
{
	public void spawnAuraDisperserFX(TileAuraDisperser tile)
	{
		if(tile == null)
			return;
		HCNet.INSTANCE.sendToAllAroundTracking(new FXHPacket(tile.getPos(), 0), HCNet.point(tile.getWorld(), new Vec3d(tile.getPos()), 32));
	}
	
	public void spawnItemCrack(World world, Vec3d pos, Vec3d motion, ItemStack stack)
	{
		HCNet.INSTANCE.sendToAllAroundTracking(new PacketSpawnItemCrack(pos, motion, stack), HCNet.point(world, pos, 32));
	}
	
	public void spawnColorCloud(World world, Vec3d pos, Vec3d targetPos, Color color, boolean noclip)
	{
		HCNet.INSTANCE.sendToAllAroundTracking(new PacketSpawnColorCloud(pos, targetPos, color.getRGB(), noclip), HCNet.point(world, pos, 32));
	}
	
	public void spawnColorDrop(World world, Vec3d pos, Vec3d targetPos, Color color)
	{
		HCNet.INSTANCE.sendToAllAroundTracking(new PacketSpawnColorDrop(pos, targetPos, color.getRGB()), HCNet.point(world, pos, 32));
	}
	
	public void spawnPollution(World world, Vec3d pos, Vec3d targetPos)
	{
	}
	
	public void renderMob(Entity entity, FrictionRotator rotator, double posX, double posY, double posZ, float partialTicks)
	{
	}
}