/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class PlantType
{
    private static final Pattern INVALID_CHARACTERS = Pattern.compile("[^a-z_]"); //Only a-z and _ are allowed, meaning names must be lower case. And use _ to separate words.
    private static final Map<String, PlantType> VALUES = new ConcurrentHashMap<>();

    public static final PlantType PLAINS = get("plains");
    public static final PlantType DESERT = get("desert");
    public static final PlantType BEACH = get("beach");
    public static final PlantType CAVE = get("cave");
    public static final PlantType WATER = get("water");
    public static final PlantType NETHER = get("nether");
    public static final PlantType CROP = get("crop");

    /**
     * Getting a custom {@link PlantType}, or an existing one if it has the same name as that one. Your plant should implement {@link IPlantable}
     * and return this custom type in {@link IPlantable#getPlantType(IBlockReader, BlockPos)}.
     *
     * <p>If your new plant grows on blocks like any one of them above, never create a new {@link PlantType}.
     * This Type is only functioning in
     * {@link net.minecraft.block.Block#canSustainPlant(BlockState, IBlockReader, BlockPos, Direction, IPlantable)},
     * which you are supposed to override this function in your new block and create a new plant type to grow on that block.
     *
     * This method can be called during parallel loading
     * @param name the name of the type of plant, you had better follow the style above
     * @return the acquired {@link PlantType}, a new one if not found.
     */
    public static PlantType get(String name)
    {
        return VALUES.computeIfAbsent(name, e ->
        {
            if (INVALID_CHARACTERS.matcher(e).find())
                throw new IllegalArgumentException("PlantType.get() called with invalid name: " + name);
            return new PlantType(e);
        });
    }

    private final String name;

    private PlantType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}

