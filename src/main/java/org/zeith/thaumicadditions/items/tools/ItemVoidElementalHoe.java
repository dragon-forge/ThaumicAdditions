package org.zeith.thaumicadditions.items.tools;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import org.zeith.thaumicadditions.TAReconstructed;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.tools.ItemElementalHoe;

public class ItemVoidElementalHoe
		extends ItemElementalHoe
{
	public static final ToolMaterial TOOLMAT_VOID_ELEMENTAL = EnumHelper.addToolMaterial("TAR_VOID_ELEMENTAL", 3, 1500, 9.0f, 3.0f, 18).setRepairItem(new ItemStack(ItemsTC.ingots, 1, 1));

	public ItemVoidElementalHoe()
	{
		super(TOOLMAT_VOID_ELEMENTAL);
		TAReconstructed.resetRegistryName(this);
		ConfigItems.ITEM_VARIANT_HOLDERS.remove(this);
		setTranslationKey("void_elemental_hoe");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(!worldIn.isRemote && stack.getItemDamage() > 0 && entityIn.ticksExisted % 10 == 0 && worldIn.rand.nextBoolean())
		{
			float cd = AuraHelper.drainVis(worldIn, entityIn.getPosition(), 0.1F, true);
			if(cd >= 0.1F)
			{
				AuraHelper.drainVis(worldIn, entityIn.getPosition(), 0.1F, false);
				stack.setItemDamage(stack.getItemDamage() - 1);
			}
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack stack1, ItemStack stack2)
	{
		return stack2.isItemEqual(new ItemStack(ItemsTC.ingots, 1, 1));
	}
}