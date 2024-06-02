package org.zeith.thaumicadditions.asm.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import thaumcraft.api.aspects.Aspect;

public class EssentiaHandlerMixin
		implements IMixin
{
	@Override
	public void apply(ClassNode node, boolean obfuscatedEnv)
	{
		findMethod(node, "addEssentia", null, this::addNPECheck);
	}
	
	private void addNPECheck(MethodNode node)
	{
		LabelNode l1 = new LabelNode();
		
		InsnList insn = new InsnList();
		insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
		insn.add(new JumpInsnNode(Opcodes.IFNONNULL, l1));
		insn.add(new InsnNode(Opcodes.H_PUTFIELD));
		insn.add(new InsnNode(Opcodes.IRETURN));
		insn.add(l1);
		
		node.instructions.insert(insn);
	}
	
	@Override
	public String getTarget()
	{
		return "thaumcraft.common.lib.events.EssentiaHandler";
	}
	
	public static boolean addEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror, int ext)
	{
		if(aspect == null) return false;
		
		tile.validate();
		return tile != null;
	}
}