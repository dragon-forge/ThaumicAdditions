package org.zeith.thaumicadditions.api.infusion;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.zeith.thaumicadditions.asm.mixins.accessor.IInfuserAccess;
import thaumcraft.common.tiles.crafting.TilePedestal;

import java.util.function.BiFunction;

public class ItemStackOperandResult
		extends InfusionResultType
{
	protected final BiFunction<IInfuserAccess, ItemStack, ItemStack> stackOperand;
	
	public ItemStackOperandResult(BiFunction<IInfuserAccess, ItemStack, ItemStack> stackOperand)
	{
		this.stackOperand = stackOperand;
	}
	
	@Override
	public IInfusionOutputInstance deserializeResult(NBTBase nbt)
	{
		return IInfusionOutputInstance.EMPTY;
	}
	
	@Override
	public NBTBase serializeResult(IInfusionOutputInstance result)
	{
		return new NBTTagByte((byte) 0);
	}
	
	@Override
	public void act(IInfusionOutputInstance result, IInfuserAccess infuser)
	{
		World world = infuser.TAR_asTile().getWorld();
		TileEntity te = world.getTileEntity(infuser.TAR_getCenterPedestal());
		if(!(te instanceof TilePedestal)) return;
		TilePedestal p = (TilePedestal) te;
		
		ItemStack qs = p.getStackInSlot(0);
		qs = stackOperand.apply(infuser, qs);
		p.setInventorySlotContentsFromInfusion(0, qs);
	}
}