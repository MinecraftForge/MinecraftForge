/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.server.ReloadableServerResources;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;


import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Commands are rebuilt whenever {@link ReloadableServerResources} is recreated.
 * You can use this event to register your commands whenever the {@link Commands} class in constructed.
 *
 * The event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class RegisterCommandsEvent extends Event
{
    private final CommandDispatcher<CommandSourceStack> dispatcher;
    private final Commands.CommandSelection environment;
    private final CommandBuildContext context;
    
    public RegisterCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment, CommandBuildContext context)
    {
        this.dispatcher = dispatcher;
        this.environment = environment;
        this.context = context;
    }

    /**
     * {@return the command dispatcher for registering commands to be executed on the client}
     */
    public CommandDispatcher<CommandSourceStack> getDispatcher()
    {
        return dispatcher;
    }

    /**
     * {@return the environment the command is being registered for}
     */
    public Commands.CommandSelection getCommandSelection()
    {
        return environment;
    }

    /**
     * {@return the context to build the commands for}
     */
    public CommandBuildContext getBuildContext()
    {
        return context;
    }
}
