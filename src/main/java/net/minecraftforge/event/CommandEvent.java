/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * CommandEvent is fired after a command is parsed, but before it is executed.
 * This event is fired during the invocation of {@link Commands#handleCommand(CommandSource, String)}. <br>
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
