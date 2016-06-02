package net.minecraftforge.event;

import net.minecraft.command.CommandHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

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
 * This event is {@link net.minecraftforge.fml.common.eventhandler.Cancelable}. <br>
 * If the event is canceled, the execution of the command does not occur.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
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
}
