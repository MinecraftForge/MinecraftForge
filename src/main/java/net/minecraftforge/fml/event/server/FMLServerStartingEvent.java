/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.server;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;

/**
 * Called after {@link FMLServerAboutToStartEvent} and before {@link FMLServerStartedEvent}.
 * This event allows for customizations of the server, such as loading custom commands, perhaps customizing recipes or
 * other activities.
 *
 * @author cpw
 */
public class FMLServerStartingEvent extends ServerLifecycleEvent
{
    public FMLServerStartingEvent(final MinecraftServer server)
    {
        super(server);
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return server.getCommandManager().getDispatcher();
    }
}
