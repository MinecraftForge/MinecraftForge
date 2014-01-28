package net.minecraft.client.shader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.vecmath.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;

@SideOnly(Side.CLIENT)
public class ShaderUniform
{
    private static final Logger field_148104_a = LogManager.getLogger();
    private int field_148102_b;
    private final int field_148103_c;
    private final int field_148100_d;
    private final IntBuffer field_148101_e;
    private final FloatBuffer field_148098_f;
    private final String field_148099_g;
    private boolean field_148105_h;
    private final ShaderManager field_148106_i;
    private static final String __OBFID = "CL_00001046";

    public ShaderUniform(String p_i45092_1_, int p_i45092_2_, int p_i45092_3_, ShaderManager p_i45092_4_)
    {
        this.field_148099_g = p_i45092_1_;
        this.field_148103_c = p_i45092_3_;
        this.field_148100_d = p_i45092_2_;
        this.field_148106_i = p_i45092_4_;

        if (p_i45092_2_ <= 3)
        {
            this.field_148101_e = BufferUtils.createIntBuffer(p_i45092_3_);
            this.field_148098_f = null;
        }
        else
        {
            this.field_148101_e = null;
            this.field_148098_f = BufferUtils.createFloatBuffer(p_i45092_3_);
        }

        this.field_148102_b = -1;
        this.func_148096_h();
    }

    private void func_148096_h()
    {
        this.field_148105_h = true;

        if (this.field_148106_i != null)
        {
            this.field_148106_i.func_147985_d();
        }
    }

    public static int func_148085_a(String p_148085_0_)
    {
        byte b0 = -1;

        if (p_148085_0_.equals("int"))
        {
            b0 = 0;
        }
        else if (p_148085_0_.equals("float"))
        {
            b0 = 4;
        }
        else if (p_148085_0_.startsWith("matrix"))
        {
            if (p_148085_0_.endsWith("2x2"))
            {
                b0 = 8;
            }
            else if (p_148085_0_.endsWith("3x3"))
            {
                b0 = 9;
            }
            else if (p_148085_0_.endsWith("4x4"))
            {
                b0 = 10;
            }
            else if (p_148085_0_.endsWith("2x3"))
            {
                b0 = 11;
            }
            else if (p_148085_0_.endsWith("2x4"))
            {
                b0 = 12;
            }
            else if (p_148085_0_.endsWith("3x2"))
            {
                b0 = 13;
            }
            else if (p_148085_0_.endsWith("3x4"))
            {
                b0 = 14;
            }
            else if (p_148085_0_.endsWith("4x2"))
            {
                b0 = 15;
            }
            else if (p_148085_0_.endsWith("4x3"))
            {
                b0 = 16;
            }
        }

        return b0;
    }

    public void func_148084_b(int p_148084_1_)
    {
        this.field_148102_b = p_148084_1_;
    }

    public String func_148086_a()
    {
        return this.field_148099_g;
    }

    public void func_148090_a(float p_148090_1_)
    {
        this.field_148098_f.position(0);
        this.field_148098_f.put(0, p_148090_1_);
        this.func_148096_h();
    }

    public void func_148087_a(float p_148087_1_, float p_148087_2_)
    {
        this.field_148098_f.position(0);
        this.field_148098_f.put(0, p_148087_1_);
        this.field_148098_f.put(1, p_148087_2_);
        this.func_148096_h();
    }

    public void func_148095_a(float p_148095_1_, float p_148095_2_, float p_148095_3_)
    {
        this.field_148098_f.position(0);
        this.field_148098_f.put(0, p_148095_1_);
        this.field_148098_f.put(1, p_148095_2_);
        this.field_148098_f.put(2, p_148095_3_);
        this.func_148096_h();
    }

    public void func_148081_a(float p_148081_1_, float p_148081_2_, float p_148081_3_, float p_148081_4_)
    {
        this.field_148098_f.position(0);
        this.field_148098_f.put(p_148081_1_);
        this.field_148098_f.put(p_148081_2_);
        this.field_148098_f.put(p_148081_3_);
        this.field_148098_f.put(p_148081_4_);
        this.field_148098_f.flip();
        this.func_148096_h();
    }

    public void func_148092_b(float p_148092_1_, float p_148092_2_, float p_148092_3_, float p_148092_4_)
    {
        this.field_148098_f.position(0);

        if (this.field_148100_d >= 4)
        {
            this.field_148098_f.put(0, p_148092_1_);
        }

        if (this.field_148100_d >= 5)
        {
            this.field_148098_f.put(1, p_148092_2_);
        }

        if (this.field_148100_d >= 6)
        {
            this.field_148098_f.put(2, p_148092_3_);
        }

        if (this.field_148100_d >= 7)
        {
            this.field_148098_f.put(3, p_148092_4_);
        }

        this.func_148096_h();
    }

    public void func_148083_a(int p_148083_1_, int p_148083_2_, int p_148083_3_, int p_148083_4_)
    {
        this.field_148101_e.position(0);

        if (this.field_148100_d >= 0)
        {
            this.field_148101_e.put(0, p_148083_1_);
        }

        if (this.field_148100_d >= 1)
        {
            this.field_148101_e.put(1, p_148083_2_);
        }

        if (this.field_148100_d >= 2)
        {
            this.field_148101_e.put(2, p_148083_3_);
        }

        if (this.field_148100_d >= 3)
        {
            this.field_148101_e.put(3, p_148083_4_);
        }

        this.func_148096_h();
    }

