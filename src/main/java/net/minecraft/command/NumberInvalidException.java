package net.minecraft.command;

public class NumberInvalidException extends CommandException
{
    private static final String __OBFID = "CL_00001188";

    public NumberInvalidException()
    {
        this("commands.generic.num.invalid", new Object[0]);
    }

    public NumberInvalidException(String par1Str, Object ... par2ArrayOfObj)
    {
        super(par1Str, par2ArrayOfObj);
    }
}