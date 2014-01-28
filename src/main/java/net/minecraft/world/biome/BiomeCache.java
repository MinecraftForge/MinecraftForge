package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache
{
    // JAVADOC FIELD $$ field_76844_a
    private final WorldChunkManager chunkManager;
    // JAVADOC FIELD $$ field_76842_b
    private long lastCleanupTime;
    // JAVADOC FIELD $$ field_76843_c
    private LongHashMap cacheMap = new LongHashMap();
    // JAVADOC FIELD $$ field_76841_d
    private List cache = new ArrayList();
    private static final String __OBFID = "CL_00000162";

    public BiomeCache(WorldChunkManager par1WorldChunkManager)
    {
        this.chunkManager = par1WorldChunkManager;
    }

    // JAVADOC METHOD $$ func_76840_a
    public BiomeCache.Block getBiomeCacheBlock(int par1, int par2)
    {
        par1 >>= 4;
        par2 >>= 4;
        long k = (long)par1 & 4294967295L | ((long)par2 & 4294967295L) << 32;
        BiomeCache.Block block = (BiomeCache.Block)this.cacheMap.getValueByKey(k);

        if (block == null)
        {
            block = new BiomeCache.Block(par1, par2);
            this.cacheMap.add(k, block);
            this.cache.add(block);
        }

        block.lastAccessTime = MinecraftServer.getSystemTimeMillis();
        return block;
    }

    // JAVADOC METHOD $$ func_76837_b
    public BiomeGenBase getBiomeGenAt(int par1, int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).getBiomeGenAt(par1, par2);
    }

    // JAVADOC METHOD $$ func_76838_a
    public void cleanupCache()
    {
        long i = MinecraftServer.getSystemTimeMillis();
        long j = i - this.lastCleanupTime;

        if (j > 7500L || j < 0L)
        {
            this.lastCleanupTime = i;

            for (int k = 0; k < this.cache.size(); ++k)
            {
                BiomeCache.Block block = (BiomeCache.Block)this.cache.get(k);
                long l = i - block.lastAccessTime;

                if (l > 30000L || l < 0L)
                {
                    this.cache.remove(k--);
                    long i1 = (long)block.xPosition & 4294967295L | ((long)block.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(i1);
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_76839_e
    public BiomeGenBase[] getCachedBiomes(int par1, int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).biomes;
    }

    public class Block
    {
        // JAVADOC FIELD $$ field_76890_b
        public float[] rainfallValues = new float[256];
        // JAVADOC FIELD $$ field_76891_c
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        // JAVADOC FIELD $$ field_76888_d
        public int xPosition;
        // JAVADOC FIELD $$ field_76889_e
        public int zPosition;
        // JAVADOC FIELD $$ field_76886_f
        public long lastAccessTime;
        private static final String __OBFID = "CL_00000163";

        public Block(int par2, int par3)
        {
            this.xPosition = par2;
            this.zPosition = par3;
            BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, par2 << 4, par3 << 4, 16, 16);
            BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, par2 << 4, par3 << 4, 16, 16, false);
        }

        // JAVADOC METHOD $$ func_76885_a
        public BiomeGenBase getBiomeGenAt(int par1, int par2)
        {
            return this.biomes[par1 & 15 | (par2 & 15) << 4];
        }
    }
}