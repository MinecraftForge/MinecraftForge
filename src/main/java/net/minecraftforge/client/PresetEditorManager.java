/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import net.minecraftforge.fml.ModLoader;

public final class PresetEditorManager
{
    private PresetEditorManager() {} // Utility class

    private static Map<ResourceKey<WorldPreset>, PresetEditor> editors = Map.of();

    @SuppressWarnings("deprecation")
    @ApiStatus.Internal
    static void init()
    {
        // Start with the vanilla entries
        Map<ResourceKey<WorldPreset>, PresetEditor> gatheredEditors = new HashMap<>();
        // Vanilla's map uses Optional<ResourceKey>s as its keys.
        // As far as we can tell there's no good reason for this, so we'll just use regular keys.
        PresetEditor.EDITORS.forEach((k, v) -> k.ifPresent(key -> gatheredEditors.put(key, v)));

        // Gather mods' entries
        RegisterPresetEditorsEvent event = new RegisterPresetEditorsEvent(gatheredEditors);
        ModLoader.get().postEventWrapContainerInModOrder(event);

        editors = gatheredEditors;
    }

    /**
     * {@return the PresetEditor for the given WorldPreset key, or null if no such PresetEditor exists}
     *
     * @param key ResourceKey for the specified WorldPreset/PresetEditor.
     */
    @Nullable
    public static PresetEditor get(ResourceKey<WorldPreset> key)
    {
        return editors.get(key);
    }
}
