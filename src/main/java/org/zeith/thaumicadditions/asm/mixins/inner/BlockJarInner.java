package org.zeith.thaumicadditions.asm.mixins.inner;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.asm.minmixin.annotations.Copy;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.tiles.essentia.TileJarFillable;

public abstract class BlockJarInner
		extends Block
{
	public static void init() {}
	
	private BlockJarInner(Material materialIn)
	{
		super(materialIn);
	}
	
	@Copy
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		TileJarFillable te = Cast.cast(world.getTileEntity(pos), TileJarFillable.class);
		if(te != null)
		{
			ItemStack drop = new ItemStack(this, 1);
			if(!(drop.getItem() instanceof BlockJarItem)) return super.getPickBlock(state, target, world, pos, player);
			if(te.amount > 0)
				((BlockJarItem) drop.getItem()).setAspects(drop, new AspectList().add(te.aspect, te.amount));
			if(te.aspectFilter != null)
			{
				if(!drop.hasTagCompound())
					drop.setTagCompound(new NBTTagCompound());
				drop.getTagCompound().setString("AspectFilter", te.aspectFilter.getTag());
			}
			return drop;
		}
		return super.getPickBlock(state, target, world, pos, player);
	}
}