package net.minecraft.world.gen;

import net.minecraft.block.Block;

public class FlatLayerInfo
{
    private Block field_151537_a;
    // JAVADOC FIELD $$ field_82664_a
    private int layerCount;
    // JAVADOC FIELD $$ field_82663_c
    private int layerFillBlockMeta;
    private int layerMinimumY;
    private static final String __OBFID = "CL_00000441";

    public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_)
    {
        this.layerCount = 1;
        this.layerCount = p_i45467_1_;
        this.field_151537_a = p_i45467_2_;
    }

    public FlatLayerInfo(int p_i45468_1_, Block p_i45468_2_, int p_i45468_3_)
    {
        this(p_i45468_1_, p_i45468_2_);
        this.layerFillBlockMeta = p_i45468_3_;
    }

    // JAVADOC METHOD $$ func_82657_a
    public int getLayerCount()
    {
        return this.layerCount;
    }

    public Block func_151536_b()
    {
        return this.field_151537_a;
    }

    // JAVADOC METHOD $$ func_82658_c
    public int getFillBlockMeta()
    {
        return this.layerFillBlockMeta;
    }

    // JAVADOC METHOD $$ func_82656_d
    public int getMinY()
    {
        return this.layerMinimumY;
    }

    // JAVADOC METHOD $$ func_82660_d
    public void setMinY(int par1)
    {
        this.layerMinimumY = par1;
    }

    public String toString()
    {
        String s = Integer.toString(Block.func_149682_b(this.field_151537_a));

        if (this.layerCount > 1)
        {
            s = this.layerCount + "x" + s;
        }

        if (this.layerFillBlockMeta > 0)
        {
            s = s + ":" + this.layerFillBlockMeta;
        }

        return s;
    }
}