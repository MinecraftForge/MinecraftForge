package net.minecraft.command;

public class SyntaxErrorException extends CommandException
{
    private static final String __OBFID = "CL_00001189";

    public SyntaxErrorException()
    {
        this("commands.generic.snytax", new Object[0]);
    }

    public SyntaxErrorException(String par1Str, Object ... par2ArrayOfObj)
    {
        super(par1Str, par2ArrayOfObj);
    }
}