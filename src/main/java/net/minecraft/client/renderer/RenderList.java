package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderList
{
    public int field_78429_a;
    public int field_78427_b;
    public int field_78428_c;
    private double field_78425_d;
    private double field_78426_e;
    private double field_78423_f;
    private IntBuffer field_78424_g = GLAllocation.createDirectIntBuffer(65536);
    private boolean field_78430_h;
    private boolean field_78431_i;
    private static final String __OBFID = "CL_00000957";

    public void func_78422_a(int par1, int par2, int par3, double par4, double par6, double par8)
    {
        this.field_78430_h = true;
        this.field_78424_g.clear();
        this.field_78429_a = par1;
        this.field_78427_b = par2;
        this.field_78428_c = par3;
        this.field_78425_d = par4;
        this.field_78426_e = par6;
        this.field_78423_f = par8;
    }

    public boolean func_78418_a(int par1, int par2, int par3)
    {
        return !this.field_78430_h ? false : par1 == this.field_78429_a && par2 == this.field_78427_b && par3 == this.field_78428_c;
    }

    public void func_78420_a(int par1)
    {
        this.field_78424_g.put(par1);

        if (this.field_78424_g.remaining() == 0)
        {
            this.func_78419_a();
        }
    }

    public void func_78419_a()
    {
        if (this.field_78430_h)
        {
            if (!this.field_78431_i)
            {
                this.field_78424_g.flip();
                this.field_78431_i = true;
            }

            if (this.field_78424_g.remaining() > 0)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)((double)this.field_78429_a - this.field_78425_d), (float)((double)this.field_78427_b - this.field_78426_e), (float)((double)this.field_78428_c - this.field_78423_f));
                GL11.glCallLists(this.field_78424_g);
                GL11.glPopMatrix();
            }
        }
    }

    public void func_78421_b()
    {
        this.field_78430_h = false;
        this.field_78431_i = false;
    }
}