package org.zeith.thaumicadditions.seals;

import com.zeitheron.hammercore.annotations.MCFBus;
import com.zeitheron.hammercore.internal.chunk.ChunkPredicate.IChunkLoader;
import com.zeitheron.hammercore.internal.chunk.ChunkPredicate.LoadableChunk;
import com.zeitheron.hammercore.internal.chunk.ChunkloadAPI;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkDataEvent.Load;
import net.minecraftforge.event.world.ChunkDataEvent.Save;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.thaumicadditions.seals.magic.SealPortal;

import java.util.ArrayList;

@MCFBus
public class SealChunkLoader
		implements IChunkLoader
{
	public static SealChunkLoader INSTANCE;
	public final ArrayList<LoadableChunk> forced = new ArrayList<>();

	{
		INSTANCE = this;
		ChunkloadAPI.addChunkLoader(this);
	}

	@SubscribeEvent
	public void chunkLoad(Load e)
	{
		if(e.getData().getBoolean("LostThaum_Chunkloaded"))
			forceChunk(e.getChunk().getWorld().provider.getDimension(), e.getChunk().x, e.getChunk().z);
	}

	@SubscribeEvent
	public void chunkSave(Save e)
	{
		e.getData().setBoolean("LostThaum_Chunkloaded", forced.contains(new LoadableChunk(e.getChunk().getWorld().provider.getDimension(), e.getChunk().x, e.getChunk().z)));
	}

	public void forceChunk(int dim, int x, int z)
	{
		LoadableChunk lc = new LoadableChunk(dim, x, z);
		if(!forced.contains(lc))
			forced.add(lc);
	}

	@Override
	public ArrayList<LoadableChunk> getForceLoadedChunks()
	{
		return forced;
	}

	public void relaxChunk(int dim, int x, int z)
	{
		LoadableChunk lc = new LoadableChunk(dim, x, z);
		if(forced.contains(lc))
			forced.remove(lc);
	}

	@SubscribeEvent
	public void unloadChunk(Unload e)
	{
		long pos = ChunkPos.asLong(e.getChunk().x, e.getChunk().z);

		if(e.getWorld().isRemote && SealPortal.CHUNKS.contains(pos))
		{
			if(e.isCancelable())
				e.setCanceled(true);
			SealPortal.CHUNKS.remove(pos);
		}
	}
}