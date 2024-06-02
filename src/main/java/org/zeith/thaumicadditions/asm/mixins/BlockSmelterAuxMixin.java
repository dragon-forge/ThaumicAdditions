package org.zeith.thaumicadditions.asm.mixins;

import net.minecraft.block.Block;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import org.zeith.thaumicadditions.asm.minmixin.MinMixin;
import org.zeith.thaumicadditions.blocks.BlockAbstractSmelter;
import thaumcraft.api.blocks.BlocksTC;

@MinMixin({
		"thaumcraft.common.blocks.essentia.BlockSmelterAux",
		"thaumcraft.common.blocks.essentia.BlockSmelterVent"
})
public class BlockSmelterAuxMixin
		implements IMixin
{
	@Override
	public void apply(ClassNode node, boolean obfuscatedEnv)
	{
		InsnList insn = new InsnList();
		
		String Block = obfuscatedEnv ? "Laow;" : "Lnet/minecraft/block/Block;";
		insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getClass().getCanonicalName().replace('.', '/'), "process", String.format("(%s)%s", Block, Block), false));
		
		for(MethodNode method : node.methods)
		{
			findFirstInsnNode(method.instructions, i ->
			{
				return i instanceof TypeInsnNode
					   && i.getOpcode() == Opcodes.INSTANCEOF
					   && ((TypeInsnNode) i).desc.equals("thaumcraft/common/blocks/essentia/BlockSmelter");
			}).ifPresent(i ->
			{
				method.instructions.insertBefore(i, insn);
			});
		}
	}
	
	public static Block process(Block block)
	{
		if(block instanceof BlockAbstractSmelter)
			return BlocksTC.smelterThaumium;
		return block;
	}
}