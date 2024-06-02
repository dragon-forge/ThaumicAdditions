package org.zeith.thaumicadditions.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import org.zeith.thaumicadditions.asm.mixins.EssentiaHandlerMixin;

public class TARTransformer
		implements IClassTransformer
{
	static final TransformerSystem asm = new TransformerSystem();
	
	static
	{
		register(new EssentiaHandlerMixin());
	}
	
	static void register(IMixin handle)
	{
		asm.register(handle);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		return asm.transform(name, transformedName, basicClass);
	}
}