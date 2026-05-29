/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired to allow mods to register client commands.
 *
 * <p>Some command arguments behave differently for the client commands dispatcher:</p>
 * <ul>
 * <li>{@link IdentifierArgument#getId(CommandContext, String)} only returns advancements that are shown on
 * the advancements screen.
 * <li>{@link ObjectiveArgument#getObjective(com.mojang.brigadier.context.CommandContext, String)} only returns
 * objectives that are displayed to the player.
 * </ul>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @param getDispatcher the command dispatcher for registering commands to be executed on the client
 * @param getBuildContext the context to build the commands for
 *
 * @see net.minecraftsingularity.event.RegisterCommandsEvent
 */
@NullMarked
public record RegisterClientCommandsEvent(
        CommandDispatcher<CommandSourceStack> getDispatcher,
        CommandBuildContext getBuildContext
) implements RecordEvent {
    public static final EventBus<RegisterClientCommandsEvent> BUS = EventBus.create(RegisterClientCommandsEvent.class);

    @ApiStatus.Internal
    public RegisterClientCommandsEvent {}
}
