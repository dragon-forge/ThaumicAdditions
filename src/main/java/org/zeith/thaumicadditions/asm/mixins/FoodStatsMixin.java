package org.zeith.thaumicadditions.asm.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.zeith.thaumicadditions.asm.minmixin.*;

@Debug
@MinMixin("net.minecraft.util.FoodStats")
public class FoodStatsMixin
		implements IMixin
{
	@Override
	public void apply(ClassNode node, boolean obfuscatedEnv)
	{
		String EntityPlayer = obfuscatedEnv ? "Laed;" : "Lnet/minecraft/entity/player/EntityPlayer;";
		
		findMethod(node, obfuscatedEnv ? "a" : "onUpdate", "(" + EntityPlayer + ")V", m ->
				onUpdateMixin(m, EntityPlayer)
		);
	}
	
	private void onUpdateMixin(MethodNode node, String EntityPlayer)
	{
		InsnList insn = new InsnList();
		
		insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
		insn.add(new VarInsnNode(Opcodes.ILOAD, 3));
		insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				FoodStatsMixin.class.getCanonicalName().replace('.', '/'),
				"hasNaturalRegeneration",
				"(" + EntityPlayer + "Z)Z",
				false
		));
		insn.add(new VarInsnNode(Opcodes.ISTORE, 3));
		
		findFirstInsnNode(node.instructions, f -> f instanceof VarInsnNode && f.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) f).var == 3).ifPresent(naturalRegeneration ->
		{
			node.instructions.insertBefore(naturalRegeneration.getNext(), insn);
		});
	}
	
	public static boolean hasNaturalRegeneration(EntityPlayer player, boolean naturalRegeneration)
	{
		return naturalRegeneration;
	}
}