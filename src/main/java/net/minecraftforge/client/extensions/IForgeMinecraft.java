/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.ForgeHooksClient;

public interface IForgeMinecraft
{
    default Minecraft getSelf() { return (Minecraft)this; }

    default void pushGuiLayer(Screen screen)
    {
        ForgeHooksClient.pushGuiLayer(getSelf(), screen);
    }

    default void popGuiLayer()
    {
        ForgeHooksClient.popGuiLayer(getSelf());
    }
}
