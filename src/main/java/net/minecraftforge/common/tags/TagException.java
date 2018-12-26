package net.minecraftforge.common.tags;

import net.minecraftforge.fml.common.EnhancedRuntimeException;

public abstract class TagException extends EnhancedRuntimeException
{
    public TagException()
    {
    }

    public TagException(String message)
    {
        super(message);
    }

    public TagException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TagException(Throwable cause)
    {
        super(cause);
    }
}
