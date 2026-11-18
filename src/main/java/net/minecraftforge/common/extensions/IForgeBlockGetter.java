/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.*;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelDataManager;
import org.jetbrains.annotations.Nullable;

public interface IForgeBlockGetter
{
    private BlockGetter self() { return (BlockGetter) this; }

    /**
     * Get the {@link BlockEntity} at the given position if it exists.
     * <p>
     * {@link Level#getBlockEntity(BlockPos)} would create a new {@link BlockEntity} if the
     * {@link net.minecraft.world.level.block.Block} has one, but it has not been placed in the world yet
     * (This can happen on world load).
     * @return The BlockEntity at the given position or null if it doesn't exist
     */
    @Nullable
    default BlockEntity getExistingBlockEntity(BlockPos pos)
    {
        if (this instanceof Level level)
        {
            if (!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
            {
                return null;
            }

            return level.getChunk(pos).getExistingBlockEntity(pos);
        }
        else if (this instanceof LevelChunk chunk)
        {
            return chunk.getBlockEntities().get(pos);
        }
        else if (this instanceof ImposterProtoChunk chunk)
        {
            return chunk.getWrapped().getExistingBlockEntity(pos);
        }
        return self().getBlockEntity(pos);
    }

    /**
     * Retrieves the model data manager for this level.
     * This will be {@code null} on a server level, and on the copies of a client level which are used by the meshing threads.
     * <p>
     * The manager must only be used from the main client thread. Use {@link #getModelData(BlockPos)} to access the model data itself.
     */
    @Nullable
    default ModelDataManager getModelDataManager()
    {
        return null;
    }

    /**
     * Retrieves the {@link ModelData} provided by the block entity at the given position.
     * <p>
     * Unlike the block entity itself, this data can safely be accessed from a client meshing thread.
     *
     * @return the model data at the given position, or {@link ModelData#EMPTY} if there is none or this is not a client level
     */
    default ModelData getModelData(BlockPos pos)
    {
        return ModelData.EMPTY;
    }
}
