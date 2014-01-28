package net.minecraft.command;

public class WrongUsageException extends SyntaxErrorException
{
    private static final String __OBFID = "CL_00001192";

    public WrongUsageException(String par1Str, Object ... par2ArrayOfObj)
    {
        super(par1Str, par2ArrayOfObj);
    }
}