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

import com.google.common.collect.ImmutableMap;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manage MapGenStructure for the ChunkGenerators.
 */
public class MapGenStructureManager
{
    private static final List<String> allStructureNames = Lists.newArrayList();
    private final Map<String, MapGenStructure> structureMap;

    public MapGenStructureManager(List<MapGenStructure> structures)
    {
        ImmutableMap.Builder<String, MapGenStructure> builder = ImmutableMap.builder();
        for (MapGenStructure s : structures)
        {
            String name = s.getStructureName();
            builder.put(name, s);
            addStructureName(name);
        }
        structureMap = builder.build();
        Collections.sort(allStructureNames);
    }

    /**
     * Add a structure name to the locate command autocomplete list.
     * <p>
     * Is automatically done on construction
     *
     * @param structure Name of the structure
     */
    protected static void addStructureName(String structure)
    {
        if (!allStructureNames.contains(structure))
        {
            allStructureNames.add(structure);
        }
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
        if (structureMap.containsKey(structureName))
        {
            return structureMap.get(structureName).getNearestStructurePos(worldIn, position, findUnexplored);
        }
        return null;
    }

    /**
     * Check if the given world position is within a structure with the given name.
     * Returns false if the structure is not found
     */
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
        if (structureMap.containsKey(structureName))
        {
            return structureMap.get(structureName).isInsideStructure(pos);
        }
        return false;
    }

    /**
     * Get the creatures of the given type the structure at the given position would like to spawn.
     *
     * @param defaults List of spawn entries. Should contain the (biome's) default entries. Won't be modified
     */
    @Nonnull
    public List<Biome.SpawnListEntry> getSpawns(List<Biome.SpawnListEntry> defaults, World world, EnumCreatureType creatureType, BlockPos pos)
    {
        List<Biome.SpawnListEntry> spawnEntries = Lists.newArrayList(defaults);//Don't modify the biome's list
        for (MapGenStructure structure : structureMap.values())
        {
            if (structure instanceof ISpawningStructure && structure.isInsideStructure(pos))
            {
                ((ISpawningStructure) structure).getSpawns(spawnEntries, world, creatureType, pos);
                return spawnEntries;
            }
        }
        return spawnEntries;
    }

    public void generateStructure(World world, Random rand, ChunkPos chunkPos)
    {
        for (MapGenStructure structure : structureMap.values())
        {
            structure.generateStructure(world, rand, chunkPos);
        }
    }

    public void generate(World worldIn, int x, int z, ChunkPrimer primer)
    {
        for (MapGenStructure structure : structureMap.values())
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
         * Collect the list of creatures of the given type that should spawn within the structures bounding box.
         * <p>
         * @param spawnEntries Contains the biome's default spawn entries. Modify this list to add your own spawns and/or clear the biome's ones.
         */
        void getSpawns(List<Biome.SpawnListEntry> spawnEntries, World world, EnumCreatureType creatureType, BlockPos pos);
    }

}
