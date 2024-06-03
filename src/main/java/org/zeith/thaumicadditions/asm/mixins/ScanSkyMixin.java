package org.zeith.thaumicadditions.asm.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.zeith.thaumicadditions.api.data.datas.ScribingToolsData;
import org.zeith.thaumicadditions.asm.minmixin.IMixin;
import org.zeith.thaumicadditions.asm.minmixin.MinMixin;
import org.zeith.thaumicadditions.init.ItemsTAR;
import thaumcraft.api.items.IScribeTools;

@MinMixin("thaumcraft.common.lib.research.ScanSky")
public class ScanSkyMixin
		implements IMixin
{
	@Override
	public void apply(ClassNode node, boolean obfuscatedEnv)
	{
		String EntityPlayer = obfuscatedEnv ? "Laed;" : "Lnet/minecraft/entity/player/EntityPlayer;";
		
		for(MethodNode method : node.methods)
		{
			if(!method.name.equals("onSuccess")) continue;
			findInsnNode(method.instructions, i ->
			{
				return i instanceof MethodInsnNode
					   && i.getOpcode() == Opcodes.INVOKESTATIC
					   && ((MethodInsnNode) i).owner.equals("thaumcraft/common/lib/utils/InventoryUtils")
					   && ((MethodInsnNode) i).name.equals("isPlayerCarryingAmount");
			}, i ->
			{
				InsnList insn = new InsnList();
				insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, getClass().getCanonicalName().replace('.', '/'), "process", String.format("(Z%s)Z", EntityPlayer), false));
				method.instructions.insert(i, insn);
			});
		}
	}
	
	public static boolean process(boolean hasItem, EntityPlayer player)
	{
		if(!hasItem)
		{
			InventoryPlayer inv = player.inventory;
			for(int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack it = inv.getStackInSlot(i);
				if(!it.isEmpty() && ScribingToolsData.isScribingTools(it))
					return true;
			}
		}
		
		return hasItem;
	}
}