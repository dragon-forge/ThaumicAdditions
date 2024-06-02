package org.zeith.thaumicadditions.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import org.zeith.thaumicadditions.asm.mixins.*;

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
	}
	
	void register(IMixin handle)
	{
		asm.register(handle);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		return asm.transform(name, transformedName, basicClass);
	}
}