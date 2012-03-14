package net.minecraft.src;

import java.util.Random;

public class BiomeGenForest extends BiomeGenBase
{
    public BiomeGenForest(int par1)
    {
        super(par1);
        this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));
        this.biomeDecorator.treesPerChunk = 10;
        this.biomeDecorator.grassPerChunk = 2;
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    public WorldGenerator getRandomWorldGenForTrees(Random par1Random)
    {
        return (WorldGenerator)(par1Random.nextInt(5) == 0 ? this.worldGenForest : (par1Random.nextInt(10) == 0 ? this.worldGenBigTree : this.worldGenTrees));
    }
}
