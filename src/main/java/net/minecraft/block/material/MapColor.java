package net.minecraft.block.material;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockColored;

public class MapColor
{
    // JAVADOC FIELD $$ field_76281_a
    public static final MapColor[] mapColorArray = new MapColor[64];
    public static final MapColor field_151660_b = new MapColor(0, 0);
    public static final MapColor field_151661_c = new MapColor(1, 8368696);
    public static final MapColor field_151658_d = new MapColor(2, 16247203);
    public static final MapColor field_151659_e = new MapColor(3, 10987431);
    public static final MapColor field_151656_f = new MapColor(4, 16711680);
    public static final MapColor field_151657_g = new MapColor(5, 10526975);
    public static final MapColor field_151668_h = new MapColor(6, 10987431);
    public static final MapColor field_151669_i = new MapColor(7, 31744);
    public static final MapColor field_151666_j = new MapColor(8, 16777215);
    public static final MapColor field_151667_k = new MapColor(9, 10791096);
    public static final MapColor field_151664_l = new MapColor(10, 12020271);
    public static final MapColor field_151665_m = new MapColor(11, 7368816);
    public static final MapColor field_151662_n = new MapColor(12, 4210943);
    public static final MapColor field_151663_o = new MapColor(13, 6837042);
    public static final MapColor field_151677_p = new MapColor(14, 16776437);
    public static final MapColor field_151676_q = new MapColor(15, 14188339);
    public static final MapColor field_151675_r = new MapColor(16, 11685080);
    public static final MapColor field_151674_s = new MapColor(17, 6724056);
    public static final MapColor field_151673_t = new MapColor(18, 15066419);
    public static final MapColor field_151672_u = new MapColor(19, 8375321);
    public static final MapColor field_151671_v = new MapColor(20, 15892389);
    public static final MapColor field_151670_w = new MapColor(21, 5000268);
    public static final MapColor field_151680_x = new MapColor(22, 10066329);
    public static final MapColor field_151679_y = new MapColor(23, 5013401);
    public static final MapColor field_151678_z = new MapColor(24, 8339378);
    public static final MapColor field_151649_A = new MapColor(25, 3361970);
    public static final MapColor field_151650_B = new MapColor(26, 6704179);
    public static final MapColor field_151651_C = new MapColor(27, 6717235);
    public static final MapColor field_151645_D = new MapColor(28, 10040115);
    public static final MapColor field_151646_E = new MapColor(29, 1644825);
    public static final MapColor field_151647_F = new MapColor(30, 16445005);
    public static final MapColor field_151648_G = new MapColor(31, 6085589);
    public static final MapColor field_151652_H = new MapColor(32, 4882687);
    public static final MapColor field_151653_I = new MapColor(33, 55610);
    public static final MapColor field_151654_J = new MapColor(34, 1381407);
    public static final MapColor field_151655_K = new MapColor(35, 7340544);
    // JAVADOC FIELD $$ field_76291_p
    public final int colorValue;
    // JAVADOC FIELD $$ field_76290_q
    public final int colorIndex;
    private static final String __OBFID = "CL_00000544";

    private MapColor(int par1, int par2)
    {
        if (par1 >= 0 && par1 <= 63)
        {
            this.colorIndex = par1;
            this.colorValue = par2;
            mapColorArray[par1] = this;
        }
        else
        {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
    }

    public static MapColor func_151644_a(int p_151644_0_)
    {
        switch (BlockColored.func_150031_c(p_151644_0_))
        {
            case 0:
                return field_151646_E;
            case 1:
                return field_151645_D;
            case 2:
                return field_151651_C;
            case 3:
                return field_151650_B;
            case 4:
                return field_151649_A;
            case 5:
                return field_151678_z;
            case 6:
                return field_151679_y;
            case 7:
                return field_151680_x;
            case 8:
                return field_151670_w;
            case 9:
                return field_151671_v;
            case 10:
                return field_151672_u;
            case 11:
                return field_151673_t;
            case 12:
                return field_151674_s;
            case 13:
                return field_151675_r;
            case 14:
                return field_151676_q;
            case 15:
                return field_151666_j;
            default:
                return field_151660_b;
        }
    }

    @SideOnly(Side.CLIENT)
    public int func_151643_b(int p_151643_1_)
    {
        short short1 = 220;

        if (p_151643_1_ == 3)
        {
            short1 = 135;
        }

        if (p_151643_1_ == 2)
        {
            short1 = 255;
        }

        if (p_151643_1_ == 1)
        {
            short1 = 220;
        }

        if (p_151643_1_ == 0)
        {
            short1 = 180;
        }

        int j = (this.colorValue >> 16 & 255) * short1 / 255;
        int k = (this.colorValue >> 8 & 255) * short1 / 255;
        int l = (this.colorValue & 255) * short1 / 255;
        return -16777216 | j << 16 | k << 8 | l;
    }
}