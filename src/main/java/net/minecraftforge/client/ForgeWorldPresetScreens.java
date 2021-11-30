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

package net.minecraftforge.client;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class ForgeWorldPresetScreens
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<ForgeWorldPreset, WorldPreset> GENERATORS = Maps.newHashMap();
    private static final Map<ForgeWorldPreset, WorldPreset.PresetEditor> GENERATOR_SCREEN_FACTORIES = Maps.newHashMap();

    public static synchronized void registerPresetEditor(ForgeWorldPreset type, WorldPreset.PresetEditor factory)
    {
        if (GENERATOR_SCREEN_FACTORIES.containsKey(type))
            throw new IllegalStateException("Factory has already been registered for: " + type);

        GENERATOR_SCREEN_FACTORIES.put(type, factory);
    }

    static WorldPreset getDefaultPreset()
    {
        ForgeWorldPreset def = ForgeWorldPreset.getDefaultWorldPreset();
        if (def == null)
        {
            return WorldPreset.NORMAL;
        }

        WorldPreset gen = GENERATORS.get(def);
        if (gen == null)
        {
            LOGGER.error("The default world type '{}' has not been added to the GUI. Was it registered too late?", def.getRegistryName());
            return WorldPreset.NORMAL;
        }

        return gen;
    }

    static WorldPreset.PresetEditor getPresetEditor(Optional<WorldPreset> generator, @Nullable WorldPreset.PresetEditor biomegeneratortypescreens$ifactory)
    {
        return generator.filter(gen -> gen instanceof GeneratorPreset)
                .map(type -> GENERATOR_SCREEN_FACTORIES.get(((GeneratorPreset)type).getWorldPreset()))
                .orElse(biomegeneratortypescreens$ifactory);
    }

    static void registerPresets()
    {
        ForgeRegistries.WORLD_TYPES.forEach(wt -> {
            GeneratorPreset gen = new GeneratorPreset(wt);
            GENERATORS.put(wt, gen);
            WorldPreset.registerGenerator(gen);
        });
    }

    private static class GeneratorPreset extends WorldPreset
    {
        private final ForgeWorldPreset worldPreset;

        public GeneratorPreset(ForgeWorldPreset wt)
        {
            super(wt.getDisplayName());
            worldPreset = wt;
        }

        public ForgeWorldPreset getWorldPreset()
        {
            return worldPreset;
        }

        @Nonnull
        @Override
        public WorldGenSettings create(@Nonnull RegistryAccess.RegistryHolder dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest)
        {
            return worldPreset.createSettings(dynamicRegistries, seed, generateStructures, bonusChest, "");
        }

        @Override
        protected ChunkGenerator generator(RegistryAccess p_194083_, long p_194084_) {
            return worldPreset.createChunkGenerator(p_194083_, p_194084_, "");
        }
    }
}
