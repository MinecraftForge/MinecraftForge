package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase
{
    private boolean field_150615_aC;
    private WorldGenIceSpike field_150616_aD = new WorldGenIceSpike();
    private WorldGenIcePath field_150617_aE = new WorldGenIcePath(4);
    private static final String __OBFID = "CL_00000174";

    public BiomeGenSnow(int p_i45378_1_, boolean p_i45378_2_)
    {
        super(p_i45378_1_);
        this.field_150615_aC = p_i45378_2_;

        if (p_i45378_2_)
        {
            this.topBlock = Blocks.snow;
        }

        this.spawnableCreatureList.clear();
    }

    public void decorate(World par1World, Random par2Random, int par3, int par4)
    {
        if (this.field_150615_aC)
        {
            int k;
            int l;
            int i1;

            for (k = 0; k < 3; ++k)
            {
                l = par3 + par2Random.nextInt(16) + 8;
                i1 = par4 + par2Random.nextInt(16) + 8;
                this.field_150616_aD.generate(par1World, par2Random, l, par1World.getHeightValue(l, i1), i1);
            }

            for (k = 0; k < 2; ++k)
            {
                l = par3 + par2Random.nextInt(16) + 8;
                i1 = par4 + par2Random.nextInt(16) + 8;
                this.field_150617_aE.generate(par1World, par2Random, l, par1World.getHeightValue(l, i1), i1);
            }
        }

        super.decorate(par1World, par2Random, par3, par4);
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return new WorldGenTaiga2(false);
    }

    public BiomeGenBase func_150566_k()
    {
        BiomeGenBase biomegenbase = (new BiomeGenSnow(this.biomeID + 128, true)).func_150557_a(13828095, true).setBiomeName(this.biomeName + " Spikes").setEnableSnow().setTemperatureRainfall(0.0F, 0.5F).func_150570_a(new BiomeGenBase.Height(this.minHeight + 0.1F, this.maxHeight + 0.1F));
        biomegenbase.minHeight = this.minHeight + 0.3F;
        biomegenbase.maxHeight = this.maxHeight + 0.4F;
        return biomegenbase;
    }
}