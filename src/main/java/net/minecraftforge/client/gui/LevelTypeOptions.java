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
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraftforge.common.world.level.LevelType;
import net.minecraftforge.common.world.level.LevelTypeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * LevelTypeOptions maintains a list of {@link net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens} which are
 * used in the world creation gui to represent user-selectable {@link LevelType}s that have been registered with
 * the {@link LevelTypeManager}.
 */
@OnlyIn(Dist.CLIENT)
public class LevelTypeOptions
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LevelTypeOptions INSTANCE = new LevelTypeOptions();

    // Holds an ordered list of level type options that appear in the ui
    private final List<BiomeGeneratorTypeScreens> options = new ArrayList<>();
    // Maps LevelTypes to their corresponding ui option
    private final Map<LevelType, BiomeGeneratorTypeScreens> levelTypes = new HashMap<>();
    // Maps ui options to their corresponding edit screen factory
    private final Map<BiomeGeneratorTypeScreens, BiomeGeneratorTypeScreens.IFactory> editScreens = new HashMap<>();

    private LevelTypeOptions()
    {
        // populate with the vanilla generators/edit screens
        LevelTypeOption.forEachOption(option -> {
            String name = getOptionName(option);
            LevelType type = LevelTypeManager.get().getLevelType(name);
            if (type == null)
            {
                LOGGER.error("Missing LevelType for '{}'", name);
                return;
            }
            levelTypes.put(type, option);
            options.add(option);
            LOGGER.debug("Registered LevelType '{}'", name);
        });
        LevelTypeOption.forEachEditScreen(editScreens::put);
    }

    public synchronized int size()
    {
        return options.size();
    }

    public synchronized int indexOf(BiomeGeneratorTypeScreens generatorType)
    {
        return options.indexOf(generatorType);
    }

    public synchronized BiomeGeneratorTypeScreens get(int index)
    {
        return options.get(index);
    }

    // gets called when opening the CreateWorldScreen to set the initial generator type
    public synchronized BiomeGeneratorTypeScreens getDefault()
    {
        LevelType levelType = LevelTypeManager.get().getDefaultLevelType();
        BiomeGeneratorTypeScreens option = levelTypes.get(levelType);
        if (option == null)
        {
            // synchronize entries with the GeneratorTypeManager & try again
            updateOptions();
            option = levelTypes.get(levelType);
        }
        return option != null ? option : BiomeGeneratorTypeScreens.field_239066_a_;
    }

    // determines whether a generator option has a a 'Customize' button to open its EditScreen
    public synchronized boolean hasEditScreen(BiomeGeneratorTypeScreens generatorType)
    {
        return editScreens.containsKey(generatorType);
    }

    // returns an 'EditScreenFactory' which creates new instances of the generator types configuration screen
    @Nullable
    public synchronized BiomeGeneratorTypeScreens.IFactory getEditScreen(BiomeGeneratorTypeScreens generatorType)
    {
        return editScreens.get(generatorType);
    }

    // synchronize the available GeneratorTypes with the options list so that they appear in the ui
    public synchronized void updateOptions()
    {
        LevelTypeManager.get().forEach(levelType -> {
            // only add new types
            if (!levelTypes.containsKey(levelType))
            {
                LevelTypeOption option = new LevelTypeOption(levelType);
                options.add(option);
                levelTypes.put(levelType, option);
                BiomeGeneratorTypeScreens.IFactory factory = levelType.getEditScreen();
                if (factory != null)
                {
                    editScreens.put(option, factory);
                }
                LOGGER.debug("Registered LevelType '{}'", levelType.getName());
            }
        });
    }

    public static LevelTypeOptions get()
    {
        return INSTANCE;
    }

    public static boolean isHidden(BiomeGeneratorTypeScreens option)
    {
        if (option instanceof LevelTypeOption)
        {
            return ((LevelTypeOption) option).isHidden();
        }
        return false;
    }

    private static String getOptionName(BiomeGeneratorTypeScreens option)
    {
        ITextComponent textComponent = option.func_239077_a_();
        if (textComponent instanceof TranslationTextComponent)
        {
            // expected to be generator.<name>
            String key = ((TranslationTextComponent) textComponent).getKey();

            int i = key.indexOf('.') + 1;
            if (i > 0 && i < key.length() - 1)
            {
                return key.substring(i);
            }
        }
        return textComponent.getString().toLowerCase();
    }

    private static class LevelTypeOption extends BiomeGeneratorTypeScreens
    {

        private final LevelType levelType;

        protected LevelTypeOption(LevelType levelType)
        {
            super(levelType.getName());
            this.levelType = levelType;
        }

        public boolean isHidden()
        {
            // option is hidden if it is a debug level type and l-shift is not held down
            return levelType.isDebug() && !Screen.func_231173_s_();
        }

        @Nonnull
        @Override
        public DimensionGeneratorSettings func_241220_a_(@Nonnull DynamicRegistries.Impl registries, long seed, boolean structures, boolean bonusChest)
        {
            return levelType.createDimensionGeneratorSettings(seed, structures, bonusChest, registries, null);
        }

        @Nonnull
        @Override
        protected ChunkGenerator func_241869_a(@Nonnull Registry<Biome> biomes, @Nonnull Registry<DimensionSettings> settings, long seed)
        {
            return levelType.createChunkGenerator(seed, biomes, settings, null);
        }

        private static void forEachOption(Consumer<BiomeGeneratorTypeScreens> consumer)
        {
            BiomeGeneratorTypeScreens.field_239068_c_.forEach(consumer);
        }

        private static void forEachEditScreen(BiConsumer<BiomeGeneratorTypeScreens, BiomeGeneratorTypeScreens.IFactory> consumer)
        {
            BiomeGeneratorTypeScreens.field_239069_d_.forEach((typeOp, factory) -> typeOp.ifPresent(type -> consumer.accept(type, factory)));
        }
    }
}
