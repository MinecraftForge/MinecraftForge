package net.minecraft.src;

public abstract class WorldSavedData
{
    /** The name of the map data nbt */
    public final String mapName;
    private boolean dirty;

    public WorldSavedData(String par1Str)
    {
        this.mapName = par1Str;
    }

    public abstract void readFromNBT(NBTTagCompound var1);

    public abstract void writeToNBT(NBTTagCompound var1);

    public void markDirty()
    {
        this.setDirty(true);
    }

    public void setDirty(boolean par1)
    {
        this.dirty = par1;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }
}
