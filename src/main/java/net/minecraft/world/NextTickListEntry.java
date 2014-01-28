package net.minecraft.world;

import net.minecraft.block.Block;

public class NextTickListEntry implements Comparable
{
    // JAVADOC FIELD $$ field_77177_f
    private static long nextTickEntryID;
    private final Block field_151352_g;
    // JAVADOC FIELD $$ field_77183_a
    public int xCoord;
    // JAVADOC FIELD $$ field_77181_b
    public int yCoord;
    // JAVADOC FIELD $$ field_77182_c
    public int zCoord;
    // JAVADOC FIELD $$ field_77180_e
    public long scheduledTime;
    public int priority;
    // JAVADOC FIELD $$ field_77178_g
    private long tickEntryID;
    private static final String __OBFID = "CL_00000156";

    public NextTickListEntry(int p_i45370_1_, int p_i45370_2_, int p_i45370_3_, Block p_i45370_4_)
    {
        this.tickEntryID = (long)(nextTickEntryID++);
        this.xCoord = p_i45370_1_;
        this.yCoord = p_i45370_2_;
        this.zCoord = p_i45370_3_;
        this.field_151352_g = p_i45370_4_;
    }

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof NextTickListEntry))
        {
            return false;
        }
        else
        {
            NextTickListEntry nextticklistentry = (NextTickListEntry)par1Obj;
            return this.xCoord == nextticklistentry.xCoord && this.yCoord == nextticklistentry.yCoord && this.zCoord == nextticklistentry.zCoord && Block.func_149680_a(this.field_151352_g, nextticklistentry.field_151352_g);
        }
    }

    public int hashCode()
    {
        return (this.xCoord * 1024 * 1024 + this.zCoord * 1024 + this.yCoord) * 256;
    }

    // JAVADOC METHOD $$ func_77176_a
    public NextTickListEntry setScheduledTime(long par1)
    {
        this.scheduledTime = par1;
        return this;
    }

    public void setPriority(int par1)
    {
        this.priority = par1;
    }

    public int compareTo(NextTickListEntry par1NextTickListEntry)
    {
        return this.scheduledTime < par1NextTickListEntry.scheduledTime ? -1 : (this.scheduledTime > par1NextTickListEntry.scheduledTime ? 1 : (this.priority != par1NextTickListEntry.priority ? this.priority - par1NextTickListEntry.priority : (this.tickEntryID < par1NextTickListEntry.tickEntryID ? -1 : (this.tickEntryID > par1NextTickListEntry.tickEntryID ? 1 : 0))));
    }

    public String toString()
    {
        return Block.func_149682_b(this.field_151352_g) + ": (" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + "), " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
    }

    public Block func_151351_a()
    {
        return this.field_151352_g;
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareTo((NextTickListEntry)par1Obj);
    }
}