package org.zeith.thaumicadditions.asm.mixins;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import org.zeith.thaumicadditions.asm.minmixin.annotations.Debug;
import org.zeith.thaumicadditions.asm.minmixin.annotations.MinMixin;
import org.zeith.thaumicadditions.asm.mixins.accessor.IInfuserAccess;

@Debug
@MinMixin("thaumcraft.common.tiles.crafting.TileInfusionMatrix")
public class InfuserMixin
		implements IMixin
{
	String InfusionResultType = "org.zeith.thaumicadditions.api.infusion.InfusionResultType".replace('.', '/');
	
	@Override
	public void apply(ClassNode node, boolean obfuscatedEnv)
	{
		String NBTTagCompound = obfuscatedEnv ? "fy" : "net/minecraft/nbt/NBTTagCompound";
		String ItemStack = obfuscatedEnv ? "aip" : "net/minecraft/item/ItemStack";
		
		String readFromNBTDesc = String.format("(L%s;)V", NBTTagCompound);
		String writeToNBTDesc = String.format("(L%s;)L%s;", NBTTagCompound, NBTTagCompound);
		
		for(MethodNode method : node.methods)
		{
			if(method.name.equals("craftingFinish")) craftingFinish(method, obfuscatedEnv);
			if(method.name.equals("craftingStart")) craftingStart(method, obfuscatedEnv);
			
			if(method.desc.equals(readFromNBTDesc))
			{
				AbstractInsnNode recipeinputLDC = IMixin.findFirstInsnNode(method.instructions, IMixin.ldcNode("recipeinput")).orElse(null);
				if(recipeinputLDC == null) continue;
				for(int i = 0; i < 4; i++) // we will land on aload_0 [this] right after the if block
					recipeinputLDC = recipeinputLDC.getPrevious();
				
				InsnList insn = new InsnList();
				
				insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, InfusionResultType, "fromNBT", String.format("(L%s;%s)V", NBTTagCompound, IMixin.getTypeDescriptor(IInfuserAccess.class)), false));
				
				method.instructions.insertBefore(recipeinputLDC, insn);
			} else if(method.desc.equals(writeToNBTDesc))
			{
				AbstractInsnNode instanceofItemStack = IMixin.findFirstInsnNode(method.instructions, IMixin.instanceofNode(ItemStack)).orElse(null);
				if(instanceofItemStack == null) continue;
				
				for(int i = 0; i < 5; i++) // we will land on aload_0 [this] right after the if block
					instanceofItemStack = instanceofItemStack.getPrevious();
				
				InsnList insn = new InsnList();
				
				insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, InfusionResultType, "toNBT", String.format("(L%s;%s)V", NBTTagCompound, IMixin.getTypeDescriptor(IInfuserAccess.class)), false));
				
				method.instructions.insertBefore(instanceofItemStack, insn);
			}
		}
	}
	
	private void craftingStart(MethodNode method, boolean obfuscatedEnv)
	{
		String NBTBase = obfuscatedEnv ? "gn" : "net/minecraft/nbt/NBTBase";
		IMixin.findInsnNode(method.instructions, n -> n.getOpcode() == Opcodes.CHECKCAST && n instanceof TypeInsnNode && ((TypeInsnNode) n).desc.equals(NBTBase), node ->
		{
			method.instructions.remove(node);
		});
	}
	
	private void craftingFinish(MethodNode method, boolean obfuscatedEnv)
	{
		String ItemStack = obfuscatedEnv ? "aip" : "net/minecraft/item/ItemStack";
		
		IMixin.findFirstInsnNode(method.instructions, IMixin.instanceofNode(ItemStack)).ifPresent(i ->
		{
			InsnList insn = new InsnList();
			
			insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
			insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
			insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
			insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, InfusionResultType, "finish", IMixin.getMethodDescriptor(Object.class, IInfuserAccess.class, Object.class, String.class), false));
			insn.add(new VarInsnNode(Opcodes.ASTORE, 1));
			
			method.instructions.insertBefore(i.getPrevious(), insn);
		});
	}
}