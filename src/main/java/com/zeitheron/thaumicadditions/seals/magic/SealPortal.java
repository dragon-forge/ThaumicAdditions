package com.zeitheron.thaumicadditions.seals.magic;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.hammercore.utils.TeleporterDimPos;
import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.thaumicadditions.InfoTAR;
import com.zeitheron.thaumicadditions.api.seals.SealCombination;
import com.zeitheron.thaumicadditions.api.seals.SealInstance;
import com.zeitheron.thaumicadditions.client.seal.PortalRenderer;
import com.zeitheron.thaumicadditions.entity.EntitySealViewer;
import com.zeitheron.thaumicadditions.net.PacketReorientPlayer;
import com.zeitheron.thaumicadditions.net.PacketTP;
import com.zeitheron.thaumicadditions.seals.SealChunkLoader;
import com.zeitheron.thaumicadditions.tiles.TileSeal;
import com.zeitheron.thaumicadditions.utils.TP;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public class SealPortal extends SealInstance
{
	public static class PortalSealCombination extends SealCombination
	{
		public PortalSealCombination()
		{
			super(null, null, null);
		}
		
		@Override
		public String getRender(TileSeal seal, int index)
		{
			return "com.zeitheron.thaumicadditions.client.seal.TARSealRenders.renderPortal";
		}
		
		@Override
		public boolean isValid(TileSeal seal)
		{
			return seal.getSymbol(0) == Aspect.MAGIC && seal.getSymbol(1) == Aspect.AIR;
		}
		
		@Override
		public String toString()
		{
			return Aspect.MAGIC.getName() + ", " + Aspect.AIR.getName() + ", Any";
		}
		
		@Override
		public String getDescription(TileSeal seal)
		{
			Aspect a;
			if((a = seal.getSymbol(2)) != null)
				return I18n.format("seal.thaumadditions:portalb", a.getName());
			return I18n.format("seal.thaumadditions:portala");
		}
	}
	
	public static Set<Long> CHUNKS = new HashSet<>();
	public static final Map<Side, Map<String, Map<Aspect, Map<Integer, List<Long>>>>> NETWORKS = new HashMap<>();
	
	public static List<Long> getNetwork(Aspect item, World world, String player)
	{
		Side s = world.isRemote ? Side.CLIENT : Side.SERVER;
		Map<String, Map<Aspect, Map<Integer, List<Long>>>> NT = NETWORKS.get(s);
		if(NT == null)
			NETWORKS.put(s, NT = new HashMap<>());
		Map<Aspect, Map<Integer, List<Long>>> lnets = NT.get(player);
		if(lnets == null)
			NT.put(player, lnets = new HashMap<>());
		Map<Integer, List<Long>> nets = lnets.get(item);
		if(nets == null)
			lnets.put(item, nets = new HashMap<>());
		List<Long> parts = nets.get(world.provider.getDimension());
		if(parts == null)
			nets.put(world.provider.getDimension(), parts = new ArrayList<>());
		return parts;
	}
	
	public Object txRender;
	
	public ItemStack target;
	
	public EntitySealViewer viewer;
	public int cooldown;
	public int holeSize, targetHoleSize;
	
	public Long bound, lastbound;
	
	public SealPortal(TileSeal seal)
	{
		super(seal);
	}
	
	@SideOnly(Side.CLIENT)
	public void acceptChunks()
	{
		if(bound == null)
			return;
		
		World w = seal.getWorld();
		
		if(!w.isRemote)
			return;
		
		BlockPos rp = BlockPos.fromLong(bound);
		ChunkProviderClient cpc = (ChunkProviderClient) w.getChunkProvider();
		
		if(rp != null && holeSize > 0)
		{
			int cx = rp.getX() >> 4, cz = rp.getZ() >> 4;
			if(!cpc.isChunkGeneratedAt(cx, cz) || cpc.provideChunk(cx, cz).isEmpty())
			{
				cpc.loadChunk(cx, cz);
				// HCNet.manager.sendToServer(new PacketRequestChunk(cx, cz,
				// 0));
				CHUNKS.add(ChunkPos.asLong(cx, cz));
			}
		}
	}
	
	@Override
	public void onEntityCollisionWithSeal(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if(entityIn instanceof EntitySealViewer)
			return;
		
		if(entityIn.getEntityBoundingBox().intersects(state.getBoundingBox(worldIn, pos).offset(pos).grow(.4)) && cooldown <= 0 && bound != null)
		{
			WorldLocation loc = new WorldLocation(worldIn, BlockPos.fromLong(bound.longValue()));
			TileSeal tile = loc.getTileOfType(TileSeal.class);
			if(tile != null)
			{
				EnumFacing towards = tile.orientation;
				BlockPos tp = tile.getPos().offset(towards);
				
				double x = tp.getX() + .5, y = tp.getY() - 1, z = tp.getZ() + .5;
				
				BlockPos target = new BlockPos(x, y, z);
				if(!tile.getWorld().isAirBlock(target))
					y += 1;
				
				float yaw = entityIn.rotationYaw;
				switch(towards.ordinal())
				{
				case 2:
				{
					yaw = 180.0f;
					break;
				}
				case 3:
				{
					yaw = 0.0f;
					break;
				}
				case 4:
				{
					yaw = 90.0f;
					break;
				}
				case 5:
				{
					yaw = 270.0f;
					break;
				}
				default:
				break;
				}
				
				entityIn.rotationYaw = yaw;
				
				if(!worldIn.isRemote)
				{
					if(entityIn instanceof EntityPlayer)
					{
						TP.teleport((EntityPlayer) entityIn, x, y, z);
						
						if(entityIn instanceof EntityPlayerMP)
						{
							EntityPlayerMP mp = (EntityPlayerMP) entityIn;
							
							PacketTP tpp = new PacketTP();
							tpp.target = new Vec3d(x, y, z);
							HCNet.INSTANCE.sendTo(tpp, mp);
							
							PacketReorientPlayer prp = new PacketReorientPlayer();
							prp.yaw = yaw;
							HCNet.INSTANCE.sendTo(prp, mp);
						}
					}
					
					TeleporterDimPos.of(x, y, z, worldIn.provider.getDimension()).teleport(entityIn);
				}
				
				SealInstance combo = tile.instance;
				if(combo instanceof SealPortal)
				{
					SealPortal portal = (SealPortal) combo;
					portal.cooldown = 60;
					portal.holeSize = 0;
				}
				cooldown = 60;
				holeSize = 0;
				SoundUtil.playSoundEffect(seal.getLocation(), "minecraft:entity.endermen.teleport", 1F, 1F, SoundCategory.BLOCKS);
				SoundUtil.playSoundEffect(tile.getLocation(), "minecraft:entity.endermen.teleport", 1F, 1F, SoundCategory.BLOCKS);
				seal.sync();
				tile.sync();
			}
		}
	}
	
	@Override
	public boolean onSealActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(hand != EnumHand.MAIN_HAND)
			return false;
		
		List<Long> positions = getNetwork(seal.getSymbol(2), seal.getWorld(), seal.placer.get());
		while(positions.contains(null))
			positions.remove(null);
		Long oldbound = bound;
		int j = positions.indexOf(bound);
		int attempts = positions.size() + 2;
		boolean ok = false;
		while(attempts > 0)
		{
			++j;
			j %= positions.size();
			bound = positions.get(j);
			--attempts;
			
			if(positions.indexOf(bound) != -1 && bound != null && !Objects.equal(bound, oldbound) && !Objects.equal(pos.toLong(), positions.get(j)))
			{
				ok = true;
				cooldown += 5;
				break;
			}
		}
		seal.sync();
		HCNet.swingArm(playerIn, hand);
		SoundUtil.playSoundEffect(seal.getLocation(), InfoTAR.MOD_ID + ":pclose", 1F, 1F, SoundCategory.BLOCKS);
		return true;
	}
	
	@Override
	public void onSealBreak()
	{
		if(viewer != null)
		{
			viewer.setDead();
			viewer = null;
			SealChunkLoader.INSTANCE.relaxChunk(seal.getWorld().provider.getDimension(), seal.getLocation().getChunk().x, seal.getLocation().getChunk().z);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		cooldown = nbt.getInteger("Cooldown");
		target = new ItemStack(nbt.getCompoundTag("Target"));
		targetHoleSize = nbt.getInteger("TargetHoleSize");
		
		if(nbt.hasKey("Bound", NBT.TAG_LONG))
			bound = nbt.getLong("Bound");
		else
			bound = null;
	}
	
	@SideOnly(Side.CLIENT)
	private void render()
	{
		if(txRender != null)
			try
			{
				((PortalRenderer) txRender).doRender = true;
			} catch(Throwable er)
			{
			}
	}
	
	@Override
	public void tick()
	{
		if(cooldown > 0)
		{
			--cooldown;
			if(cooldown == 0)
				seal.sendChangesToNearby();
		}
		
		if(!Objects.equal(lastbound, bound) && viewer != null)
		{
			viewer.setDead();
			viewer = null;
		}
		
		lastbound = bound;
		
		BlockPos pos = seal.getPos();
		EnumFacing face = seal.orientation;
		
		float spreadNegX = face.getAxis() != Axis.X ? 1 : face == EnumFacing.WEST ? .5F : 0;
		float spreadNegY = face.getAxis() != Axis.Y ? 1 : face == EnumFacing.DOWN ? .5F : 0;
		float spreadNegZ = face.getAxis() != Axis.Z ? 1 : face == EnumFacing.NORTH ? .5F : 0;
		
		float spreadPosX = face.getAxis() != Axis.X ? 1 : face == EnumFacing.EAST ? .5F : 0;
		float spreadPosY = face.getAxis() != Axis.Y ? 1 : face == EnumFacing.UP ? .5F : 0;
		float spreadPosZ = face.getAxis() != Axis.Z ? 1 : face == EnumFacing.SOUTH ? .5F : 0;
		
		World w = seal.getWorld();
		List<Entity> ents = seal.getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - spreadNegX, pos.getY() - spreadNegY, pos.getZ() - spreadNegZ, pos.getX() + spreadPosX + 1, pos.getY() + spreadPosY + 1, pos.getZ() + spreadPosZ + 1), e -> !(e instanceof EntitySealViewer));
		
		int prevHoleSize = holeSize;
		int ths = targetHoleSize;
		if(!w.isRemote)
		{
			// if(!w.isRemote && bound != null)
			// {
			// BlockPos rpos = BlockPos.fromLong(bound);
			// TileSeal seal = WorldUtil.cast(w.getTileEntity(rpos),
			// TileSeal.class);
			//
			// if(seal != null)
			// {
			// SealPortal portal = WorldUtil.cast(seal.instance,
			// SealPortal.class);
			// ents.addAll(seal.getWorld().getEntitiesWithinAABB(Entity.class,
			// new AxisAlignedBB(rpos.getX() - spreadNegX, rpos.getY() -
			// spreadNegY, rpos.getZ() - spreadNegZ, rpos.getX() + spreadPosX +
			// 1, rpos.getY() + spreadPosY + 1, rpos.getZ() + spreadPosZ + 1), e
			// -> !(e instanceof EntitySealViewer)));
			// }
			// }
			
			targetHoleSize = ents.isEmpty() || cooldown > 0 ? 0 : 5;
			
			if(ths != targetHoleSize)
				seal.sendChangesToNearby();
		}
		
		holeSize += Math.min(Math.max(-1, targetHoleSize - holeSize), 1);
		
		List<Long> positions = getNetwork(seal.getSymbol(2), seal.getWorld(), seal.placer.get());
		
		while(positions.contains(null))
			positions.remove(null);
		
		long lpos = pos.toLong();
		if(!positions.contains(lpos))
			positions.add(lpos);
		
		for(int i = 0; i < positions.size(); ++i)
		{
			BlockPos rp = BlockPos.fromLong(positions.get(i));
			TileSeal seal = WorldUtil.cast(w.getTileEntity(rp), TileSeal.class);
			if(seal == null || !(seal.instance instanceof SealPortal))
				positions.remove(i);
		}
		if(prevHoleSize == 0 && holeSize > 0 && !w.isRemote)
			SoundUtil.playSoundEffect(seal.getLocation(), InfoTAR.MOD_ID + ":popen", 1F, 1F, SoundCategory.BLOCKS);
		if(prevHoleSize > 0 && holeSize == 0 && !w.isRemote)
			SoundUtil.playSoundEffect(seal.getLocation(), InfoTAR.MOD_ID + ":pclose", 1F, 1F, SoundCategory.BLOCKS);
		if(bound == null || positions.indexOf(bound) == -1 || Objects.equal(bound, seal.getPos().toLong()))
			try
			{
				for(Long l : positions)
				{
					if(l == null)
						continue;
					WorldLocation loc = new WorldLocation(seal.getWorld(), BlockPos.fromLong(l));
					if(Objects.equal(l, seal.getPos().toLong()))
						continue;
					bound = l;
					break;
				}
			} catch(ConcurrentModificationException cme /* So stupid -_- */)
			{
			}
		if(w.isRemote)
			try
			{
				render();
			} catch(Throwable er)
			{
				// Extra safe about stuff
			}
		
		if(!w.isRemote)
			SealChunkLoader.INSTANCE.forceChunk(seal.getWorld().provider.getDimension(), seal.getLocation().getChunk().x, seal.getLocation().getChunk().z);
		
		if(!w.isRemote && bound != null)
		{
			BlockPos rpos = BlockPos.fromLong(bound);
			TileSeal seal = WorldUtil.cast(w.getTileEntity(rpos), TileSeal.class);
			
			if(seal != null)
			{
				SealPortal portal = WorldUtil.cast(seal.instance, SealPortal.class);
				
				boolean changed = target == null || seal.stack.get().isItemEqual(target);
				
				if(portal != null)
					target = seal.stack.get();
				
				if(changed)
					seal.sendChangesToNearby();
			} else
				bound = null;
		}
		
		if(w.isRemote && bound != null)
		{
			BlockPos rp = BlockPos.fromLong(bound);
			
			TileSeal remote = WorldUtil.cast(w.getTileEntity(rp), TileSeal.class);
			
			if(remote != null && viewer == null)
			{
				SealPortal portal = WorldUtil.cast(remote.instance, SealPortal.class);
				if(portal != null)
					w.spawnEntity(viewer = new EntitySealViewer(w, remote.getPos().getX(), remote.getPos().getY(), remote.getPos().getZ(), remote.orientation));
			} else
				acceptChunks();
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Cooldown", cooldown);
		nbt.setInteger("TargetHoleSize", targetHoleSize);
		if(target != null)
			nbt.setTag("Target", target.serializeNBT());
		
		if(bound != null)
			nbt.setLong("Bound", bound);
		
		return nbt;
	}
}