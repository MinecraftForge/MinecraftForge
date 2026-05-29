/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.server.ReloadableServerResources;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;

/**
 * Commands are rebuilt whenever {@link ReloadableServerResources} is recreated.
 * You can use this event to register your commands whenever the {@link Commands} class in constructed.
 *
 * @param getDispatcher The command dispatcher for registering commands to be executed on the client
 * @param getCommandSelection The environment the command is being registered for
 * @param getBuildContext The context to build the commands for
 */
public record RegisterCommandsEvent(
        CommandDispatcher<CommandSourceStack> getDispatcher,
        Commands.CommandSelection getCommandSelection,
        CommandBuildContext getBuildContext
) implements RecordEvent {
    public static final EventBus<RegisterCommandsEvent> BUS = EventBus.create(RegisterCommandsEvent.class);
}
