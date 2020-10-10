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
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.level.LevelType;
import net.minecraftforge.event.world.LevelTypeEvent;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.registries.ForgeRegistries;
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
 * used in the world creation gui to represent user-selectable {@link LevelType}s.
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
            ResourceLocation name = getOptionName(option);
            if (name == null)
            {
                return;
            }

            LevelType type = ForgeRegistries.LEVEL_TYPES.getValue(name);
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

    public int size()
    {
        return options.size();
    }

    public int indexOf(BiomeGeneratorTypeScreens generatorType)
    {
        return options.indexOf(generatorType);
    }

    public BiomeGeneratorTypeScreens get(int index)
    {
        return options.get(index);
    }

    // called when opening the CreateWorldScreen to set the initial generator type
    public BiomeGeneratorTypeScreens getDefault()
    {
        LevelTypeEvent.DefaultLevelType event = new LevelTypeEvent.DefaultLevelType(LevelType.DEFAULT);
        MinecraftForge.EVENT_BUS.post(event);
        LevelType type = event.getLevelType();
        BiomeGeneratorTypeScreens option = levelTypes.get(type);
        if (option == null) {
            updateOptions();
            option = levelTypes.get(type);
        }
        return option != null ? option : BiomeGeneratorTypeScreens.field_239066_a_;
    }

    // determines whether a generator option has a a 'Customize' button to open its EditScreen
    public boolean hasEditScreen(BiomeGeneratorTypeScreens generatorType)
    {
        return editScreens.containsKey(generatorType);
    }

    // returns an 'EditScreenFactory' which creates new instances of the generator types configuration screen
    @Nullable
    public BiomeGeneratorTypeScreens.IFactory getEditScreen(BiomeGeneratorTypeScreens generatorType)
    {
        return editScreens.get(generatorType);
    }

    // synchronize the available GeneratorTypes with the options list so that they appear in the ui
    public void updateOptions()
    {
        ForgeRegistries.LEVEL_TYPES.forEach(levelType -> {
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
                LOGGER.debug("Registered LevelType '{}'", levelType.getRegistryName());
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

    @Nullable
    private static ResourceLocation getOptionName(BiomeGeneratorTypeScreens option)
    {
        ITextComponent textComponent = option.func_239077_a_();
        if (textComponent instanceof TranslationTextComponent)
        {
            // expected to be generator.<name>
            String key = ((TranslationTextComponent) textComponent).getKey();

            int i = key.indexOf('.') + 1;
            if (i > 0 && i < key.length())
            {
                String name = StringUtils.toLowerCase(key.substring(i));
                return ResourceLocation.tryCreate(name);
            }
        }
        return null;
    }

    private static class LevelTypeOption extends BiomeGeneratorTypeScreens
    {

        private final LevelType levelType;

        protected LevelTypeOption(LevelType levelType)
        {
            super(toTranslationKey(levelType.getRegistryName()));
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
            return levelType.createLevel(seed, structures, bonusChest, registries, null);
        }

        @Nonnull
        @Override
        protected ChunkGenerator func_241869_a(@Nonnull Registry<Biome> biomes, @Nonnull Registry<DimensionSettings> settings, long seed)
        {
            return levelType.createOverworldChunkGenerator(seed, biomes, settings, null);
        }

        private static String toTranslationKey(ResourceLocation name)
        {
            // Note: "generator." is prepended in the parent class
            return name.getNamespace() + '.' + name.getPath();
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
