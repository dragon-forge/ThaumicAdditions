package org.zeith.thaumicadditions.client.fx;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

public class FXTextureAtlasSpriteDigging
		extends ParticleDigging
{
	public FXTextureAtlasSpriteDigging(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state, TextureAtlasSprite sprite)
	{
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
		setParticleTexture(sprite);
	}
}