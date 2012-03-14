package net.minecraft.src;

public class NextTickListEntry implements Comparable
{
    /** The id number for the next tick entry */
    private static long nextTickEntryID = 0L;

    /** X position this tick is occuring at */
    public int xCoord;

    /** Y position this tick is occuring at */
    public int yCoord;

    /** Z position this tick is occuring at */
    public int zCoord;

    /**
     * blockID of the scheduled tick (ensures when the tick occurs its still for this block)
     */
    public int blockID;

    /** Time this tick is scheduled to occur at */
    public long scheduledTime;

    /** The id of the tick entry */
    private long tickEntryID;

    public NextTickListEntry(int par1, int par2, int par3, int par4)
    {
        this.tickEntryID = (long)(nextTickEntryID++);
        this.xCoord = par1;
        this.yCoord = par2;
        this.zCoord = par3;
        this.blockID = par4;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof NextTickListEntry))
        {
            return false;
        }
        else
        {
            NextTickListEntry var2 = (NextTickListEntry)par1Obj;
            return this.xCoord == var2.xCoord && this.yCoord == var2.yCoord && this.zCoord == var2.zCoord && this.blockID == var2.blockID;
        }
    }

    public int hashCode()
    {
        return (this.xCoord * 1024 * 1024 + this.zCoord * 1024 + this.yCoord) * 256 + this.blockID;
    }

    /**
     * Sets the scheduled time for this tick entry
     */
    public NextTickListEntry setScheduledTime(long par1)
    {
        this.scheduledTime = par1;
        return this;
    }

    /**
     * Compares this tick entry to another tick entry for sorting purposes. Compared first based on the scheduled time
     * and second based on tickEntryID.
     */
    public int comparer(NextTickListEntry par1NextTickListEntry)
    {
        return this.scheduledTime < par1NextTickListEntry.scheduledTime ? -1 : (this.scheduledTime > par1NextTickListEntry.scheduledTime ? 1 : (this.tickEntryID < par1NextTickListEntry.tickEntryID ? -1 : (this.tickEntryID > par1NextTickListEntry.tickEntryID ? 1 : 0)));
    }

    public int compareTo(Object par1Obj)
    {
        return this.comparer((NextTickListEntry)par1Obj);
    }
}
