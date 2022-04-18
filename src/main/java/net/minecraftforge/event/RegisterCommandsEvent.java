/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import com.mojang.brigadier.CommandDispatcher;
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
    
    public RegisterCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment)
    {
        this.dispatcher = dispatcher;
        this.environment = environment;
    }
    
    public CommandDispatcher<CommandSourceStack> getDispatcher()
    {
        return dispatcher;
    }
    
    public Commands.CommandSelection getEnvironment()
    {
        return environment;
    }
}
