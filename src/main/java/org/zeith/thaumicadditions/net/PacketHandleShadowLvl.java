package org.zeith.thaumicadditions.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.MainThreaded;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.zeith.thaumicadditions.inventory.container.ContainerShadowEnchanter;
import org.zeith.thaumicadditions.tiles.TileShadowEnchanter;

@MainThreaded
public class PacketHandleShadowLvl
		implements IPacket
{
	Enchantment ench;
	int action;

	public static PacketHandleShadowLvl create(int action, Enchantment ench)
	{
		PacketHandleShadowLvl p = new PacketHandleShadowLvl();
		p.ench = ench;
		p.action = action;
		return p;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("a", action);
		nbt.setString("e", ench.getRegistryName().toString());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		action = nbt.getInteger("a");
		ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(nbt.getString("e")));
	}

	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP mp = net.getSender();
		if(mp != null && mp.openContainer instanceof ContainerShadowEnchanter)
		{
			ContainerShadowEnchanter cse = Cast.cast(mp.openContainer);
			TileShadowEnchanter ench = cse.t;
			if(ench != null && this.ench != null)
			{
				if(action == 1)
					ench.upLvl(this.ench, mp);
				if(action == 2)
					ench.downLvl(this.ench, mp);
			}
		}
	}
}