package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class BiomeCache
{
    /** The world chunk manager object. */
    private final WorldChunkManager chunkManager;
    private long lastCleanupTime = 0L;
    private LongHashMap cacheMap = new LongHashMap();
    private List cache = new ArrayList();

    public BiomeCache(WorldChunkManager par1WorldChunkManager)
    {
        this.chunkManager = par1WorldChunkManager;
    }

    /**
     * Returns a biome cache block at location specified.
     */
    public BiomeCacheBlock getBiomeCacheBlock(int par1, int par2)
    {
        par1 >>= 4;
        par2 >>= 4;
        long var3 = (long)par1 & 4294967295L | ((long)par2 & 4294967295L) << 32;
        BiomeCacheBlock var5 = (BiomeCacheBlock)this.cacheMap.getValueByKey(var3);

        if (var5 == null)
        {
            var5 = new BiomeCacheBlock(this, par1, par2);
            this.cacheMap.add(var3, var5);
            this.cache.add(var5);
        }

        var5.lastAccessTime = System.currentTimeMillis();
        return var5;
    }

    /**
     * Returns the BiomeGenBase related to the x, z position from the cache.
     */
    public BiomeGenBase getBiomeGenAt(int par1, int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).getBiomeGenAt(par1, par2);
    }

    /**
     * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
     */
    public void cleanupCache()
    {
        long var1 = System.currentTimeMillis();
        long var3 = var1 - this.lastCleanupTime;

        if (var3 > 7500L || var3 < 0L)
        {
            this.lastCleanupTime = var1;

            for (int var5 = 0; var5 < this.cache.size(); ++var5)
            {
                BiomeCacheBlock var6 = (BiomeCacheBlock)this.cache.get(var5);
                long var7 = var1 - var6.lastAccessTime;

                if (var7 > 30000L || var7 < 0L)
                {
                    this.cache.remove(var5--);
                    long var9 = (long)var6.xPosition & 4294967295L | ((long)var6.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(var9);
                }
            }
        }
    }

    public BiomeGenBase[] getCachedBiomes(int par1, int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).biomes;
    }

    /**
     * Get the world chunk manager object for a biome list.
     */
    static WorldChunkManager getChunkManager(BiomeCache par0BiomeCache)
    {
        return par0BiomeCache.chunkManager;
    }
}
