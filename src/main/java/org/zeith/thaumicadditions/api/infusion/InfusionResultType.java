package org.zeith.thaumicadditions.api.infusion;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.zeith.thaumicadditions.TAReconstructed;
import org.zeith.thaumicadditions.asm.mixins.accessor.IInfuserAccess;
import org.zeith.thaumicadditions.init.RegistriesTAR;

public abstract class InfusionResultType
		extends Impl<InfusionResultType>
{
	public abstract IInfusionOutputInstance deserializeResult(NBTBase nbt);
	
	public abstract NBTBase serializeResult(IInfusionOutputInstance result);
	
	public abstract void act(IInfusionOutputInstance result, IInfuserAccess infuser);
	
	public Object createRecipeResult(IInfusionOutputInstance result)
	{
		return new Object[] { "TAR~" + getRegistryName(), result };
	}
	
	public static Object finish(IInfuserAccess infuser, Object out, String label)
	{
		if(label != null && label.startsWith("TAR~"))
		{
			ResourceLocation loc = new ResourceLocation(label.substring(4));
			InfusionResultType type = RegistriesTAR.INFUSION_TYPES().getValue(loc);
			if(type != null && out instanceof IInfusionOutputInstance)
			{
				type.act((IInfusionOutputInstance) out, infuser);
				return null;
			}
			TAReconstructed.LOG.warn("Unable to find custom recipe output label {}.", loc);
		}
		return out;
	}
	
	public static void toNBT(NBTTagCompound nbt, IInfuserAccess infuser)
	{
		String label = infuser.TAR_getRecipeOutputLabel();
		Object out = infuser.TAR_getRecipeOutput();
		if(label != null && label.startsWith("TAR~") && out instanceof IInfusionOutputInstance)
		{
			ResourceLocation loc = new ResourceLocation(label.substring(4));
			InfusionResultType type = RegistriesTAR.INFUSION_TYPES().getValue(loc);
			if(type != null)
			{
				nbt.setString("rotype", label);
				nbt.setTag("recipeout", type.serializeResult((IInfusionOutputInstance) out));
			}
		}
	}
	
	public static void fromNBT(NBTTagCompound nbt, IInfuserAccess infuser)
	{
		String label = nbt.getString("rotype");
		if(label != null && label.startsWith("TAR~"))
		{
			ResourceLocation loc = new ResourceLocation(label.substring(4));
			InfusionResultType type = RegistriesTAR.INFUSION_TYPES().getValue(loc);
			if(type != null)
			{
				infuser.TAR_setRecipeOutputLabel(label);
				infuser.TAR_setRecipeOutput(type.deserializeResult(nbt.getTag("recipeout")));
			}
		}
	}
}