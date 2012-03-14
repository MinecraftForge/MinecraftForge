package net.minecraft.src;

public abstract class WorldSavedData
{
    public final String mapName;

    /** Whether this MapDataBase needs saving to disk. */
    private boolean dirty;

    public WorldSavedData(String par1Str)
    {
        this.mapName = par1Str;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public abstract void readFromNBT(NBTTagCompound var1);

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public abstract void writeToNBT(NBTTagCompound var1);

    /**
     * Marks this MapDataBase dirty, to be saved to disk when the level next saves.
     */
    public void markDirty()
    {
        this.setDirty(true);
    }

    /**
     * Sets the dirty state of this MapDataBase, whether it needs saving to disk.
     */
    public void setDirty(boolean par1)
    {
        this.dirty = par1;
    }

    /**
     * Whether this MapDataBase needs saving to disk.
     */
    public boolean isDirty()
    {
        return this.dirty;
    }
}
