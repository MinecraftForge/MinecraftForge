package net.minecraft.command;

public class CommandException extends RuntimeException
{
    private Object[] errorObjects;
    private static final String __OBFID = "CL_00001187";

    public CommandException(String par1Str, Object ... par2ArrayOfObj)
    {
        super(par1Str);
        this.errorObjects = par2ArrayOfObj;
    }

    public Object[] getErrorOjbects()
    {
        return this.errorObjects;
    }
}