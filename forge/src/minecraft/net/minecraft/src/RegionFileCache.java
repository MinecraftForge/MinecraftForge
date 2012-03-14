package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache
{
    /** A map containing Files and keys and RegionFiles as values */
    private static final Map regionsByFilename = new HashMap();

    public static synchronized RegionFile createOrLoadRegionFile(File par0File, int par1, int par2)
    {
        File var3 = new File(par0File, "region");
        File var4 = new File(var3, "r." + (par1 >> 5) + "." + (par2 >> 5) + ".mca");
        Reference var5 = (Reference)regionsByFilename.get(var4);
        RegionFile var6;

        if (var5 != null)
        {
            var6 = (RegionFile)var5.get();

            if (var6 != null)
            {
                return var6;
            }
        }

        if (!var3.exists())
        {
            var3.mkdirs();
        }

        if (regionsByFilename.size() >= 256)
        {
            clearRegionFileReferences();
        }

        var6 = new RegionFile(var4);
        regionsByFilename.put(var4, new SoftReference(var6));
        return var6;
    }

    /**
     * Saves the current Chunk Map Cache
     */
    public static synchronized void clearRegionFileReferences()
    {
        Iterator var0 = regionsByFilename.values().iterator();

        while (var0.hasNext())
        {
            Reference var1 = (Reference)var0.next();

            try
            {
                RegionFile var2 = (RegionFile)var1.get();

                if (var2 != null)
                {
                    var2.close();
                }
            }
            catch (IOException var3)
            {
                var3.printStackTrace();
            }
        }

        regionsByFilename.clear();
    }

    /**
     * Returns an input stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataInputStream getChunkInputStream(File par0File, int par1, int par2)
    {
        RegionFile var3 = createOrLoadRegionFile(par0File, par1, par2);
        return var3.getChunkDataInputStream(par1 & 31, par2 & 31);
    }

    /**
     * Returns an output stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataOutputStream getChunkOutputStream(File par0File, int par1, int par2)
    {
        RegionFile var3 = createOrLoadRegionFile(par0File, par1, par2);
        return var3.getChunkDataOutputStream(par1 & 31, par2 & 31);
    }
}
