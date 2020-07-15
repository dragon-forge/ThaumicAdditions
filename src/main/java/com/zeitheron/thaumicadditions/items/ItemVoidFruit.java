package com.zeitheron.thaumicadditions.items;

import com.zeitheron.thaumicadditions.TAReconstructed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.lib.SoundsTC;

public class ItemVoidFruit
		extends ItemFood
{
	public ItemVoidFruit()
	{
		super(5, 10F, false);
		setTranslationKey("void_fruit");
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		if(entityLiving instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			entityplayer.getFoodStats().addStats(this, stack);
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundsTC.poof, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			this.onFoodEaten(stack, worldIn, entityplayer);
			entityplayer.addStat(StatList.getObjectUseStats(this));

			if(entityplayer instanceof EntityPlayerMP)
			{
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
			}
		}

		stack.shrink(1);
		return stack;
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
	{
		if(!worldIn.isRemote)
		{
			int oProg = EnumKnowledgeType.OBSERVATION.getProgression();
			int tProg = EnumKnowledgeType.THEORY.getProgression();

			ResearchCategory[] rc = ResearchCategories.researchCategories.values().toArray(new ResearchCategory[0]);
			ThaumcraftApi.internalMethods.addKnowledge(player, EnumKnowledgeType.OBSERVATION, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), oProg / 4, oProg / 3));
			ThaumcraftApi.internalMethods.addKnowledge(player, EnumKnowledgeType.THEORY, rc[player.getRNG().nextInt(rc.length)], MathHelper.getInt(player.getRNG(), tProg / 8, tProg / 6));
		}
	}
}
