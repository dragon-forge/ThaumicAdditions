package com.zeitheron.thaumicadditions.blocks.decor;

import com.zeitheron.hammercore.api.inconnect.EnumConnTexVersion;
import com.zeitheron.hammercore.api.inconnect.IBlockConnectable;
import com.zeitheron.hammercore.api.inconnect.block.BlockConnectable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIronFramedGreatwood
		extends BlockConnectable
{
	protected EnumConnTexVersion connV;

	public BlockIronFramedGreatwood(String name)
	{
		super(Material.ROCK);
		this.connV = EnumConnTexVersion.V3;
		setHarvestLevel("axe", 0);
		setSoundType(SoundType.WOOD);
		setHardness(2F);
		setTranslationKey(name);
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTxMap()
	{
		return new ResourceLocation(getRegistryName().getNamespace(), "blocks/" + getRegistryName().getPath() + "_ic");
	}

	@Override
	public EnumConnTexVersion getConnectTextureVersion()
	{
		return connV;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getParticleTexture(IBlockState state)
	{
		return IBlockConnectable.getSprite(getRegistryName().getNamespace() + ":blocks/" + getRegistryName().getPath());
	}
}