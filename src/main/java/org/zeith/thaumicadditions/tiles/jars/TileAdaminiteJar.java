package org.zeith.thaumicadditions.tiles.jars;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldUtil;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import org.zeith.thaumicadditions.tiles.TileAbstractJarFillable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;

public class TileAdaminiteJar
		extends TileAbstractJarFillable
{
	private int time = 0;

	@Override
	public int getCapacity()
	{
		return 2000;
	}

	@Override
	public void update()
	{
		super.update();
		++time;

		if(!world.isRemote && time % 1000 == 0 && world.rand.nextBoolean() && aspect == Aspect.FLUX && amount >= getCapacity() * .9)
		{
			SoundUtil.playSoundEffect(world, SoundEvents.ENTITY_GENERIC_EXPLODE.soundName.toString(), pos, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, SoundCategory.BLOCKS);
			HCNet.spawnParticle(world, EnumParticleTypes.EXPLOSION_LARGE, getPos().getX() + .5, getPos().getY() + .5, getPos().getZ() + .5, 1.0D, 0.0D, 0.0D);
			WorldUtil.spawnItemStack(world, pos, new ItemStack(getBlockType()));
			world.destroyBlock(pos, false);
			AuraHelper.polluteAura(world, pos, amount, true);
		}
	}
}