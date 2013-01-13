package net.minecraftforge.craft.condition;

public class ConditionException extends RuntimeException
{
    private static final long serialVersionUID = 1761896151351608878L;
    
    public ConditionException(Throwable cause)
    {
        super(cause);
    }
    
    public ConditionException(String cause)
    {
        super(cause);
    }
}
