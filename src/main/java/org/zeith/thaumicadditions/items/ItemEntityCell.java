package org.zeith.thaumicadditions.items;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.raytracer.RayTracer;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.ChatTA;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.config.ConfigsTAR;
import org.zeith.thaumicadditions.init.ItemsTAR;
import thaumcraft.common.lib.SoundsTC;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ItemEntityCell
		extends Item
{
	public static final Set<Class<? extends Entity>> SAMPLE_BLACKLIST = new HashSet<>();

	public ItemEntityCell()
	{
		setTranslationKey("dna_sample");
		setMaxStackSize(1);
	}

	public static ItemStack sample(@Nullable EntityEntry entry, @Nullable Entity entity)
	{
		ItemStack stack = new ItemStack(ItemsTAR.ENTITY_CELL);
		NBTTagCompound sampled = sampleNBT(entry, entity);
		if(sampled == null)
			return stack;
		NBTTagCompound nbt = new NBTTagCompound();
		stack.setTagCompound(nbt);
		nbt.setTag("Entity", sampled);
		return stack;
	}

	@Nullable
	public static NBTTagCompound sampleNBT(@Nullable EntityEntry entry, @Nullable Entity entity)
	{
		if(entry == null && entity != null)
			entry = EntityRegistry.getEntry(entity.getClass());
		if(entry == null)
			return null;
		if(SAMPLE_BLACKLIST.contains(entry.getEntityClass()))
			return null;

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Id", entry.getRegistryName().toString());

		if(entity != null)
			nbt.setTag("Data", entity.serializeNBT());

		return entry.getEgg() == null ? null : nbt;
	}

	@Nullable
	public static Entity createFromDNA(ItemStack sample, World world, Vec3d pos, boolean spawn)
	{
		if(sample.isEmpty() || !sample.hasTagCompound() || !sample.getTagCompound().hasKey("Entity", NBT.TAG_COMPOUND))
			return null;
		NBTTagCompound nbt = sample.getTagCompound().getCompoundTag("Entity");
		EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("Id")));
		if(entry != null)
		{
			Entity ent = entry.newInstance(world);
			if(ent != null)
			{
				if(nbt.hasKey("Data", NBT.TAG_COMPOUND))
					ent.deserializeNBT(nbt.getCompoundTag("Data"));
				ent.posX = ent.prevPosX = pos.x;
				ent.posY = ent.prevPosY = pos.y;
				ent.posZ = ent.prevPosZ = pos.z;
				ent.setPositionAndUpdate(pos.x, pos.y, pos.z);
				if(spawn && !world.isRemote)
					world.spawnEntity(ent);
				return ent;
			}
		}
		return null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		double reach = RayTracer.getBlockReachDistance(playerIn);
		Vec3d vec3d = playerIn.getPositionEyes(1);
		Vec3d vec3d1 = playerIn.getLook(1);
		Vec3d vec3d2 = vec3d.add(vec3d1.x * reach, vec3d1.y * reach, vec3d1.z * reach);
		RayTraceResult r = worldIn.rayTraceBlocks(vec3d, vec3d2, false, false, true);
		ItemStack stack = playerIn.getHeldItem(handIn);

		if(!worldIn.isRemote && playerIn.isSneaking())
		{
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("Entity", NBT.TAG_COMPOUND))
			{
				stack.getTagCompound().removeTag("Entity");
				HCNet.swingArm(playerIn, handIn);
			}
		} else
			spawn:if(!worldIn.isRemote && r != null && r.typeOfHit == Type.BLOCK && stack.hasTagCompound() && stack.getTagCompound().hasKey("Entity", NBT.TAG_COMPOUND))
			{
				NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Entity").getCompoundTag("Data");
				BlockPos pos = r.getBlockPos().offset(r.sideHit);
				World world = worldIn;

				nbttagcompound.setString("id", stack.getTagCompound().getCompoundTag("Entity").getString("Id"));
				UUID uuid = UUID.randomUUID();
				nbttagcompound.setString("UUID", uuid.toString());
				NBTTagList nbttaglist = nbttagcompound.getTagList("Pos", 6);
				double d0 = (double) pos.getX() + .5;
				double d1 = pos.getY() + .5;
				double d2 = (double) pos.getZ() + .5;
				Entity entity = AnvilChunkLoader.readWorldEntityPos(nbttagcompound, world, d0, d1, d2, false);
				if(entity == null)
					break spawn;
				entity.setUniqueId(uuid);
				WorldServer ws = Cast.cast(world, WorldServer.class);
				for(int k = 0; k < 16 && ws != null && ws.getEntityFromUuid(entity.getUniqueID()) != null; ++k)
					entity.setUniqueId(UUID.randomUUID());
				EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving) entity : null;
				entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
				AnvilChunkLoader.spawnEntity(entity, world);
				SoundUtil.playSoundEffect(world, SoundsTC.poof.getRegistryName().toString(), pos, 1F, 1F, SoundCategory.PLAYERS);
				stack.getTagCompound().removeTag("Entity");
				HCNet.swingArm(playerIn, handIn);
			}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(isInCreativeTab(tab))
		{
			items.add(new ItemStack(this));
			for(EntityEntry e : ForgeRegistries.ENTITIES.getValuesCollection())
				if(e.getEgg() != null && !ConfigsTAR.entityBlacklist.contains(e.getRegistryName()))
					items.add(sample(e, null));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack sample, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(sample.isEmpty() || !sample.hasTagCompound() || !sample.getTagCompound().hasKey("Entity", NBT.TAG_COMPOUND))
		{
			tooltip.add(I18n.format("item.thaumadditions:dna_sample.blank"));
			return;
		}
		NBTTagCompound nbt = sample.getTagCompound().getCompoundTag("Entity");
		EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("Id")));
		if(entry != null)
			tooltip.add(I18n.format("item.thaumadditions:dna_sample.entity") + ": " + I18n.format("entity." + entry.getName() + ".name"));
	}

	@SideOnly(Side.CLIENT)
	public int getColor(ItemStack sample, int layer)
	{
		if(layer != 1 && layer != 2)
			return 0xFFFFFF;
		if(sample.isEmpty() || !sample.hasTagCompound() || !sample.getTagCompound().hasKey("Entity", NBT.TAG_COMPOUND))
			return 0xFFFFFF;
		NBTTagCompound nbt = sample.getTagCompound().getCompoundTag("Entity");
		EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("Id")));
		if(entry != null && entry.getEgg() != null)
		{
			EntityEggInfo inf = entry.getEgg();
			return layer == 2 ? inf.primaryColor : inf.secondaryColor;
		}
		return 0xFFFFFF;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null)
			stack.setTagCompound(nbt = new NBTTagCompound());
		EntityEntry ee = EntityRegistry.getEntry(entity.getClass());
		if(!nbt.hasKey("Entity") && !player.world.isRemote && ee != null && !ConfigsTAR.entityBlacklist.contains(ee.getRegistryName()))
		{
			NBTTagCompound sampled = sampleNBT(null, entity);
			if(sampled != null && !sampled.isEmpty())
			{
				NBTTagCompound data = sampled.getCompoundTag("Data");

				// Prevent lead dupe
				if(data.getBoolean("Leashed"))
				{
					data.setBoolean("Leashed", false);
					data.removeTag("Leash");
					WorldUtil.spawnItemStack(player.world, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.LEAD));
				}

				nbt.setTag("Entity", sampled);
				SoundUtil.playSoundEffect(player.world, SoundsTC.poof.getRegistryName().toString(), player.getPosition(), 1F, 1F, SoundCategory.PLAYERS);
				entity.setDead();
				return true;
			} else
			{
				TextComponentTranslation tct = new TextComponentTranslation("status." + InfoTAR.MOD_ID + ":dna_unpickable" + (entity instanceof EntityLivingBase ? "" : ".1"));
				tct.getStyle().setColor(TextFormatting.DARK_RED);
				ChatTA.sendMessage(player, tct);
				SoundUtil.playSoundEffect(player.world, SoundsTC.wandfail.getRegistryName().toString(), player.getPosition(), 1F, 1F, SoundCategory.PLAYERS);
			}
		}
		return false;
	}
}