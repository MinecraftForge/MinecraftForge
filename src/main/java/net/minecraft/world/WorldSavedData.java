package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData
{
    // JAVADOC FIELD $$ field_76190_i
    public final String mapName;
    // JAVADOC FIELD $$ field_76189_a
    private boolean dirty;
    private static final String __OBFID = "CL_00000580";

    public WorldSavedData(String par1Str)
    {
        this.mapName = par1Str;
    }

    // JAVADOC METHOD $$ func_76184_a
    public abstract void readFromNBT(NBTTagCompound var1);

    // JAVADOC METHOD $$ func_76187_b
    public abstract void writeToNBT(NBTTagCompound var1);

    // JAVADOC METHOD $$ func_76185_a
    public void markDirty()
    {
        this.setDirty(true);
    }

    // JAVADOC METHOD $$ func_76186_a
    public void setDirty(boolean par1)
    {
        this.dirty = par1;
    }

    // JAVADOC METHOD $$ func_76188_b
    public boolean isDirty()
    {
        return this.dirty;
    }
}