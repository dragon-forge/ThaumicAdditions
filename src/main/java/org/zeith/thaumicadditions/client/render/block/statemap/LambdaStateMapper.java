package org.zeith.thaumicadditions.client.render.block.statemap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class LambdaStateMapper
		extends StateMapperBase
{
	final IStateMapper mapper;

	public LambdaStateMapper(IStateMapper mapper)
	{
		this.mapper = mapper;
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		return mapper.getModelResourceLocation(state);
	}

	public interface IStateMapper
	{
		ModelResourceLocation getModelResourceLocation(IBlockState state);
	}
}