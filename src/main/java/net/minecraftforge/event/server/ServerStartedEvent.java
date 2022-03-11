/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;

/**
 * Called after {@link ServerStartingEvent} when the server is available and ready to play.
 *
 * @author cpw
 */
public class ServerStartedEvent extends ServerLifecycleEvent
{

    public ServerStartedEvent(final MinecraftServer server)
    {
        super(server);
    }
}
