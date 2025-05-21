/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a {@link ForgeLayeredDraw} is computed using {@link ForgeLayeredDraw#computeOrder() if editing is enabled.
 * This can be used to add to
 *
 * <p> This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable} and does not {@linkplain net.minecraftforge.eventbus.api.Event.HasResult have a result}</p>
 *
 * <p> This event is fired on the {@linkplain IModBusEvent mod event bus},
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide logical client}.</p>
 */
public final class ModifyOverlayLayersEvent extends Event implements IModBusEvent {
    @NotNull
    private final ForgeLayeredDraw layeredDraw;

    @ApiStatus.Internal
    public ModifyOverlayLayersEvent(ForgeLayeredDraw layeredDraw) {
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

    /**
     * Helper method to check which layered draw's compute this is.
     * @param target rl to test against.
     * @return true if this layered draw matches the provided rl
     */
    public boolean isPhase(ResourceLocation target) {
        return layeredDraw.getPhase().equals(target);
    }
}
