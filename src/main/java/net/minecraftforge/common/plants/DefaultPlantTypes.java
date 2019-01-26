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

package net.minecraftforge.common.plants;

import net.minecraft.util.ResourceLocation;

public final class DefaultPlantTypes
{

	/**
	 * Represents plants that should be placed near water, such as Sugarcane.
	 */
    public static final PlantType BEACH = PlantType.get(new ResourceLocation("beach"));

    /**
     * Represents plants that grow in dark areas or underground, like mushrooms.
     */
    public static final PlantType CAVE = PlantType.get(new ResourceLocation("cave"));

    /**
     * Represents plants that should be placed on farmland, such as wheat, carrots, and potatoes.
     */
    public static final PlantType CROP = PlantType.get(new ResourceLocation("crop"));

    /**
     * Represents plants that should be grown on sand, such as cacti.
     */
    public static final PlantType DESERT = PlantType.get(new ResourceLocation("desert"));

    /**
     * Represents plants of hell, such as nether wart.
     */
    public static final PlantType NETHER = PlantType.get(new ResourceLocation("nether"));

    /**
     * Represents generic plants such as flowers that grow on grass in most locations.
     */
    public static final PlantType PLAINS = PlantType.get(new ResourceLocation("plains"));

    /**
     * Represents plants that grow on water, like lily pads.
     */
    public static final PlantType WATER = PlantType.get(new ResourceLocation("water"));
    
    /**
     * Represents tree saplings.
     */
    public static final PlantType SAPLING = PlantType.get(new ResourceLocation("sapling"));
    
    /**
     * Represents plants that grow in the end, like chorus flowers.
     */
    public static final PlantType ENDER = PlantType.get(new ResourceLocation("ender"));

}
