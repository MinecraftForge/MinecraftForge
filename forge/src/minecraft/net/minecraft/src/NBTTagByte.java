package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase
{
    /** The byte value for the tag. */
    public byte data;

    public NBTTagByte(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagByte(String par1Str, byte par2)
    {
        super(par1Str);
        this.data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        this.data = par1DataInput.readByte();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)1;
    }

    public String toString()
    {
        return "" + this.data;
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagByte var2 = (NBTTagByte)par1Obj;
            return this.data == var2.data;
        }
        else
        {
            return false;
        }
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagByte(this.getName(), this.data);
    }
}
