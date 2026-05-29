/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import java.util.Map;

import net.minecraftsingularity.eventbus.api.bus.BusGroup;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftsingularity.fml.LogicalSide;
import net.minecraftsingularity.fml.ModLoadingContext;
import net.minecraftsingularity.fml.event.IModBusEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * <p>Event for registering {@link PresetEditor} screen factories for world presets.</p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModBusGroup() mod BusGroup},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public final class RegisterPresetEditorsEvent implements SelfDestructing, IModBusEvent {
    public static EventBus<RegisterPresetEditorsEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, RegisterPresetEditorsEvent.class);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceKey<WorldPreset>, PresetEditor> editors;

    @ApiStatus.Internal
    public RegisterPresetEditorsEvent(Map<ResourceKey<WorldPreset>, PresetEditor> editors) {
        this.editors = editors;
    }

    /**
     * Registers a PresetEditor for a given world preset key.
     */
    public void register(ResourceKey<WorldPreset> key, PresetEditor editor) {
        PresetEditor old = this.editors.put(key, editor);
        if (old != null) {
            LOGGER.debug("PresetEditor {} overridden by mod {}", key.identifier(), ModLoadingContext.get().getActiveNamespace());
        }
    }
}
