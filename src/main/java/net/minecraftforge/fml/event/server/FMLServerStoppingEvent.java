/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;

/**
 * Called when the server begins an orderly shutdown, before {@link FMLServerStoppedEvent}.
 *
 * @author cpw
 */
public class FMLServerStoppingEvent extends ServerLifecycleEvent
{
    public FMLServerStoppingEvent(MinecraftServer server)
    {
        super(server);
    }
}
