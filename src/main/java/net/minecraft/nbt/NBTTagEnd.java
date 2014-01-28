package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase
{
    private static final String __OBFID = "CL_00001219";

    // JAVADOC METHOD $$ func_74735_a
    void load(DataInput par1DataInput, int par2) throws IOException {}

    // JAVADOC METHOD $$ func_74734_a
    void write(DataOutput par1DataOutput) throws IOException {}

    // JAVADOC METHOD $$ func_74732_a
    public byte getId()
    {
        return (byte)0;
    }

    public String toString()
    {
        return "END";
    }

    // JAVADOC METHOD $$ func_74737_b
    public NBTBase copy()
    {
        return new NBTTagEnd();
    }
}