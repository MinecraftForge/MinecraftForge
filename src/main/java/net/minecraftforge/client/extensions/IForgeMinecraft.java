/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.Minecraft;
import java.util.Locale;

/**
 * Extension interface for {@link Minecraft}.
 */
public interface IForgeMinecraft {
    private Minecraft self() {
        return (Minecraft)this;
    }

    /**
     * Retrieves the {@link Locale} set by the player.
     * Useful for creating string and number formatters.
     */
    default Locale getLocale() {
        return self().getLanguageManager().getJavaLocale();
    }
}
