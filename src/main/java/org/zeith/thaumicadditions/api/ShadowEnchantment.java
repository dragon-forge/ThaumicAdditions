package org.zeith.thaumicadditions.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public class ShadowEnchantment
{
	private static final Map<Enchantment, ShadowEnchantment> ENCH_REGISTRY = new HashMap<>();
	private static final List<ShadowEnchantment> REGISTRY = new ArrayList<>();

	public final Enchantment enchantment;
	protected final IntFunction<AspectList> aspects;
	protected final ResourceLocation icon;
	protected final String research;

	public ShadowEnchantment(Enchantment enchantment, IntFunction<AspectList> aspects, ResourceLocation icon, String research)
	{
		this.enchantment = enchantment;
		this.aspects = aspects;
		this.icon = icon;
		this.research = research;
	}

	public static ShadowEnchantment pick(Enchantment ench)
	{
		return ENCH_REGISTRY.get(ench);
	}

	public static void registerEnchantment(ShadowEnchantment ench)
	{
		REGISTRY.add(ench);
		ENCH_REGISTRY.put(ench.enchantment, ench);
	}

	public static void registerEnchantment(Enchantment ench, IntFunction<AspectList> aspects, ResourceLocation icon, @Nullable String research)
	{
		registerEnchantment(new ShadowEnchantment(ench, aspects, icon, research));
	}

	public static AspectBuilder aspectBuilder()
	{
		return new AspectBuilder();
	}

	public static List<ShadowEnchantment> getRegistry()
	{
		return REGISTRY;
	}

	public AspectList getAspects(int lvl)
	{
		return aspects.apply(lvl);
	}

	public Enchantment getEnchantment()
	{
		return enchantment;
	}

	public ResourceLocation getIcon()
	{
		return icon;
	}

	public String getResearch()
	{
		return research;
	}

	public boolean canBeAppliedBy(EntityPlayer player)
	{
		if(research != null)
		{
			IPlayerKnowledge k = ThaumcraftCapabilities.getKnowledge(player);
			return k != null && k.isResearchComplete(research);
		}
		return true;
	}

	public static class AspectBuilder
			implements IntFunction<AspectList>
	{
		private final Map<Aspect, IntUnaryOperator> aspects = new HashMap<>();

		private AspectBuilder()
		{
		}

		public AspectBuilder powByLvl(Aspect aspect, int baseAmount)
		{
			aspects.put(aspect, lvl -> (int) Math.round(Math.pow(baseAmount, lvl)));
			return this;
		}

		public AspectBuilder multiplyByLvl(Aspect aspect, int baseAmount)
		{
			aspects.put(aspect, lvl -> baseAmount * lvl);
			return this;
		}

		public AspectBuilder constant(Aspect aspect, int amount)
		{
			aspects.put(aspect, lvl -> amount);
			return this;
		}

		@Override
		public AspectList apply(int i)
		{
			AspectList list = new AspectList();
			aspects.forEach((a, f) -> list.add(a, f.applyAsInt(i)));
			return list;
		}
	}
}