package org.zeith.thaumicadditions.asm.mixins.accessor;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.zeith.thaumicadditions.InfoTAR;
import org.zeith.thaumicadditions.api.data.DataType;
import org.zeith.thaumicadditions.asm.minmixin.annotations.Accessor;
import org.zeith.thaumicadditions.asm.minmixin.annotations.Modifier;

import java.util.ArrayList;

public interface IInfuserAccess
{
	DataType<IInfuserAccess> TYPE = new DataType<>(InfoTAR.id("infuser_data"), IInfuserAccess.class);
	
	@Accessor("pedestals")
	ArrayList<BlockPos> TAR_getPedestals();
	
	@Accessor("dangerCount")
	int TAR_getDangerCount();
	
	@Accessor("ingredients")
	ArrayList<ItemStack> TAR_getIngredients();
	
	@Accessor("recipePlayer")
	String TAR_getRecipePlayer();
	
	@Accessor("recipeOutputLabel")
	String TAR_getRecipeOutputLabel();
	
	@Accessor("recipeOutput")
	Object TAR_getRecipeOutput();
	
	@Modifier("recipeOutputLabel")
	void TAR_setRecipeOutputLabel(String label);
	
	@Modifier("recipeOutput")
	void TAR_setRecipeOutput(Object val);
	
	default TileEntity TAR_asTile()
	{
		return Cast.cast(this);
	}
	
	default BlockPos TAR_getCenterPedestal()
	{
		TileEntity te = TAR_asTile();
		return te.getPos().down(2);
	}
	
	default EntityPlayer TAR_getCraftingPlayer()
	{
		TileEntity te = TAR_asTile();
		return te.getWorld().getPlayerEntityByName(TAR_getRecipePlayer());
	}
}