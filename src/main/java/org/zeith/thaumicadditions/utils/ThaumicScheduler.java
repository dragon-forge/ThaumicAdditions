package org.zeith.thaumicadditions.utils;

import com.zeitheron.hammercore.annotations.MCFBus;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

/**
 * For future use, coded for Rivkat
 *
 * @author Zeitheron
 */
@MCFBus
public class ThaumicScheduler
{
	private static ThaumicScheduler instance;
	private final List<Runnable> scheduleds = new ArrayList<>();
	private final IntList delayss = new IntArrayList();
	private final List<Runnable> scheduledc = new ArrayList<>();
	private final IntList delaysc = new IntArrayList();

	{
		instance = this;
	}

	public static void schedule(int runAfterTicks, Runnable run)
	{
		if(instance == null)
		{
			instance = new ThaumicScheduler();
			MinecraftForge.EVENT_BUS.register(instance);
		}

		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			instance.delayss.add(runAfterTicks);
			instance.scheduleds.add(run);
		} else
		{
			instance.delaysc.add(runAfterTicks);
			instance.scheduledc.add(run);
		}
	}

	@SubscribeEvent
	public void serverTick(ServerTickEvent e)
	{
		if(e.phase == Phase.END)
			for(int i = 0; i < delayss.size(); ++i)
			{
				int j = delayss.get(i);
				if(j > 0)
					if(j == 1)
					{
						delayss.remove(i);
						scheduleds.remove(i).run();
					} else
						delayss.set(i, j - 1);
			}
	}

	@SubscribeEvent
	public void clientTick(ClientTickEvent e)
	{
		if(e.phase == Phase.END)
			for(int i = 0; i < delaysc.size(); ++i)
			{
				int j = delaysc.get(i);
				if(j > 0)
					if(j == 1)
					{
						delaysc.remove(i);
						scheduledc.remove(i).run();
					} else
						delaysc.set(i, j - 1);
			}
	}
}