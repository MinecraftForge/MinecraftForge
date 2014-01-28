package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound extends NBTBase
{
    private static final Logger field_150301_b = LogManager.getLogger();
    // JAVADOC FIELD $$ field_74784_a
    private Map tagMap = new HashMap();
    private static final String __OBFID = "CL_00001215";

    // JAVADOC METHOD $$ func_74734_a
    void write(DataOutput par1DataOutput) throws IOException
    {
        Iterator iterator = this.tagMap.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            NBTBase nbtbase = (NBTBase)this.tagMap.get(s);
            func_150298_a(s, nbtbase, par1DataOutput);
        }

        par1DataOutput.writeByte(0);
    }

    // JAVADOC METHOD $$ func_74735_a
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        if (par2 > 512)
        {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        else
        {
            this.tagMap.clear();
            byte b0;

            while ((b0 = func_150300_a(par1DataInput)) != 0)
            {
                String s = func_150294_b(par1DataInput);
                NBTBase nbtbase = func_150293_a(b0, s, par1DataInput, par2 + 1);
                this.tagMap.put(s, nbtbase);
            }
        }
    }

    public Set func_150296_c()
    {
        return this.tagMap.keySet();
    }

    // JAVADOC METHOD $$ func_74732_a
    public byte getId()
    {
        return (byte)10;
    }

    // JAVADOC METHOD $$ func_74782_a
    public void setTag(String par1Str, NBTBase par2NBTBase)
    {
        this.tagMap.put(par1Str, par2NBTBase);
    }

    // JAVADOC METHOD $$ func_74774_a
    public void setByte(String par1Str, byte par2)
    {
        this.tagMap.put(par1Str, new NBTTagByte(par2));
    }

    // JAVADOC METHOD $$ func_74777_a
    public void setShort(String par1Str, short par2)
    {
        this.tagMap.put(par1Str, new NBTTagShort(par2));
    }

    // JAVADOC METHOD $$ func_74768_a
    public void setInteger(String par1Str, int par2)
    {
        this.tagMap.put(par1Str, new NBTTagInt(par2));
    }

    // JAVADOC METHOD $$ func_74772_a
    public void setLong(String par1Str, long par2)
    {
        this.tagMap.put(par1Str, new NBTTagLong(par2));
    }

    // JAVADOC METHOD $$ func_74776_a
    public void setFloat(String par1Str, float par2)
    {
        this.tagMap.put(par1Str, new NBTTagFloat(par2));
    }

    // JAVADOC METHOD $$ func_74780_a
    public void setDouble(String par1Str, double par2)
    {
        this.tagMap.put(par1Str, new NBTTagDouble(par2));
    }

    // JAVADOC METHOD $$ func_74778_a
    public void setString(String par1Str, String par2Str)
    {
        this.tagMap.put(par1Str, new NBTTagString(par2Str));
    }

    // JAVADOC METHOD $$ func_74773_a
    public void setByteArray(String par1Str, byte[] par2ArrayOfByte)
    {
        this.tagMap.put(par1Str, new NBTTagByteArray(par2ArrayOfByte));
    }

    // JAVADOC METHOD $$ func_74783_a
    public void setIntArray(String par1Str, int[] par2ArrayOfInteger)
    {
        this.tagMap.put(par1Str, new NBTTagIntArray(par2ArrayOfInteger));
    }

    // JAVADOC METHOD $$ func_74757_a
    public void setBoolean(String par1Str, boolean par2)
    {
        this.setByte(par1Str, (byte)(par2 ? 1 : 0));
    }

    // JAVADOC METHOD $$ func_74781_a
    public NBTBase getTag(String par1Str)
    {
        return (NBTBase)this.tagMap.get(par1Str);
    }

    public byte func_150299_b(String p_150299_1_)
    {
        NBTBase nbtbase = (NBTBase)this.tagMap.get(p_150299_1_);
        return nbtbase != null ? nbtbase.getId() : 0;
    }

    // JAVADOC METHOD $$ func_74764_b
    public boolean hasKey(String par1Str)
    {
        return this.tagMap.containsKey(par1Str);
    }

    public boolean func_150297_b(String p_150297_1_, int p_150297_2_)
    {
        byte b0 = this.func_150299_b(p_150297_1_);

        if (b0 == p_150297_2_)
        {
            return true;
        }
        else if (p_150297_2_ != 99)
        {
            if (b0 > 0)
            {
                field_150301_b.warn("NBT tag {} was of wrong type; expected {}, found {}", new Object[] {p_150297_1_, func_150283_g(p_150297_2_), func_150283_g(b0)});
            }

            return false;
        }
        else
        {
            return b0 == 1 || b0 == 2 || b0 == 3 || b0 == 4 || b0 == 5 || b0 == 6;
        }
    }

    // JAVADOC METHOD $$ func_74771_c
    public byte getByte(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(par1Str)).func_150290_f();
        }
        catch (ClassCastException classcastexception)
        {
            return (byte)0;
        }
    }

    // JAVADOC METHOD $$ func_74765_d
    public short getShort(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(par1Str)).func_150289_e();
        }
        catch (ClassCastException classcastexception)
        {
            return (short)0;
        }
    }

    // JAVADOC METHOD $$ func_74762_e
    public int getInteger(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(par1Str)).func_150287_d();
        }
        catch (ClassCastException classcastexception)
        {
            return 0;
        }
    }

    // JAVADOC METHOD $$ func_74763_f
    public long getLong(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0L : ((NBTBase.NBTPrimitive)this.tagMap.get(par1Str)).func_150291_c();
        }
        catch (ClassCastException classcastexception)
        {
            return 0L;
        }
    }

    // JAVADOC METHOD $$ func_74760_g
    public float getFloat(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0.0F : ((NBTBase.NBTPrimitive)this.tagMap.get(par1Str)).func_150288_h();
        }
        catch (ClassCastException classcastexception)
        {
            return 0.0F;
        }
    }

    // JAVADOC METHOD $$ func_74769_h
    public double getDouble(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0.0D : ((NBTBase.NBTPrimitive)this.tagMap.get(par1Str)).func_150286_g();
        }
        catch (ClassCastException classcastexception)
        {
            return 0.0D;
        }
    }

    // JAVADOC METHOD $$ func_74779_i
    public String getString(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? "" : ((NBTBase)this.tagMap.get(par1Str)).func_150285_a_();
        }
        catch (ClassCastException classcastexception)
        {
            return "";
        }
    }

    // JAVADOC METHOD $$ func_74770_j
    public byte[] getByteArray(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(par1Str)).func_150292_c();
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 7, classcastexception));
        }
    }

    // JAVADOC METHOD $$ func_74759_k
    public int[] getIntArray(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(par1Str)).func_150302_c();
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 11, classcastexception));
        }
    }

    // JAVADOC METHOD $$ func_74775_l
    public NBTTagCompound getCompoundTag(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new NBTTagCompound() : (NBTTagCompound)this.tagMap.get(par1Str);
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 10, classcastexception));
        }
    }

    public NBTTagList func_150295_c(String p_150295_1_, int p_150295_2_)
    {
        try
        {
            if (this.func_150299_b(p_150295_1_) != 9)
            {
                return new NBTTagList();
            }
            else
            {
                NBTTagList nbttaglist = (NBTTagList)this.tagMap.get(p_150295_1_);
                return nbttaglist.tagCount() > 0 && nbttaglist.func_150303_d() != p_150295_2_ ? new NBTTagList() : nbttaglist;
            }
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.createCrashReport(p_150295_1_, 9, classcastexception));
        }
    }

    // JAVADOC METHOD $$ func_74767_n
    public boolean getBoolean(String par1Str)
    {
        return this.getByte(par1Str) != 0;
    }

    // JAVADOC METHOD $$ func_82580_o
    public void removeTag(String par1Str)
    {
        this.tagMap.remove(par1Str);
    }

    public String toString()
    {
        String s = "{";
        String s1;

        for (Iterator iterator = this.tagMap.keySet().iterator(); iterator.hasNext(); s = s + s1 + ':' + this.tagMap.get(s1) + ',')
        {
            s1 = (String)iterator.next();
        }

        return s + "}";
    }

    // JAVADOC METHOD $$ func_82582_d
    public boolean hasNoTags()
    {
        return this.tagMap.isEmpty();
    }

    // JAVADOC METHOD $$ func_82581_a
    private CrashReport createCrashReport(final String par1Str, final int par2, ClassCastException par3ClassCastException)
    {
        CrashReport crashreport = CrashReport.makeCrashReport(par3ClassCastException, "Reading NBT data");
        CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
        crashreportcategory.addCrashSectionCallable("Tag type found", new Callable()
        {
            private static final String __OBFID = "CL_00001216";
            public String call()
            {
                return NBTBase.NBTTypes[((NBTBase)NBTTagCompound.this.tagMap.get(par1Str)).getId()];
            }
        });
        crashreportcategory.addCrashSectionCallable("Tag type expected", new Callable()
        {
            private static final String __OBFID = "CL_00001217";
            public String call()
            {
                return NBTBase.NBTTypes[par2];
            }
        });
        crashreportcategory.addCrashSection("Tag name", par1Str);
        return crashreport;
    }

    // JAVADOC METHOD $$ func_74737_b
    public NBTBase copy()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.tagMap.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            nbttagcompound.setTag(s, ((NBTBase)this.tagMap.get(s)).copy());
        }

        return nbttagcompound;
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)par1Obj;
            return this.tagMap.entrySet().equals(nbttagcompound.tagMap.entrySet());
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    private static void func_150298_a(String p_150298_0_, NBTBase p_150298_1_, DataOutput p_150298_2_) throws IOException
    {
        p_150298_2_.writeByte(p_150298_1_.getId());

        if (p_150298_1_.getId() != 0)
        {
            p_150298_2_.writeUTF(p_150298_0_);
            p_150298_1_.write(p_150298_2_);
        }
    }

    private static byte func_150300_a(DataInput p_150300_0_) throws IOException
    {
        return p_150300_0_.readByte();
    }

    private static String func_150294_b(DataInput p_150294_0_) throws IOException
    {
        return p_150294_0_.readUTF();
    }

    static NBTBase func_150293_a(byte p_150293_0_, String p_150293_1_, DataInput p_150293_2_, int p_150293_3_)
    {
        NBTBase nbtbase = NBTBase.func_150284_a(p_150293_0_);

        try
        {
            nbtbase.load(p_150293_2_, p_150293_3_);
            return nbtbase;
        }
        catch (IOException ioexception)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
            crashreportcategory.addCrashSection("Tag name", p_150293_1_);
            crashreportcategory.addCrashSection("Tag type", Byte.valueOf(p_150293_0_));
            throw new ReportedException(crashreport);
        }
    }
}