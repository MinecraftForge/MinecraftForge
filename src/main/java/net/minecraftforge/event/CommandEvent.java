/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import net.minecraftsingularity.fml.LogicalSide;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * CommandEvent is fired after a command is parsed, but before it is executed.
 * This event is fired during the invocation of {@link Commands#performCommand(ParseResults, String)}.
 * <p>
 * This event is {@linkplain Cancellable cancellable}. If the event is cancelled, the command will not be executed.
 * <p>
 * This event is fired only on the {@linkplain LogicalSide#SERVER logical server}.
 **/
@NullMarked
public final class CommandEvent extends MutableEvent implements Cancellable {
    public static final CancellableEventBus<CommandEvent> BUS = CancellableEventBus.create(CommandEvent.class);

    private ParseResults<CommandSourceStack> parse;
    private @Nullable Throwable exception;

    public CommandEvent(ParseResults<CommandSourceStack> parse) {
        this.parse = parse;
    }

    /**
     * {@return the parsed command results}
     */
    public ParseResults<CommandSourceStack> getParseResults() {
        return this.parse;
    }

    public void setParseResults(ParseResults<CommandSourceStack> parse) {
        this.parse = parse;
    }

    /**
     * {@return an exception to be thrown when performing the command, starts null}
     */
    @Nullable
    public Throwable getException() {
        return this.exception;
    }

    public void setException(@Nullable Throwable exception) {
        this.exception = exception;
    }
}
