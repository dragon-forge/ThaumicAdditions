package org.zeith.thaumicadditions.net.fxh;

import com.zeitheron.hammercore.api.lighting.ColoredLightManager;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.thaumicadditions.client.fx.FXShadowBeam;

import java.util.ArrayList;
import java.util.List;

public class FXShadowBeamParticle
		implements IPacket
{
	private List<Vec3d> positions;

	public static FXShadowBeamParticle create(List<Vec3d> list)
	{
		FXShadowBeamParticle p = new FXShadowBeamParticle();
		p.positions = list;
		return p;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Count", positions.size());
		int i = -1;
		for(Vec3d v : positions)
			Helper.setVec3d(nbt, "P" + ++i, v);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		int j = nbt.getInteger("Count");
		positions = new ArrayList<>(j);
		for(int i = 0; i < j; ++i)
			positions.add(Helper.getVec3d(nbt, "P" + i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		new FXShadowBeam(ColoredLightManager.getClientPlayer().world, positions.get(0).x, positions.get(0).y, positions.get(0).z, positions).spawn();
	}
}