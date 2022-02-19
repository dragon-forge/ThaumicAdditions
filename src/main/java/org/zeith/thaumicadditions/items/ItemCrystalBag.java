package org.zeith.thaumicadditions.items;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.utils.WorldLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.api.items.IAspectChargableItem;
import org.zeith.thaumicadditions.api.items.IAspectChargableItem.AspectChargableItemHelper;
import org.zeith.thaumicadditions.init.GuisTAR;
import org.zeith.thaumicadditions.inventory.container.ContainerCrystalBag;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import java.util.List;

@EventBusSubscriber
public class ItemCrystalBag
		extends Item
{
	public ItemCrystalBag()
	{
		setTranslationKey("crystal_bag");
		setMaxStackSize(1);
	}

	@SubscribeEvent
	public static void tryPickupItem(EntityItemPickupEvent e)
	{
		EntityPlayer picker = e.getEntityPlayer();
		EntityItem picked = e.getItem();
		ItemStack stack;

		if(picker.openContainer instanceof ContainerCrystalBag) return;

		if(picker != null && picked != null && !(stack = picked.getItem()).isEmpty() && stack.getItem() == ItemsTC.crystalEssence)
		{
			stack = stack.copy();
			ItemCrystalEssence ce = (ItemCrystalEssence) ItemsTC.crystalEssence;
			AspectList aspects = ce.getAspects(stack);
			if(aspects != null && aspects.visSize() > 0)
				for(int i = 0; i < 9; ++i)
				{
					ItemStack bagStack = picker.inventory.mainInventory.get(i);
					if(!bagStack.isEmpty() && bagStack.getItem() instanceof ItemCrystalBag)
					{
						AspectList list = AspectChargableItemHelper.getAspects(bagStack);

						int amt = 0;

						twice:
						while(!stack.isEmpty())
							for(Aspect a : aspects.getAspectsSortedByAmount())
							{
								if(32_768 - list.getAmount(a) > 0)
								{
									list.add(a, 1);
									stack.shrink(1);
									++amt;
									AspectChargableItemHelper.setAspects(bagStack, list);
									break;
								} else
									break twice;
							}

						e.setCanceled(true);

						picker.onItemPickup(picked, amt);
						picked.setItem(stack);

						return;
					}
				}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		AspectList list = AspectChargableItemHelper.getAspects(stack);
		int i = 0;
		for(Aspect a : list.getAspectsSortedByAmount())
		{
			if(i >= 6)
			{
				tooltip.add("..." + (list.aspects.size() - 6) + " More!");
				break;
			}
			tooltip.add(" - " + a.getName() + " x" + String.format("%,d", list.getAmount(a)));
			++i;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		GuiManager.openGuiCallback(GuisTAR.CRYSTAL_BAG, playerIn, new WorldLocation(worldIn, playerIn.getPosition()));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}