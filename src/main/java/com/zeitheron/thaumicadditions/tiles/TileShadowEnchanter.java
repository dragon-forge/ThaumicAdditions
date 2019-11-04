package com.zeitheron.thaumicadditions.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.api.ShadowEnchantment;
import com.zeitheron.thaumicadditions.inventory.container.ContainerShadowEnchanter;
import com.zeitheron.thaumicadditions.inventory.gui.GuiShadowEnchanter;
import com.zeitheron.thaumicadditions.utils.ListHelper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.events.EssentiaHandler;

public class TileShadowEnchanter extends TileSyncableTickable
{
	public final InventoryDummy items = new InventoryDummy(2);
	
	public List<EnchantmentData> enchants = new ArrayList<>();
	
	public boolean infusing = false;
	public AspectList pending;
	public int craftTimer;
	
	@Override
	public void tick()
	{
		enchants.removeIf(e -> !isAplicable(e.enchantment));
		
		if(infusing && items.getStackInSlot(1).isEmpty())
		{
			if(pending == null)
			{
				SoundUtil.playSoundEffect(world, InfoTAR.MOD_ID + ":craftfail", pos, 1F, 1F, SoundCategory.BLOCKS);
				infusing = false;
				craftTimer = 0;
				return;
			} else
			{
				++craftTimer;
				SoundUtil.playSoundEffect(world, InfoTAR.MOD_ID + ":brain", pos, 1F, 1F, SoundCategory.BLOCKS);
				if(pending.visSize() == 0)
				{
					ItemStack stack = items.getStackInSlot(0).copy();
					enchants.forEach(ed -> stack.addEnchantment(ed.enchantment, ed.enchantmentLevel));
					items.setInventorySlotContents(1, stack);
					items.removeStackFromSlot(0);
					
					SoundUtil.playSoundEffect(world, InfoTAR.MOD_ID + ":poof", pos, 1F, 1F, SoundCategory.BLOCKS);
					infusing = false;
					craftTimer = 0;
					return;
				} else
				{
					Aspect a = pending.getAspects()[0];
					if(EssentiaHandler.drainEssentia(this, a, null, 12, 1))
						pending.remove(a, 1);
				}
			}
		}
	}
	
	public void startCraft()
	{
		if(!infusing && !enchants.isEmpty() && items.getStackInSlot(1).isEmpty())
		{
			AspectList al = new AspectList();
			
			for(AspectList list : enchants.stream().map(ed ->
			{
				ShadowEnchantment se = ShadowEnchantment.pick(ed.enchantment);
				if(se != null)
					return se.getAspects(ed.enchantmentLevel);
				return null;
			}).collect(Collectors.toList()))
				if(list != null)
					al.add(list);
				else
					return;
				
			infusing = true;
			craftTimer = 0;
			pending = al;
			
			SoundUtil.playSoundEffect(world, InfoTAR.MOD_ID + ":craftstart", pos, 1F, 1F, SoundCategory.BLOCKS);
		}
	}
	
	public boolean isAplicable(Enchantment ench)
	{
		ItemStack s = items.getStackInSlot(0);
		return !s.isEmpty() && s.isItemEnchantable() && ench.canApply(s);
	}
	
	public boolean isAplicableBy(Enchantment ench, EntityPlayer player)
	{
		if(!isAplicable(ench))
			return false;
		ShadowEnchantment e = ShadowEnchantment.pick(ench);
		if(e == null || !e.canBeAppliedBy(player))
			return false;
		return true;
	}
	
	public void upLvl(Enchantment ench, EntityPlayer player)
	{
		if(!isAplicableBy(ench, player) || infusing)
			return;
		if(ListHelper.replace(enchants, e -> e.enchantment == ench, k -> new EnchantmentData(ench, Math.max(ench.getMinLevel(), Math.min(k.enchantmentLevel + 1, ench.getMaxLevel())))) == 0)
			enchants.add(new EnchantmentData(ench, ench.getMinLevel()));
		sendChangesToNearby();
	}
	
	public void downLvl(Enchantment ench, EntityPlayer player)
	{
		if(!infusing && ListHelper.replace(enchants, e -> e.enchantment == ench, k ->
		{
			if(k.enchantmentLevel == ench.getMinLevel())
				return null;
			return new EnchantmentData(ench, Math.max(ench.getMinLevel(), Math.min(k.enchantmentLevel - 1, ench.getMaxLevel())));
		}) > 0)
			sendChangesToNearby();
	}
	
	@Override
	public boolean hasGui()
	{
		return true;
	}
	
	@Override
	public Object getClientGuiElement(EntityPlayer player)
	{
		return new GuiShadowEnchanter(new ContainerShadowEnchanter(player, this));
	}
	
	@Override
	public Object getServerGuiElement(EntityPlayer player)
	{
		return new ContainerShadowEnchanter(player, this);
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setTag("Items", items.writeToNBT(new NBTTagCompound()));
		
		NBTTagList ench = new NBTTagList();
		enchants.forEach(d ->
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("id", d.enchantment.getRegistryName().toString());
			tag.setInteger("lvl", d.enchantmentLevel);
			ench.appendTag(tag);
		});
		nbt.setTag("Enchantments", ench);
		nbt.setBoolean("Infusing", infusing);
		nbt.setInteger("Timer", craftTimer);
		if(pending != null)
			pending.writeToNBT(nbt, "Aspects");
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		items.readFromNBT(nbt.getCompoundTag("Items"));
		infusing = nbt.getBoolean("Infusing");
		craftTimer = nbt.getInteger("Timer");
		NBTTagList ench = nbt.getTagList("Enchantments", NBT.TAG_LIST);
		enchants.clear();
		for(int i = 0; i < ench.tagCount(); ++i)
		{
			NBTTagCompound tag = ench.getCompoundTagAt(i);
			Enchantment e = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(tag.getString("id")));
			int lvl = tag.getInteger("lvl");
			enchants.add(new EnchantmentData(e, lvl));
		}
		if(nbt.hasKey("Aspects"))
		{
			pending = new AspectList();
			pending.readFromNBT(nbt, "Aspects");
		} else
			pending = null;
	}
}