package com.zeitheron.thaumicadditions.blocks;

import java.util.Arrays;
import java.util.List;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.api.ITileBlock;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.EnumRotation;
import com.zeitheron.hammercore.utils.NBTUtils;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.color.ColorNamePicker;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.TAReconstructed;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.api.seals.SealManager;
import com.zeitheron.thaumicadditions.blocks.def.BlockRendered;
import com.zeitheron.thaumicadditions.init.ItemsTAR;
import com.zeitheron.thaumicadditions.items.ItemSealSymbol;
import com.zeitheron.thaumicadditions.tiles.TileSeal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.ItemsTC;

public class BlockSeal extends BlockRendered implements ITileEntityProvider, ITileBlock<TileSeal>
{
	public static final AxisAlignedBB[] SEAL_BOUNDS = { new AxisAlignedBB(5 / 16D, 0, 5 / 16D, 11 / 16D, 1 / 16D, 11 / 16D), new AxisAlignedBB(5 / 16D, 14 / 16D, 5 / 16D, 11 / 16D, 1, 11 / 16D), new AxisAlignedBB(5 / 16D, 5 / 16D, 0, 11 / 16D, 11 / 16D, 1 / 16D), new AxisAlignedBB(5 / 16D, 5 / 16D, 14 / 16D, 11 / 16D, 11 / 16D, 1), new AxisAlignedBB(0, 5 / 16D, 5 / 16D, 1 / 16D, 11 / 16D, 11 / 16D), new AxisAlignedBB(14 / 16D, 5 / 16D, 5 / 16D, 1, 11 / 16D, 11 / 16D) };
	
	private static final int[] RGBs = new int[] { 255, 0, 0 };
	
