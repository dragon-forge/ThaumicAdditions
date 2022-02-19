package org.zeith.thaumicadditions.proxy.fx;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.FrictionRotator;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.zeith.thaumicadditions.net.fxh.FXHPacket;
import org.zeith.thaumicadditions.tiles.TileAuraDisperser;

public class FXHandler
{
	public void spawnAuraDisperserFX(TileAuraDisperser tile)
	{
		if(tile == null)
			return;
		HCNet.INSTANCE.sendToAllAround(new FXHPacket(tile.getPos(), 0), new TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 32));
	}

	public void renderMob(Entity entity, FrictionRotator rotator, double posX, double posY, double posZ, float partialTicks)
	{

	}
}