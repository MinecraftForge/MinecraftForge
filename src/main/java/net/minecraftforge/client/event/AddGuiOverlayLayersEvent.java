/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when the {@linkplain ForgeLayeredDraw#VANILLA_ROOT}'s order is resolved during{@link ForgeLayeredDraw#resolveLayers().
 * This can be used to add additional or move gui layers and entire layer stacks as needed.
 *
 * <p> This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable} and does not {@linkplain net.minecraftforge.eventbus.api.Event.HasResult have a result}</p>
 *
 * <p> This event is fired on the {@linkplain IModBusEvent mod event bus},
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide logical client}.</p>
 */
public final class AddGuiOverlayLayersEvent extends Event implements IModBusEvent {
    @NotNull
    private final ForgeLayeredDraw layeredDraw;

    @ApiStatus.Internal
    public AddGuiOverlayLayersEvent(ForgeLayeredDraw layeredDraw) {
        this.layeredDraw = layeredDraw;
    }

    /**
     * Get the layered draw.
     * @return the draw layer stack
     */
    @NotNull
    public ForgeLayeredDraw getLayeredDraw() {
        return layeredDraw;
    }
}
