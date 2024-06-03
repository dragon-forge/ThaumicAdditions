package org.zeith.thaumicadditions.blocks;

import com.zeitheron.hammercore.internal.blocks.IItemBlock;
import com.zeitheron.hammercore.internal.blocks.base.BlockTileHC;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.api.AspectUtil;
import org.zeith.thaumicadditions.api.items.EssentiaJarManager;
import org.zeith.thaumicadditions.api.items.EssentiaJarManager.IJar;
import org.zeith.thaumicadditions.tiles.TileAbstractJarFillable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileAlembic;

import java.util.List;

public class BlockAbstractEssentiaJar<T extends TileAbstractJarFillable>
		extends BlockTileHC<T>
		implements ILabelable, IItemBlock
{
	public static boolean spillEssentia;
	public final int capacity;
	public final boolean voidsExcess;
	public final BlockAbstractJarItem itemBlock = new BlockAbstractJarItem(this);
	
	public BlockAbstractEssentiaJar(Class<T> t, int capacity, String name)
	{
		this(t, capacity, false, name);
	}
	
	public BlockAbstractEssentiaJar(Class<T> t, int capacity, boolean voidsExcess, String name)
	{
		super(Material.GLASS, t, name);
		this.voidsExcess = voidsExcess;
		this.capacity = capacity;
		setHardness(0.3F);
		setSoundType(SoundsTC.JAR);
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_)
	{
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		TileAbstractJarFillable te = Cast.cast(world.getTileEntity(pos), TileAbstractJarFillable.class);
		if(te != null)
		{
			ItemStack drop = new ItemStack(this, 1);
			if(te.amount > 0)
				((BlockAbstractJarItem) drop.getItem()).setAspects(drop, new AspectList().add(te.aspect, te.amount));
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
	
	@Override
	public SoundType getSoundType()
	{
		return SoundsTC.JAR;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getStateFromMeta(meta);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		spillEssentia = false;
		super.breakBlock(worldIn, pos, state);
		spillEssentia = true;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof TileAbstractJarFillable)
		{
			this.spawnFilledJar(worldIn, pos, state, (TileAbstractJarFillable) te);
		} else
		{
			super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
		}
	}
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
	{
		if(te instanceof TileAbstractJarFillable)
		{
			this.spawnFilledJar(worldIn, pos, state, (TileAbstractJarFillable) te);
		} else
		{
			super.harvestBlock(worldIn, player, pos, state, null, stack);
		}
	}
	
	private void spawnFilledJar(World world, BlockPos pos, IBlockState state, TileAbstractJarFillable te)
	{
		ItemStack drop = new ItemStack(this, 1, this.getMetaFromState(state));
		if(te.amount > 0)
			((BlockAbstractJarItem) drop.getItem()).setAspects(drop, new AspectList().add(te.aspect, te.amount));
		if(te.aspectFilter != null)
		{
			if(!drop.hasTagCompound())
				drop.setTagCompound(new NBTTagCompound());
			drop.getTagCompound().setString("AspectFilter", te.aspectFilter.getTag());
		}
		if(te.blocked)
			Block.spawnAsEntity(world, pos, new ItemStack(ItemsTC.jarBrace));
		Block.spawnAsEntity(world, pos, drop);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase ent, ItemStack stack)
	{
		int l = MathHelper.floor(ent.rotationYaw * 4.0f / 360.0f + 0.5) & 3;
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileAbstractJarFillable)
		{
			if(l == 0)
			{
				((TileAbstractJarFillable) tile).facing = 2;
			}
			if(l == 1)
			{
				((TileAbstractJarFillable) tile).facing = 5;
			}
			if(l == 2)
			{
				((TileAbstractJarFillable) tile).facing = 3;
			}
			if(l == 3)
			{
				((TileAbstractJarFillable) tile).facing = 4;
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileAbstractJarFillable tile = Cast.cast(world.getTileEntity(pos), TileAbstractJarFillable.class);
		if(tile == null) return false;
		
		ItemStack heldItem = player.getHeldItem(hand);
		
		if(!heldItem.isEmpty())
		{
			IJar jar;
			
			ItemStack held = heldItem;
			if(held.getItem() == ItemsTC.phial)
			{
				ItemPhial ip = (ItemPhial) ItemsTC.phial;
				
				if(held.getItemDamage() == 0 && tile.amount >= 10)
				{
					if(world.isRemote)
						return true;
					
					Aspect asp = tile.aspect;
					if(asp != null && tile.takeFromContainer(asp, 10))
					{
						held.shrink(1);
						ItemStack phial = new ItemStack(ip, 1, 1);
						ip.setAspects(phial, new AspectList().add(asp, 10));
						if(!player.inventory.addItemStackToInventory(phial))
							world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, phial));
						SoundUtil.playSoundEffect(world, SoundEvents.ITEM_BOTTLE_FILL.getRegistryName().toString(), pos, .5F, 1F, SoundCategory.PLAYERS);
						player.inventoryContainer.detectAndSendChanges();
						return true;
					}
				} else
				{
					AspectList al;
					if((al = ip.getAspects(heldItem)) != null && al.size() == 1)
					{
						Aspect aspect = al.getAspects()[0];
						if(heldItem.getItemDamage() != 0)
						{
							if(tile.amount <= tile.getCapacity() - 10 && tile.doesContainerAccept(aspect))
							{
								if(world.isRemote)
									return true;
								if(tile.addToContainer(aspect, 10) == 0)
								{
									world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 3);
									tile.syncTile(true);
									heldItem.shrink(1);
									if(!player.inventory.addItemStackToInventory(new ItemStack(ip, 1, 0)))
										world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(ip, 1, 0)));
									SoundUtil.playSoundEffect(world, SoundEvents.ITEM_BOTTLE_EMPTY.getRegistryName().toString(), pos, .5F, 1F, SoundCategory.PLAYERS);
									player.inventoryContainer.detectAndSendChanges();
									return true;
								}
							}
						}
					}
				}
			} else if(held.getCount() == 1 && (jar = EssentiaJarManager.fromStack(held)) != null && jar.getEssentia(held).size() > 0)
			{
				boolean didTheThing = false;
				AspectList list = jar.getEssentia(held);
				for(Aspect a : list.getAspectsSortedByAmount())
					if(tile.aspect == null || (tile.aspect == a && tile.amount < tile.getCapacity()))
					{
						int e = Math.min(tile.getCapacity() - tile.amount, list.getAmount(a));
						tile.aspect = a;
						tile.addToContainer(a, e);
						jar.drain(held, a, e);
						didTheThing = true;
					}
				if(didTheThing)
				{
					world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
					world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.5f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f, false);
				}
			} else if(!tile.blocked && heldItem.getItem() == ItemsTC.jarBrace)
			{
				tile.blocked = true;
				heldItem.shrink(1);
				if(world.isRemote)
					world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.key, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
				else
					tile.markDirty();
			}
		} else if(player.isSneaking() && tile.aspectFilter != null && side.ordinal() == tile.facing)
		{
			tile.aspectFilter = null;
			if(world.isRemote)
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.page, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
			else
				world.spawnEntity(new EntityItem(world, pos.getX() + 0.5f + side.getXOffset() / 3.0f, pos.getY() + 0.5f, pos.getZ() + 0.5f + side.getZOffset() / 3.0f, new ItemStack(ItemsTC.label)));
		} else if(player.isSneaking() && heldItem.isEmpty())
		{
			if(tile.aspectFilter == null) tile.aspect = null;
			if(world.isRemote)
			{
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.4f, 1.0f, false);
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 0.5f, 1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3f, false);
			} else
				AuraHelper.polluteAura(world, pos, tile.amount, true);
			tile.amount = 0;
			tile.markDirty();
		}
		return true;
	}
	
	@Override
	public boolean applyLabel(EntityPlayer player, BlockPos pos, EnumFacing side, ItemStack labelstack)
	{
		TileAbstractJarFillable tile = Cast.cast(player.world.getTileEntity(pos), TileAbstractJarFillable.class);
		if(tile == null) return false;
		
		if(tile.aspectFilter == null)
		{
			if(tile.amount == 0 && ((IEssentiaContainerItem) labelstack.getItem()).getAspects(labelstack) == null)
			{
				return false;
			}
			if(tile.amount == 0 && ((IEssentiaContainerItem) labelstack.getItem()).getAspects(labelstack) != null)
			{
				tile.aspect = ((IEssentiaContainerItem) labelstack.getItem()).getAspects(labelstack).getAspects()[0];
			}
			this.onBlockPlacedBy(player.world, pos, player.world.getBlockState(pos), player, null);
			tile.aspectFilter = tile.aspect;
			player.world.markAndNotifyBlock(pos, player.world.getChunk(pos), player.world.getBlockState(pos), player.world.getBlockState(pos), 3);
			tile.markDirty();
			player.world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundsTC.jar, SoundCategory.BLOCKS, 0.4f, 1.0f);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile instanceof TileAbstractJarFillable)
		{
			TileAbstractJarFillable jar = (TileAbstractJarFillable) tile;
			float r = jar.amount / ((float) jar.getCapacity());
			return MathHelper.floor(r * 14.0f) + (jar.amount > 0 ? 1 : 0);
		}
		return super.getComparatorInputOverride(state, world, pos);
	}
	
	@Override
	public ItemBlock getItemBlock()
	{
		return itemBlock;
	}
	
	public static class BlockAbstractJarItem
			extends ItemBlock
			implements IEssentiaContainerItem
	{
		public final BlockAbstractEssentiaJar block;
		
		protected BlockAbstractJarItem(BlockAbstractEssentiaJar bl)
		{
			super(bl);
			this.block = bl;
		}
		
		@Override
		public boolean showDurabilityBar(ItemStack stack)
		{
			return this.getAspects(stack) != null;
		}
		
		@Override
		public double getDurabilityForDisplay(ItemStack stack)
		{
			AspectList al = this.getAspects(stack);
			return al == null ? 0.0 : 1 - al.visSize() / (double) block.capacity;
		}
		
		@Override
		public int getRGBDurabilityForDisplay(ItemStack stack)
		{
			AspectList al = this.getAspects(stack);
			
			if(al != null)
			{
				float percent = (float) getDurabilityForDisplay(stack);
				int rgb = AspectUtil.getColor(al, true);
				
				float r = ColorHelper.getRed(rgb);
				float g = ColorHelper.getGreen(rgb);
				float b = ColorHelper.getBlue(rgb);
				
				return ColorHelper.packRGB(r, g, b);
			}
			
			return super.getRGBDurabilityForDisplay(stack);
		}
		
		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
		{
			Block bi = world.getBlockState(pos).getBlock();
			ItemStack itemstack = player.getHeldItem(hand);
			if(bi == BlocksTC.alembic && !world.isRemote)
			{
				TileAlembic tile = (TileAlembic) world.getTileEntity(pos);
				if(tile.amount > 0)
				{
					if(this.getFilter(itemstack) != null && this.getFilter(itemstack) != tile.aspect)
					{
						return EnumActionResult.FAIL;
					}
					if(this.getAspects(itemstack) != null && this.getAspects(itemstack).getAspects()[0] != tile.aspect)
					{
						return EnumActionResult.FAIL;
					}
					int amt = tile.amount;
					if(this.getAspects(itemstack) != null && this.getAspects(itemstack).visSize() + amt > block.capacity)
					{
						amt = Math.abs(this.getAspects(itemstack).visSize() - block.capacity);
					}
					if(amt <= 0)
					{
						return EnumActionResult.FAIL;
					}
					Aspect a = tile.aspect;
					if(tile.takeFromContainer(tile.aspect, amt))
					{
						int base;
						int n = base = this.getAspects(itemstack) == null ? 0 : this.getAspects(itemstack).visSize();
						if(itemstack.getCount() > 1)
						{
							ItemStack stack = itemstack.copy();
							this.setAspects(stack, new AspectList().add(a, base + amt));
							itemstack.shrink(1);
							stack.setCount(1);
							if(!player.inventory.addItemStackToInventory(stack))
							{
								world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, stack));
							}
						} else
						{
							this.setAspects(itemstack, new AspectList().add(a, base + amt));
						}
						player.playSound(SoundEvents.ITEM_BOTTLE_FILL, 0.25f, 1.0f);
						player.inventoryContainer.detectAndSendChanges();
						return EnumActionResult.SUCCESS;
					}
				}
			}
			return EnumActionResult.PASS;
		}
		
		@Override
		public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
		{
			TileEntity te;
			boolean b = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
			if(b && !world.isRemote && (te = world.getTileEntity(pos)) != null && te instanceof TileAbstractJarFillable)
			{
				TileAbstractJarFillable jar = (TileAbstractJarFillable) te;
				jar.setAspects(this.getAspects(stack));
				if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AspectFilter"))
					jar.aspectFilter = Aspect.getAspect(stack.getTagCompound().getString("AspectFilter"));
				te.markDirty();
				world.markAndNotifyBlock(pos, world.getChunk(pos), newState, newState, 3);
			}
			return b;
		}
		
		@Override
		@SideOnly(value = Side.CLIENT)
		public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
		{
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AspectFilter"))
			{
				String tf = stack.getTagCompound().getString("AspectFilter");
				Aspect tag = Aspect.getAspect(tf);
				tooltip.add("\u00a75" + tag.getName());
			}
			super.addInformation(stack, worldIn, tooltip, flagIn);
		}
		
		@Override
		public AspectList getAspects(ItemStack itemstack)
		{
			if(itemstack.hasTagCompound())
			{
				AspectList aspects = new AspectList();
				aspects.readFromNBT(itemstack.getTagCompound());
				return aspects.size() > 0 ? aspects : null;
			}
			return null;
		}
		
		public Aspect getFilter(ItemStack itemstack)
		{
			if(itemstack.hasTagCompound())
				return Aspect.getAspect(itemstack.getTagCompound().getString("AspectFilter"));
			return null;
		}
		
		@Override
		public void setAspects(ItemStack itemstack, AspectList aspects)
		{
			if(!itemstack.hasTagCompound())
				itemstack.setTagCompound(new NBTTagCompound());
			aspects.writeToNBT(itemstack.getTagCompound());
		}
		
		@Override
		public boolean ignoreContainedAspects()
		{
			return false;
		}
	}
}