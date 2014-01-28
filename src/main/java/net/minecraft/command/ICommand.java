package net.minecraft.command;

import java.util.List;

public interface ICommand extends Comparable
{
    String getCommandName();

    String getCommandUsage(ICommandSender var1);

    List getCommandAliases();

    void processCommand(ICommandSender var1, String[] var2);

    // JAVADOC METHOD $$ func_71519_b
    boolean canCommandSenderUseCommand(ICommandSender var1);

    // JAVADOC METHOD $$ func_71516_a
    List addTabCompletionOptions(ICommandSender var1, String[] var2);

    // JAVADOC METHOD $$ func_82358_a
    boolean isUsernameIndex(String[] var1, int var2);
}