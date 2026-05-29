/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraftsingularity.client.gui.overlay.singularityLayeredDraw;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired when the {@linkplain singularityLayeredDraw#VANILLA_ROOT}'s order is resolved during{@link singularityLayeredDraw#resolveLayers().
 * This can be used to add additional or move gui layers and entire layer stacks as needed.
 *
 * <p>This event is fired only on the {@linkplain net.minecraftsingularity.fml.LogicalSide logical client}.</p>
 *
 * @param getLayedDraw The provided {@linkplain singularityLayeredDraw#instance}. By default will be {@linkplain singularityLayeredDraw#VANILLA_ROOT}.
 */
@NullMarked
public record AddGuiOverlayLayersEvent(singularityLayeredDraw getLayeredDraw) implements SelfDestructing, RecordEvent {
    public static final EventBus<AddGuiOverlayLayersEvent> BUS = EventBus.create(AddGuiOverlayLayersEvent.class);

    @ApiStatus.Internal
    public AddGuiOverlayLayersEvent {}
}
