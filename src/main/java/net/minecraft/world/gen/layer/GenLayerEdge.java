package net.minecraft.world.gen.layer;

public class GenLayerEdge extends GenLayer
{
    private final GenLayerEdge.Mode field_151627_c;
    private static final String __OBFID = "CL_00000547";

    public GenLayerEdge(long p_i45474_1_, GenLayer p_i45474_3_, GenLayerEdge.Mode p_i45474_4_)
    {
        super(p_i45474_1_);
        this.parent = p_i45474_3_;
        this.field_151627_c = p_i45474_4_;
    }

    // JAVADOC METHOD $$ func_75904_a
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        switch (GenLayerEdge.SwitchMode.field_151642_a[this.field_151627_c.ordinal()])
        {
            case 1:
            default:
                return this.func_151626_c(par1, par2, par3, par4);
            case 2:
                return this.func_151624_d(par1, par2, par3, par4);
            case 3:
                return this.func_151625_e(par1, par2, par3, par4);
        }
    }

    private int[] func_151626_c(int p_151626_1_, int p_151626_2_, int p_151626_3_, int p_151626_4_)
    {
        int i1 = p_151626_1_ - 1;
        int j1 = p_151626_2_ - 1;
        int k1 = 1 + p_151626_3_ + 1;
        int l1 = 1 + p_151626_4_ + 1;
        int[] aint = this.parent.getInts(i1, j1, k1, l1);
        int[] aint1 = IntCache.getIntCache(p_151626_3_ * p_151626_4_);

        for (int i2 = 0; i2 < p_151626_4_; ++i2)
        {
            for (int j2 = 0; j2 < p_151626_3_; ++j2)
            {
                this.initChunkSeed((long)(j2 + p_151626_1_), (long)(i2 + p_151626_2_));
                int k2 = aint[j2 + 1 + (i2 + 1) * k1];

                if (k2 == 1)
                {
                    int l2 = aint[j2 + 1 + (i2 + 1 - 1) * k1];
                    int i3 = aint[j2 + 1 + 1 + (i2 + 1) * k1];
                    int j3 = aint[j2 + 1 - 1 + (i2 + 1) * k1];
                    int k3 = aint[j2 + 1 + (i2 + 1 + 1) * k1];
                    boolean flag = l2 == 3 || i3 == 3 || j3 == 3 || k3 == 3;
                    boolean flag1 = l2 == 4 || i3 == 4 || j3 == 4 || k3 == 4;

                    if (flag || flag1)
                    {
                        k2 = 2;
                    }
                }

                aint1[j2 + i2 * p_151626_3_] = k2;
            }
        }

        return aint1;
    }

    private int[] func_151624_d(int p_151624_1_, int p_151624_2_, int p_151624_3_, int p_151624_4_)
    {
        int i1 = p_151624_1_ - 1;
        int j1 = p_151624_2_ - 1;
        int k1 = 1 + p_151624_3_ + 1;
        int l1 = 1 + p_151624_4_ + 1;
        int[] aint = this.parent.getInts(i1, j1, k1, l1);
        int[] aint1 = IntCache.getIntCache(p_151624_3_ * p_151624_4_);

        for (int i2 = 0; i2 < p_151624_4_; ++i2)
        {
            for (int j2 = 0; j2 < p_151624_3_; ++j2)
            {
                int k2 = aint[j2 + 1 + (i2 + 1) * k1];

                if (k2 == 4)
                {
                    int l2 = aint[j2 + 1 + (i2 + 1 - 1) * k1];
                    int i3 = aint[j2 + 1 + 1 + (i2 + 1) * k1];
                    int j3 = aint[j2 + 1 - 1 + (i2 + 1) * k1];
                    int k3 = aint[j2 + 1 + (i2 + 1 + 1) * k1];
                    boolean flag = l2 == 2 || i3 == 2 || j3 == 2 || k3 == 2;
                    boolean flag1 = l2 == 1 || i3 == 1 || j3 == 1 || k3 == 1;

                    if (flag1 || flag)
                    {
                        k2 = 3;
                    }
                }

                aint1[j2 + i2 * p_151624_3_] = k2;
            }
        }

        return aint1;
    }

    private int[] func_151625_e(int p_151625_1_, int p_151625_2_, int p_151625_3_, int p_151625_4_)
    {
        int[] aint = this.parent.getInts(p_151625_1_, p_151625_2_, p_151625_3_, p_151625_4_);
        int[] aint1 = IntCache.getIntCache(p_151625_3_ * p_151625_4_);

        for (int i1 = 0; i1 < p_151625_4_; ++i1)
        {
            for (int j1 = 0; j1 < p_151625_3_; ++j1)
            {
                this.initChunkSeed((long)(j1 + p_151625_1_), (long)(i1 + p_151625_2_));
                int k1 = aint[j1 + i1 * p_151625_3_];

                if (k1 != 0 && this.nextInt(13) == 0)
                {
                    k1 |= 1 + this.nextInt(15) << 8 & 3840;
                }

                aint1[j1 + i1 * p_151625_3_] = k1;
            }
        }

        return aint1;
    }

    static final class SwitchMode
        {
            static final int[] field_151642_a = new int[GenLayerEdge.Mode.values().length];
            private static final String __OBFID = "CL_00000548";

            static
            {
                try
                {
                    field_151642_a[GenLayerEdge.Mode.COOL_WARM.ordinal()] = 1;
                }
                catch (NoSuchFieldError var3)
                {
                    ;
                }

                try
                {
                    field_151642_a[GenLayerEdge.Mode.HEAT_ICE.ordinal()] = 2;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    field_151642_a[GenLayerEdge.Mode.SPECIAL.ordinal()] = 3;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }

    public static enum Mode
    {
        COOL_WARM,
        HEAT_ICE,
        SPECIAL;

        private static final String __OBFID = "CL_00000549";
    }
}