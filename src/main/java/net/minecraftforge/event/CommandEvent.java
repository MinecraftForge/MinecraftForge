/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * CommandEvent is fired after a command is parsed, but before it is executed.
 * This event is fired during the invocation of {@link Commands#performCommand(CommandSourceStack, String)}. <br>
 * <br>
 * {@link #parse} contains the instance of {@link ParseResults} for the parsed command.<br>
 * {@link #exception} begins null, but can be populated with an exception to be thrown within the command.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If the event is canceled, the execution of the command does not occur.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class CommandEvent extends Event
{
    private ParseResults<CommandSourceStack> parse;
    private Throwable exception;

    public CommandEvent(ParseResults<CommandSourceStack> parse)
    {
        this.parse = parse;
    }

    public ParseResults<CommandSourceStack> getParseResults() { return parse; }
    public void setParseResults(ParseResults<CommandSourceStack> parse) { this.parse = parse; }
    public Throwable getException() { return exception; }
    public void setException(Throwable exception) { this.exception = exception; }
}
