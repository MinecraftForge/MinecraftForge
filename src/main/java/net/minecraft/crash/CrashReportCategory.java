package net.minecraft.crash;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;

public class CrashReportCategory
{
    private final CrashReport theCrashReport;
    private final String field_85076_b;
    private final List field_85077_c = new ArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];
    private static final String __OBFID = "CL_00001409";

    public CrashReportCategory(CrashReport par1CrashReport, String par2Str)
    {
        this.theCrashReport = par1CrashReport;
        this.field_85076_b = par2Str;
    }

    @SideOnly(Side.CLIENT)
    public static String func_85074_a(double par0, double par2, double par4)
    {
        return String.format("%.2f,%.2f,%.2f - %s", new Object[] {Double.valueOf(par0), Double.valueOf(par2), Double.valueOf(par4), getLocationInfo(MathHelper.floor_double(par0), MathHelper.floor_double(par2), MathHelper.floor_double(par4))});
    }

    // JAVADOC METHOD $$ func_85071_a
    public static String getLocationInfo(int par0, int par1, int par2)
    {
        StringBuilder stringbuilder = new StringBuilder();

        try
        {
            stringbuilder.append(String.format("World: (%d,%d,%d)", new Object[] {Integer.valueOf(par0), Integer.valueOf(par1), Integer.valueOf(par2)}));
        }
        catch (Throwable throwable2)
        {
            stringbuilder.append("(Error finding world loc)");
        }

        stringbuilder.append(", ");
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;

        try
        {
            l = par0 >> 4;
            i1 = par2 >> 4;
            j1 = par0 & 15;
            k1 = par1 >> 4;
            l1 = par2 & 15;
            i2 = l << 4;
            j2 = i1 << 4;
            k2 = (l + 1 << 4) - 1;
            l2 = (i1 + 1 << 4) - 1;
            stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", new Object[] {Integer.valueOf(j1), Integer.valueOf(k1), Integer.valueOf(l1), Integer.valueOf(l), Integer.valueOf(i1), Integer.valueOf(i2), Integer.valueOf(j2), Integer.valueOf(k2), Integer.valueOf(l2)}));
        }
        catch (Throwable throwable1)
        {
            stringbuilder.append("(Error finding chunk loc)");
        }

        stringbuilder.append(", ");

        try
        {
            l = par0 >> 9;
            i1 = par2 >> 9;
            j1 = l << 5;
            k1 = i1 << 5;
            l1 = (l + 1 << 5) - 1;
            i2 = (i1 + 1 << 5) - 1;
            j2 = l << 9;
            k2 = i1 << 9;
            l2 = (l + 1 << 9) - 1;
            int i3 = (i1 + 1 << 9) - 1;
            stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", new Object[] {Integer.valueOf(l), Integer.valueOf(i1), Integer.valueOf(j1), Integer.valueOf(k1), Integer.valueOf(l1), Integer.valueOf(i2), Integer.valueOf(j2), Integer.valueOf(k2), Integer.valueOf(l2), Integer.valueOf(i3)}));
        }
        catch (Throwable throwable)
        {
            stringbuilder.append("(Error finding world loc)");
        }

        return stringbuilder.toString();
    }

    // JAVADOC METHOD $$ func_71500_a
    public void addCrashSectionCallable(String par1Str, Callable par2Callable)
    {
        try
        {
            this.addCrashSection(par1Str, par2Callable.call());
        }
        catch (Throwable throwable)
        {
            this.addCrashSectionThrowable(par1Str, throwable);
        }
    }

    // JAVADOC METHOD $$ func_71507_a
    public void addCrashSection(String par1Str, Object par2Obj)
    {
        this.field_85077_c.add(new CrashReportCategory.Entry(par1Str, par2Obj));
    }

    // JAVADOC METHOD $$ func_71499_a
    public void addCrashSectionThrowable(String par1Str, Throwable par2Throwable)
    {
        this.addCrashSection(par1Str, par2Throwable);
    }

    public int func_85073_a(int par1)
    {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();

        if (astacktraceelement.length <= 0)
        {
            return 0;
        }
        else
        {
            int len = astacktraceelement.length - 3 - par1;
            // Really Mojang, Still, god damn...
            if (len <= 0) len = astacktraceelement.length;
            this.stackTrace = new StackTraceElement[len];
            System.arraycopy(astacktraceelement, astacktraceelement.length - len, this.stackTrace, 0, this.stackTrace.length);
            return this.stackTrace.length;
        }
    }

    public boolean func_85069_a(StackTraceElement par1StackTraceElement, StackTraceElement par2StackTraceElement)
    {
        if (this.stackTrace.length != 0 && par1StackTraceElement != null)
        {
            StackTraceElement stacktraceelement2 = this.stackTrace[0];

            if (stacktraceelement2.isNativeMethod() == par1StackTraceElement.isNativeMethod() && stacktraceelement2.getClassName().equals(par1StackTraceElement.getClassName()) && stacktraceelement2.getFileName().equals(par1StackTraceElement.getFileName()) && stacktraceelement2.getMethodName().equals(par1StackTraceElement.getMethodName()))
            {
                if (par2StackTraceElement != null != this.stackTrace.length > 1)
                {
                    return false;
                }
                else if (par2StackTraceElement != null && !this.stackTrace[1].equals(par2StackTraceElement))
                {
                    return false;
                }
                else
                {
                    this.stackTrace[0] = par1StackTraceElement;
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public void func_85070_b(int par1)
    {
        StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - par1];
        System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
        this.stackTrace = astacktraceelement;
    }

    public void func_85072_a(StringBuilder par1StringBuilder)
    {
        par1StringBuilder.append("-- ").append(this.field_85076_b).append(" --\n");
        par1StringBuilder.append("Details:");
        Iterator iterator = this.field_85077_c.iterator();

        while (iterator.hasNext())
        {
            CrashReportCategory.Entry entry = (CrashReportCategory.Entry)iterator.next();
            par1StringBuilder.append("\n\t");
            par1StringBuilder.append(entry.func_85089_a());
            par1StringBuilder.append(": ");
            par1StringBuilder.append(entry.func_85090_b());
        }

        if (this.stackTrace != null && this.stackTrace.length > 0)
        {
            par1StringBuilder.append("\nStacktrace:");
            StackTraceElement[] astacktraceelement = this.stackTrace;
            int j = astacktraceelement.length;

            for (int i = 0; i < j; ++i)
            {
                StackTraceElement stacktraceelement = astacktraceelement[i];
                par1StringBuilder.append("\n\tat ");
                par1StringBuilder.append(stacktraceelement.toString());
            }
        }
    }

    public StackTraceElement[] func_147152_a()
    {
        return this.stackTrace;
    }

    public static void func_147153_a(CrashReportCategory p_147153_0_, final int p_147153_1_, final int p_147153_2_, final int p_147153_3_, final Block p_147153_4_, final int p_147153_5_)
    {
        final int i = Block.func_149682_b(p_147153_4_);
        p_147153_0_.addCrashSectionCallable("Block type", new Callable()
        {
            private static final String __OBFID = "CL_00001426";
            public String call()
            {
                try
                {
                    return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(i), p_147153_4_.func_149739_a(), p_147153_4_.getClass().getCanonicalName()});
                }
                catch (Throwable throwable)
                {
                    return "ID #" + i;
                }
            }
        });
        p_147153_0_.addCrashSectionCallable("Block data value", new Callable()
        {
            private static final String __OBFID = "CL_00001441";
            public String call()
            {
                if (p_147153_5_ < 0)
                {
                    return "Unknown? (Got " + p_147153_5_ + ")";
                }
                else
                {
                    String s = String.format("%4s", new Object[] {Integer.toBinaryString(p_147153_5_)}).replace(" ", "0");
                    return String.format("%1$d / 0x%1$X / 0b%2$s", new Object[] {Integer.valueOf(p_147153_5_), s});
                }
            }
        });
        p_147153_0_.addCrashSectionCallable("Block location", new Callable()
        {
            private static final String __OBFID = "CL_00001465";
            public String call()
            {
                return CrashReportCategory.getLocationInfo(p_147153_1_, p_147153_2_, p_147153_3_);
            }
        });
    }

    static class Entry
        {
            private final String field_85092_a;
            private final String field_85091_b;
            private static final String __OBFID = "CL_00001489";

            public Entry(String par1Str, Object par2Obj)
            {
                this.field_85092_a = par1Str;

                if (par2Obj == null)
                {
                    this.field_85091_b = "~~NULL~~";
                }
                else if (par2Obj instanceof Throwable)
                {
                    Throwable throwable = (Throwable)par2Obj;
                    this.field_85091_b = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
                }
                else
                {
                    this.field_85091_b = par2Obj.toString();
                }
            }

            public String func_85089_a()
            {
                return this.field_85092_a;
            }

            public String func_85090_b()
            {
                return this.field_85091_b;
            }
        }
}