package net.minecraft.profiler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler
{
    private static final Logger field_151234_b = LogManager.getLogger();
    // JAVADOC FIELD $$ field_76325_b
    private final List sectionList = new ArrayList();
    // JAVADOC FIELD $$ field_76326_c
    private final List timestampList = new ArrayList();
    // JAVADOC FIELD $$ field_76327_a
    public boolean profilingEnabled;
    // JAVADOC FIELD $$ field_76323_d
    private String profilingSection = "";
    // JAVADOC FIELD $$ field_76324_e
    private final Map profilingMap = new HashMap();
    private static final String __OBFID = "CL_00001497";

    // JAVADOC METHOD $$ func_76317_a
    public void clearProfiling()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }

    // JAVADOC METHOD $$ func_76320_a
    public void startSection(String par1Str)
    {
        if (this.profilingEnabled)
        {
            if (this.profilingSection.length() > 0)
            {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + par1Str;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(Long.valueOf(System.nanoTime()));
        }
    }

    // JAVADOC METHOD $$ func_76319_b
    public void endSection()
    {
        if (this.profilingEnabled)
        {
            long i = System.nanoTime();
            long j = ((Long)this.timestampList.remove(this.timestampList.size() - 1)).longValue();
            this.sectionList.remove(this.sectionList.size() - 1);
            long k = i - j;

            if (this.profilingMap.containsKey(this.profilingSection))
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(((Long)this.profilingMap.get(this.profilingSection)).longValue() + k));
            }
            else
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(k));
            }

            if (k > 100000000L)
            {
                field_151234_b.warn("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double)k / 1000000.0D + " ms");
            }

            this.profilingSection = !this.sectionList.isEmpty() ? (String)this.sectionList.get(this.sectionList.size() - 1) : "";
        }
    }

    // JAVADOC METHOD $$ func_76321_b
    public List getProfilingData(String par1Str)
    {
        if (!this.profilingEnabled)
        {
            return null;
        }
        else
        {
            long i = this.profilingMap.containsKey("root") ? ((Long)this.profilingMap.get("root")).longValue() : 0L;
            long j = this.profilingMap.containsKey(par1Str) ? ((Long)this.profilingMap.get(par1Str)).longValue() : -1L;
            ArrayList arraylist = new ArrayList();

            if (par1Str.length() > 0)
            {
                par1Str = par1Str + ".";
            }

            long k = 0L;
            Iterator iterator = this.profilingMap.keySet().iterator();

            while (iterator.hasNext())
            {
                String s1 = (String)iterator.next();

                if (s1.length() > par1Str.length() && s1.startsWith(par1Str) && s1.indexOf(".", par1Str.length() + 1) < 0)
                {
                    k += ((Long)this.profilingMap.get(s1)).longValue();
                }
            }

            float f = (float)k;

            if (k < j)
            {
                k = j;
            }

            if (i < k)
            {
                i = k;
            }

            Iterator iterator1 = this.profilingMap.keySet().iterator();
            String s2;

            while (iterator1.hasNext())
            {
                s2 = (String)iterator1.next();

                if (s2.length() > par1Str.length() && s2.startsWith(par1Str) && s2.indexOf(".", par1Str.length() + 1) < 0)
                {
                    long l = ((Long)this.profilingMap.get(s2)).longValue();
                    double d0 = (double)l * 100.0D / (double)k;
                    double d1 = (double)l * 100.0D / (double)i;
                    String s3 = s2.substring(par1Str.length());
                    arraylist.add(new Profiler.Result(s3, d0, d1));
                }
            }

            iterator1 = this.profilingMap.keySet().iterator();

            while (iterator1.hasNext())
            {
                s2 = (String)iterator1.next();
                this.profilingMap.put(s2, Long.valueOf(((Long)this.profilingMap.get(s2)).longValue() * 999L / 1000L));
            }

            if ((float)k > f)
            {
                arraylist.add(new Profiler.Result("unspecified", (double)((float)k - f) * 100.0D / (double)k, (double)((float)k - f) * 100.0D / (double)i));
            }

            Collections.sort(arraylist);
            arraylist.add(0, new Profiler.Result(par1Str, 100.0D, (double)k * 100.0D / (double)i));
            return arraylist;
        }
    }

    // JAVADOC METHOD $$ func_76318_c
    public void endStartSection(String par1Str)
    {
        this.endSection();
        this.startSection(par1Str);
    }

    public String getNameOfLastSection()
    {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }

    public static final class Result implements Comparable
        {
            public double field_76332_a;
            public double field_76330_b;
            public String field_76331_c;
            private static final String __OBFID = "CL_00001498";

            public Result(String par1Str, double par2, double par4)
            {
                this.field_76331_c = par1Str;
                this.field_76332_a = par2;
                this.field_76330_b = par4;
            }

            public int compareTo(Profiler.Result par1ProfilerResult)
            {
                return par1ProfilerResult.field_76332_a < this.field_76332_a ? -1 : (par1ProfilerResult.field_76332_a > this.field_76332_a ? 1 : par1ProfilerResult.field_76331_c.compareTo(this.field_76331_c));
            }

            @SideOnly(Side.CLIENT)
            public int func_76329_a()
            {
                return (this.field_76331_c.hashCode() & 11184810) + 4473924;
            }

            public int compareTo(Object par1Obj)
            {
                return this.compareTo((Profiler.Result)par1Obj);
            }
        }
}