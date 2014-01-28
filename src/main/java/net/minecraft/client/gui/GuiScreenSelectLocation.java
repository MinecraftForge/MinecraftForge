package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class GuiScreenSelectLocation
{
    private final Minecraft field_148368_a;
    private int field_148363_g;
    private int field_148375_h;
    protected int field_148366_b;
    protected int field_148367_c;
    private int field_148376_i;
    private int field_148373_j;
    protected final int field_148364_d;
    private int field_148374_k;
    private int field_148371_l;
    protected int field_148365_e;
    protected int field_148362_f;
    private float field_148372_m = -2.0F;
    private float field_148369_n;
    private float field_148370_o;
    private int field_148381_p = -1;
    private long field_148380_q;
    private boolean field_148379_r = true;
    private boolean field_148378_s;
    private int field_148377_t;
    private static final String __OBFID = "CL_00000785";

    public GuiScreenSelectLocation(Minecraft par1Minecraft, int par2, int par3, int par4, int par5, int par6)
    {
        this.field_148368_a = par1Minecraft;
        this.field_148363_g = par2;
        this.field_148375_h = par3;
        this.field_148366_b = par4;
        this.field_148367_c = par5;
        this.field_148364_d = par6;
        this.field_148373_j = 0;
        this.field_148376_i = par2;
    }

    public void func_148346_a(int p_148346_1_, int p_148346_2_, int p_148346_3_, int p_148346_4_)
    {
        this.field_148363_g = p_148346_1_;
        this.field_148375_h = p_148346_2_;
        this.field_148366_b = p_148346_3_;
        this.field_148367_c = p_148346_4_;
        this.field_148373_j = 0;
        this.field_148376_i = p_148346_1_;
    }

    protected abstract int func_148355_a();

    protected abstract void func_148352_a(int var1, boolean var2);

    protected abstract boolean func_148356_a(int var1);

    protected abstract boolean func_148349_b(int var1);

    protected int func_148351_b()
    {
        return this.func_148355_a() * this.field_148364_d + this.field_148377_t;
    }

    protected abstract void func_148358_c();

    protected abstract void func_148348_a(int var1, int var2, int var3, int var4, Tessellator var5);

    protected void func_148354_a(int p_148354_1_, int p_148354_2_, Tessellator p_148354_3_) {}

    protected void func_148359_a(int p_148359_1_, int p_148359_2_) {}

    protected void func_148353_b(int p_148353_1_, int p_148353_2_) {}

    private void func_148361_h()
    {
        int i = this.func_148347_d();

        if (i < 0)
        {
            i /= 2;
        }

        if (this.field_148370_o < 0.0F)
        {
            this.field_148370_o = 0.0F;
        }

        if (this.field_148370_o > (float)i)
        {
            this.field_148370_o = (float)i;
        }
    }

    public int func_148347_d()
    {
        return this.func_148351_b() - (this.field_148367_c - this.field_148366_b - 4);
    }

    public void func_148357_a(GuiButton p_148357_1_)
    {
        if (p_148357_1_.field_146124_l)
        {
            if (p_148357_1_.field_146127_k == this.field_148374_k)
            {
                this.field_148370_o -= (float)(this.field_148364_d * 2 / 3);
                this.field_148372_m = -2.0F;
                this.func_148361_h();
            }
            else if (p_148357_1_.field_146127_k == this.field_148371_l)
            {
                this.field_148370_o += (float)(this.field_148364_d * 2 / 3);
                this.field_148372_m = -2.0F;
                this.func_148361_h();
            }
        }
    }

    public void func_148350_a(int p_148350_1_, int p_148350_2_, float p_148350_3_)
    {
        this.field_148365_e = p_148350_1_;
        this.field_148362_f = p_148350_2_;
        this.func_148358_c();
        int k = this.func_148355_a();
        int l = this.func_148360_g();
        int i1 = l + 6;
        int k1;
        int l1;
        int i2;
        int j2;
        int j3;

        if (Mouse.isButtonDown(0))
        {
            if (this.field_148372_m == -1.0F)
            {
                boolean flag = true;

                if (p_148350_2_ >= this.field_148366_b && p_148350_2_ <= this.field_148367_c)
                {
                    int j1 = this.field_148363_g / 2 - 110;
                    k1 = this.field_148363_g / 2 + 110;
                    l1 = p_148350_2_ - this.field_148366_b - this.field_148377_t + (int)this.field_148370_o - 4;
                    i2 = l1 / this.field_148364_d;

                    if (p_148350_1_ >= j1 && p_148350_1_ <= k1 && i2 >= 0 && l1 >= 0 && i2 < k)
                    {
                        boolean flag1 = i2 == this.field_148381_p && Minecraft.getSystemTime() - this.field_148380_q < 250L;
                        this.func_148352_a(i2, flag1);
                        this.field_148381_p = i2;
                        this.field_148380_q = Minecraft.getSystemTime();
                    }
                    else if (p_148350_1_ >= j1 && p_148350_1_ <= k1 && l1 < 0)
                    {
                        this.func_148359_a(p_148350_1_ - j1, p_148350_2_ - this.field_148366_b + (int)this.field_148370_o - 4);
                        flag = false;
                    }

                    if (p_148350_1_ >= l && p_148350_1_ <= i1)
                    {
                        this.field_148369_n = -1.0F;
                        j3 = this.func_148347_d();

                        if (j3 < 1)
                        {
                            j3 = 1;
                        }

                        j2 = (int)((float)((this.field_148367_c - this.field_148366_b) * (this.field_148367_c - this.field_148366_b)) / (float)this.func_148351_b());

                        if (j2 < 32)
                        {
                            j2 = 32;
                        }

                        if (j2 > this.field_148367_c - this.field_148366_b - 8)
                        {
                            j2 = this.field_148367_c - this.field_148366_b - 8;
                        }

                        this.field_148369_n /= (float)(this.field_148367_c - this.field_148366_b - j2) / (float)j3;
                    }
                    else
                    {
                        this.field_148369_n = 1.0F;
                    }

                    if (flag)
                    {
                        this.field_148372_m = (float)p_148350_2_;
                    }
                    else
                    {
                        this.field_148372_m = -2.0F;
                    }
                }
                else
                {
                    this.field_148372_m = -2.0F;
                }
            }
            else if (this.field_148372_m >= 0.0F)
            {
                this.field_148370_o -= ((float)p_148350_2_ - this.field_148372_m) * this.field_148369_n;
                this.field_148372_m = (float)p_148350_2_;
            }
        }
        else
        {
            while (!this.field_148368_a.gameSettings.touchscreen && Mouse.next())
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

                    this.field_148370_o += (float)(i3 * this.field_148364_d / 2);
                }
            }

            this.field_148372_m = -1.0F;
        }

        this.func_148361_h();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        this.field_148368_a.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f1 = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(2105376);
        tessellator.addVertexWithUV((double)this.field_148373_j, (double)this.field_148367_c, 0.0D, (double)((float)this.field_148373_j / f1), (double)((float)(this.field_148367_c + (int)this.field_148370_o) / f1));
        tessellator.addVertexWithUV((double)this.field_148376_i, (double)this.field_148367_c, 0.0D, (double)((float)this.field_148376_i / f1), (double)((float)(this.field_148367_c + (int)this.field_148370_o) / f1));
        tessellator.addVertexWithUV((double)this.field_148376_i, (double)this.field_148366_b, 0.0D, (double)((float)this.field_148376_i / f1), (double)((float)(this.field_148366_b + (int)this.field_148370_o) / f1));
        tessellator.addVertexWithUV((double)this.field_148373_j, (double)this.field_148366_b, 0.0D, (double)((float)this.field_148373_j / f1), (double)((float)(this.field_148366_b + (int)this.field_148370_o) / f1));
        tessellator.draw();
        k1 = this.field_148363_g / 2 - 92 - 16;
        l1 = this.field_148366_b + 4 - (int)this.field_148370_o;

        if (this.field_148378_s)
        {
            this.func_148354_a(k1, l1, tessellator);
        }

        int k2;

        for (i2 = 0; i2 < k; ++i2)
        {
            j3 = l1 + i2 * this.field_148364_d + this.field_148377_t;
            j2 = this.field_148364_d - 4;

            if (j3 <= this.field_148367_c && j3 + j2 >= this.field_148366_b)
            {
                int l2;

                if (this.field_148379_r && this.func_148349_b(i2))
                {
                    k2 = this.field_148363_g / 2 - 110;
                    l2 = this.field_148363_g / 2 + 110;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.setColorOpaque_I(0);
                    tessellator.addVertexWithUV((double)k2, (double)(j3 + j2 + 2), 0.0D, 0.0D, 1.0D);
                    tessellator.addVertexWithUV((double)l2, (double)(j3 + j2 + 2), 0.0D, 1.0D, 1.0D);
                    tessellator.addVertexWithUV((double)l2, (double)(j3 - 2), 0.0D, 1.0D, 0.0D);
                    tessellator.addVertexWithUV((double)k2, (double)(j3 - 2), 0.0D, 0.0D, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                if (this.field_148379_r && this.func_148356_a(i2))
                {
                    k2 = this.field_148363_g / 2 - 110;
                    l2 = this.field_148363_g / 2 + 110;
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

                this.func_148348_a(i2, k1, j3, j2, tessellator);
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte b0 = 4;
        this.func_148345_b(0, this.field_148366_b, 255, 255);
        this.func_148345_b(this.field_148367_c, this.field_148375_h, 255, 255);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV((double)this.field_148373_j, (double)(this.field_148366_b + b0), 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double)this.field_148376_i, (double)(this.field_148366_b + b0), 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV((double)this.field_148376_i, (double)this.field_148366_b, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV((double)this.field_148373_j, (double)this.field_148366_b, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV((double)this.field_148373_j, (double)this.field_148367_c, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((double)this.field_148376_i, (double)this.field_148367_c, 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV((double)this.field_148376_i, (double)(this.field_148367_c - b0), 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV((double)this.field_148373_j, (double)(this.field_148367_c - b0), 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        j3 = this.func_148347_d();

        if (j3 > 0)
        {
            j2 = (this.field_148367_c - this.field_148366_b) * (this.field_148367_c - this.field_148366_b) / this.func_148351_b();

            if (j2 < 32)
            {
                j2 = 32;
            }

            if (j2 > this.field_148367_c - this.field_148366_b - 8)
            {
                j2 = this.field_148367_c - this.field_148366_b - 8;
            }

            k2 = (int)this.field_148370_o * (this.field_148367_c - this.field_148366_b - j2) / j3 + this.field_148366_b;

            if (k2 < this.field_148366_b)
            {
                k2 = this.field_148366_b;
            }

            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 255);
            tessellator.addVertexWithUV((double)l, (double)this.field_148367_c, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)this.field_148367_c, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double)i1, (double)this.field_148366_b, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV((double)l, (double)this.field_148366_b, 0.0D, 0.0D, 0.0D);
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

        this.func_148353_b(p_148350_1_, p_148350_2_);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected int func_148360_g()
    {
        return this.field_148363_g / 2 + 124;
    }

    private void func_148345_b(int p_148345_1_, int p_148345_2_, int p_148345_3_, int p_148345_4_)
    {
        Tessellator tessellator = Tessellator.instance;
        this.field_148368_a.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(4210752, p_148345_4_);
        tessellator.addVertexWithUV(0.0D, (double)p_148345_2_, 0.0D, 0.0D, (double)((float)p_148345_2_ / f));
        tessellator.addVertexWithUV((double)this.field_148363_g, (double)p_148345_2_, 0.0D, (double)((float)this.field_148363_g / f), (double)((float)p_148345_2_ / f));
        tessellator.setColorRGBA_I(4210752, p_148345_3_);
        tessellator.addVertexWithUV((double)this.field_148363_g, (double)p_148345_1_, 0.0D, (double)((float)this.field_148363_g / f), (double)((float)p_148345_1_ / f));
        tessellator.addVertexWithUV(0.0D, (double)p_148345_1_, 0.0D, 0.0D, (double)((float)p_148345_1_ / f));
        tessellator.draw();
    }
}