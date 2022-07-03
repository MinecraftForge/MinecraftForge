package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.eventbus.api.Event;

import java.util.Optional;

public class RegisterPresetEditorEvent extends Event
{
    private final ImmutableMap.Builder<Optional<ResourceKey<WorldPreset>>, PresetEditor> presetEditorBuilder;

    public RegisterPresetEditorEvent(ImmutableMap.Builder<Optional<ResourceKey<WorldPreset>>, PresetEditor> presetEditorBuilder)
    {
        this.presetEditorBuilder = presetEditorBuilder;
    }

    public void addPresetEditor(ResourceKey<WorldPreset> presetKey, PresetEditor editor)
    {
        this.presetEditorBuilder.put(Optional.of(presetKey), editor);
    }
}
