package net.minecraft.src;

import java.util.Random;

public class BiomeGenSwamp extends BiomeGenBase
{
    protected BiomeGenSwamp(int par1)
    {
        super(par1);
        this.biomeDecorator.treesPerChunk = 2;
        this.biomeDecorator.flowersPerChunk = -999;
        this.biomeDecorator.deadBushPerChunk = 1;
        this.biomeDecorator.mushroomsPerChunk = 8;
        this.biomeDecorator.reedsPerChunk = 10;
        this.biomeDecorator.clayPerChunk = 1;
        this.biomeDecorator.waterlilyPerChunk = 4;
        this.waterColorMultiplier = 14745518;
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    public WorldGenerator getRandomWorldGenForTrees(Random par1Random)
    {
        return this.worldGenSwamp;
    }
}
