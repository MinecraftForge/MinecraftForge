package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase.NBTPrimitive
{
    // JAVADOC FIELD $$ field_74752_a
    private short data;
    private static final String __OBFID = "CL_00001227";

    public NBTTagShort() {}

    public NBTTagShort(short p_i45135_1_)
    {
        this.data = p_i45135_1_;
    }

    // JAVADOC METHOD $$ func_74734_a
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeShort(this.data);
    }

    // JAVADOC METHOD $$ func_74735_a
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        this.data = par1DataInput.readShort();
    }

    // JAVADOC METHOD $$ func_74732_a
    public byte getId()
    {
        return (byte)2;
    }

    public String toString()
    {
        return "" + this.data + "s";
    }

    // JAVADOC METHOD $$ func_74737_b
    public NBTBase copy()
    {
        return new NBTTagShort(this.data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagShort nbttagshort = (NBTTagShort)par1Obj;
            return this.data == nbttagshort.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.data;
    }

    public long func_150291_c()
    {
        return (long)this.data;
    }

    public int func_150287_d()
    {
        return this.data;
    }

    public short func_150289_e()
    {
        return this.data;
    }

    public byte func_150290_f()
    {
        return (byte)(this.data & 255);
    }

    public double func_150286_g()
    {
        return (double)this.data;
    }

    public float func_150288_h()
    {
        return (float)this.data;
    }
}