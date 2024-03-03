/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ForgeHooksClient;

public interface IForgeMinecraft
{
    private Minecraft self() { return (Minecraft)this; }

    default void pushGuiLayer(Screen screen)
    {
        ForgeHooksClient.pushGuiLayer(self(), screen);
    }

    default void popGuiLayer()
    {
        ForgeHooksClient.popGuiLayer(self());
    }
}
