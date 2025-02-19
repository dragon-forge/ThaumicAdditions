package org.zeith.thaumicadditions.asm.mixins;

import net.minecraft.block.Block;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.zeith.thaumicadditions.api.data.datas.SmelterData;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import org.zeith.thaumicadditions.asm.minmixin.annotations.MinMixin;
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
		String Block = obfuscatedEnv ? "Laow;" : "Lnet/minecraft/block/Block;";
		
		for(MethodNode method : node.methods)
		{
			findFirstInsnNode(method.instructions, i ->
			{
				return i instanceof TypeInsnNode
					   && i.getOpcode() == Opcodes.INSTANCEOF
					   && ((TypeInsnNode) i).desc.equals("thaumcraft/common/blocks/essentia/BlockSmelter");
			}).ifPresent(i ->
			{
				InsnList insn = new InsnList();
				insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getClass().getCanonicalName().replace('.', '/'), "process", String.format("(%s)%s", Block, Block), false));
				method.instructions.insertBefore(i, insn);
			});
		}
	}
	
	public static Block process(Block block)
	{
		if(SmelterData.isSmelter(block)) return BlocksTC.smelterThaumium;
		return block;
	}
}