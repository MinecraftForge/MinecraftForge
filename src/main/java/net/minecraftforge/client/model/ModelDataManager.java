package net.minecraftforge.client.model;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;

public class ModelDataManager
{
    private static WeakReference<World> currentWorld = new WeakReference<>(null);
    
    private static final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new HashMap<>();
    
    private static final LoadingCache<ChunkPos, Map<BlockPos, IModelData>> modelDataCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .concurrencyLevel(5)
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(new CacheLoader<ChunkPos, Map<BlockPos, IModelData>>(){

                @Override
                public Map<BlockPos, IModelData> load(@Nonnull ChunkPos key)
                {
                    return new ConcurrentHashMap<>();
                }
            });
    
    private static void cleanCaches(World world)
    {
        if (world != currentWorld.get())
        {
            currentWorld = new WeakReference<>(world);
            needModelDataRefresh.clear();
            modelDataCache.invalidateAll();
        }
    }
    
    public static void requestModelDataRefresh(TileEntity te)
    {
        World world = te.getWorld();
        Preconditions.checkNotNull(world, "Tile entity world must not be null");
        Preconditions.checkArgument(world == Minecraft.getInstance().world, "Cannot request a model data refresh for a world other than the current client world");
        synchronized (needModelDataRefresh)
        {
            cleanCaches(world);
            needModelDataRefresh.computeIfAbsent(new ChunkPos(te.getPos()), $ -> Collections.synchronizedSet(new HashSet<>()))
                                .add(te.getPos());
        }
    }
    
    private static void refreshModelData(World world, ChunkPos chunk)
    {
        cleanCaches(world);
        Set<BlockPos> needUpdate;
        synchronized (needModelDataRefresh)
        {
            needUpdate = needModelDataRefresh.remove(chunk);
        }
        if (needUpdate != null)
        {
            Map<BlockPos, IModelData> data = modelDataCache.getUnchecked(chunk);
            for (BlockPos pos : needUpdate)
            {
                TileEntity toUpdate = world.getTileEntity(pos);
                if (toUpdate != null)
                {
                    data.put(pos, toUpdate.getModelData());
                }
            }
        }
    }
    
    public static @Nullable IModelData getModelData(World world, BlockPos pos)
    {
        return getModelData(world, new ChunkPos(pos)).get(pos);
    }
    
    public static Map<BlockPos, IModelData> getModelData(World world, ChunkPos pos)
    {
        refreshModelData(world, pos);
        return modelDataCache.getUnchecked(pos);
    }
}
