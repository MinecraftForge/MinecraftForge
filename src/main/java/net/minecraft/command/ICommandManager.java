package net.minecraft.command;

import java.util.List;
import java.util.Map;

public interface ICommandManager
{
    int executeCommand(ICommandSender var1, String var2);

    // JAVADOC METHOD $$ func_71558_b
    List getPossibleCommands(ICommandSender var1, String var2);

    // JAVADOC METHOD $$ func_71557_a
    List getPossibleCommands(ICommandSender var1);

    // JAVADOC METHOD $$ func_71555_a
    Map getCommands();
}