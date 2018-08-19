package com.zeitheron.thaumicadditions.items;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.config.ConfigsTAR;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.client.fx.FXDispatcher;

public class ItemKnowledgeTome extends Item
{
	public ItemKnowledgeTome()
	{
		setTranslationKey("knowledge_tome");
		setMaxStackSize(1);
	}
	
	final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
	
	@Override
	public void addInformation(ItemStack held, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(held.hasTagCompound() && held.getTagCompound().hasKey("KnowledgeOwner") && held.getTagCompound().hasKey("Knowledge"))
		{
			tooltip.add(I18n.format(getTranslationKey() + ".desc1", held.getTagCompound().getString("KnowledgeOwner")));
			tooltip.add(I18n.format(getTranslationKey() + ".desc2", held.getTagCompound().getString("KnowledgeTimestamp")));
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack held = playerIn.getHeldItem(handIn);
		
		if(playerIn.isSneaking() && (!held.hasTagCompound() || !(held.getTagCompound().hasKey("Knowledge") && held.getTagCompound().hasKey("KnowledgeOwner"))))
		{
			NBTTagCompound nbt = held.getTagCompound();
			if(nbt == null)
				nbt = new NBTTagCompound();
			
			IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(playerIn);
			
			if(know != null)
			{
				NBTTagList known = new NBTTagList();
				
				List<String> research = new ArrayList<>();
				List<String> complete = new ArrayList<>();
				
				know.getResearchList().stream().forEach(k ->
				{
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("K", k);
					tag.setByte("S", (byte) know.getResearchStage(k));
					known.appendTag(tag);
					if(know.isResearchComplete(k))
						complete.add(k);
					research.add(k);
				});
				
				nbt.setString("KnowledgeOwner", playerIn.getGameProfile().getName());
				nbt.setString("KnowledgeTimestamp", sdf.format(Date.from(Instant.now())));
				nbt.setTag("Knowledge", known);
				
				if(!worldIn.isRemote)
					playerIn.sendMessage(new TextComponentTranslation("gui." + InfoTAR.MOD_ID + ":knowledge_tome.store", String.format("%,d", research.size()), String.format("%,d", complete.size())));
			}
			
			held.setTagCompound(nbt);
		} else if(!playerIn.isSneaking() && held.hasTagCompound() && held.getTagCompound().hasKey("KnowledgeOwner") && held.getTagCompound().hasKey("Knowledge"))
		{
			IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(playerIn);
			
			if(know != null)
			{
				NBTTagList list = held.getTagCompound().getTagList("Knowledge", NBT.TAG_COMPOUND);
				for(int i = 0; i < list.tagCount(); ++i)
				{
					NBTTagCompound tag = list.getCompoundTagAt(i);
					String k = tag.getString("K");
					know.setResearchStage(k, Math.max(know.getResearchStage(k), tag.getByte("S")));
				}
				
				if(playerIn instanceof EntityPlayerMP)
					know.sync((EntityPlayerMP) playerIn);
				
				if(!worldIn.isRemote)
				{
					playerIn.sendMessage(new TextComponentTranslation("gui." + InfoTAR.MOD_ID + ":knowledge_tome.restore", held.getTagCompound().getString("KnowledgeOwner"), held.getTagCompound().getString("KnowledgeTimestamp")));
					SoundUtil.playSoundEffect(worldIn, "thaumcraft:write", playerIn.getPosition(), 1F, 1F, SoundCategory.PLAYERS);
					SoundUtil.playSoundEffect(worldIn, "thaumcraft:learn", playerIn.getPosition(), 1F, 1F, SoundCategory.PLAYERS);
				} else if(!ConfigsTAR.reusable)
				{
					Vec3d look = Vec3d.fromPitchYaw(0, playerIn.rotationYawHead + 20);
					
					double x = playerIn.posX + look.x * .8;
					double y = playerIn.posY + playerIn.getEyeHeight() * .6;
					double z = playerIn.posZ + look.z * .8;
					
					FXDispatcher.INSTANCE.burst(x, y, z, 8F);
					SoundUtil.playSoundEffect(worldIn, "thaumcraft:wandfail", playerIn.getPosition(), .5F, 1F, SoundCategory.PLAYERS);
				}
				
				if(!ConfigsTAR.reusable)
					held.shrink(1);
			}
		}
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}