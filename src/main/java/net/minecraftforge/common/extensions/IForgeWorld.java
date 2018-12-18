/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.extensions;

import java.util.Iterator;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IForgeWorld extends ICapabilityProvider
{
    /**
     * Used in the getEntitiesWithinAABB functions to expand the search area for entities.
     * Modders should change this variable to a higher value if it is less then the radius
     * of one of there entities.
     */
    public static double MAX_ENTITY_RADIUS = 2.0D;

    default ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket> getPersistentChunks()
    {
        return ForgeChunkManager.getPersistentChunksFor((World)this);
    }

    default Iterator<Chunk> getPersistentChunkIterable(Iterator<Chunk> chunkIterator)
    {
        @SuppressWarnings("resource")
        World world = (World)this;
        final ImmutableSetMultimap<ChunkPos, Ticket> persistentChunksFor = getPersistentChunks();
        final ImmutableSet.Builder<Chunk> builder = ImmutableSet.builder();
        world.profiler.startSection("forcedChunkLoading");
        builder.addAll(persistentChunksFor.keys().stream().filter(Objects::nonNull).map(input -> world.getChunk(input.x, input.z)).iterator());
        world.profiler.endStartSection("regularChunkLoading");
        builder.addAll(chunkIterator);
        world.profiler.endSection();
        return builder.build().iterator();
    }
}
