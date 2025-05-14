/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.eventbus.api.Event;

public final class ModifyOverlayLayersEvent extends Event {
    private final ForgeLayeredDraw layeredDraw;

    public ModifyOverlayLayersEvent(ForgeLayeredDraw layeredDraw) {
        this.layeredDraw = layeredDraw;
    }

    public ForgeLayeredDraw getLayeredDraw() {
        return layeredDraw;
    }
}
