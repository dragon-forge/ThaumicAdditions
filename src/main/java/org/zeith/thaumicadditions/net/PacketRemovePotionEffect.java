package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MainThreaded
public class PacketRemovePotionEffect
		implements IPacket
{
	Potion potion;

	public PacketRemovePotionEffect(Potion potion)
	{
		this.potion = potion;
	}

	public PacketRemovePotionEffect()
	{
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("p", potion.getRegistryName().toString());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(nbt.getString("p")));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		Minecraft.getMinecraft().player.removePotionEffect(potion);
	}
}