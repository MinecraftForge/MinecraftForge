/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.model.data;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A manager for the lifecycle of all the {@link ModelData} instances in a {@link Level}.
 * <p>
 * Users should not be instantiating or using this themselves unless they know what they're doing.
 */
@ApiStatus.Internal
@EventBusSubscriber(modid = "forge", bus = Bus.FORGE, value = Dist.CLIENT)
public class ModelDataManager
{
    private final Level level;
    private final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap<>();
    private final Map<ChunkPos, Map<BlockPos, ModelData>> modelDataCache = new ConcurrentHashMap<>();

    public ModelDataManager(Level level)
    {
        this.level = level;
    }

    public void requestRefresh(@NotNull BlockEntity blockEntity)
    {
        Preconditions.checkNotNull(blockEntity, "Block entity must not be null");
        needModelDataRefresh.computeIfAbsent(new ChunkPos(blockEntity.getBlockPos()), $ -> Collections.synchronizedSet(new HashSet<>()))
                            .add(blockEntity.getBlockPos());
    }

    private void refreshAt(ChunkPos chunk)
    {
        Set<BlockPos> needUpdate = needModelDataRefresh.remove(chunk);

        if (needUpdate != null)
        {
            Map<BlockPos, ModelData> data = modelDataCache.computeIfAbsent(chunk, $ -> new ConcurrentHashMap<>());
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

    public @Nullable ModelData getAt(BlockPos pos)
    {
        return getAt(new ChunkPos(pos)).get(pos);
    }

    public Map<BlockPos, ModelData> getAt(ChunkPos pos)
    {
        Preconditions.checkArgument(level.isClientSide, "Cannot request model data for server level");
        refreshAt(pos);
        return modelDataCache.getOrDefault(pos, Collections.emptyMap());
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        var level = event.getChunk().getWorldForge();
        if (level == null)
            return;

        var modelDataManager = level.getModelDataManager();
        if (modelDataManager == null)
            return;

        ChunkPos chunk = event.getChunk().getPos();
        modelDataManager.needModelDataRefresh.remove(chunk);
        modelDataManager.modelDataCache.remove(chunk);
    }
}
