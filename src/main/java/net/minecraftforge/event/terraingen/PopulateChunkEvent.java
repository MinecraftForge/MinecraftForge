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

import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * PopulateChunkEvent is fired when an event involving chunk terrain feature population occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #world} contains the world this event is occurring in.<br>
 * {@link #rand} contains an instance of random that can be used in this event.<br>
 * {@link #chunkX} contains the x-coordinate of the chunk currently being populated with a terrain feature.<br>
 * {@link #chunkZ} contains the z-coordinate of the chunk currently being populated with ores.<br>
 * {@link #hasVillageGenerated} contains the boolean value stating if the chunk already has a village spawned in it.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}, except {@link Populate}, which fires on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
 **/
public class PopulateChunkEvent extends ChunkGeneratorEvent
{
    private final World world;
    private final Random rand;
    private final int chunkX;
    private final int chunkZ;
    private final boolean hasVillageGenerated;

    public PopulateChunkEvent(IChunkGenerator gen, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
    {
        super(gen);
        this.world = world;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.hasVillageGenerated = hasVillageGenerated;
    }

    public World getWorld() { return world; }
    public Random getRand() { return rand; }
    public int getChunkX() { return chunkX; }
    public int getChunkZ() { return chunkZ; }
    public boolean isHasVillageGenerated() { return hasVillageGenerated; }
    /**
     * PopulateChunkEvent.Pre is fired just before a chunk is populated a terrain feature.<br>
     * This event is fired just before terrain feature generation in
     * {@link ChunkProviderEnd#populate(int, int)},
     * {@link ChunkProviderOverworld#populate(int, int)},
     * and {@link ChunkProviderHell#populate(int, int)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Pre extends PopulateChunkEvent
    {
        public Pre(IChunkGenerator gen, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(gen, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }

    /**
     * PopulateChunkEvent.Post is fired just after a chunk is populated with a terrain feature.<br>
     * This event is fired just after terrain feature generation in
     * {@link ChunkProviderEnd#populate(int, int)},
     * {@link ChunkProviderOverworld#populate(int, int)},
     * and {@link ChunkProviderHell#populate(int, int)}. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Post extends PopulateChunkEvent
    {
        public Post(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated)
        {
            super(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
        }
    }

    /**
     * PopulateChunkEvent.Populate is fired when a chunk is populated with a terrain feature.<br>
     * This event is fired during terrain feature generation in
     * {@link ChunkProviderEnd#populate(int, int)},
     * {@link ChunkProviderOverworld#populate(int, int)},
     * and {@link ChunkProviderHell#populate(int, int)}. <br>
     * <br>
     * {@link #type} contains the enum value for the terrain feature being generated. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event has a result. {@link HasResult} <br>
     * This result determines if the chunk is populated with the terrain feature. <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    @HasResult
    public static class Populate extends PopulateChunkEvent
    {
        public EventType getType()
        {
            return type;
        }

        /** Use CUSTOM to filter custom event types
         */
        public static enum EventType { DUNGEON, FIRE, GLOWSTONE, ICE, LAKE, LAVA, NETHER_LAVA, NETHER_LAVA2, NETHER_MAGMA, ANIMALS, CUSTOM }

        private final EventType type;

        public Populate(IChunkGenerator gen, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, EventType type)
        {
            super(gen, world, rand, chunkX, chunkZ, hasVillageGenerated);
            this.type = type;
        }
    }
}
