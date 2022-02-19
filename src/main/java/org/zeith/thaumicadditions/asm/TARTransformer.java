package org.zeith.thaumicadditions.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.zeith.thaumicadditions.asm.TransformerSystem.IASMHook;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class TARTransformer
		implements IClassTransformer
{
	static final TransformerSystem asm = new TransformerSystem();

	static
	{
		hook((node, obf) ->
		{

		}, "Patching Golem AI", cv(""));
	}

	public static Predicate<String> cv(String c)
	{
		return s -> c.compareTo(s) == 0;
	}

	public static void hook(BiConsumer<ClassNode, Boolean> handle, String desc, Predicate<String> acceptor)
	{
		asm.addHook(new IASMHook()
		{
			@Override
			public void transform(ClassNode node, boolean obf)
			{
				handle.accept(node, obf);
			}

			@Override
			public String opName()
			{
				return desc;
			}

			@Override
			public boolean accepts(String name)
			{
				return acceptor.test(name);
			}
		});
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		return asm.transform(name, transformedName, basicClass);
	}
}