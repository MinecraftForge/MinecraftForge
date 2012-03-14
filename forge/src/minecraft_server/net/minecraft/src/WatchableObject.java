package net.minecraft.src;

public class WatchableObject
{
    private final int objectType;

    /** id of max 31 */
    private final int dataValueId;
    private Object watchedObject;
    private boolean isWatching;

    public WatchableObject(int par1, int par2, Object par3Obj)
    {
        this.dataValueId = par2;
        this.watchedObject = par3Obj;
        this.objectType = par1;
        this.isWatching = true;
    }

    public int getDataValueId()
    {
        return this.dataValueId;
    }

    public void setObject(Object par1Obj)
    {
        this.watchedObject = par1Obj;
    }

    public Object getObject()
    {
        return this.watchedObject;
    }

    public int getObjectType()
    {
        return this.objectType;
    }

    public boolean getWatching()
    {
        return this.isWatching;
    }

    public void setWatching(boolean par1)
    {
        this.isWatching = par1;
    }
}
