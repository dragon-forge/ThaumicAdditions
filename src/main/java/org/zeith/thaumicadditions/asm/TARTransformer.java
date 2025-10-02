package org.zeith.thaumicadditions.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import org.zeith.thaumicadditions.asm.minmixin.base.AccessorMixin;
import org.zeith.thaumicadditions.asm.mixins.*;
import org.zeith.thaumicadditions.asm.mixins.accessor.IInfuserAccess;

public class TARTransformer
		implements IClassTransformer
{
	protected final TransformerSystem asm = new TransformerSystem();
	
	public TARTransformer()
	{
		register(new EssentiaHandlerMixin());
		register(new FoodStatsMixin());
		register(new BlockSmelterAuxMixin());
		register(new ScanSkyMixin());
		register(new BlockJarMixin());
		register(new BlockJarItemMixin());
		registerAccessor(IInfuserAccess.class, "thaumcraft.common.tiles.crafting.TileInfusionMatrix");
		register(new InfuserMixin());
	}
	
	void register(IMixin handle)
	{
		asm.register(handle);
	}
	
	void register(IMixin handle, String... targets)
	{
		asm.register(handle, targets);
	}
	
	void registerAccessor(Class<?> accessor, String... targets)
	{
		asm.register(new AccessorMixin(accessor), targets);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		return asm.transform(name, transformedName, basicClass);
	}
}