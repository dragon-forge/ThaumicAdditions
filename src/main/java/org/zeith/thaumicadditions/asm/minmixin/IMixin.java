package org.zeith.thaumicadditions.asm.minmixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.*;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IMixin
{
	Logger LOG = LogManager.getLogger("ThaumicAdditionsCore");
	
	void apply(ClassNode node, boolean obfuscatedEnv);
	
	default void findInsnNode(InsnList insn, Predicate<AbstractInsnNode> filter, Consumer<AbstractInsnNode> handler)
	{
		AbstractInsnNode i = insn.getFirst();
		while(i != null)
		{
			if(filter.test(i)) handler.accept(i);
			i = i.getNext();
		}
	}
	
	default Optional<AbstractInsnNode> findFirstInsnNode(InsnList insn, Predicate<AbstractInsnNode> filter)
	{
		AbstractInsnNode i = insn.getFirst();
		while(i != null)
		{
			if(filter.test(i)) return Optional.of(i);
			i = i.getNext();
		}
		return Optional.empty();
	}
	
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
}