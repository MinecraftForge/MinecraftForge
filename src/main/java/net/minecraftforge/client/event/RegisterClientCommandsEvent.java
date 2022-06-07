/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired to allow mods to register client commands.
 *
 * <p>Some command arguments behave differently for the client commands dispatcher:</p>
 * <ul>
 * <li>{@link ResourceLocationArgument#getAdvancement(com.mojang.brigadier.context.CommandContext, String)} only returns
 * advancements that are shown on the advancements screen.
 * <li>{@link ObjectiveArgument#getObjective(com.mojang.brigadier.context.CommandContext, String)} only returns
 * objectives that are displayed to the player.
 * </ul>
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 *
 * @see net.minecraftforge.event.RegisterCommandsEvent
 */
public class RegisterClientCommandsEvent extends Event
{

    private final CommandDispatcher<CommandSourceStack> dispatcher;

    /**
     * @hidden
     * @see net.minecraftforge.client.ClientCommandHandler#mergeServerCommands(CommandDispatcher)
     */
    public RegisterClientCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    /**
     * {@return the command dispatcher for registering commands to be executed on the client}
     */
    public CommandDispatcher<CommandSourceStack> getDispatcher()
    {
        return dispatcher;
    }
}
