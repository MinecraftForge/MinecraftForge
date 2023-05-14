/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * <p>Event for registering {@link PresetEditor} screen factories for world presets.</p>
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class RegisterPresetEditorsEvent extends Event implements IModBusEvent
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceKey<WorldPreset>, PresetEditor> editors;

    @ApiStatus.Internal
    public RegisterPresetEditorsEvent(Map<ResourceKey<WorldPreset>, PresetEditor> editors)
    {
        this.editors = editors;
    }

    /**
     * Registers a PresetEditor for a given world preset key.
     */
    public void register(ResourceKey<WorldPreset> key, PresetEditor editor)
    {
        PresetEditor old = this.editors.put(key, editor);
        if (old != null)
        {
            LOGGER.debug("PresetEditor {} overridden by mod {}", key.location(), ModLoadingContext.get().getActiveNamespace());
        }
    }
}
