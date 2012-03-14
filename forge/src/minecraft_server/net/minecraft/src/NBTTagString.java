package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase
{
    /** The string value for the tag (cannot be empty). */
    public String data;

    public NBTTagString(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagString(String par1Str, String par2Str)
    {
        super(par1Str);
        this.data = par2Str;

        if (par2Str == null)
        {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeUTF(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        this.data = par1DataInput.readUTF();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)8;
    }

    public String toString()
    {
        return "" + this.data;
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagString(this.getName(), this.data);
    }

    public boolean equals(Object par1Obj)
    {
        if (!super.equals(par1Obj))
        {
            return false;
        }
        else
        {
            NBTTagString var2 = (NBTTagString)par1Obj;
            return this.data == null && var2.data == null || this.data != null && this.data.equals(var2.data);
        }
    }
}
