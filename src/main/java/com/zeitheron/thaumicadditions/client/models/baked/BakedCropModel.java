package com.zeitheron.thaumicadditions.client.models.baked;

import static com.zeitheron.hammercore.client.model.mc.BakedConnectModel.FACE_BAKERY;
import static com.zeitheron.thaumicadditions.blocks.plants.BlockVisCrop.AGE_5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.zeitheron.hammercore.api.inconnect.IBlockConnectable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class BakedCropModel implements IBakedModel
{
	public static final String[] textures0 = { //
	        "thaumadditions:blocks/vis_crop/10", //
	        "thaumadditions:blocks/vis_crop/20", //
	        "thaumadditions:blocks/vis_crop/30", //
	        "thaumadditions:blocks/vis_crop/40", //
	        "thaumadditions:blocks/vis_crop/50" //
	};
	
	public static final String[] textures1 = { //
	        "", //
	        "", //
	        "thaumadditions:blocks/vis_crop/31", //
	        "thaumadditions:blocks/vis_crop/41", //
	        "thaumadditions:blocks/vis_crop/51" //
	};
	
	IBlockState state;
	int age;
	
	public BakedCropModel(IBlockState state)
	{
		this.state = state;
		if(!state.getPropertyKeys().contains(AGE_5))
			return;
		age = state.getValue(AGE_5);
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
	{
		if(side == null || !state.getPropertyKeys().contains(AGE_5))
			return Collections.emptyList();
		int age = state.getValue(AGE_5);
		
		List<BakedQuad> quads = new ArrayList<>();
		
		String tx0 = textures0[age];
		String tx1 = textures1[age];
		TextureAtlasSprite[] sprites = new TextureAtlasSprite[tx1.isEmpty() ? 1 : 2];
		sprites[0] = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(tx0);
		if(sprites.length > 1)
			sprites[1] = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(tx1);
		int index = 0;
		for(TextureAtlasSprite sprite : sprites)
		{
			if(side.getAxis() == Axis.X)
			{
				quads.add(FACE_BAKERY.makeBakedQuad( //
				        new Vector3f(4, -1, 0), //
				        new Vector3f(4, 15, 16), //
				        new BlockPartFace(null, index, "#crop", new BlockFaceUV(new float[] { 0, 0, 16, 16 }, 0)), //
				        sprite, side, ModelRotation.X0_Y0, null, false, false));
				
				quads.add(FACE_BAKERY.makeBakedQuad( //
				        new Vector3f(12, -1, 0), //
				        new Vector3f(12, 15, 16), //
				        new BlockPartFace(null, index, "#crop", new BlockFaceUV(new float[] { 0, 0, 16, 16 }, 0)), //
				        sprite, side, ModelRotation.X0_Y0, null, false, false));
			}
			
			if(side.getAxis() == Axis.Z)
			{
				quads.add(FACE_BAKERY.makeBakedQuad( //
				        new Vector3f(0, -1, 4), //
				        new Vector3f(16, 15, 4), //
				        new BlockPartFace(null, index, "#crop", new BlockFaceUV(new float[] { 0, 0, 16, 16 }, 0)), //
				        sprite, side, ModelRotation.X0_Y0, null, false, false));
				
				quads.add(FACE_BAKERY.makeBakedQuad( //
				        new Vector3f(0, -1, 12), //
				        new Vector3f(16, 15, 12), //
				        new BlockPartFace(null, index, "#crop", new BlockFaceUV(new float[] { 0, 0, 16, 16 }, 0)), //
				        sprite, side, ModelRotation.X0_Y0, null, false, false));
			}
			
			++index;
		}
		
		return quads;
	}
	
	@Override
	public boolean isAmbientOcclusion()
	{
		return false;
	}
	
	@Override
	public boolean isGui3d()
	{
		return false;
	}
	
	@Override
	public boolean isBuiltInRenderer()
	{
		return true;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textures0[age]);
	}
	
	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.NONE;
	}
}