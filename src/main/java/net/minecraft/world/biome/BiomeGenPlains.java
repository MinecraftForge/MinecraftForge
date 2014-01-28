package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase
{
    protected boolean field_150628_aC;
    private static final String __OBFID = "CL_00000180";

    public BiomeGenPlains(int par1)
    {
        super(par1);
        this.setTemperatureRainfall(0.8F, 0.4F);
        this.func_150570_a(field_150593_e);
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 10;
        this.flowers.clear();
        this.addFlower(Blocks.red_flower,    4,  3);
        this.addFlower(Blocks.red_flower,    5,  3);
        this.addFlower(Blocks.red_flower,    6,  3);
        this.addFlower(Blocks.red_flower,    7,  3);
        this.addFlower(Blocks.red_flower,    0, 20);
        this.addFlower(Blocks.red_flower,    3, 20);
        this.addFlower(Blocks.red_flower,    8, 20);
        this.addFlower(Blocks.yellow_flower, 0, 30);
    }

    public String func_150572_a(Random p_150572_1_, int p_150572_2_, int p_150572_3_, int p_150572_4_)
    {
        double d0 = field_150606_ad.func_151601_a((double)p_150572_2_ / 200.0D, (double)p_150572_4_ / 200.0D);
        int l;

        if (d0 < -0.8D)
        {
            l = p_150572_1_.nextInt(4);
            return BlockFlower.field_149859_a[4 + l];
        }
        else if (p_150572_1_.nextInt(3) > 0)
        {
            l = p_150572_1_.nextInt(3);
            return l == 0 ? BlockFlower.field_149859_a[0] : (l == 1 ? BlockFlower.field_149859_a[3] : BlockFlower.field_149859_a[8]);
        }
        else
        {
            return BlockFlower.field_149858_b[0];
        }
    }

    public void decorate(World par1World, Random par2Random, int par3, int par4)
    {
        double d0 = field_150606_ad.func_151601_a((double)(par3 + 8) / 200.0D, (double)(par4 + 8) / 200.0D);
        int k;
        int l;
        int i1;
        int j1;

        if (d0 < -0.8D)
        {
            this.theBiomeDecorator.flowersPerChunk = 15;
            this.theBiomeDecorator.grassPerChunk = 5;
        }
        else
        {
            this.theBiomeDecorator.flowersPerChunk = 4;
            this.theBiomeDecorator.grassPerChunk = 10;
            field_150610_ae.func_150548_a(2);

            for (k = 0; k < 7; ++k)
            {
                l = par3 + par2Random.nextInt(16) + 8;
                i1 = par4 + par2Random.nextInt(16) + 8;
                j1 = par2Random.nextInt(par1World.getHeightValue(l, i1) + 32);
                field_150610_ae.generate(par1World, par2Random, l, j1, i1);
            }
        }

        if (this.field_150628_aC)
        {
            field_150610_ae.func_150548_a(0);

            for (k = 0; k < 10; ++k)
            {
                l = par3 + par2Random.nextInt(16) + 8;
                i1 = par4 + par2Random.nextInt(16) + 8;
                j1 = par2Random.nextInt(par1World.getHeightValue(l, i1) + 32);
                field_150610_ae.generate(par1World, par2Random, l, j1, i1);
            }
        }

        super.decorate(par1World, par2Random, par3, par4);
    }

    public BiomeGenBase func_150566_k()
    {
        BiomeGenPlains biomegenplains = new BiomeGenPlains(this.biomeID + 128);
        biomegenplains.setBiomeName("Sunflower Plains");
        biomegenplains.field_150628_aC = true;
        biomegenplains.setColor(9286496);
        biomegenplains.field_150609_ah = 14273354;
        return biomegenplains;
    }
}