package net.minecraft.src;

public class NetClientHandlerEntity
{
    public Class entityClass = null;
    public boolean entityHasOwner = false;

    public NetClientHandlerEntity(Class var1, boolean var2)
    {
        this.entityClass = var1;
        this.entityHasOwner = var2;
    }
}
