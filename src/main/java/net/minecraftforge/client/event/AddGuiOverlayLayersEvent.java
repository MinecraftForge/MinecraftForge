/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when the {@linkplain ForgeLayeredDraw#VANILLA_ROOT}'s order is resolved during{@link ForgeLayeredDraw#resolveLayers().
 * This can be used to add additional or move gui layers and entire layer stacks as needed.
 *
 * <p> This event is fired on the {@linkplain IModBusEvent mod event bus},
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide logical client}.</p>
 *
 * @param getLayedDraw The provided {@linkplain ForgeLayeredDraw#instance}. By default will be {@linkplain ForgeLayeredDraw#VANILLA_ROOT}.
 */
@NullMarked
public record AddGuiOverlayLayersEvent(ForgeLayeredDraw getLayeredDraw) implements IModBusEvent, SelfDestructing, RecordEvent {
    @ApiStatus.Internal
    public AddGuiOverlayLayersEvent {}

    public static EventBus<AddGuiOverlayLayersEvent> getBus(BusGroup modBusGroup) {
        return IModBusEvent.getBus(modBusGroup, AddGuiOverlayLayersEvent.class);
    }
}
