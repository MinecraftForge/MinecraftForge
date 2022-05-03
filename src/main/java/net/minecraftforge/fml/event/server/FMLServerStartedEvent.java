/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;

/**
 * Called after {@link FMLServerStartingEvent} when the server is available and ready to play.
 *
 * @author cpw
 */
public class FMLServerStartedEvent extends ServerLifecycleEvent
{

    public FMLServerStartedEvent(final MinecraftServer server)
    {
        super(server);
    }
}
