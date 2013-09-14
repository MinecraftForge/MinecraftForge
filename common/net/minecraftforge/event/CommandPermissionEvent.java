package net.minecraftforge.event;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.event.Event.HasResult;

/**
 * Determines if a command can be used by an {@link ICommandSender}.
 * @author HoBoS_TaCo
 */
@HasResult
public class CommandPermissionEvent extends Event
{
    public final ICommand command;
    public final ICommandSender sender;
    public final String[] parameters;
    
    /**
     * Determines if a command can be used by an {@link ICommandSender}.
     * @param command {@link ICommand} to be checked.
     * @param sender Command user.
     * @param parameters Command parameters following <i>ICommand.getCommandName()</i>
     * @return ALLOW to allow, DENY to deny, DEFAULT for <i>ICommand.canCommandSenderUseCommand(ICommandSender sender)</i> to be used.
     */
    public CommandPermissionEvent(ICommand command, ICommandSender sender, String[] parameters)
    {
        this.command = command;
        this.sender = sender;
        this.parameters = parameters;
    }
}
