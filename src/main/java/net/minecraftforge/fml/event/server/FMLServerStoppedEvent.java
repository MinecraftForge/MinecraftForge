/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;

/**
 * Called after {@link FMLServerStoppingEvent} when the server has completely shut down.
 * Called immediately before shutting down, on the dedicated server, and before returning
 * to the main menu on the client.
 *
 * @author cpw
 */
public class FMLServerStoppedEvent extends ServerLifecycleEvent {
    public FMLServerStoppedEvent(MinecraftServer server)
    {
        super(server);
    }
}
