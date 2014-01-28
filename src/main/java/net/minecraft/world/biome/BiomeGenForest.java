package net.minecraft.world.biome;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase
{
    private int field_150632_aF;
    protected static final WorldGenForest field_150629_aC = new WorldGenForest(false, true);
    protected static final WorldGenForest field_150630_aD = new WorldGenForest(false, false);
    protected static final WorldGenCanopyTree field_150631_aE = new WorldGenCanopyTree(false);
    private static final String __OBFID = "CL_00000170";

    public BiomeGenForest(int p_i45377_1_, int p_i45377_2_)
    {
        super(p_i45377_1_);
        this.field_150632_aF = p_i45377_2_;
        this.theBiomeDecorator.treesPerChunk = 10;
        this.theBiomeDecorator.grassPerChunk = 2;

        if (this.field_150632_aF == 1)
        {
            this.theBiomeDecorator.treesPerChunk = 6;
            this.theBiomeDecorator.flowersPerChunk = 100;
            this.theBiomeDecorator.grassPerChunk = 1;
        }

        this.func_76733_a(5159473);
        this.setTemperatureRainfall(0.7F, 0.8F);

        if (this.field_150632_aF == 2)
        {
            this.field_150609_ah = 353825;
            this.color = 3175492;
            this.setTemperatureRainfall(0.6F, 0.6F);
        }

        if (this.field_150632_aF == 0)
        {
            this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 5, 4, 4));
        }

        if (this.field_150632_aF == 3)
        {
            this.theBiomeDecorator.treesPerChunk = -999;
        }

        if (this.field_150632_aF == 1)
        {
            this.flowers.clear();
            for (int x = 0; x < BlockFlower.field_149859_a.length; x++)
            {
                this.addFlower(Blocks.red_flower, x, 10);
            }
        }
    }

    public BiomeGenBase func_150557_a(int p_150557_1_, boolean p_150557_2_)
    {
        if (this.field_150632_aF == 2)
        {
            this.field_150609_ah = 353825;
            this.color = p_150557_1_;

            if (p_150557_2_)
            {
                this.field_150609_ah = (this.field_150609_ah & 16711422) >> 1;
            }

            return this;
        }
        else
        {
            return super.func_150557_a(p_150557_1_, p_150557_2_);
        }
    }

    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return (WorldGenAbstractTree)(this.field_150632_aF == 3 && p_150567_1_.nextInt(3) > 0 ? field_150631_aE : (this.field_150632_aF != 2 && p_150567_1_.nextInt(5) != 0 ? this.worldGeneratorTrees : field_150630_aD));
    }

    public String func_150572_a(Random p_150572_1_, int p_150572_2_, int p_150572_3_, int p_150572_4_)
    {
        if (this.field_150632_aF == 1)
        {
            double d0 = MathHelper.func_151237_a((1.0D + field_150606_ad.func_151601_a((double)p_150572_2_ / 48.0D, (double)p_150572_4_ / 48.0D)) / 2.0D, 0.0D, 0.9999D);
            int l = (int)(d0 * (double)BlockFlower.field_149859_a.length);

            if (l == 1)
            {
                l = 0;
            }

            return BlockFlower.field_149859_a[l];
        }
        else
        {
            return super.func_150572_a(p_150572_1_, p_150572_2_, p_150572_3_, p_150572_4_);
        }
    }

    public void decorate(World par1World, Random par2Random, int par3, int par4)
    {
        int k;
        int l;
        int i1;
        int j1;
        int k1;

        if (this.field_150632_aF == 3)
        {
            for (k = 0; k < 4; ++k)
            {
                for (l = 0; l < 4; ++l)
                {
                    i1 = par3 + k * 4 + 1 + 8 + par2Random.nextInt(3);
                    j1 = par4 + l * 4 + 1 + 8 + par2Random.nextInt(3);
                    k1 = par1World.getHeightValue(i1, j1);

                    if (par2Random.nextInt(20) == 0)
                    {
                        WorldGenBigMushroom worldgenbigmushroom = new WorldGenBigMushroom();
                        worldgenbigmushroom.generate(par1World, par2Random, i1, k1, j1);
                    }
                    else
                    {
                        WorldGenAbstractTree worldgenabstracttree = this.func_150567_a(par2Random);
                        worldgenabstracttree.setScale(1.0D, 1.0D, 1.0D);

                        if (worldgenabstracttree.generate(par1World, par2Random, i1, k1, j1))
                        {
                            worldgenabstracttree.func_150524_b(par1World, par2Random, i1, k1, j1);
                        }
                    }
                }
            }
        }

        k = par2Random.nextInt(5) - 3;

        if (this.field_150632_aF == 1)
        {
            k += 2;
        }

        l = 0;

        while (l < k)
        {
            i1 = par2Random.nextInt(3);

            if (i1 == 0)
            {
                field_150610_ae.func_150548_a(1);
            }
            else if (i1 == 1)
            {
                field_150610_ae.func_150548_a(4);
            }
            else if (i1 == 2)
            {
                field_150610_ae.func_150548_a(5);
            }

            j1 = 0;

            while (true)
            {
                if (j1 < 5)
                {
                    k1 = par3 + par2Random.nextInt(16) + 8;
                    int i2 = par4 + par2Random.nextInt(16) + 8;
                    int l1 = par2Random.nextInt(par1World.getHeightValue(k1, i2) + 32);

                    if (!field_150610_ae.generate(par1World, par2Random, k1, l1, i2))
                    {
                        ++j1;
                        continue;
                    }
                }

                ++l;
                break;
            }
        }

        super.decorate(par1World, par2Random, par3, par4);
    }

    @SideOnly(Side.CLIENT)
    public int func_150558_b(int p_150558_1_, int p_150558_2_, int p_150558_3_)
    {
        int l = super.func_150558_b(p_150558_1_, p_150558_2_, p_150558_3_);
        return this.field_150632_aF == 3 ? (l & 16711422) + 2634762 >> 1 : l;
    }

    public BiomeGenBase func_150566_k()
    {
        if (this.biomeID == BiomeGenBase.forest.biomeID)
        {
            BiomeGenForest biomegenforest = new BiomeGenForest(this.biomeID + 128, 1);
            biomegenforest.func_150570_a(new BiomeGenBase.Height(this.minHeight, this.maxHeight + 0.2F));
            biomegenforest.setBiomeName("Flower Forest");
            biomegenforest.func_150557_a(6976549, true);
            biomegenforest.func_76733_a(8233509);
            return biomegenforest;
        }
        else
        {
            return this.biomeID != BiomeGenBase.field_150583_P.biomeID && this.biomeID != BiomeGenBase.field_150582_Q.biomeID ? new BiomeGenMutated(this.biomeID + 128, this)
            {
                private static final String __OBFID = "CL_00000171";
                public void decorate(World world, Random random, int i, int j)
                {
                    this.field_150611_aD.decorate(world, random, i, j);
                }
            }: new BiomeGenMutated(this.biomeID + 128, this)
            {
                private static final String __OBFID = "CL_00000172";
                public WorldGenAbstractTree func_150567_a(Random random)
                {
                    return random.nextBoolean() ? BiomeGenForest.field_150629_aC : BiomeGenForest.field_150630_aD;
                }
            };
        }
    }
}