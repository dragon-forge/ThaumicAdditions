package org.zeith.thaumicadditions.init;

import com.zeitheron.hammercore.annotations.RegistryName;
import com.zeitheron.hammercore.annotations.SimplyRegister;
import net.minecraft.nbt.NBTTagCompound;
import org.zeith.thaumicadditions.api.infusion.ItemStackOperandResult;

@SimplyRegister
public interface InfuserRecipeTypesTAR
{
	@RegistryName("unbreakable")
	ItemStackOperandResult MAKE_UNBREAKABLE = new ItemStackOperandResult((iInfuserAccess, stack) ->
	{
		if(stack.getHasSubtypes()) return stack;
		if(!stack.isItemStackDamageable()) return stack;
		NBTTagCompound t = stack.getTagCompound();
		if(t == null) stack.setTagCompound(t = new NBTTagCompound());
		t.setBoolean("Unbreakable", true);
		stack.setItemDamage(0);
		return stack;
	});
}