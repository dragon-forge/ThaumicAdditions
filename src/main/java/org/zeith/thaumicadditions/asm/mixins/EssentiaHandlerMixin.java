package org.zeith.thaumicadditions.asm.mixins;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.zeith.thaumicadditions.asm.minmixin.*;

@MinMixin("thaumcraft.common.lib.events.EssentiaHandler")
public class EssentiaHandlerMixin
		implements IMixin
{
	@Override
	public void apply(ClassNode node, boolean obfuscatedEnv)
	{
		findMethod(node, "addEssentia", null, this::addNPECheck);
		findMethod(node, "drainEssentia", null, this::addNPECheck);
		findMethod(node, "drainEssentiaWithConfirmation", null, this::addNPECheck);
		findMethod(node, "findEssentia", null, this::addNPECheck);
		findMethod(node, "canAcceptEssentia", null, this::addNPECheck);
	}
	
	private void addNPECheck(MethodNode node)
	{
		node.instructions.insert(MMShortcuts.returnFalseIfArgIsNull(1));
	}
}