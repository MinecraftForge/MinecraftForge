package net.minecraft.world.gen.layer;

import java.util.ArrayList;
import java.util.List;

public class IntCache
{
    private static int intCacheSize = 256;
    // JAVADOC FIELD $$ field_76449_b
    private static List freeSmallArrays = new ArrayList();
    // JAVADOC FIELD $$ field_76450_c
    private static List inUseSmallArrays = new ArrayList();
    // JAVADOC FIELD $$ field_76447_d
    private static List freeLargeArrays = new ArrayList();
    // JAVADOC FIELD $$ field_76448_e
    private static List inUseLargeArrays = new ArrayList();
    private static final String __OBFID = "CL_00000557";

    public static synchronized int[] getIntCache(int par0)
    {
        int[] aint;

        if (par0 <= 256)
        {
            if (freeSmallArrays.isEmpty())
            {
                aint = new int[256];
                inUseSmallArrays.add(aint);
                return aint;
            }
            else
            {
                aint = (int[])freeSmallArrays.remove(freeSmallArrays.size() - 1);
                inUseSmallArrays.add(aint);
                return aint;
            }
        }
        else if (par0 > intCacheSize)
        {
            intCacheSize = par0;
            freeLargeArrays.clear();
            inUseLargeArrays.clear();
            aint = new int[intCacheSize];
            inUseLargeArrays.add(aint);
            return aint;
        }
        else if (freeLargeArrays.isEmpty())
        {
            aint = new int[intCacheSize];
            inUseLargeArrays.add(aint);
            return aint;
        }
        else
        {
            aint = (int[])freeLargeArrays.remove(freeLargeArrays.size() - 1);
            inUseLargeArrays.add(aint);
            return aint;
        }
    }

    // JAVADOC METHOD $$ func_76446_a
    public static synchronized void resetIntCache()
    {
        if (!freeLargeArrays.isEmpty())
        {
            freeLargeArrays.remove(freeLargeArrays.size() - 1);
        }

        if (!freeSmallArrays.isEmpty())
        {
            freeSmallArrays.remove(freeSmallArrays.size() - 1);
        }

        freeLargeArrays.addAll(inUseLargeArrays);
        freeSmallArrays.addAll(inUseSmallArrays);
        inUseLargeArrays.clear();
        inUseSmallArrays.clear();
    }

    public static synchronized String func_85144_b()
    {
        return "cache: " + freeLargeArrays.size() + ", tcache: " + freeSmallArrays.size() + ", allocated: " + inUseLargeArrays.size() + ", tallocated: " + inUseSmallArrays.size();
    }
}