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

package net.minecraftforge.event.world;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class FoundChunksForSpawningEvent extends Event
{
    private final Builder builder;
    private ImmutableList<ChunkPos> eligibleChunksForSpawning;
    private ImmutableMap<EnumCreatureType, CreatureTypeData> creatureTypeData;

    public FoundChunksForSpawningEvent(Builder builderIn)
    {
        builder = builderIn;
    }

    public WorldServer getWorldServer()
    {
        return builder.worldServer;
    }

    /**
     * Usually the number of eligible chunks for spawning, but sometimes greater, depending on something about the world border and the player chunk map. A value of -1 indicates
     * that there was no attempt to calculate this number.
     */
    public int getNumAttemptedEligibleChunksForSpawning()
    {
        return builder.numAttemptedEligibleChunksForSpawning;
    }

    /**
     * The eligible chunks for spawning, as determined by player positions, etc.
     */
    public ImmutableList<ChunkPos> getEligibleChunksForSpawning()
    {
        if (eligibleChunksForSpawning == null)
        {
            eligibleChunksForSpawning = builder.eligibleChunksForSpawning.build();
        }
        return eligibleChunksForSpawning;
    }

    /**
     * Information about the current and maximum number of creatures in each category. An empty map indicates that no attempt was made to calculate i or to spawn any mobs. Missing
     * values indicate that a new entity exception was thrown while attempting to spawn a previous creature type.
     */
    public ImmutableMap<EnumCreatureType, CreatureTypeData> getCreatureTypeData()
    {
        if (creatureTypeData == null)
        {
            creatureTypeData = builder.creatureTypeData.build();
        }
        return creatureTypeData;
    }

    public static class CreatureTypeData
    {
        private final int creatureCount;
        private final int maxCreatureCountWhereCanSpawnMore;

        public CreatureTypeData()
        {
            creatureCount = -1;
            maxCreatureCountWhereCanSpawnMore = -1;
        }

        public CreatureTypeData(int creatureCountIn, int maxCreatureCountWhereCanSpawnMoreIn)
        {
            creatureCount = creatureCountIn;
            maxCreatureCountWhereCanSpawnMore = maxCreatureCountWhereCanSpawnMoreIn;
        }

        /**
         * The number of creatures of the given type that currently reside in the eligible spawn chunks. A value of -1 indicates that no attempt was made to calculate this number.
         */
        public int getCreatureCount()
        {
            return creatureCount;
        }

        /**
         * The maximum number of creatures of the given type that can reside in the eligible spawn chunks before the game stops attempting to spawn more. A value of -1 indicates
         * that no attempt was made to calculate this number.
         */
        public int getMaxCreatureCountWhereCanSpawnMore()
        {
            return maxCreatureCountWhereCanSpawnMore;
        }
    }

    /**
     * If not null, an exception was thrown in net.minecraft.world.biome.Biome.SpawnListEntry:newInstance. Entries may be missing from creatureTypeData.
     */
    @Nullable
    public Exception getNewEntityException()
    {
        return builder.newEntityException;
    }

    public static class Builder
    {
        public final WorldServer worldServer;
        public int numAttemptedEligibleChunksForSpawning = -1;
        public final ImmutableList.Builder<ChunkPos> eligibleChunksForSpawning = new ImmutableList.Builder<ChunkPos>();
        public final ImmutableMap.Builder<EnumCreatureType, CreatureTypeData> creatureTypeData = new ImmutableMap.Builder<EnumCreatureType, CreatureTypeData>();
        public Exception newEntityException;

        public Builder(WorldServer worldServerIn)
        {
            worldServer = worldServerIn;
        }
    }
}
