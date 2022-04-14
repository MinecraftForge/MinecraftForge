/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "forge", bus = Bus.FORGE, value = Dist.CLIENT)
public class ModelDataManager
{
    private static WeakReference<Level> currentLevel = new WeakReference<>(null);
    
    private static final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap<>();
    
    private static final Map<ChunkPos, Map<BlockPos, IModelData>> modelDataCache = new ConcurrentHashMap<>();

    private static void cleanCaches(Level level)
    {
        Preconditions.checkNotNull(level, "Level must not be null");
        Preconditions.checkArgument(level == Minecraft.getInstance().level, "Cannot use model data for a level other than the current client level");
        if (level != currentLevel.get())
        {
            currentLevel = new WeakReference<>(level);
            needModelDataRefresh.clear();
            modelDataCache.clear();
        }
    }
    
    public static void requestModelDataRefresh(BlockEntity te)
    {
        Preconditions.checkNotNull(te, "Tile entity must not be null");
        Level level = te.getLevel();

        cleanCaches(level);
        needModelDataRefresh.computeIfAbsent(new ChunkPos(te.getBlockPos()), $ -> Collections.synchronizedSet(new HashSet<>()))
                            .add(te.getBlockPos());
    }
    
    private static void refreshModelData(Level level, ChunkPos chunk)
    {        
        cleanCaches(level);
        Set<BlockPos> needUpdate = needModelDataRefresh.remove(chunk);

        if (needUpdate != null)
        {
            Map<BlockPos, IModelData> data = modelDataCache.computeIfAbsent(chunk, $ -> new ConcurrentHashMap<>());
            for (BlockPos pos : needUpdate)
            {
                BlockEntity toUpdate = level.getBlockEntity(pos);
                if (toUpdate != null && !toUpdate.isRemoved())
                {
                    data.put(pos, toUpdate.getModelData());
                }
                else
                {
                    data.remove(pos);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        if (!event.getChunk().getWorldForge().isClientSide()) return;
        
        ChunkPos chunk = event.getChunk().getPos();
        needModelDataRefresh.remove(chunk);
        modelDataCache.remove(chunk);
    }
    
    public static @Nullable IModelData getModelData(Level level, BlockPos pos)
    {
        return getModelData(level, new ChunkPos(pos)).get(pos);
    }
    
    public static Map<BlockPos, IModelData> getModelData(Level level, ChunkPos pos)
    {
        Preconditions.checkArgument(level.isClientSide, "Cannot request model data for server level");
        refreshModelData(level, pos);
        return modelDataCache.getOrDefault(pos, Collections.emptyMap());
    }
}
