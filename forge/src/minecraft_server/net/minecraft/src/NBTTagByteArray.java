package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByteArray extends NBTBase
{
    /** The byte array stored in the tag. */
    public byte[] byteArray;

    public NBTTagByteArray(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagByteArray(String par1Str, byte[] par2ArrayOfByte)
    {
        super(par1Str);
        this.byteArray = par2ArrayOfByte;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.byteArray.length);
        par1DataOutput.write(this.byteArray);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        int var2 = par1DataInput.readInt();
        this.byteArray = new byte[var2];
        par1DataInput.readFully(this.byteArray);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)7;
    }

    public String toString()
    {
        return "[" + this.byteArray.length + " bytes]";
    }

    public boolean equals(Object par1Obj)
    {
        if (!super.equals(par1Obj))
        {
            return false;
        }
        else
        {
            NBTTagByteArray var2 = (NBTTagByteArray)par1Obj;
            return this.byteArray == null && var2.byteArray == null || this.byteArray != null && this.byteArray.equals(var2.byteArray);
        }
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        byte[] var1 = new byte[this.byteArray.length];
        System.arraycopy(this.byteArray, 0, var1, 0, this.byteArray.length);
        return new NBTTagByteArray(this.getName(), var1);
    }
}
