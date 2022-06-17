/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ForgeHooksClient;

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
        ForgeHooksClient.pushGuiLayer(self(), screen);
    }

    /**
     * Pops a GUI layer from the screen.
     */
    default void popGuiLayer()
    {
        ForgeHooksClient.popGuiLayer(self());
    }
}
