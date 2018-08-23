package com.zeitheron.thaumicadditions.items;

import java.util.List;

import com.zeitheron.thaumicadditions.TAReconstructed;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.lib.SoundsTC;

public class ItemZeithScale extends ItemMaterial
{
	public ItemZeithScale()
	{
		super("zeith_scale", "scalesDragon", "scaleDragon");
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(I18n.format("item.curio.text"));
		tooltip.add(I18n.format(getTranslationKey() + ".tip"));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
	{
		worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.learn, SoundCategory.NEUTRAL, 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
		
		if(!worldIn.isRemote)
		{
			int oProg = EnumKnowledgeType.OBSERVATION.getProgression();
			int tProg = EnumKnowledgeType.THEORY.getProgression();
			
			ThaumcraftApi.internalMethods.addKnowledge(player, EnumKnowledgeType.OBSERVATION, TAReconstructed.RES_CAT, MathHelper.getInt(player.getRNG(), oProg / 3, oProg / 2));
			ThaumcraftApi.internalMethods.addKnowledge(player, EnumKnowledgeType.THEORY, TAReconstructed.RES_CAT, MathHelper.getInt(player.getRNG(), tProg / 5, tProg / 4));
			
			ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
			ThaumcraftApi.internalMethods.addKnowledge(player, EnumKnowledgeType.OBSERVATION, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), oProg / 5, oProg / 4));
			ThaumcraftApi.internalMethods.addKnowledge(player, EnumKnowledgeType.THEORY, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), tProg / 10, tProg / 8));
			
			if(!player.capabilities.isCreativeMode && !player.getGameProfile().getName().equalsIgnoreCase("zeitheron"))
				player.getHeldItem(hand).shrink(1);
		}
		
		player.addStat(StatList.getObjectUseStats(this));
		return super.onItemRightClick(worldIn, player, hand);
	}
}