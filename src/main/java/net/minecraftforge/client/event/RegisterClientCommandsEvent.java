/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraftforge.eventbus.api.Event;

/**
 * Register commands to be executed on the client using this event.
 * 
 * Some arguments behave differently:
 * <ul>
 * <li>{@link ResourceLocationArgument#getAdvancement(com.mojang.brigadier.context.CommandContext, String)} only returns advancements that are shown on the advancements screen
 * <li>{@link ObjectiveArgument#getObjective(com.mojang.brigadier.context.CommandContext, String)} only returns objectives that are set to display
 * </ul>
 */
public class RegisterClientCommandsEvent extends Event
{

    private final CommandDispatcher<CommandSourceStack> dispatcher;

    public RegisterClientCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    /**
     * @return The command dispatcher for registering commands to be executed on the client
     */
    public CommandDispatcher<CommandSourceStack> getDispatcher()
    {
        return dispatcher;
    }
}
