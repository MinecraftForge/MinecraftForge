/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;

/**
 * Called after {@link FMLServerAboutToStartEvent} and before {@link FMLServerStartedEvent}.
 * This event allows for customizations of the server.
 *
 * If you need to add commands use {@link net.minecraftforge.event.RegisterCommandsEvent}.
 *
 * @author cpw
 */
public class FMLServerStartingEvent extends ServerLifecycleEvent
{
    public FMLServerStartingEvent(final MinecraftServer server)
    {
        super(server);
    }

}
