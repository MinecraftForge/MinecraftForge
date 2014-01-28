package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class WorldGenerator
{
    // JAVADOC FIELD $$ field_76488_a
    private final boolean doBlockNotify;
    private static final String __OBFID = "CL_00000409";

    public WorldGenerator()
    {
        this.doBlockNotify = false;
    }

    public WorldGenerator(boolean par1)
    {
        this.doBlockNotify = par1;
    }

    public abstract boolean generate(World var1, Random var2, int var3, int var4, int var5);

    // JAVADOC METHOD $$ func_76487_a
    public void setScale(double par1, double par3, double par5) {}

    protected void func_150515_a(World p_150515_1_, int p_150515_2_, int p_150515_3_, int p_150515_4_, Block p_150515_5_)
    {
        this.func_150516_a(p_150515_1_, p_150515_2_, p_150515_3_, p_150515_4_, p_150515_5_, 0);
    }

    protected void func_150516_a(World p_150516_1_, int p_150516_2_, int p_150516_3_, int p_150516_4_, Block p_150516_5_, int p_150516_6_)
    {
        if (this.doBlockNotify)
        {
            p_150516_1_.func_147465_d(p_150516_2_, p_150516_3_, p_150516_4_, p_150516_5_, p_150516_6_, 3);
        }
        else
        {
            p_150516_1_.func_147465_d(p_150516_2_, p_150516_3_, p_150516_4_, p_150516_5_, p_150516_6_, 2);
        }
    }
}