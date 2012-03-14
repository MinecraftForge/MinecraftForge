package net.minecraft.src;

public class BiomeEndDecorator extends BiomeDecorator
{
    protected WorldGenerator spikeGen;

    public BiomeEndDecorator(BiomeGenBase par1BiomeGenBase)
    {
        super(par1BiomeGenBase);
        this.spikeGen = new WorldGenSpikes(Block.whiteStone.blockID);
    }

    /**
     * The method that does the work of actually decorating chunks
     */
    protected void decorate()
    {
        this.generateOres();

        if (this.randomGenerator.nextInt(5) == 0)
        {
            int var1 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int var2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int var3 = this.currentWorld.getTopSolidOrLiquidBlock(var1, var2);

            if (var3 > 0)
            {
                ;
            }

            this.spikeGen.generate(this.currentWorld, this.randomGenerator, var1, var3, var2);
        }

        if (this.chunk_X == 0 && this.chunk_Z == 0)
        {
            EntityDragon var4 = new EntityDragon(this.currentWorld);
            var4.setLocationAndAngles(0.0D, 128.0D, 0.0D, this.randomGenerator.nextFloat() * 360.0F, 0.0F);
            this.currentWorld.spawnEntityInWorld(var4);
        }
    }
}
