/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/** singularity 1.20.5 - Removed, Mojang created a layered rendering system that should make this all obsolete finally.. - Lex 042724
package net.minecraftsingularity.client.gui.overlay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftsingularity.client.event.RegisterGuiOverlaysEvent;

/**
 * A HUD overlay.
 *
 * @see RegisterGuiOverlaysEvent
 * /
@FunctionalInterface
public interface IGuiOverlay {
    void render(singularityGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight);
}
*/
