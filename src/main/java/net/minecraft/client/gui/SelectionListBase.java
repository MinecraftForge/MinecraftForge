package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class SelectionListBase
{
    private final Minecraft field_148456_a;
    private final int field_148453_e;
    private final int field_148450_f;
    private final int field_148451_g;
    private final int field_148461_h;
    protected final int field_148454_b;
    protected int field_148455_c;
    protected int field_148452_d;
    private float field_148462_i = -2.0F;
    private float field_148459_j;
    private float field_148460_k;
    private int field_148457_l = -1;
    private long field_148458_m;
    private static final String __OBFID = "CL_00000789";

    public SelectionListBase(Minecraft par1Minecraft, int par2, int par3, int par4, int par5, int par6)
    {
        this.field_148456_a = par1Minecraft;
        this.field_148450_f = par3;
        this.field_148461_h = par3 + par5;
        this.field_148454_b = par6;
        this.field_148453_e = par2;
        this.field_148451_g = par2 + par4;
    }

    protected abstract int func_148443_a();

    protected abstract void func_148449_a(int var1, boolean var2);

    protected abstract boolean func_148444_a(int var1);

    protected int func_148447_b()
    {
        return this.func_148443_a() * this.field_148454_b;
    }

    protected abstract void func_148445_c();

    protected abstract void func_148442_a(int var1, int var2, int var3, int var4, Tessellator var5);

    private void func_148448_f()
    {
        int i = this.func_148441_d();

        if (i < 0)
        {
            i = 0;
        }

        if (this.field_148460_k < 0.0F)
        {
            this.field_148460_k = 0.0F;
        }

        if (this.field_148460_k > (float)i)
        {
            this.field_148460_k = (float)i;
        }
    }

    public int func_148441_d()
    {
        return this.func_148447_b() - (this.field_148461_h - this.field_148450_f - 4);
    }

    public void func_148446_a(int p_148446_1_, int p_148446_2_, float p_148446_3_)
    {
        this.field_148455_c = p_148446_1_;
        this.field_148452_d = p_148446_2_;
        this.func_148445_c();
        int k = this.func_148443_a();
        int l = this.func_148440_e();
        int i1 = l + 6;
        int k1;
        int l1;
        int i2;
        int j2;
        int j3;

        if (Mouse.isButtonDown(0))
        {
            if (this.field_148462_i == -1.0F)
            {
                boolean flag = true;

                if (p_148446_2_ >= this.field_148450_f && p_148446_2_ <= this.field_148461_h)
                {
                    int j1 = this.field_148453_e + 2;
                    k1 = this.field_148451_g - 2;
                    l1 = p_148446_2_ - this.field_148450_f + (int)this.field_148460_k - 4;
                    i2 = l1 / this.field_148454_b;

                    if (p_148446_1_ >= j1 && p_148446_1_ <= k1 && i2 >= 0 && l1 >= 0 && i2 < k)
                    {
                        boolean flag1 = i2 == this.field_148457_l && Minecraft.getSystemTime() - this.field_148458_m < 250L;
                        this.func_148449_a(i2, flag1);
                        this.field_148457_l = i2;
                        this.field_148458_m = Minecraft.getSystemTime();
                    }
                    else if (p_148446_1_ >= j1 && p_148446_1_ <= k1 && l1 < 0)
                    {
                        flag = false;
                    }

                    if (p_148446_1_ >= l && p_148446_1_ <= i1)
                    {
                        this.field_148459_j = -1.0F;
                        j3 = this.func_148441_d();

                        if (j3 < 1)
                        {
                            j3 = 1;
                        }

                        j2 = (int)((float)((this.field_148461_h - this.field_148450_f) * (this.field_148461_h - this.field_148450_f)) / (float)this.func_148447_b());

                        if (j2 < 32)
                        {
                            j2 = 32;
                        }

                        if (j2 > this.field_148461_h - this.field_148450_f - 8)
                        {
                            j2 = this.field_148461_h - this.field_148450_f - 8;
                        }

                        this.field_148459_j /= (float)(this.field_148461_h - this.field_148450_f - j2) / (float)j3;
                    }
                    else
                    {
                        this.field_148459_j = 1.0F;
                    }

                    if (flag)
                    {
                        this.field_148462_i = (float)p_148446_2_;
                    }
                    else
                    {
                        this.field_148462_i = -2.0F;
                    }
                }
                else
                {
                    this.field_148462_i = -2.0F;
                }
            }
            else if (this.field_148462_i >= 0.0F)
            {
                this.field_148460_k -= ((float)p_148446_2_ - this.field_148462_i) * this.field_148459_j;
                this.field_148462_i = (float)p_148446_2_;
            }
        }
        else
        {
            while (!this.field_148456_a.gameSettings.touchscreen && Mouse.next())
            {
                int i3 = Mouse.getEventDWheel();

                if (i3 != 0)
                {
                    if (i3 > 0)
                    {
                        i3 = -1;
                    }
                    else if (i3 < 0)
                    {
                        i3 = 1;
                    }

                    this.field_148460_k += (float)(i3 * this.field_148454_b / 2);
                }
            }

            this.field_148462_i = -1.0F;
        }

        this.func_148448_f();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        this.field_148456_a.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f1 = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(2105376);
        tessellator.addVertexWithUV((double)this.field_148453_e, (double)this.field_148461_h, 0.0D, (double)((float)this.field_148453_e / f1), (double)((float)(this.field_148461_h + (int)this.field_148460_k) / f1));
        tessellator.addVertexWithUV((double)this.field_148451_g, (double)this.field_148461_h, 0.0D, (double)((float)this.field_148451_g / f1), (double)((float)(this.field_148461_h + (int)this.field_148460_k) / f1));
        tessellator.addVertexWithUV((double)this.field_148451_g, (double)this.field_148450_f, 0.0D, (double)((float)this.field_148451_g / f1), (double)((float)(this.field_148450_f + (int)this.field_148460_k) / f1));
        tessellator.addVertexWithUV((double)this.field_148453_e, (double)this.field_148450_f, 0.0D, (double)((float)this.field_148453_e / f1), (double)((float)(this.field_148450_f + (int)this.field_148460_k) / f1));
        tessellator.draw();
        k1 = this.field_148453_e + 2;
        l1 = this.field_148450_f + 4 - (int)this.field_148460_k;
        int k2;

        for (i2 = 0; i2 < k; ++i2)
        {
            j3 = l1 + i2 * this.field_148454_b;
            j2 = this.field_148454_b - 4;

            if (j3 + this.field_148454_b <= this.field_148461_h && j3 - 4 >= this.field_148450_f)
            {
                if (this.func_148444_a(i2))
                {
                    k2 = this.field_148453_e + 2;
                    int l2 = this.field_148451_g - 2;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.setColorOpaque_I(8421504);
                    tessellator.addVertexWithUV((double)k2, (double)(j3 + j2 + 2), 0.0D, 0.0D, 1.0D);
                    tessellator.addVertexWithUV((double)l2, (double)(j3 + j2 + 2), 0.0D, 1.0D, 1.0D);
                    tessellator.addVertexWithUV((double)l2, (double)(j3 - 2), 0.0D, 1.0D, 0.0D);
                    tessellator.addVertexWithUV((double)k2, (double)(j3 - 2), 0.0D, 0.0D, 0.0D);
                    tessellator.setColorOpaque_I(0);
                    tessellator.addVertexWithUV((double)(k2 + 1), (double)(j3 + j2 + 1), 0.0D, 0.0D, 1.0D);
                    tessellator.addVertexWithUV((double)(l2 - 1), (double)(j3 + j2 + 1), 0.0D, 1.0D, 1.0D);
                    tessellator.addVertexWithUV((double)(l2 - 1), (double)(j3 - 1), 0.0D, 1.0D, 0.0D);
                    tessellator.addVertexWithUV((double)(k2 + 1), (double)(j3 - 1), 0.0D, 0.0D, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.func_148442_a(i2, k1, j3, j2, tessellator);
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte b0 = 4;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV((double)this.field_148453_e, (double)(this.field_148450_f + b0), 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double)this.field_148451_g, (double)(this.field_148450_f + b0), 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV((double)this.field_148451_g, (double)this.field_148450_f, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV((double)this.field_148453_e, (double)this.field_148450_f, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV((double)this.field_148453_e, (double)this.field_148461_h, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double)this.field_148451_g, (double)this.field_148461_h, 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV((double)this.field_148451_g, (double)(this.field_148461_h - b0), 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV((double)this.field_148453_e, (double)(this.field_148461_h - b0), 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        j3 = this.func_148441_d();

        if (j3 > 0)
        {
            j2 = (this.field_148461_h - this.field_148450_f) * (this.field_148461_h - this.field_148450_f) / this.func_148447_b();

            if (j2 < 32)
            {
                j2 = 32;
            }

            if (j2 > this.field_148461_h - this.field_148450_f - 8)
            {
                j2 = this.field_148461_h - this.field_148450_f - 8;
            }

            k2 = (int)this.field_148460_k * (this.field_148461_h - this.field_148450_f - j2) / j3 + this.field_148450_f;

            if (k2 < this.field_148450_f)
            {
                k2 = this.field_148450_f;
            }

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 255);
            tessellator.addVertexWithUV((double)l, (double)this.field_148461_h, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)this.field_148461_h, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)this.field_148450_f, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)l, (double)this.field_148450_f, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(8421504, 255);
            tessellator.addVertexWithUV((double)l, (double)(k2 + j2), 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)(k2 + j2), 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)k2, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)l, (double)k2, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(12632256, 255);
            tessellator.addVertexWithUV((double)l, (double)(k2 + j2 - 1), 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)(i1 - 1), (double)(k2 + j2 - 1), 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)(i1 - 1), (double)k2, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)l, (double)k2, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected int func_148440_e()
    {
        return this.field_148451_g - 8;
    }
}