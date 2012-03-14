package net.minecraft.src;

public class BiomeGenMushroomIsland extends BiomeGenBase
{
    public BiomeGenMushroomIsland(int par1)
    {
        super(par1);
        this.biomeDecorator.treesPerChunk = -100;
        this.biomeDecorator.flowersPerChunk = -100;
        this.biomeDecorator.grassPerChunk = -100;
        this.biomeDecorator.mushroomsPerChunk = 1;
        this.biomeDecorator.bigMushroomsPerChunk = 1;
        this.topBlock = (byte)Block.mycelium.blockID;
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCreatureList.add(new SpawnListEntry(EntityMooshroom.class, 8, 4, 8));
    }
}
