package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase
{
    // JAVADOC FIELD $$ field_75040_a
    protected int range = 8;
    // JAVADOC FIELD $$ field_75038_b
    protected Random rand = new Random();
    // JAVADOC FIELD $$ field_75039_c
    protected World worldObj;
    private static final String __OBFID = "CL_00000394";

    public void func_151539_a(IChunkProvider p_151539_1_, World p_151539_2_, int p_151539_3_, int p_151539_4_, Block[] p_151539_5_)
    {
        int k = this.range;
        this.worldObj = p_151539_2_;
        this.rand.setSeed(p_151539_2_.getSeed());
        long l = this.rand.nextLong();
        long i1 = this.rand.nextLong();

        for (int j1 = p_151539_3_ - k; j1 <= p_151539_3_ + k; ++j1)
        {
            for (int k1 = p_151539_4_ - k; k1 <= p_151539_4_ + k; ++k1)
            {
                long l1 = (long)j1 * l;
                long i2 = (long)k1 * i1;
                this.rand.setSeed(l1 ^ i2 ^ p_151539_2_.getSeed());
                this.func_151538_a(p_151539_2_, j1, k1, p_151539_3_, p_151539_4_, p_151539_5_);
            }
        }
    }

    protected void func_151538_a(World p_151538_1_, int p_151538_2_, int p_151538_3_, int p_151538_4_, int p_151538_5_, Block[] p_151538_6_) {}
}