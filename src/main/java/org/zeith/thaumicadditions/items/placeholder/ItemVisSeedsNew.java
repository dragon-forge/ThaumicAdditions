package org.zeith.thaumicadditions.items.placeholder;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import org.zeith.thaumicadditions.blocks.plants.BlockVisCrop;
import org.zeith.thaumicadditions.init.ItemsTAR;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import static org.zeith.thaumicadditions.items.seed.ItemVisSeeds.ASPECT_COUNT;

public class ItemVisSeedsNew
		extends ItemSeeds
		implements IPlantable, IEssentiaContainerItem
{
	public final BlockVisCrop crop;

	public ItemVisSeedsNew(BlockVisCrop crop)
	{
		super(crop, Blocks.FARMLAND);
		this.crop = crop;
		setTranslationKey("vis_seeds/" + crop.aspect.getTag());
	}

	public Aspect getAspect()
	{
		return crop.aspect;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String an = crop.aspect.getName();
		return I18n.translateToLocalFormatted(ItemsTAR.VIS_SEEDS.getUnlocalizedNameInefficiently(stack) + ".name", an).trim();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
	{
		return EnumPlantType.Crop;
	}

	@Override
	public AspectList getAspects(ItemStack stack)
	{
		AspectList al = new AspectList();
		Aspect a = getAspect();
		if(a != null) al.add(a, ASPECT_COUNT);
		return al;
	}

	@Override
	public boolean ignoreContainedAspects()
	{
		return false;
	}

	@Override
	public void setAspects(ItemStack stack, AspectList list)
	{
		if(list.getAspects().length > 0)
		{
			Aspect a = list.getAspects()[0];
			int ac = list.getAmount(a) / ASPECT_COUNT;
			stack.setCount(ac);
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setString("Aspect", a.getTag());
		}
	}
}
