package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase
{
    public static final String[] NBTTypes = new String[] {"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"};
    private static final String __OBFID = "CL_00001229";

    // JAVADOC METHOD $$ func_74734_a
    abstract void write(DataOutput var1) throws IOException;

    // JAVADOC METHOD $$ func_74735_a
    abstract void load(DataInput var1, int var2) throws IOException;

    public abstract String toString();

    // JAVADOC METHOD $$ func_74732_a
    public abstract byte getId();

    protected static NBTBase func_150284_a(byte p_150284_0_)
    {
        switch (p_150284_0_)
        {
            case 0:
                return new NBTTagEnd();
            case 1:
                return new NBTTagByte();
            case 2:
                return new NBTTagShort();
            case 3:
                return new NBTTagInt();
            case 4:
                return new NBTTagLong();
            case 5:
                return new NBTTagFloat();
            case 6:
                return new NBTTagDouble();
            case 7:
                return new NBTTagByteArray();
            case 8:
                return new NBTTagString();
            case 9:
                return new NBTTagList();
            case 10:
                return new NBTTagCompound();
            case 11:
                return new NBTTagIntArray();
            default:
                return null;
        }
    }

    public static String func_150283_g(int p_150283_0_)
    {
        switch (p_150283_0_)
        {
            case 0:
                return "TAG_End";
            case 1:
                return "TAG_Byte";
            case 2:
                return "TAG_Short";
            case 3:
                return "TAG_Int";
            case 4:
                return "TAG_Long";
            case 5:
                return "TAG_Float";
            case 6:
                return "TAG_Double";
            case 7:
                return "TAG_Byte_Array";
            case 8:
                return "TAG_String";
            case 9:
                return "TAG_List";
            case 10:
                return "TAG_Compound";
            case 11:
                return "TAG_Int_Array";
            case 99:
                return "Any Numeric Tag";
            default:
                return "UNKNOWN";
        }
    }

    // JAVADOC METHOD $$ func_74737_b
    public abstract NBTBase copy();

    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof NBTBase))
        {
            return false;
        }
        else
        {
            NBTBase nbtbase = (NBTBase)par1Obj;
            return this.getId() == nbtbase.getId();
        }
    }

    public int hashCode()
    {
        return this.getId();
    }

    protected String func_150285_a_()
    {
        return this.toString();
    }

    public abstract static class NBTPrimitive extends NBTBase
        {
            private static final String __OBFID = "CL_00001230";

            public abstract long func_150291_c();

            public abstract int func_150287_d();

            public abstract short func_150289_e();

            public abstract byte func_150290_f();

            public abstract double func_150286_g();

            public abstract float func_150288_h();
        }
}