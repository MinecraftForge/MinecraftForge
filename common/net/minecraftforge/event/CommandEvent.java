package net.minecraftforge.event;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

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
