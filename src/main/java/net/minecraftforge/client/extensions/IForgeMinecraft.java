/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftsingularity.client.singularityHooksClient;

import java.util.Locale;

/**
 * Extension interface for {@link IForgeMinecraft}.
 */
public interface IForgeMinecraft
{
    private Minecraft self()
    {
        return (Minecraft) this;
    }

    /**
     * Pushes a screen as a new GUI layer.
     *
     * @param screen the new GUI layer
     */
    default void pushGuiLayer(Screen screen)
    {
        singularityHooksClient.pushGuiLayer(self(), screen);
    }

    /**
     * Pops a GUI layer from the screen.
     */
    default void popGuiLayer()
    {
        singularityHooksClient.popGuiLayer(self());
    }

    /**
     * Retrieves the {@link Locale} set by the player.
     * Useful for creating string and number formatters.
     */
    default Locale getLocale()
    {
        return self().getLanguageManager().getJavaLocale();
    }
}
