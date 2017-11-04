/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * CommandEvent is fired whenever a command is scheduled to be executed. 
 * This event is fired during the invocation of {@link CommandHandler#executeCommand(ICommandSender, String)}
 * and {@link ClientCommandHandler#executeCommand(ICommandSender, String)}. <br>
 * <br>
 * {@link #command} contains the instance of ICommand which is representative of the currently executing command.<br>
 * {@link #sender} contains the instance of ICommandSender for the given command sender.<br>
 * {@link #parameters} contains the arguments passed for the command execution.<br>
 * {@link #exception} begins null, but can be populated with an exception to be thrown within the command.<br>
 * <br>
 * This event is not {@link Cancelable}. <br>
 * <br>
 * This event has a result. {@link HasResult}<br>
 * If the result is {@link Result#ALLOW}, the command is executed.<br>
 * If the result is {@link Result#DEFAULT}, the command is executed if {@link ICommand#checkPermission(MinecraftServer, ICommandSender)} returns true.<br>
 * If the result is {@link Result#DENY}, the command isn't executed.
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@HasResult
public class CommandEvent extends Event
{

    private final ICommand command;
    private final ICommandSender sender;
    private String[] parameters;
    private Throwable exception;

    public CommandEvent(ICommand command, ICommandSender sender, String[] parameters)
    {
        this.command = command;
        this.sender = sender;
        this.setParameters(parameters);
    }

    public ICommand getCommand() { return command; }
    public ICommandSender getSender() { return sender; }
    public String[] getParameters() { return parameters; }
    public void setParameters(String[] parameters) { this.parameters = parameters; }
    public Throwable getException() { return exception; }
    public void setException(Throwable exception) { this.exception = exception; }

    /**
     * {@link CommandEvent} now {@link HasResult}, use {@link Event#setResult(Result)}.
     */
    @Deprecated
    @Override
    public void setCanceled(boolean cancel)
    {
        if(cancel)
        {
            this.setResult(Result.DENY);
        }
        else 
        {
            this.setResult(Result.DEFAULT);
        }
    }
}
