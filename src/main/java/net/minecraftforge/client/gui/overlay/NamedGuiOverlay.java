/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/** singularity 1.20.5 - Removed, Mojang created a layered rendering system that should make this all obsolete finally.. - Lex 042724
package net.minecraftsingularity.client.gui.overlay;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * An object representation of an {@link IGuiOverlay overlay} with a name.
 * <p>
 * Useful to identify overlays in {@link net.minecraftsingularity.client.event.RenderGuiOverlayEvent}.
 * <p>
 * Users should not be instantiating this themselves. Retrieve from {@link GuiOverlayManager}.
 * /
public record NamedGuiOverlay(Identifier id, IGuiOverlay overlay)
{
    @ApiStatus.Internal
    public NamedGuiOverlay
    {
    }
}
*/
