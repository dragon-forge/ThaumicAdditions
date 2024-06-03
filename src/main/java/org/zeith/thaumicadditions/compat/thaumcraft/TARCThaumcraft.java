package org.zeith.thaumicadditions.compat.thaumcraft;

import com.zeitheron.hammercore.mod.ModuleLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.api.data.DataProviderRegistry;
import org.zeith.thaumicadditions.api.data.datas.*;
import org.zeith.thaumicadditions.api.data.datas.JarData.JarStorage;
import org.zeith.thaumicadditions.blocks.BlockAbstractEssentiaJar.BlockAbstractJarItem;
import org.zeith.thaumicadditions.blocks.BlockAbstractSmelter;
import org.zeith.thaumicadditions.compat.ITARC;
import org.zeith.thaumicadditions.items.tools.ItemVisScribingTools;
import org.zeith.thaumicadditions.tiles.TileAbstractJarFillable;
import thaumcraft.api.aspects.*;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.tools.ItemScribingTools;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import java.util.function.BiFunction;

@ModuleLoader(requiredModid = "thaumcraft")
public class TARCThaumcraft
		implements ITARC
{
	@Override
	public void init()
	{
		initSmelters();
		initMirrors();
		initGauntlets();
		initScribingTools();
		initJars();
	}
	
	public void initJars()
	{
		BiFunction<JarData, ItemStack, JarStorage> getJarStorage = (jar, stack) ->
		{
			if(stack.isEmpty()) return null;
			if(!(stack.getItem() instanceof IEssentiaContainerItem)) return null;
			IEssentiaContainerItem ctr = (IEssentiaContainerItem) stack.getItem();
			
			Aspect aspectFilter = null;
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AspectFilter"))
				aspectFilter = Aspect.getAspect(stack.getTagCompound().getString("AspectFilter"));
			
			AspectList al = ctr.getAspects(stack);
			if(al == null || al.size() <= 0) return new JarStorage(jar, null, aspectFilter, 0);
			Aspect aspect = al.getAspectsSortedByAmount()[0];
			return new JarStorage(jar, aspect, aspectFilter, al.getAmount(aspect));
		};
		
		BiFunction<ItemStack, JarStorage, ItemStack> setStorage = (stack, storage) ->
		{
			if(stack.isEmpty()) return stack;
			if(!(stack.getItem() instanceof IEssentiaContainerItem)) return stack;
			IEssentiaContainerItem ctr = (IEssentiaContainerItem) stack.getItem();
			ctr.setAspects(stack, storage.asList());
			NBTTagCompound tag = stack.getTagCompound();
			if(tag != null)
			{
				if(storage.aspectFilter != null) tag.setString("AspectFilter", storage.aspectFilter.getTag());
				else stack.removeSubCompound("AspectFilter");
			}
			return stack;
		};
		
		DataProviderRegistry.registerSimple(JarData.TYPE, TileJarFillable.class, j ->
		{
			if(j instanceof TileAbstractJarFillable)
			{
				TileAbstractJarFillable aj = (TileAbstractJarFillable) j;
				return new JarData(j, aj.getCapacity(), aj.voidsExcess(), getJarStorage, setStorage);
			}
			Block bt = j.getBlockType();
			if(bt == BlocksTC.jarNormal) return new JarData(j, 250, false, getJarStorage, setStorage);
			if(bt == BlocksTC.jarVoid) return new JarData(j, 250, true, getJarStorage, setStorage);
			return null;
		});
		
		DataProviderRegistry.registerSimple(JarData.TYPE, BlockAbstractJarItem.class, it ->
		{
			return new JarData(null, it.block.capacity, it.block.voidsExcess, getJarStorage, setStorage);
		});
		
		Item jarNormal = Item.getItemFromBlock(BlocksTC.jarNormal);
		Item jarVoid = Item.getItemFromBlock(BlocksTC.jarVoid);
		
		DataProviderRegistry.registerSimple(JarData.TYPE, BlockJarItem.class, it ->
		{
			if(it == jarNormal || it == jarVoid)
				return new JarData(null, 250, it == jarVoid, getJarStorage, setStorage);
			return null;
		});
	}
	
	public void initScribingTools()
	{
		DataProviderRegistry.registerStable(ScribingToolsData.TYPE, ItemScribingTools.class, ScribingToolsData.INSTANCE);
		DataProviderRegistry.registerStable(ScribingToolsData.TYPE, ItemVisScribingTools.class, ScribingToolsData.INSTANCE);
	}
	
	public void initSmelters()
	{
		DataProviderRegistry.registerStable(SmelterData.TYPE, BlockAbstractSmelter.class, SmelterData.INSTANCE);
	}
	
	public void initMirrors()
	{
		DataProviderRegistry.registerSimple(MirrorData.TYPE, TileMirror.class, m -> new MirrorData(
				m.linked,
				new BlockPos(m.linkX, m.linkY, m.linkZ),
				m.linkDim
		));
		
		DataProviderRegistry.registerSimple(MirrorData.TYPE, TileMirrorEssentia.class, m -> new MirrorData(
				m.linked,
				new BlockPos(m.linkX, m.linkY, m.linkZ),
				m.linkDim
		));
	}
	
	public void initGauntlets()
	{
		DataProviderRegistry.registerStable(GauntletData.TYPE, ItemCaster.class, GauntletData.INSTANCE);
	}
}