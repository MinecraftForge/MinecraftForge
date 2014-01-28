package net.minecraft.client.util;

import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.JsonUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

@SideOnly(Side.CLIENT)
public class JsonBlendingMode
{
    private static JsonBlendingMode field_148118_a = null;
    private final int field_148116_b;
    private final int field_148117_c;
    private final int field_148114_d;
    private final int field_148115_e;
    private final int field_148112_f;
    private final boolean field_148113_g;
    private final boolean field_148119_h;
    private static final String __OBFID = "CL_00001038";

    private JsonBlendingMode(boolean p_i45084_1_, boolean p_i45084_2_, int p_i45084_3_, int p_i45084_4_, int p_i45084_5_, int p_i45084_6_, int p_i45084_7_)
    {
        this.field_148113_g = p_i45084_1_;
        this.field_148116_b = p_i45084_3_;
        this.field_148114_d = p_i45084_4_;
        this.field_148117_c = p_i45084_5_;
        this.field_148115_e = p_i45084_6_;
        this.field_148119_h = p_i45084_2_;
        this.field_148112_f = p_i45084_7_;
    }

    public JsonBlendingMode()
    {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public JsonBlendingMode(int p_i45085_1_, int p_i45085_2_, int p_i45085_3_)
    {
        this(false, false, p_i45085_1_, p_i45085_2_, p_i45085_1_, p_i45085_2_, p_i45085_3_);
    }

    public JsonBlendingMode(int p_i45086_1_, int p_i45086_2_, int p_i45086_3_, int p_i45086_4_, int p_i45086_5_)
    {
        this(true, false, p_i45086_1_, p_i45086_2_, p_i45086_3_, p_i45086_4_, p_i45086_5_);
    }

    public void func_148109_a()
    {
        if (!this.equals(field_148118_a))
        {
            if (field_148118_a == null || this.field_148119_h != field_148118_a.func_148111_b())
            {
                field_148118_a = this;

                if (this.field_148119_h)
                {
                    GL11.glDisable(GL11.GL_BLEND);
                    return;
                }

                GL11.glEnable(GL11.GL_BLEND);
            }

            GL14.glBlendEquation(this.field_148112_f);

            if (this.field_148113_g)
            {
                GL14.glBlendFuncSeparate(this.field_148116_b, this.field_148114_d, this.field_148117_c, this.field_148115_e);
            }
            else
            {
                GL11.glBlendFunc(this.field_148116_b, this.field_148114_d);
            }
        }
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof JsonBlendingMode))
        {
            return false;
        }
        else
        {
            JsonBlendingMode jsonblendingmode = (JsonBlendingMode)par1Obj;
            return this.field_148112_f != jsonblendingmode.field_148112_f ? false : (this.field_148115_e != jsonblendingmode.field_148115_e ? false : (this.field_148114_d != jsonblendingmode.field_148114_d ? false : (this.field_148119_h != jsonblendingmode.field_148119_h ? false : (this.field_148113_g != jsonblendingmode.field_148113_g ? false : (this.field_148117_c != jsonblendingmode.field_148117_c ? false : this.field_148116_b == jsonblendingmode.field_148116_b)))));
        }
    }

    public int hashCode()
    {
        int i = this.field_148116_b;
        i = 31 * i + this.field_148117_c;
        i = 31 * i + this.field_148114_d;
        i = 31 * i + this.field_148115_e;
        i = 31 * i + this.field_148112_f;
        i = 31 * i + (this.field_148113_g ? 1 : 0);
        i = 31 * i + (this.field_148119_h ? 1 : 0);
        return i;
    }

    public boolean func_148111_b()
    {
        return this.field_148119_h;
    }

    public static JsonBlendingMode func_148110_a(JsonObject p_148110_0_)
    {
        if (p_148110_0_ == null)
        {
            return new JsonBlendingMode();
        }
        else
        {
            int i = 32774;
            int j = 1;
            int k = 0;
            int l = 1;
            int i1 = 0;
            boolean flag = true;
            boolean flag1 = false;

            if (JsonUtils.func_151205_a(p_148110_0_, "func"))
            {
                i = func_148108_a(p_148110_0_.get("func").getAsString());

                if (i != 32774)
                {
                    flag = false;
                }
            }

            if (JsonUtils.func_151205_a(p_148110_0_, "srcrgb"))
            {
                j = func_148107_b(p_148110_0_.get("srcrgb").getAsString());

                if (j != 1)
                {
                    flag = false;
                }
            }

            if (JsonUtils.func_151205_a(p_148110_0_, "dstrgb"))
            {
                k = func_148107_b(p_148110_0_.get("dstrgb").getAsString());

                if (k != 0)
                {
                    flag = false;
                }
            }

            if (JsonUtils.func_151205_a(p_148110_0_, "srcalpha"))
            {
                l = func_148107_b(p_148110_0_.get("srcalpha").getAsString());

                if (l != 1)
                {
                    flag = false;
                }

                flag1 = true;
            }

            if (JsonUtils.func_151205_a(p_148110_0_, "dstalpha"))
            {
                i1 = func_148107_b(p_148110_0_.get("dstalpha").getAsString());

                if (i1 != 0)
                {
                    flag = false;
                }

                flag1 = true;
            }

            return flag ? new JsonBlendingMode() : (flag1 ? new JsonBlendingMode(j, k, l, i1, i) : new JsonBlendingMode(j, k, i));
        }
    }

    private static int func_148108_a(String p_148108_0_)
    {
        String s1 = p_148108_0_.trim().toLowerCase();
        return s1.equals("add") ? 32774 : (s1.equals("subtract") ? 32778 : (s1.equals("reversesubtract") ? 32779 : (s1.equals("reverse_subtract") ? 32779 : (s1.equals("min") ? 32775 : (s1.equals("max") ? 32776 : 32774)))));
    }

    private static int func_148107_b(String p_148107_0_)
    {
        String s1 = p_148107_0_.trim().toLowerCase();
        s1 = s1.replaceAll("_", "");
        s1 = s1.replaceAll("one", "1");
        s1 = s1.replaceAll("zero", "0");
        s1 = s1.replaceAll("minus", "-");
        return s1.equals("0") ? 0 : (s1.equals("1") ? 1 : (s1.equals("srccolor") ? 768 : (s1.equals("1-srccolor") ? 769 : (s1.equals("dstcolor") ? 774 : (s1.equals("1-dstcolor") ? 775 : (s1.equals("srcalpha") ? 770 : (s1.equals("1-srcalpha") ? 771 : (s1.equals("dstalpha") ? 772 : (s1.equals("1-dstalpha") ? 773 : -1)))))))));
    }
}