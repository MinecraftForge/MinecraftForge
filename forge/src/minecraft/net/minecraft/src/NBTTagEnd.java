package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase
{
    public NBTTagEnd()
    {
        super((String)null);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException {}

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException {}

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)0;
    }

    public String toString()
    {
        return "END";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagEnd();
    }

    public boolean equals(Object par1Obj)
    {
        return super.equals(par1Obj);
    }
}
