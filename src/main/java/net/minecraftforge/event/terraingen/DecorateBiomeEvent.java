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

package net.minecraftforge.event.terraingen;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * DecorateBiomeEvent is fired when a BiomeDecorator is created.
 * <br>
 * This event is fired whenever a BiomeDecorator is created in
 * {@link DeferredBiomeDecorator#fireCreateEventAndReplace(Biome)}.<br>
 * <br>
 * {@link #world} contains the world that is being decorated. <br>
 * {@link #rand} contains an instance of Random to be used. <br>
 * {@link #chunkPos} contains the original chunk for the decorator. <br>
 * <br>
 * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
 * <br>
 * This event does not have a result. {@link HasResult}
 * <br>
 * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.
 **/
public class DecorateBiomeEvent extends Event
{
    private final World world;
    private final Random rand;
    private final ChunkPos chunkPos;

    public DecorateBiomeEvent(World world, Random rand, ChunkPos chunkPos)
    {
        this.world = world;
        this.rand = rand;
        this.chunkPos = chunkPos;
    }

    public World getWorld()
    {
        return world;
    }

    public Random getRand()
    {
        return rand;
    }

    public ChunkPos getChunkPos()
    {
        return chunkPos;
    }

    /**
     * This event is fired before a chunk is decorated with a biome feature.
     */
    public static class Pre extends DecorateBiomeEvent
    {
        public Pre(World world, Random rand, ChunkPos chunkPos)
        {
            super(world, rand, chunkPos);
        }
    }

    /**
     * This event is fired after a chunk is decorated with a biome feature.
     */
    public static class Post extends DecorateBiomeEvent
    {
        public Post(World world, Random rand, ChunkPos chunkPos)
        {
            super(world, rand, chunkPos);
        }
    }

    /**
     * This event is fired when a chunk is decorated with a biome feature.
     * <p>
     * You can set the result to DENY to prevent the default biome decoration.
     */
    @HasResult
    public static class Decorate extends DecorateBiomeEvent
    {
        /**
         * Use {@link EventType#CUSTOM} to filter custom event types
         */
        public enum EventType
        {
            BIG_SHROOM, CACTUS, CLAY, DEAD_BUSH, DESERT_WELL, LILYPAD, FLOWERS, FOSSIL, GRASS, ICE, LAKE_WATER, LAKE_LAVA, PUMPKIN, REED, ROCK, SAND, SAND_PASS2, SHROOM, TREE, CUSTOM
        }

        private final EventType type;
        @Nullable
        private final BlockPos placementPos;

        public Decorate(World world, Random rand, ChunkPos chunkPos, @Nullable BlockPos placementPos, EventType type)
        {
            super(world, rand, chunkPos);
            this.type = type;
            this.placementPos = placementPos;
        }

        public EventType getType()
        {
            return type;
        }

        /**
         * This may be anywhere inside the 2x2 chunk area for generation.
         * To get the original chunk position of the generation before a random location was chosen, use {@link #getChunkPos()}.
         *
         * @return the position used for original decoration, or null if it is not specified.
         */
        @Nullable
        public BlockPos getPlacementPos()
        {
            return this.placementPos;
        }
    }
}
