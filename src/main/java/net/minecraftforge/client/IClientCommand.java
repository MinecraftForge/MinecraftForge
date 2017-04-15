package net.minecraftforge.client;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

/**
 * Client-side commands can implement this interface to allow additional control over when the command may be used.
 */
public interface IClientCommand extends ICommand
{

    /**
     * Determine whether this command can be used without the "/" prefix. By default this is true.
     * @param sender the command sender
     * @param message the message, without potential prefix
     * @return true to allow the usage of this command without the prefix
     */
    boolean allowUsageWithoutPrefix(ICommandSender sender, String message);

}