	public BlockSeal()
	{
		super(Material.ROCK);
		MinecraftForge.EVENT_BUS.register(this);
		setTranslationKey("seal");
		setHarvestLevel("pickaxe", -1);
		setHardness(0);
		setResistance(0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced)
	{
		int[] rgb = RGBs;
		String col = "#FFFFFF";
		if(stack.hasTagCompound())
			rgb = stack.getTagCompound().getIntArray("RGB");
		if(rgb == null || rgb.length < 3)
			rgb = RGBs;
		if(rgb.length >= 3)
			col = "#" + Integer.toHexString(rgb[0] << 16 | rgb[1] << 8 | rgb[2]);
		String name = ColorNamePicker.getColorNameFromRgb(rgb[0], rgb[1], rgb[2]);
		tooltip.add(I18n.format(getTranslationKey() + ".desc").replace("@COLOR", advanced.isAdvanced() ? name + " (" + col.toUpperCase() + ")" : name));
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
	{
		return canStay(worldIn, pos, side);
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return false;
	}
	
	public boolean canStay(World world, BlockPos pos, EnumFacing face)
	{
		if(world == null || face == null || pos == null || !world.isBlockLoaded(pos.offset(face.getOpposite())))
			return true;
		return world.getBlockState(pos.offset(face.getOpposite())).isSideSolid(world, pos, face);
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, EnumRotation.EFACING);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileSeal();
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return SEAL_BOUNDS[state.getValue(EnumRotation.EFACING).getOpposite().ordinal()];
	}
	
	public ItemStack getDrop(WorldLocation loc)
	{
		TileSeal seal = loc.getTileOfType(TileSeal.class);
		if(seal != null)
			return seal.stack.get().copy();
		return ItemStack.EMPTY;
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return Arrays.asList();
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(EnumRotation.EFACING).ordinal();
	}
	
	@Override
	public String getParticleSprite(World world, BlockPos pos)
	{
		return InfoTAR.MOD_ID + ":blocks/seal_base";
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return getDrop(new WorldLocation(world, pos));
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(EnumRotation.EFACING, facing);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(EnumRotation.EFACING, EnumFacing.VALUES[meta % 6]);
	}
	
	@Override
	public Class<TileSeal> getTileClass()
	{
		return TileSeal.class;
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		TileSeal ts = WorldUtil.cast(worldIn.getTileEntity(pos), TileSeal.class);
		
		if(ts != null && ts.orientation != null && !canStay(worldIn, pos, ts.orientation))
		{
			spawnAsEntity(worldIn, pos, getDrop(new WorldLocation(worldIn, pos)));
			worldIn.setBlockToAir(pos);
		}
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileSeal seal = WorldUtil.cast(worldIn.getTileEntity(pos), TileSeal.class);
		
		if(seal != null && !playerIn.getHeldItem(hand).isEmpty() && playerIn.getHeldItem(hand).getItem() == ItemsTAR.SEAL_GLOBE)
		{
			TAReconstructed.proxy.viewSeal(seal);
			if(!worldIn.isRemote)
				HammerCore.audioProxy.playSoundAt(worldIn, InfoTAR.MOD_ID + ":rune_set", pos, .5F, 1F, SoundCategory.PLAYERS);
			return true;
		}
		
		if(seal != null && !worldIn.isRemote)
		{
			ItemStack stack = playerIn.getHeldItem(hand);
			Aspect a = stack.isEmpty() || stack.getItem() != ItemsTAR.SEAL_SYMBOL ? null : ItemSealSymbol.getAspect(stack);
			
			if(a != null && seal.getSymbol(2) == null)
			{
				if(seal.getSymbol(0) == null)
					seal.setSymbol(0, a);
				else if(seal.getSymbol(1) == null)
					seal.setSymbol(1, a);
				else
					seal.setSymbol(2, a);
				playerIn.getHeldItem(hand).shrink(1);
				HammerCore.audioProxy.playSoundAt(worldIn, InfoTAR.MOD_ID + ":rune_set", pos, .5F, 1F, SoundCategory.BLOCKS);
				HCNet.swingArm(playerIn, hand);
				
				SealInstance old = seal.instance;
				seal.instance = SealManager.makeInstance(seal, seal.combination, seal.optInstNBT);
				if(old != null)
					old.onSealBreak();
				
				return false;
			} else if(!playerIn.getHeldItem(hand).isEmpty() && playerIn.getHeldItem(hand).getItem() == ItemsTC.casterBasic && seal.getSymbol(0) != null)
				for(int i = 2; i >= 0; --i)
					if(seal.getSymbol(i) != null)
						if(seal.getSymbol(i) != null)
						{
							Aspect as = seal.getSymbol(i);
							seal.setSymbol(i, null);
							
							if(worldIn.rand.nextInt(100) > 10)
								WorldUtil.spawnItemStack(worldIn, pos, ItemSealSymbol.createItem(as, 1));
							else
								HammerCore.audioProxy.playSoundAt(worldIn, InfoTAR.MOD_ID + ":fizz", pos, .5F, 1F, SoundCategory.BLOCKS);
							
							HammerCore.audioProxy.playSoundAt(worldIn, "thaumcraft:zap", pos, .5F, 1F, SoundCategory.BLOCKS);
							HCNet.swingArm(playerIn, hand);
							
							return false;
						}
			SealInstance i = seal.instance;
			if(i != null && i.onSealActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
				HCNet.swingArm(playerIn, hand);
		}
		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		TileSeal seal = WorldUtil.cast(worldIn.getTileEntity(pos), TileSeal.class);
		if(seal == null)
			worldIn.setTileEntity(pos, seal = new TileSeal());
		if(seal != null)
		{
			ItemStack stack2 = stack.copy();
			stack2.setCount(1);
			seal.stack.set(stack2);
			seal.placer.set(placer.getName());
		}
	}
	
	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		TileSeal tile = WorldUtil.cast(worldIn.getTileEntity(pos), TileSeal.class);
		if(tile != null && tile.instance != null)
			tile.instance.onEntityCollisionWithSeal(worldIn, pos, state, entityIn);
		super.onEntityCollision(worldIn, pos, state, entityIn);
	}
	
	@SubscribeEvent
	public void interact(PlayerInteractEvent e)
	{
		EntityPlayer player = e.getEntityPlayer();
		EnumHand hand = e.getHand();
		ItemStack held = player.getHeldItem(hand);
		BlockPos pos = e.getPos();
		
		if(!held.isEmpty() && held.getItem() == Item.getItemFromBlock(this))
		{
			ItemStack copy  = held.copy();
			copy.setCount(1);
			
			IBlockState state = player.world.getBlockState(pos);
			if(state.getBlock() == Blocks.CAULDRON)
			{
				int fill = state.getValue(BlockCauldron.LEVEL);
				if(fill > 0 && copy.hasTagCompound() && copy.getTagCompound().hasKey("RGB", NBT.TAG_INT_ARRAY))
				{
					held.shrink(1);
					player.world.setBlockState(pos, state.withProperty(BlockCauldron.LEVEL, fill - 1), 3);
					NBTUtils.removeTagFromItemStack(copy, "RGB");
					SoundUtil.playSoundEffect(player.world, "item.bucket.fill", pos, .5F, 1F, SoundCategory.PLAYERS);
					if(!player.inventory.addItemStackToInventory(copy) && !player.world.isRemote)
						WorldUtil.spawnItemStack(player.world, player.posX, player.posY, player.posZ, copy);
					e.setCancellationResult(EnumActionResult.SUCCESS);
					e.setCanceled(true);
				}
			}
		}
	}
}