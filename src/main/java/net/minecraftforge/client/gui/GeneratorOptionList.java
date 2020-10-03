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

package net.minecraftforge.client.gui;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.generator.GeneratorType;
import net.minecraftforge.common.world.generator.GeneratorTypeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * GeneratorOptionList maintains a list of {@link BiomeGeneratorTypeScreens} which are
 * used in the world creation ui to represent user-selectable world generators.
 */
@OnlyIn(Dist.CLIENT)
public class GeneratorOptionList {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final GeneratorOptionList INSTANCE = new GeneratorOptionList();

    // Holds an ordered list of generator options that appear in the ui
    private final List<BiomeGeneratorTypeScreens> options = new ArrayList<>();
    // Maps GeneratorTypes to their corresponding ui option
    private final Map<GeneratorType, BiomeGeneratorTypeScreens> generatorTypes = new HashMap<>();
    // Maps ui options to their corresponding edit screen factory
    private final Map<BiomeGeneratorTypeScreens, BiomeGeneratorTypeScreens.IFactory> editScreens = new HashMap<>();

    private GeneratorOptionList() {
        // populate with the vanilla generators/edit screens
        GeneratorOption.collectGeneratorTypes(option -> {
            String name = getGeneratorName(option);
            GeneratorType type = GeneratorTypeManager.get().getGeneratorType(name);
            if (type == null) {
                LOGGER.error("Missing generator type for '{}'", name);
                return;
            }
            generatorTypes.put(type, option);
            options.add(option);
            LOGGER.debug("Registered GeneratorOption '{}'", name);
        });
        GeneratorOption.collectEditScreens(editScreens::put);
    }

    public synchronized int size() {
        return options.size();
    }

    public synchronized int indexOf(BiomeGeneratorTypeScreens generatorType) {
        return options.indexOf(generatorType);
    }

    public synchronized BiomeGeneratorTypeScreens get(int index) {
        return options.get(index);
    }

    // gets called when opening the CreateWorldScreen to set the initially selected generator
    public synchronized BiomeGeneratorTypeScreens getDefault() {
        // synchronize entries with the GeneratorTypeManager
        updateOptions();
        GeneratorType generatorType = GeneratorTypeManager.get().getDefaultGeneratorType();
        // returns vanilla's 'default' if not present
        return generatorTypes.getOrDefault(generatorType, BiomeGeneratorTypeScreens.field_239066_a_);
    }

    public synchronized boolean hasEditScreen(BiomeGeneratorTypeScreens generatorType) {
        return editScreens.containsKey(generatorType);
    }

    @Nullable
    public synchronized BiomeGeneratorTypeScreens.IFactory getEditScreen(BiomeGeneratorTypeScreens generatorType) {
        return editScreens.get(generatorType);
    }

    // synchronize the available GeneratorTypes with the options list so that they appear in the ui
    private void updateOptions() {
        GeneratorTypeManager.get().forEach(generatorType -> {
            if (!generatorType.isVisible()) {
                return;
            }

            // only add new generators
            if (!generatorTypes.containsKey(generatorType)) {
                GeneratorOption option = new GeneratorOption(generatorType);
                options.add(option);
                generatorTypes.put(generatorType, option);
                BiomeGeneratorTypeScreens.IFactory factory = generatorType.getEditScreen();
                if (factory != null) {
                    editScreens.put(option, factory);
                }
                LOGGER.debug("Registered GeneratorOption '{}'", generatorType.getName());
            }
        });
    }

    public static GeneratorOptionList get() {
        return INSTANCE;
    }

    private static String getGeneratorName(BiomeGeneratorTypeScreens type) {
        ITextComponent textComponent = type.func_239077_a_();
        if (textComponent instanceof TranslationTextComponent) {
            // expected to be generator.<name>
            String key = ((TranslationTextComponent) textComponent).getKey();

            int i = key.indexOf('.') + 1;
            if (i > 0 && i < key.length() - 1) {
                return key.substring(i);
            }
        }
        // shouldn't happen unless a mod has overridden BiomeGeneratorTypeScreens.func_239077_a_
        return textComponent.getString().toLowerCase();
    }

    private static class GeneratorOption extends BiomeGeneratorTypeScreens {

        private final GeneratorType generatorType;

        protected GeneratorOption(GeneratorType generatorType) {
            super(generatorType.getName());
            this.generatorType = generatorType;
        }

        @Override
        public DimensionGeneratorSettings func_241220_a_(DynamicRegistries.Impl registries, long seed, boolean structures, boolean bonusChest) {
            return generatorType.createDimensionGeneratorSettings(seed, structures, bonusChest, registries);
        }

        @Override
        protected ChunkGenerator func_241869_a(Registry<Biome> biomes, Registry<DimensionSettings> settings, long seed) {
            return generatorType.createChunkGenerator(seed, biomes, settings, null);
        }

        private static void collectGeneratorTypes(Consumer<BiomeGeneratorTypeScreens> consumer) {
            BiomeGeneratorTypeScreens.field_239068_c_.forEach(consumer);
        }

        private static void collectEditScreens(BiConsumer<BiomeGeneratorTypeScreens, BiomeGeneratorTypeScreens.IFactory> consumer) {
            BiomeGeneratorTypeScreens.field_239069_d_.forEach((typeOp, factory) -> typeOp.ifPresent(type -> consumer.accept(type, factory)));
        }
    }
}
