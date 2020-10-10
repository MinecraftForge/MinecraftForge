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

package net.minecraftforge.common.world.level;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.world.level.impl.DebugLevelType;
import net.minecraftforge.common.world.level.impl.FlatLevelType;
import net.minecraftforge.common.world.level.impl.OverworldLevelType;
import net.minecraftforge.common.world.level.impl.SingleBiomeLevelType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class LevelTypes
{
    private static final List<LevelType> BUILTIN_LEVEL_TYPES = new ArrayList<>();

    public static final LevelType DEFAULT = create("default", new OverworldLevelType(DimensionSettings.field_242734_c, false, false));
    public static final LevelType FLAT = create("flat", new FlatLevelType());
    public static final LevelType LARGE_BIOMES = create("large_biomes", new OverworldLevelType(DimensionSettings.field_242734_c, false, true));
    public static final LevelType AMPLIFIED = create("amplified", new OverworldLevelType(DimensionSettings.field_242735_d, false, false));
    public static final LevelType SINGLE_BIOME = create("single_biome_surface", new SingleBiomeLevelType(DimensionSettings.field_242734_c, Biomes.PLAINS));
    public static final LevelType CAVES = create("single_biome_caves", new SingleBiomeLevelType(DimensionSettings.field_242738_g, Biomes.PLAINS));
    public static final LevelType FLOATING_ISLANDS = create("single_biome_floating_islands", new SingleBiomeLevelType(DimensionSettings.field_242739_h, Biomes.PLAINS));
    public static final LevelType DEBUG = create("debug_all_block_states", new DebugLevelType());

    private LevelTypes()
    {

    }

    /**
     * Iterate each of the builtin {@link LevelType}s in order.
     *
     * @param action The action to perform on each {@link LevelType}
     */
    public static void forEach(Consumer<LevelType> action)
    {
        BUILTIN_LEVEL_TYPES.forEach(action);
    }

    private static LevelType create(String name, LevelType levelType) {
        // set registry name without namespace check
        levelType.setNameUnchecked(name);
        BUILTIN_LEVEL_TYPES.add(levelType);
        return levelType;
    }
}
