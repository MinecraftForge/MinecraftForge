package net.minecraftforge.client;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;
import java.util.Optional;

public class WorldPresetScreens
{

    private static final Map<ResourceKey<WorldPreset>, PresetEditor> GENERATOR_SCREEN_FACTORIES = Maps.newHashMap();

    public static synchronized void registerPresetEditor(ResourceKey<WorldPreset> preset, PresetEditor presetEditor)
    {
        if (GENERATOR_SCREEN_FACTORIES.containsKey(preset))
            throw new IllegalStateException("Factory has already been registered for: " + preset);

        GENERATOR_SCREEN_FACTORIES.put(preset, presetEditor);
    }

    public static PresetEditor getPresetEditor(Optional<ResourceKey<WorldPreset>> presetResourceKey, PresetEditor fallback)
    {
        return presetResourceKey.map(GENERATOR_SCREEN_FACTORIES::get).orElse(fallback);
    }
}
