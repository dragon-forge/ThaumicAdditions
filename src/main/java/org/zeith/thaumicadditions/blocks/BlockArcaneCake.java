package org.zeith.thaumicadditions.blocks;

import com.zeitheron.hammercore.api.ITileBlock;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.api.EdibleAspect;
import org.zeith.thaumicadditions.config.ConfigsTAR;
import org.zeith.thaumicadditions.init.BlocksTAR;
import org.zeith.thaumicadditions.tiles.TileArcaneCake;

import java.util.Random;

public class BlockArcaneCake
		extends BlockCake
		implements ITileBlock<TileArcaneCake>
{
	public BlockArcaneCake()
	{
		setTranslationKey("cake");
		setSoundType(SoundType.CLOTH);
		setDefaultState(blockState.getBaseState().withProperty(BITES, 0));
		setTickRandomly(true);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		TileArcaneCake tile = Cast.cast(worldIn.getTileEntity(pos), TileArcaneCake.class);
		if(tile == null)
		{
			tile = new TileArcaneCake();
			worldIn.setTileEntity(pos, tile);
		}

		tile.aspects.add(EdibleAspect.getSalt(stack));
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if(rand.nextInt(ConfigsTAR.cateRestoreRate) == 0)
		{
			int bites = state.getValue(BITES);
			if(bites > 0)
			{
				TileArcaneCake cake = Cast.cast(worldIn.getTileEntity(pos), TileArcaneCake.class);
				worldIn.setBlockState(pos, state.withProperty(BITES, bites - 1), 3);
				if(cake != null)
				{
					cake.validate();
					worldIn.setTileEntity(pos, cake);
				}
			}
		}

		super.updateTick(worldIn, pos, state, rand);
	}

	protected boolean eatCake(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		if(player instanceof FakePlayer)
			return false;

		player.addStat(StatList.CAKE_SLICES_EATEN);
		player.getFoodStats().addStats(4, 1F);
		int i = state.getValue(BITES);

		TileArcaneCake cake = Cast.cast(worldIn.getTileEntity(pos), TileArcaneCake.class);

		if(cake != null)
			EdibleAspect.execute(player, cake.aspects);

		if(i < 6)
		{
			worldIn.setBlockState(pos, state.withProperty(BITES, Integer.valueOf(i + 1)), 3);

			cake.validate();
			worldIn.setTileEntity(pos, cake);
		} else
			worldIn.setBlockToAir(pos);

		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return CAKE_AABB[state.getValue(BITES)];
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
			return this.eatCake(worldIn, pos, state, playerIn);
		else
		{
			ItemStack itemstack = playerIn.getHeldItem(hand);
			return this.eatCake(worldIn, pos, state, playerIn) || itemstack.isEmpty();
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		return super.canPlaceBlockAt(worldIn, pos) && canBlockStay(worldIn, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(!canBlockStay(worldIn, pos))
			worldIn.setBlockToAir(pos);
	}

	private boolean canBlockStay(World worldIn, BlockPos pos)
	{
		return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Items.AIR;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(BlocksTAR.CAKE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(BITES, meta);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(BITES);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, BITES);
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return (7 - blockState.getValue(BITES)) * 2;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public Class<TileArcaneCake> getTileClass()
	{
		return TileArcaneCake.class;
	}
}