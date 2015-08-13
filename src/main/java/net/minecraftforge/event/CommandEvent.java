package net.minecraftforge.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

/**
 * CommandEvent is fired whenever a command is scheduled to be executed. 
 * This event is fired during the invocation of CommandHandler#executeCommand(ICommandSender, String)
 * and ClientCommandHandler#executeCommand(ICommandSender, String). <br>
 * <br>
 * {@link #command} contains the instance of ICommand which is representative of the currently executing command.<br>
 * {@link #sender} contains the instance of ICommandSender for the given command sender.<br>
 * {@link #parameters} contains the arguments passed for the command execution.<br>
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

    public final ICommand command;
    public final ICommandSender sender;
    public String[] parameters;
    public Throwable exception;

    public CommandEvent(ICommand command, ICommandSender sender, String[] parameters)
    {
        this.command = command;
        this.sender = sender;
        this.parameters = parameters;
    }
}
