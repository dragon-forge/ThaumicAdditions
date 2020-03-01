package com.zeitheron.thaumicadditions.items.weapons;

import com.google.common.collect.Multimap;
import com.zeitheron.hammercore.utils.IRegisterListener;
import com.zeitheron.thaumicadditions.utils.LootHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

public class ItemTheBeheader
		extends Item
		implements IRegisterListener
{
	public final int attackDamage = 10;

	public ItemTheBeheader()
	{
		setTranslationKey("the_beheader");
		setMaxDamage(4096);
		setMaxStackSize(1);
	}

	@Override
	public void onRegistered()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
	{
		return false;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
	{
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

		if(equipmentSlot == EntityEquipmentSlot.MAINHAND)
		{
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.8D, 0));
		}

		return multimap;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		stack.damageItem(1, attacker);
		return true;
	}

	@SubscribeEvent
	public void livingDrops(LivingDropsEvent e)
	{
		if(e.getSource() != null && e.getSource().getTrueSource() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) e.getSource().getTrueSource();
			EntityLivingBase base = e.getEntityLiving();

			ItemStack main = player.getHeldItemMainhand();
			if(!main.isEmpty() && main.getItem() instanceof ItemTheBeheader)
			{
				if(base instanceof EntityZombie)
				{
					if(player.getRNG().nextInt(100) <= 20)
						e.getDrops().add(base.entityDropItem(new ItemStack(Items.SKULL, 1, 2), 0F));
				} else if(base instanceof EntitySkeleton)
				{
					if(player.getRNG().nextInt(100) <= 20)
						e.getDrops().add(base.entityDropItem(new ItemStack(Items.SKULL, 1, 0), 0F));
				} else if(base instanceof EntityCreeper)
				{
					if(player.getRNG().nextInt(100) <= 20)
						e.getDrops().add(base.entityDropItem(new ItemStack(Items.SKULL, 1, 4), 0F));
				} else if(base instanceof EntityPlayer)
				{
					if(player.getRNG().nextInt(100) <= 20)
					{
						ItemStack head = new ItemStack(Items.SKULL, 1, 3);
						NBTTagCompound root = new NBTTagCompound();
						NBTTagCompound tag = new NBTTagCompound();
						NBTUtil.writeGameProfile(tag, ((EntityPlayer) base).getGameProfile());
						root.setTag("SkullOwner", tag);
						head.setTagCompound(root);

						e.getDrops().add(base.entityDropItem(head, 0F));
					}
				} else if(base instanceof EntityLiving)
				{
					LootTable table = LootHelper.getDeathTable((EntityLiving) base);
					LootContext context = LootHelper.generateDropContent(e);
					LootHelper.lootEntryStream(table).forEach(e2 ->
					{
						NonNullList<ItemStack> items = LootHelper.getEntryItems(e2, player.getRNG(), context);
						Optional<ItemStack> head = items.stream().filter(stack -> !stack.isEmpty() && (stack.getItem().getRegistryName().toString().contains("head") || stack.getItem().getRegistryName().toString().contains("skull"))).findAny();
						if(head.isPresent() && player.getRNG().nextInt(100) <= 20)
							e.getDrops().add(base.entityDropItem(head.get().copy(), 0F));
					});
				}
			}
		}
	}
}