    public void func_148097_a(float[] p_148097_1_)
    {
        if (p_148097_1_.length < this.field_148103_c)
        {
            field_148104_a.warn("Uniform.set called with a too-small value array (expected " + this.field_148103_c + ", got " + p_148097_1_.length + "). Ignoring.");
        }
        else
        {
            this.field_148098_f.position(0);
            this.field_148098_f.put(p_148097_1_);
            this.field_148098_f.position(0);
            this.func_148096_h();
        }
    }

    public void func_148094_a(float p_148094_1_, float p_148094_2_, float p_148094_3_, float p_148094_4_, float p_148094_5_, float p_148094_6_, float p_148094_7_, float p_148094_8_, float p_148094_9_, float p_148094_10_, float p_148094_11_, float p_148094_12_, float p_148094_13_, float p_148094_14_, float p_148094_15_, float p_148094_16_)
    {
        this.field_148098_f.position(0);
        this.field_148098_f.put(0, p_148094_1_);
        this.field_148098_f.put(1, p_148094_2_);
        this.field_148098_f.put(2, p_148094_3_);
        this.field_148098_f.put(3, p_148094_4_);
        this.field_148098_f.put(4, p_148094_5_);
        this.field_148098_f.put(5, p_148094_6_);
        this.field_148098_f.put(6, p_148094_7_);
        this.field_148098_f.put(7, p_148094_8_);
        this.field_148098_f.put(8, p_148094_9_);
        this.field_148098_f.put(9, p_148094_10_);
        this.field_148098_f.put(10, p_148094_11_);
        this.field_148098_f.put(11, p_148094_12_);
        this.field_148098_f.put(12, p_148094_13_);
        this.field_148098_f.put(13, p_148094_14_);
        this.field_148098_f.put(14, p_148094_15_);
        this.field_148098_f.put(15, p_148094_16_);
        this.func_148096_h();
    }

    public void func_148088_a(Matrix4f p_148088_1_)
    {
        this.func_148094_a(p_148088_1_.m00, p_148088_1_.m01, p_148088_1_.m02, p_148088_1_.m03, p_148088_1_.m10, p_148088_1_.m11, p_148088_1_.m12, p_148088_1_.m13, p_148088_1_.m20, p_148088_1_.m21, p_148088_1_.m22, p_148088_1_.m23, p_148088_1_.m30, p_148088_1_.m31, p_148088_1_.m32, p_148088_1_.m33);
    }

    public void func_148093_b()
    {
        if (!this.field_148105_h)
        {
            ;
        }

        this.field_148105_h = false;

        if (this.field_148100_d <= 3)
        {
            this.func_148091_i();
        }
        else if (this.field_148100_d <= 7)
        {
            this.func_148089_j();
        }
        else
        {
            if (this.field_148100_d > 16)
            {
                field_148104_a.warn("Uniform.upload called, but type value (" + this.field_148100_d + ") is not " + "a valid type. Ignoring.");
                return;
            }

            this.func_148082_k();
        }
    }

    private void func_148091_i()
    {
        switch (this.field_148100_d)
        {
            case 0:
                GL20.glUniform1(this.field_148102_b, this.field_148101_e);
                break;
            case 1:
                GL20.glUniform2(this.field_148102_b, this.field_148101_e);
                break;
            case 2:
                GL20.glUniform3(this.field_148102_b, this.field_148101_e);
                break;
            case 3:
                GL20.glUniform4(this.field_148102_b, this.field_148101_e);
                break;
            default:
                field_148104_a.warn("Uniform.upload called, but count value (" + this.field_148103_c + ") is " + " not in the range of 1 to 4. Ignoring.");
        }
    }

    private void func_148089_j()
    {
        switch (this.field_148100_d)
        {
            case 4:
                GL20.glUniform1(this.field_148102_b, this.field_148098_f);
                break;
            case 5:
                GL20.glUniform2(this.field_148102_b, this.field_148098_f);
                break;
            case 6:
                GL20.glUniform3(this.field_148102_b, this.field_148098_f);
                break;
            case 7:
                GL20.glUniform4(this.field_148102_b, this.field_148098_f);
                break;
            default:
                field_148104_a.warn("Uniform.upload called, but count value (" + this.field_148103_c + ") is " + "not in the range of 1 to 4. Ignoring.");
        }
    }

    private void func_148082_k()
    {
        switch (this.field_148100_d)
        {
            case 8:
                GL20.glUniformMatrix2(this.field_148102_b, true, this.field_148098_f);
                break;
            case 9:
                GL20.glUniformMatrix3(this.field_148102_b, true, this.field_148098_f);
                break;
            case 10:
                GL20.glUniformMatrix4(this.field_148102_b, true, this.field_148098_f);
                break;
            case 11:
                GL21.glUniformMatrix2x3(this.field_148102_b, true, this.field_148098_f);
                break;
            case 12:
                GL21.glUniformMatrix2x4(this.field_148102_b, true, this.field_148098_f);
                break;
            case 13:
                GL21.glUniformMatrix3x2(this.field_148102_b, true, this.field_148098_f);
                break;
            case 14:
                GL21.glUniformMatrix3x4(this.field_148102_b, true, this.field_148098_f);
                break;
            case 15:
                GL21.glUniformMatrix4x2(this.field_148102_b, true, this.field_148098_f);
                break;
            case 16:
                GL21.glUniformMatrix4x3(this.field_148102_b, true, this.field_148098_f);
        }
    }
}