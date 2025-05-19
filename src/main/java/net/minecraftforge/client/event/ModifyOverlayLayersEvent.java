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

    public boolean isPhase(ResourceLocation target) {
        return layeredDraw.getPhase().equals(target);
    }
}
