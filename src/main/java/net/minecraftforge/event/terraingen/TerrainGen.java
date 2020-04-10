/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public abstract class TerrainGen
{
    public static <T extends InitNoiseGensEvent.Context> T getModdedNoiseGenerators(World world, Random rand, T original)
    {
        InitNoiseGensEvent<T> event = new InitNoiseGensEvent<>(world, rand, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getNewValues();
    }

    public static MapGenBase getModdedMapGen(MapGenBase original, InitMapGenEvent.EventType type)
    {
        InitMapGenEvent event = new InitMapGenEvent(type, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getNewGen();
    }

    public static boolean populate(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, Populate.EventType type)
    {
        PopulateChunkEvent.Populate event = new PopulateChunkEvent.Populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    /**
     * Use this method when there is a specific BlockPos location given for decoration.
     * If only the chunk position is available, use {@link #decorate(World, Random, ChunkPos, Decorate.EventType)} instead.
     *
     * @param world the world being generated in
     * @param rand the random generator used for decoration
     * @param chunkPos the original chunk position used for generation, passed to the decorator
     * @param placementPos the specific position used for generating a feature, somewhere in the 2x2 chunks used for decoration
     * @param type the type of decoration
     */
    public static boolean decorate(World world, Random rand, ChunkPos chunkPos, BlockPos placementPos, Decorate.EventType type)
    {
        Decorate event = new Decorate(world, rand, chunkPos, placementPos, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    /**
     * Use this method when generation doesn't have a specific BlockPos location for generation in the chunk.
     * If a specific BlockPos for generation is available, use {@link #decorate(World, Random, ChunkPos, BlockPos, Decorate.EventType)} instead.
     *
     * @param world the world being generated in
     * @param rand the random generator used for decoration
     * @param chunkPos the original chunk position used for generation, passed to the decorator
     * @param type the type of decoration
     */
    public static boolean decorate(World world, Random rand, ChunkPos chunkPos, Decorate.EventType type)
    {
        Decorate event = new Decorate(world, rand, chunkPos, null, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    @Deprecated
    public static boolean decorate(World world, Random rand, BlockPos pos, Decorate.EventType type)
    {
        return decorate(world, rand, new ChunkPos(pos), type);
    }

    public static boolean generateOre(World world, Random rand, WorldGenerator generator, BlockPos pos, GenerateMinable.EventType type)
    {
        GenerateMinable event = new GenerateMinable(world, rand, generator, pos, type);
        MinecraftForge.ORE_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    public static boolean saplingGrowTree(World world, Random rand, BlockPos pos)
    {
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, pos);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
}
