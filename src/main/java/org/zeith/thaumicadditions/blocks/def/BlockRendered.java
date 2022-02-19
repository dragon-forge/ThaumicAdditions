package org.zeith.thaumicadditions.blocks.def;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.client.fx.FXTextureAtlasSpriteDigging;
import org.zeith.thaumicadditions.proxy.ClientProxy;

public abstract class BlockRendered
		extends Block
{
	public BlockRendered(Material material)
	{
		super(material);
	}

	public BlockRendered(Material material, MapColor color)
	{
		super(material, color);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		IBlockState state = world.getBlockState(pos).getActualState(world, pos);
		int i = 4;

		for(int j = 0; j < i; ++j)
			for(int k = 0; k < i; ++k)
				for(int l = 0; l < i; ++l)
				{
					double d0 = (j + 0.5D) / 4.0D;
					double d1 = (k + 0.5D) / 4.0D;
					double d2 = (l + 0.5D) / 4.0D;
					Minecraft.getMinecraft().effectRenderer.addEffect(new FXTextureAtlasSpriteDigging(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, d0 - .5, d1 - .5, d2 - .5, state, ClientProxy.getSprite(getParticleSprite(world, pos))).setBlockPos(pos));
				}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager)
	{
		BlockPos pos = target.getBlockPos();
		EnumFacing side = target.sideHit;

		if(state.getRenderType() != EnumBlockRenderType.INVISIBLE)
		{
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
			double d0 = i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
			double d1 = j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
			double d2 = k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

			double consta = 0.10000000149011612D;
			if(side == EnumFacing.DOWN)
				d1 = j + axisalignedbb.minY - consta;
			if(side == EnumFacing.UP)
				d1 = j + axisalignedbb.maxY + consta;
			if(side == EnumFacing.NORTH)
				d2 = k + axisalignedbb.minZ - consta;
			if(side == EnumFacing.SOUTH)
				d2 = k + axisalignedbb.maxZ + consta;
			if(side == EnumFacing.WEST)
				d0 = i + axisalignedbb.minX - consta;
			if(side == EnumFacing.EAST)
				d0 = i + axisalignedbb.maxX + consta;
			Minecraft.getMinecraft().effectRenderer.addEffect(new FXTextureAtlasSpriteDigging(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, state, ClientProxy.getSprite(getParticleSprite(world, pos))).setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
		}

		return true;
	}

	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	public abstract String getParticleSprite(World world, BlockPos pos);
}