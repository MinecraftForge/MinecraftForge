package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Profiler
{
    /** Flag profiling enabled */
    public static boolean profilingEnabled = false;

    /** List of parent sections */
    private static List sectionList = new ArrayList();

    /** List of timestamps (System.nanoTime) */
    private static List timestampList = new ArrayList();

    /** Current profiling section */
    private static String profilingSection = "";

    /** Profiling map */
    private static Map profilingMap = new HashMap();

    /**
     * Clear profiling
     */
    public static void clearProfiling()
    {
        profilingMap.clear();
    }

    /**
     * Start section
     */
    public static void startSection(String par0Str)
    {
        if (profilingEnabled)
        {
            if (profilingSection.length() > 0)
            {
                profilingSection = profilingSection + ".";
            }

            profilingSection = profilingSection + par0Str;
            sectionList.add(profilingSection);
            timestampList.add(Long.valueOf(System.nanoTime()));
        }
    }

    /**
     * End section
     */
    public static void endSection()
    {
        if (profilingEnabled)
        {
            long var0 = System.nanoTime();
            long var2 = ((Long)timestampList.remove(timestampList.size() - 1)).longValue();
            sectionList.remove(sectionList.size() - 1);
            long var4 = var0 - var2;

            if (profilingMap.containsKey(profilingSection))
            {
                profilingMap.put(profilingSection, Long.valueOf(((Long)profilingMap.get(profilingSection)).longValue() + var4));
            }
            else
            {
                profilingMap.put(profilingSection, Long.valueOf(var4));
            }

            profilingSection = sectionList.size() > 0 ? (String)sectionList.get(sectionList.size() - 1) : "";

            if (var4 > 100000000L)
            {
                System.out.println(profilingSection + " " + var4);
            }
        }
    }

    /**
     * Get profiling data
     */
    public static List getProfilingData(String par0Str)
    {
        if (!profilingEnabled)
        {
            return null;
        }
        else
        {
            long var2 = profilingMap.containsKey("root") ? ((Long)profilingMap.get("root")).longValue() : 0L;
            long var4 = profilingMap.containsKey(par0Str) ? ((Long)profilingMap.get(par0Str)).longValue() : -1L;
            ArrayList var6 = new ArrayList();

            if (par0Str.length() > 0)
            {
                par0Str = par0Str + ".";
            }

            long var7 = 0L;
            Iterator var9 = profilingMap.keySet().iterator();

            while (var9.hasNext())
            {
                String var10 = (String)var9.next();

                if (var10.length() > par0Str.length() && var10.startsWith(par0Str) && var10.indexOf(".", par0Str.length() + 1) < 0)
                {
                    var7 += ((Long)profilingMap.get(var10)).longValue();
                }
            }

            float var19 = (float)var7;

            if (var7 < var4)
            {
                var7 = var4;
            }

            if (var2 < var7)
            {
                var2 = var7;
            }

            Iterator var20 = profilingMap.keySet().iterator();
            String var11;

            while (var20.hasNext())
            {
                var11 = (String)var20.next();

                if (var11.length() > par0Str.length() && var11.startsWith(par0Str) && var11.indexOf(".", par0Str.length() + 1) < 0)
                {
                    long var12 = ((Long)profilingMap.get(var11)).longValue();
                    double var14 = (double)var12 * 100.0D / (double)var7;
                    double var16 = (double)var12 * 100.0D / (double)var2;
                    String var18 = var11.substring(par0Str.length());
                    var6.add(new ProfilerResult(var18, var14, var16));
                }
            }

            var20 = profilingMap.keySet().iterator();

            while (var20.hasNext())
            {
                var11 = (String)var20.next();
                profilingMap.put(var11, Long.valueOf(((Long)profilingMap.get(var11)).longValue() * 999L / 1000L));
            }

            if ((float)var7 > var19)
            {
                var6.add(new ProfilerResult("unspecified", (double)((float)var7 - var19) * 100.0D / (double)var7, (double)((float)var7 - var19) * 100.0D / (double)var2));
            }

            Collections.sort(var6);
            var6.add(0, new ProfilerResult(par0Str, 100.0D, (double)var7 * 100.0D / (double)var2));
            return var6;
        }
    }

    /**
     * End current section and start a new section
     */
    public static void endStartSection(String par0Str)
    {
        endSection();
        startSection(par0Str);
    }
}
