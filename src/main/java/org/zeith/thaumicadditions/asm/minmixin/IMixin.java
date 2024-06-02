package org.zeith.thaumicadditions.asm.minmixin;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Consumer;

public interface IMixin
{
	void apply(ClassNode node, boolean obfuscatedEnv);
	
	default void findMethod(ClassNode node, String name, String desc, Consumer<MethodNode> handler)
	{
		if(desc == null)
		{
			for(MethodNode method : node.methods)
				if(method.name.equals(name))
					handler.accept(method);
		} else
		{
			for(MethodNode method : node.methods)
				if(method.desc.equals(desc))
				{
					handler.accept(method);
					return;
				}
		}
	}
	
	default String getTarget()
	{
		return "";
	}
	
	default String[] getTargets()
	{
		return new String[] { getTarget() };
	}
}