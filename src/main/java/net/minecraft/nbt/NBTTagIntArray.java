package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase
{
    // JAVADOC FIELD $$ field_74749_a
    private int[] intArray;
    private static final String __OBFID = "CL_00001221";

    NBTTagIntArray() {}

    public NBTTagIntArray(int[] p_i45132_1_)
    {
        this.intArray = p_i45132_1_;
    }

    // JAVADOC METHOD $$ func_74734_a
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.intArray.length);

        for (int i = 0; i < this.intArray.length; ++i)
        {
            par1DataOutput.writeInt(this.intArray[i]);
        }
    }

    // JAVADOC METHOD $$ func_74735_a
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        int j = par1DataInput.readInt();
        this.intArray = new int[j];

        for (int k = 0; k < j; ++k)
        {
            this.intArray[k] = par1DataInput.readInt();
        }
    }

    // JAVADOC METHOD $$ func_74732_a
    public byte getId()
    {
        return (byte)11;
    }

    public String toString()
    {
        String s = "[";
        int[] aint = this.intArray;
        int i = aint.length;

        for (int j = 0; j < i; ++j)
        {
            int k = aint[j];
            s = s + k + ",";
        }

        return s + "]";
    }

    // JAVADOC METHOD $$ func_74737_b
    public NBTBase copy()
    {
        int[] aint = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
        return new NBTTagIntArray(aint);
    }

    public boolean equals(Object par1Obj)
    {
        return super.equals(par1Obj) ? Arrays.equals(this.intArray, ((NBTTagIntArray)par1Obj).intArray) : false;
    }

    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(this.intArray);
    }

    public int[] func_150302_c()
    {
        return this.intArray;
    }
}