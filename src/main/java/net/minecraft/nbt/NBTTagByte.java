package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase.NBTPrimitive
{
    // JAVADOC FIELD $$ field_74756_a
    private byte data;
    private static final String __OBFID = "CL_00001214";

    NBTTagByte() {}

    public NBTTagByte(byte p_i45129_1_)
    {
        this.data = p_i45129_1_;
    }

    // JAVADOC METHOD $$ func_74734_a
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeByte(this.data);
    }

    // JAVADOC METHOD $$ func_74735_a
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        this.data = par1DataInput.readByte();
    }

    // JAVADOC METHOD $$ func_74732_a
    public byte getId()
    {
        return (byte)1;
    }

    public String toString()
    {
        return "" + this.data + "b";
    }

    // JAVADOC METHOD $$ func_74737_b
    public NBTBase copy()
    {
        return new NBTTagByte(this.data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagByte nbttagbyte = (NBTTagByte)par1Obj;
            return this.data == nbttagbyte.data;
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
        return (short)this.data;
    }

    public byte func_150290_f()
    {
        return this.data;
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