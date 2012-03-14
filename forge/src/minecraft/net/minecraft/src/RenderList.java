package net.minecraft.src;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;

public class RenderList
{
    private int field_1242_a;
    private int field_1241_b;
    private int field_1240_c;
    private double field_1239_d;
    private double field_1238_e;
    private double field_1237_f;
    private IntBuffer field_1236_g = GLAllocation.createDirectIntBuffer(65536);
    private boolean field_1235_h = false;
    private boolean field_1234_i = false;

    public void func_861_a(int par1, int par2, int par3, double par4, double par6, double par8)
    {
        this.field_1235_h = true;
        this.field_1236_g.clear();
        this.field_1242_a = par1;
        this.field_1241_b = par2;
        this.field_1240_c = par3;
        this.field_1239_d = par4;
        this.field_1238_e = par6;
        this.field_1237_f = par8;
    }

    public boolean func_862_a(int par1, int par2, int par3)
    {
        return !this.field_1235_h ? false : par1 == this.field_1242_a && par2 == this.field_1241_b && par3 == this.field_1240_c;
    }

    public void func_858_a(int par1)
    {
        this.field_1236_g.put(par1);

        if (this.field_1236_g.remaining() == 0)
        {
            this.func_860_a();
        }
    }

    public void func_860_a()
    {
        if (this.field_1235_h)
        {
            if (!this.field_1234_i)
            {
                this.field_1236_g.flip();
                this.field_1234_i = true;
            }

            if (this.field_1236_g.remaining() > 0)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)((double)this.field_1242_a - this.field_1239_d), (float)((double)this.field_1241_b - this.field_1238_e), (float)((double)this.field_1240_c - this.field_1237_f));
                GL11.glCallLists(this.field_1236_g);
                GL11.glPopMatrix();
            }
        }
    }

    public void func_859_b()
    {
        this.field_1235_h = false;
        this.field_1234_i = false;
    }
}
