package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MapGenCavesHell extends MapGenBase
{
    private static final String __OBFID = "CL_00000395";

    protected void func_151544_a(long p_151544_1_, int p_151544_3_, int p_151544_4_, Block[] p_151544_5_, double p_151544_6_, double p_151544_8_, double p_151544_10_)
    {
        this.func_151543_a(p_151544_1_, p_151544_3_, p_151544_4_, p_151544_5_, p_151544_6_, p_151544_8_, p_151544_10_, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void func_151543_a(long p_151543_1_, int p_151543_3_, int p_151543_4_, Block[] p_151543_5_, double p_151543_6_, double p_151543_8_, double p_151543_10_, float p_151543_12_, float p_151543_13_, float p_151543_14_, int p_151543_15_, int p_151543_16_, double p_151543_17_)
    {
        double d4 = (double)(p_151543_3_ * 16 + 8);
        double d5 = (double)(p_151543_4_ * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;
        Random random = new Random(p_151543_1_);

        if (p_151543_16_ <= 0)
        {
            int j1 = this.range * 16 - 16;
            p_151543_16_ = j1 - random.nextInt(j1 / 4);
        }

        boolean flag1 = false;

        if (p_151543_15_ == -1)
        {
            p_151543_15_ = p_151543_16_ / 2;
            flag1 = true;
        }

        int k1 = random.nextInt(p_151543_16_ / 2) + p_151543_16_ / 4;

        for (boolean flag = random.nextInt(6) == 0; p_151543_15_ < p_151543_16_; ++p_151543_15_)
        {
            double d6 = 1.5D + (double)(MathHelper.sin((float)p_151543_15_ * (float)Math.PI / (float)p_151543_16_) * p_151543_12_ * 1.0F);
            double d7 = d6 * p_151543_17_;
            float f5 = MathHelper.cos(p_151543_14_);
            float f6 = MathHelper.sin(p_151543_14_);
            p_151543_6_ += (double)(MathHelper.cos(p_151543_13_) * f5);
            p_151543_8_ += (double)f6;
            p_151543_10_ += (double)(MathHelper.sin(p_151543_13_) * f5);

            if (flag)
            {
                p_151543_14_ *= 0.92F;
            }
            else
            {
                p_151543_14_ *= 0.7F;
            }

            p_151543_14_ += f4 * 0.1F;
            p_151543_13_ += f3 * 0.1F;
            f4 *= 0.9F;
            f3 *= 0.75F;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!flag1 && p_151543_15_ == k1 && p_151543_12_ > 1.0F)
            {
                this.func_151543_a(random.nextLong(), p_151543_3_, p_151543_4_, p_151543_5_, p_151543_6_, p_151543_8_, p_151543_10_, random.nextFloat() * 0.5F + 0.5F, p_151543_13_ - ((float)Math.PI / 2F), p_151543_14_ / 3.0F, p_151543_15_, p_151543_16_, 1.0D);
                this.func_151543_a(random.nextLong(), p_151543_3_, p_151543_4_, p_151543_5_, p_151543_6_, p_151543_8_, p_151543_10_, random.nextFloat() * 0.5F + 0.5F, p_151543_13_ + ((float)Math.PI / 2F), p_151543_14_ / 3.0F, p_151543_15_, p_151543_16_, 1.0D);
                return;
            }

            if (flag1 || random.nextInt(4) != 0)
            {
                double d8 = p_151543_6_ - d4;
                double d9 = p_151543_10_ - d5;
                double d10 = (double)(p_151543_16_ - p_151543_15_);
                double d11 = (double)(p_151543_12_ + 2.0F + 16.0F);

                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11)
                {
                    return;
                }

                if (p_151543_6_ >= d4 - 16.0D - d6 * 2.0D && p_151543_10_ >= d5 - 16.0D - d6 * 2.0D && p_151543_6_ <= d4 + 16.0D + d6 * 2.0D && p_151543_10_ <= d5 + 16.0D + d6 * 2.0D)
                {
                    int i4 = MathHelper.floor_double(p_151543_6_ - d6) - p_151543_3_ * 16 - 1;
                    int l1 = MathHelper.floor_double(p_151543_6_ + d6) - p_151543_3_ * 16 + 1;
                    int k4 = MathHelper.floor_double(p_151543_8_ - d7) - 1;
                    int i2 = MathHelper.floor_double(p_151543_8_ + d7) + 1;
                    int j4 = MathHelper.floor_double(p_151543_10_ - d6) - p_151543_4_ * 16 - 1;
                    int j2 = MathHelper.floor_double(p_151543_10_ + d6) - p_151543_4_ * 16 + 1;

                    if (i4 < 0)
                    {
                        i4 = 0;
                    }

                    if (l1 > 16)
                    {
                        l1 = 16;
                    }

                    if (k4 < 1)
                    {
                        k4 = 1;
                    }

                    if (i2 > 120)
                    {
                        i2 = 120;
                    }

                    if (j4 < 0)
                    {
                        j4 = 0;
                    }

                    if (j2 > 16)
                    {
                        j2 = 16;
                    }

                    boolean flag2 = false;
                    int k2;
                    int j3;

                    for (k2 = i4; !flag2 && k2 < l1; ++k2)
                    {
                        for (int l2 = j4; !flag2 && l2 < j2; ++l2)
                        {
                            for (int i3 = i2 + 1; !flag2 && i3 >= k4 - 1; --i3)
                            {
                                j3 = (k2 * 16 + l2) * 128 + i3;

                                if (i3 >= 0 && i3 < 128)
                                {
                                    Block block = p_151543_5_[j3];

                                    if (block == Blocks.flowing_lava || block == Blocks.lava)
                                    {
                                        flag2 = true;
                                    }

                                    if (i3 != k4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != j4 && l2 != j2 - 1)
                                    {
                                        i3 = k4;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag2)
                    {
                        for (k2 = i4; k2 < l1; ++k2)
                        {
                            double d14 = ((double)(k2 + p_151543_3_ * 16) + 0.5D - p_151543_6_) / d6;

                            for (j3 = j4; j3 < j2; ++j3)
                            {
                                double d13 = ((double)(j3 + p_151543_4_ * 16) + 0.5D - p_151543_10_) / d6;
                                int k3 = (k2 * 16 + j3) * 128 + i2;

                                for (int l3 = i2 - 1; l3 >= k4; --l3)
                                {
                                    double d12 = ((double)l3 + 0.5D - p_151543_8_) / d7;

                                    if (d12 > -0.7D && d14 * d14 + d12 * d12 + d13 * d13 < 1.0D)
                                    {
                                        Block block1 = p_151543_5_[k3];

                                        if (block1 == Blocks.netherrack || block1 == Blocks.dirt || block1 == Blocks.grass)
                                        {
                                            p_151543_5_[k3] = null;
                                        }
                                    }

                                    --k3;
                                }
                            }
                        }

                        if (flag1)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    protected void func_151538_a(World p_151538_1_, int p_151538_2_, int p_151538_3_, int p_151538_4_, int p_151538_5_, Block[] p_151538_6_)
    {
        int i1 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);

        if (this.rand.nextInt(5) != 0)
        {
            i1 = 0;
        }

        for (int j1 = 0; j1 < i1; ++j1)
        {
            double d0 = (double)(p_151538_2_ * 16 + this.rand.nextInt(16));
            double d1 = (double)this.rand.nextInt(128);
            double d2 = (double)(p_151538_3_ * 16 + this.rand.nextInt(16));
            int k1 = 1;

            if (this.rand.nextInt(4) == 0)
            {
                this.func_151544_a(this.rand.nextLong(), p_151538_4_, p_151538_5_, p_151538_6_, d0, d1, d2);
                k1 += this.rand.nextInt(4);
            }

            for (int l1 = 0; l1 < k1; ++l1)
            {
                float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                this.func_151543_a(this.rand.nextLong(), p_151538_4_, p_151538_5_, p_151538_6_, d0, d1, d2, f2 * 2.0F, f, f1, 0, 0, 0.5D);
            }
        }
    }
}