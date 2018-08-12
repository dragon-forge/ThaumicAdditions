package com.zeitheron.thaumicadditions.seals;

import java.util.ArrayList;

import com.zeitheron.hammercore.annotations.MCFBus;
import com.zeitheron.hammercore.internal.chunk.ChunkPredicate.IChunkLoader;
import com.zeitheron.hammercore.internal.chunk.ChunkPredicate.LoadableChunk;
import com.zeitheron.hammercore.internal.chunk.ChunkloadAPI;
import com.zeitheron.thaumicadditions.seals.magic.SealPortal;

import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@MCFBus
public class SealChunkLoader implements IChunkLoader
{
	public static SealChunkLoader INSTANCE;
	
	{
		INSTANCE = this;
		ChunkloadAPI.addChunkLoader(this);
	}
	
	public final ArrayList<LoadableChunk> forced = new ArrayList<>();
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load e)
	{
		if(e.getData().getBoolean("LostThaum_Chunkloaded"))
			forceChunk(e.getChunk().getWorld().provider.getDimension(), e.getChunk().x, e.getChunk().z);
	}
	
	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save e)
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
			forced.remove(forced.indexOf(lc));
	}
	
	@SubscribeEvent
	public void unloadChunk(ChunkEvent.Unload e)
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