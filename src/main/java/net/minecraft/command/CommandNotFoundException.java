package net.minecraft.command;

public class CommandNotFoundException extends CommandException
{
    private static final String __OBFID = "CL_00001191";

    public CommandNotFoundException()
    {
        this("commands.generic.notFound", new Object[0]);
    }

    public CommandNotFoundException(String par1Str, Object ... par2ArrayOfObj)
    {
        super(par1Str, par2ArrayOfObj);
    }
}