/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common;

import com.google.common.collect.Lists;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Manage MapGenStructure for the ChunkGenerators.
 */
public class MapGenStructureManager
{
    private static final List<String> allStructureNames = Lists.newArrayList();
    private final List<MapGenStructure> structures;

    public MapGenStructureManager(List<MapGenStructure> structures)
    {
        this.structures = structures;
    }

    /**
     * Add a structure name to the locate command autocomplete list.
     * <p>
     * Is automatically done when registering your structure in {@link net.minecraftforge.event.terraingen.InitStructureGensEvent}
     *
     * @param structure Name of the structure
     */
    public static void addStructureName(String structure)
    {
        if(!allStructureNames.contains(structure))allStructureNames.add(structure);
    }

    /**
     * @return A new array containing both the given strings as well as the registered structure names
     */
    public static String[] appendStructureNames(String[] vanilla)
    {
        return ArrayUtils.addAll(vanilla, allStructureNames.toArray(new String[0]));
    }

    /**
     * Get the nearest structure with the given name to the given world position.
     * Returns null if the structure is not found
     */
    @Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
    {
        for (MapGenStructure struct : structures)
        {
            if (struct.getStructureName().equals(structureName))
            {
                return struct.getNearestStructurePos(worldIn, position, findUnexplored);
            }
        }
        return null;
    }

    /**
     * Check if the given world position is within a structure with the given name.
     * Returns false if the structure is not found
     */
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
        for (MapGenStructure struct : structures)
        {
            if (struct.getStructureName().equals(structureName))
            {
                return struct.isInsideStructure(pos);
            }
        }
        return false;
    }

    /**
     * Check if there is a given structure at the given position that would like to spawn creatures of the given type.
     */
    public boolean shouldSpawn(EnumCreatureType creatureType, BlockPos pos)
    {
        for (MapGenStructure structure : structures)
        {
            if (structure instanceof ISpawningStructure && structure.isInsideStructure(pos))
            {
                return ((ISpawningStructure) structure).shouldSpawn(creatureType);
            }
        }
        return false;
    }

    /**
     * Get the creatures of the given type the structure at the given position would like to spawn.
     * <p>
     * Only call if {@link MapGenStructureManager#shouldSpawn(EnumCreatureType, BlockPos)} returns true.
     * <p>
     * Returns an empty list if no structure is found
     */
    @Nonnull
    public List<Biome.SpawnListEntry> getSpawns(World world, EnumCreatureType creatureType, BlockPos pos)
    {
        for (MapGenStructure structure : structures)
        {
            if (structure instanceof ISpawningStructure && structure.isInsideStructure(pos))
            {
                return ((ISpawningStructure) structure).getSpawns(world, creatureType, pos);
            }
        }
        return Lists.newArrayList();
    }

    public void generateStructure(World world, Random rand, ChunkPos chunkPos)
    {
        for (MapGenStructure structure : structures)
        {
            structure.generateStructure(world, rand, chunkPos);
        }
    }

    public void generate(World worldIn, int x, int z, ChunkPrimer primer)
    {
        for (MapGenStructure structure : structures)
        {
            structure.generate(worldIn, x, z, primer);
        }
    }

    /**
     * Should be implemented by any {@link MapGenStructure} that would like to spawn creatures within it's area
     */
    public interface ISpawningStructure
    {
        /**
         * If you want to replace creature spawn list of the biome with your own list within the bounding box of your structure.
         */
        boolean shouldSpawn(EnumCreatureType creatureType);

        /**
         * Called when shouldSpawn returns true.
         *
         * Used instead of the biome's creature spawn list
         */
        @Nonnull
        List<Biome.SpawnListEntry> getSpawns(World world, EnumCreatureType creatureType, BlockPos pos);
    }

}
