package net.minecraftforge.permissions.implementation;

import net.minecraftforge.permissions.api.IServerOperator;

public class ServerOperatorBase implements IServerOperator
{
    private boolean op;
    
    public ServerOperatorBase()
    {
        this.op = false;
    }
    
    public ServerOperatorBase(boolean isOp)
    {
        this.op = isOp;
    }
    
    @Override
    public boolean isOp()
    {
        return op;
    }

    @Override
    public void setOp(boolean value)
    {
        op = value;
    }

}
