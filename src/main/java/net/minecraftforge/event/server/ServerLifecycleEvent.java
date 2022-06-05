/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;

public class ServerLifecycleEvent extends Event
{

    protected final MinecraftServer server;

    public ServerLifecycleEvent(MinecraftServer server)
    {
        this.server = server;
    }

    public MinecraftServer getServer()
    {
        return server;
    }
}
